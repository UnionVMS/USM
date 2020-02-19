package eu.europa.ec.mare.usm.administration.service.user.impl;

import eu.europa.ec.fisheries.uvms.audit.model.mapper.AuditLogModelMapper;
import eu.europa.ec.mare.usm.administration.domain.AuditObjectTypeEnum;
import eu.europa.ec.mare.usm.administration.domain.AuditOperationEnum;
import eu.europa.ec.mare.usm.administration.domain.ChallengeInformation;
import eu.europa.ec.mare.usm.administration.domain.ChallengeInformationResponse;
import eu.europa.ec.mare.usm.administration.domain.ChangePassword;
import eu.europa.ec.mare.usm.administration.domain.Notification;
import eu.europa.ec.mare.usm.administration.domain.NotificationQuery;
import eu.europa.ec.mare.usm.administration.domain.Organisation;
import eu.europa.ec.mare.usm.administration.domain.Person;
import eu.europa.ec.mare.usm.administration.domain.ResetPasswordQuery;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.USMApplication;
import eu.europa.ec.mare.usm.administration.domain.USMFeature;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;
import eu.europa.ec.mare.usm.administration.domain.UserAccount;
import eu.europa.ec.mare.usm.administration.domain.UserStatus;
import eu.europa.ec.mare.usm.administration.service.AuditProducer;
import eu.europa.ec.mare.usm.administration.service.NotificationBuilder;
import eu.europa.ec.mare.usm.administration.service.NotificationSender;
import eu.europa.ec.mare.usm.administration.service.PasswordDigester;
import eu.europa.ec.mare.usm.administration.service.organisation.impl.OrganisationJpaDao;
import eu.europa.ec.mare.usm.administration.service.policy.DefinitionService;
import eu.europa.ec.mare.usm.administration.service.policy.PasswordPolicyEnforcer;
import eu.europa.ec.mare.usm.administration.service.user.ManageUserService;
import eu.europa.ec.mare.usm.administration.service.user.PasswordGenerator;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.service.AuthenticationService;
import eu.europa.ec.mare.usm.authentication.service.impl.CreateLdapUser;
import eu.europa.ec.mare.usm.authentication.service.impl.CreateLdapUserEvent;
import eu.europa.ec.mare.usm.information.entity.ChallengeEntity;
import eu.europa.ec.mare.usm.information.entity.OrganisationEntity;
import eu.europa.ec.mare.usm.information.entity.PasswordHistEntity;
import eu.europa.ec.mare.usm.information.entity.PersonEntity;
import eu.europa.ec.mare.usm.information.entity.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ManageUserServiceBean implements ManageUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageUserServiceBean.class);
    private static final String USER_DOES_NOT_EXIST = "User does not exist";
    private static final String USER_ALREADY_EXISTS = "User already exists";
    private static final String USER_IS_DISABLED = "User status is disabled";
    private static final String USER_UNAUTHENTICATED = "User cannot be authenticated";
    private static final String CREATE_USER = "create";
    private static final String UPDATE_USER = "update";
    private static final String RESET_PASSWD = "reset";
    private static final String ACCOUNT_DISABLED = "Account disabled";
    private static final String ACCOUNT_LOCKED = "Account locked";
    private static final String INTERNAL_ERROR = "Internal error";
    private static final String INVALID_CREDENTIALS = "Invalid credentials";
    private static final String INVALID_TIME = "Invalid time";
    private static final String INVALID_ANSWERS = "Invalid security answers";
    private static final String NO_EMAIL_FOUND = "No email found";
    private static final String RESET_PASSWD_NO_USER = "We encountered an error. Please try again later.";
    private static final int NUMBER_OF_CHALLENGES = 3;

    @EJB
    private PasswordPolicyEnforcer policyEnforcer;

    @EJB
    private AuthenticationService authService;

    @EJB
    private DefinitionService definitionService;

    @Inject
    private UserJpaDao userDao;

    @Inject
    private ChallengeJpaDao challengeJpaDao;

    @Inject
    private PasswordDigester digester;

    @Inject
    private OrganisationJpaDao organisationDao;

    @Inject
    private ManageUserValidator validator;

    @Inject
    private AuditProducer auditProducer;

    @Override
    public UserAccount createUser(ServiceRequest<UserAccount> request) {
        LOGGER.info("createUser(" + request + ") - (ENTER)");

        validator.assertValidUser(request, USMFeature.manageUsers);

        UserEntity entity = userDao.read(request.getBody().getUserName());
        if (entity != null) {
            throw new IllegalArgumentException(USER_ALREADY_EXISTS);
        }

        entity = new UserEntity();
        update(entity, request.getBody());
        entity.setCreatedBy(request.getRequester());
        entity.setCreatedOn(new Date());

        UserEntity user = userDao.create(entity);

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(), AuditOperationEnum.CREATE.getValue(), AuditObjectTypeEnum.USER.getValue() + " " + request.getBody().getUserName(), request.getBody().getNotes(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.info("createUser() - (LEAVE)");
        return convert(user);
    }

    public void createUserFromLdap(@Observes @CreateLdapUser CreateLdapUserEvent event) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(event.username);

        userEntity.setStatus("E");
        userEntity.setCreatedBy("LDAP");

        Date createdOn = new Date();
        userEntity.setActiveFrom(createdOn);
        userEntity.setCreatedOn(createdOn);

        userDao.create(userEntity);

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(), AuditOperationEnum.CREATE.getValue(), AuditObjectTypeEnum.USER.getValue() + " " + event.username, "", "LDAP");
        auditProducer.sendModuleMessage(auditLog);
    }

    @Override
    public UserAccount updateUser(ServiceRequest<UserAccount> request) {
        LOGGER.info("updateUser(" + request + ") - (ENTER)");

        validator.assertValidUser(request, USMFeature.manageUsers);
        UserEntity entity = userDao.read(request.getBody().getUserName());
        if (entity == null) {
            throw new IllegalArgumentException(USER_DOES_NOT_EXIST);
        }

        update(entity, request.getBody());
        entity.setModifiedBy(request.getRequester());
        entity.setModifiedOn(new Date());

        UserEntity updatedUser = userDao.update(entity);
        UserAccount userAccount = convert(updatedUser);

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(), AuditOperationEnum.UPDATE.getValue(), AuditObjectTypeEnum.USER.getValue() + " " + request.getBody().getUserName(), request.getBody().getNotes(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.info("updateUser() - (LEAVE)");
        return userAccount;
    }

    @Override
    public void changePassword(ServiceRequest<ChangePassword> request) throws RuntimeException {
        LOGGER.info("changePassword(" + request + ") - (ENTER)");

        // Sanity check
        validator.assertValidChangePassword(request, null);
        String userName = request.getBody().getUserName();
        UserEntity entity = userDao.read(userName);
        if (entity == null) {
            throw new IllegalArgumentException(USER_DOES_NOT_EXIST);
        }

        // Check who is using the feature?
        String requester = request.getRequester();
        if (requester.equals(userName)) {
            // This is a regular User changing his/her own password
            // make sure an administrator has not disabled the user
            if (UserStatus.DISABLED.getValue().equals(entity.getStatus())) {
                throw new UnauthorisedException(USER_IS_DISABLED);
            }
            // and make sure the user is who he/she claims to be
            validator.assertNotEmpty("changePassword.currentPassword",
                    request.getBody().getCurrentPassword());
            authenticateUser(request.getBody().getUserName(), request.getBody().getCurrentPassword());
        } else {
            // This is an Administrator changing the password of another user
            // make sure he/she is authorized to do so
            validator.assertValidChangePassword(request, USMFeature.manageUsers);
        }

        // Everything looks OK,
        // Just do it!
        changePassword(entity, request, false);

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(), AuditOperationEnum.UPDATE.getValue(), AuditObjectTypeEnum.PASSWORD.getValue() + " " + request.getBody().getUserName(), request.getRequester());
        auditProducer.sendModuleMessage(auditLog);

        LOGGER.info("changePassword() - (LEAVE)");
    }

    private void authenticateUser(String userName, String password) throws IllegalArgumentException {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUserName(userName);
        authenticationRequest.setPassword(password);
        AuthenticationResponse authenticationResponse = authService.authenticateUser(authenticationRequest);

        if (!authenticationResponse.isAuthenticated()) {
            String message;

            switch (authenticationResponse.getStatusCode()) {
                case AuthenticationResponse.ACCOUNT_DISABLED:
                    message = ACCOUNT_DISABLED;
                    break;
                case AuthenticationResponse.ACCOUNT_LOCKED:
                    message = ACCOUNT_LOCKED;
                    break;
                case AuthenticationResponse.INTERNAL_ERROR:
                    message = INTERNAL_ERROR;
                    break;
                case AuthenticationResponse.INVALID_CREDENTIALS:
                    message = INVALID_CREDENTIALS;
                    break;
                case AuthenticationResponse.INVALID_TIME:
                    message = INVALID_TIME;
                    break;
                case AuthenticationResponse.OTHER:
                    // Fall through
                default:
                    message = USER_UNAUTHENTICATED;
                    break;
            }

            throw new IllegalArgumentException(message);
        }
    }

    private void update(UserEntity user, UserAccount request) {
        //create user
        user.setUserName(request.getUserName());
        user.setStatus(request.getStatus());
        user.setActiveFrom(request.getActiveFrom());
        user.setActiveTo(request.getActiveTo());
        user.setNotes(request.getNotes());

        OrganisationEntity organisation = organisationDao.read(request.getOrganisation().getName());
        user.setOrganisation(organisation);

        PersonEntity person = user.getPerson();
        if (user.getPerson() == null || user.getPerson().getPersonId() == null) {
            person = new PersonEntity();
        }

        person.setFirstName(request.getPerson().getFirstName());
        person.setLastName(request.getPerson().getLastName());
        person.setMobileNumber(request.getPerson().getMobileNumber());
        person.setFaxNumber(request.getPerson().getFaxNumber());
        person.setEMail(request.getPerson().getEmail());
        person.setPhoneNumber(request.getPerson().getPhoneNumber());

        //extra fields for sure populated on update user
        user.setLockoutReason(request.getLockoutReason());
        user.setLockoutTo(request.getLockoutTo());
        user.setPerson(person);
    }

    private UserAccount convert(UserEntity userEntity) {
        UserAccount userAccount = null;
        if (userEntity != null) {
            userAccount = new UserAccount();
            userAccount.setStatus(userEntity.getStatus());
            userAccount.setUserName(userEntity.getUserName());
            userAccount.setActiveFrom(userEntity.getActiveFrom());
            userAccount.setActiveTo(userEntity.getActiveTo());
            userAccount.setLastLogon(userEntity.getLastLogon());
            userAccount.setLockoutReason(userEntity.getLockoutReason());
            userAccount.setLockoutTo(userEntity.getLockoutTo());
            userAccount.setNotes(userEntity.getNotes());
            userAccount.setPerson(convertPersonEntityToDomain(userEntity.getPerson()));
            userAccount.setOrganisation(convertOrgEntityToDomain(userEntity.getOrganisation()));

            if (userAccount.getOrganisation() != null) {
                userAccount.setOrganisation_parent(userAccount.getOrganisation().getParent() + " / " + userAccount.getOrganisation().getName());
            }
        }

        return userAccount;
    }

    private Organisation convertOrgEntityToDomain(OrganisationEntity entity) {
        if (entity != null) {
            Organisation organisation = new Organisation();
            organisation.setName(entity.getName());
            if (entity.getParentOrganisation() != null) {
                organisation.setParent(entity.getParentOrganisation().getName());
            }
            organisation.setNation(entity.getIsoa3code());
            return organisation;
        } else {
            return null;
        }
    }

    private Person convertPersonEntityToDomain(PersonEntity entity) {
        if (entity != null) {
            Person person = new Person();
            person.setPersonId(entity.getPersonId());
            person.setFirstName(entity.getFirstName());
            person.setLastName(entity.getLastName());
            person.setFaxNumber(entity.getFaxNumber());
            person.setEmail(entity.getEMail());
            person.setMobileNumber(entity.getMobileNumber());
            person.setPhoneNumber(entity.getPhoneNumber());
            return person;
        } else {
            return null;
        }

    }

    private void auditAction(String actionName, ServiceRequest<UserAccount> request) {
        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(), actionName, "ManageUserService " + request.getBody().getUserName(), request.getRequester(), request.getBody().getNotes());
        auditProducer.sendModuleMessage(auditLog);
    }

    private void changePassword(UserEntity entity, ServiceRequest<ChangePassword> request, boolean isTemporaryPassword) {
        // Apply applicable policies
        Date expiry = policyEnforcer.assertValid(request);

        // Keep track of time
        Date now = new Date();
        // Hash password
        String hash = digester.hashPassword(request.getBody().getNewPassword());

        // Update password history, if needed
        if (entity.getPassword() != null && !entity.getPassword().trim().isEmpty()) {
            PasswordHistEntity h = new PasswordHistEntity();
            h.setPassword(entity.getPassword());
            h.setChangedOn(now);
            h.setCreatedBy(request.getRequester());
            h.setCreatedOn(now);
            h.setUser(entity);
            entity.setPasswordHistList(new ArrayList<>());
            entity.getPasswordHistList().add(h);
        }

        // Change password
        entity.setPassword(hash);

        if (isTemporaryPassword) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, 0);
            entity.setPasswordExpiry(c.getTime());
        } else {
            entity.setPasswordExpiry(expiry);
        }

        // Write back user
        entity.setModifiedBy(request.getRequester());
        entity.setModifiedOn(now);
        userDao.update(entity);
        auditActionGeneric(RESET_PASSWD, request);
    }

    private <T> void auditActionGeneric(String actionName, ServiceRequest<? extends T> request) {
        ServiceRequest<UserAccount> userAccountRequest = new ServiceRequest<UserAccount>();
        userAccountRequest.setBody(new UserAccount());
        userAccountRequest.setRequester(request.getRequester());
        if (request.getBody() instanceof ChangePassword) {
            ChangePassword requestBody = (ChangePassword) request.getBody();
            userAccountRequest.getBody().setUserName(requestBody.getUserName());
            userAccountRequest.getBody().setNotes(requestBody.getUserName());
        }
        auditAction(actionName, userAccountRequest);
    }

    @Override
    public ChallengeInformationResponse getChallengeInformation(ServiceRequest<String> request) throws RuntimeException {
        LOGGER.info("getChallengeInformation(" + request + ") - (ENTER)");

        String userName = request.getBody();
        validator.assertValidChallengeUSer(request, null, userName);

        ChallengeInformationResponse response = new ChallengeInformationResponse();

        UserEntity entity = userDao.read(userName);
        if (entity == null) {
            throw new IllegalArgumentException(RESET_PASSWD_NO_USER);
        }

        // Check who is using the feature?        
        String requester = request.getRequester();
        if (requester.equals(userName)) {
            if (UserStatus.DISABLED.getValue().equals(entity.getStatus())) {
                throw new UnauthorisedException(USER_IS_DISABLED);
            }

            List<ChallengeEntity> challengeEntities = challengeJpaDao.getChallenges(userName);
            List<ChallengeInformation> challengeInformations = new ArrayList<>();

            for (ChallengeEntity challengeEntity : challengeEntities) {
                ChallengeInformation challengeInformation = new ChallengeInformation();

                challengeInformation.setChallengeId(challengeEntity.getChallengeId());
                challengeInformation.setChallenge(challengeEntity.getChallenge());
                challengeInformation.setResponse(challengeEntity.getResponse());
                challengeInformations.add(challengeInformation);
            }

            // return exactly the number of messages needed
            trunkChallengeInformation(challengeInformations, true);
            response.setResults(challengeInformations);
        } else {
            throw new UnauthorisedException(USER_UNAUTHENTICATED);
        }

        LOGGER.info("getChallengeInformation() - (LEAVE)");

        return response;
    }

    private void trunkChallengeInformation(List<ChallengeInformation> challengeInformationList, boolean isGetOperation) {
        int numberOfChallenges = getNumberOfChallenges();

        if (challengeInformationList.size() < numberOfChallenges && isGetOperation) {
            while (challengeInformationList.size() < numberOfChallenges) {
                challengeInformationList.add(new ChallengeInformation());
            }
        }

        if (challengeInformationList.size() > numberOfChallenges) {
            while (challengeInformationList.size() > numberOfChallenges) {
                challengeInformationList.remove(challengeInformationList.size() - 1);
            }
        }
    }

    private int getNumberOfChallenges() {
        int numberOfChallenges = NUMBER_OF_CHALLENGES;

        String policyProperty = definitionService.getPolicyProperty("Password", "password.numberOfChallenges");

        if (policyProperty != null) {
            try {
                numberOfChallenges = Integer.parseInt(policyProperty);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return numberOfChallenges;
    }


    @Override
    public ChallengeInformationResponse setChallengeInformation(ServiceRequest<ChallengeInformationResponse> request, String userName) throws RuntimeException {
        LOGGER.info("setChallengeInformation(" + request + ") - (ENTER)");

        validator.assertValidChallengeUSer(request, null, userName);
        validator.assertValidChallengeInformation(request, null);

        ChallengeInformationResponse challengeInformationResponse = request.getBody();
        String password = challengeInformationResponse.getUserPassword();
        List<ChallengeInformation> challengeInformations = challengeInformationResponse.getResults();

        UserEntity userEntity = userDao.read(userName);
        if (userEntity == null) {
            throw new IllegalArgumentException(USER_DOES_NOT_EXIST);
        }

        // try to authenticate it
        authenticateUser(userName, password);

        // if everything is ok then update the chanllenge information
        List<ChallengeEntity> challengeEntities = challengeJpaDao.getChallenges(userName);

        // return exactly the number of messages needed
        trunkChallengeInformation(challengeInformations, false);

        for (int i = 0; i < challengeInformations.size(); i++) {
            ChallengeInformation challengeInformation = challengeInformations.get(i);
            ChallengeEntity challengeEntity = null;

            if (challengeEntities.size() > i) {
                challengeEntity = challengeEntities.get(i);
            } else {
                // we need to create a new entity which will be saved
                challengeEntity = new ChallengeEntity();
                challengeEntity.setUser(userEntity);
            }

            challengeEntity.setChallenge(challengeInformation.getChallenge());
            challengeEntity.setResponse(challengeInformation.getResponse());

            String auditOperation;

            if (challengeEntity.getChallengeId() == null) {
                challengeJpaDao.create(challengeEntity);
                auditOperation = AuditOperationEnum.CREATE.getValue();
            } else {
                challengeJpaDao.update(challengeEntity);
                auditOperation = AuditOperationEnum.UPDATE.getValue();
            }

            String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(), auditOperation, AuditObjectTypeEnum.CHALLENGE.getValue() + " " + userName, userName, request.getRequester());
            auditProducer.sendModuleMessage(auditLog);

            challengeInformation.setChallengeId(challengeEntity.getChallengeId());
        }

        LOGGER.info("setChallengeInformation() - (LEAVE)");
        return challengeInformationResponse;
    }

    @Override
    public void resetPassword(ServiceRequest<ResetPasswordQuery> request) throws RuntimeException {
        LOGGER.info("resetPassword(" + request + ") - (ENTER)");

        String userName = request.getBody().getUserName();

        UserEntity entity = userDao.read(userName);
        // check if user exists
        if (entity == null) {
            throw new IllegalArgumentException(RESET_PASSWD_NO_USER);
        }

        // check if user is disabled
        if (UserStatus.DISABLED.getValue().equals(entity.getStatus())) {
            throw new UnauthorisedException(USER_IS_DISABLED);
        }

        validator.assertValidChallengeUSer(request, null, userName);

        // check if user's security questions response is valid
        if (verifySecurityAnswers(request.getBody().getChallenges(), challengeJpaDao.getChallenges(userName))) {
            // answers are valid, reset password
            resetPassword(entity, request.getBody().getPassword(), request.getBody().isTemporaryPassword());
        } else {
            throw new IllegalArgumentException(INVALID_ANSWERS);
        }

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(), AuditOperationEnum.RESET.getValue(), AuditObjectTypeEnum.PASSWORD.getValue() + " " + userName, userName, request.getRequester());
        auditProducer.sendModuleMessage(auditLog);
        LOGGER.info("resetPassword() - (LEAVE)");
    }

    private boolean verifySecurityAnswers(List<ChallengeInformation> userChallenges, List<ChallengeEntity> challengesStroredInDB) {
        int correctAnswers = 0;

        for (ChallengeEntity challengeInDB : challengesStroredInDB) {
            for (ChallengeInformation userChallenge : userChallenges) {
                if (challengeInDB.getResponse().equals(userChallenge.getResponse())) {
                    correctAnswers += 1;
                    break;
                }
            }
        }

        return correctAnswers == challengesStroredInDB.size();
    }

    private void resetPassword(UserEntity entity, String newPassword, boolean isTemporaryPassword) {
        // make sure the user's password is not empty
        validator.assertNotEmpty("changePassword.newPassword", newPassword);

        ServiceRequest<ChangePassword> request = new ServiceRequest<>();
        request.setRequester(entity.getUserName());
        ChangePassword changePassword = new ChangePassword();
        changePassword.setUserName(entity.getUserName());
        changePassword.setNewPassword(newPassword);
        request.setBody(changePassword);
        changePassword(entity, request, isTemporaryPassword);
    }

    @Override
    public void resetPasswordAndNotify(ServiceRequest<NotificationQuery> request)
            throws IllegalArgumentException, UnauthorisedException {
        LOGGER.info("resetPasswordAndNotify(" + request + ") - (ENTER)");

        String userName = request.getBody().getUserName();

        UserEntity entity = userDao.read(userName);
        // check if user exists
        if (entity == null) {
            throw new IllegalArgumentException(USER_DOES_NOT_EXIST);
        }
        // check if user is disabled
        if (UserStatus.DISABLED.getValue().equals(entity.getStatus())) {
            throw new UnauthorisedException(USER_IS_DISABLED);
        }

        // check if user has email stored in DB
        PersonEntity person = entity.getPerson();
        if (person == null || person.getEMail() == null) {
            throw new IllegalArgumentException(NO_EMAIL_FOUND);
        }

        // generate a random password
        String autoGeneratedPassword = PasswordGenerator.generatePswd(8, 32, 2, 2, 2, 2);

        // reset password to the newly created random password
        resetPassword(entity, autoGeneratedPassword, true);

        // create the notification
        String recipient = person.getEMail();
        Notification notification = NotificationBuilder.buildNotification(recipient,
                autoGeneratedPassword);
        // send the temporary password to user's email
        NotificationSender messageSender = new NotificationSender();
        try {
            messageSender.sendNotification(notification);
        } catch (MessagingException ex) {
            throw new RuntimeException("Failed to send e-mail to " + recipient, ex);
        }

        String auditLog = AuditLogModelMapper.mapToAuditLog(USMApplication.USM.name(), AuditOperationEnum.RESET.getValue(), AuditObjectTypeEnum.PASSWORD.getValue() + " " + userName, userName, request.getRequester());
        auditProducer.sendModuleMessage(auditLog);
        LOGGER.info("resetPasswordAndNotify() - (LEAVE)");
    }

    @Override
    public String getPasswordPolicy(ServiceRequest<String> request) {
        LOGGER.info("getPasswordPolicy(" + request + ") - (ENTER)");
        String policy = policyEnforcer.getPasswordPolicy();
        LOGGER.info("getPasswordPolicy() - (LEAVE)");
        return policy;
    }
}
