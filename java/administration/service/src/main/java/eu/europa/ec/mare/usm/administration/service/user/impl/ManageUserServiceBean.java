package eu.europa.ec.mare.usm.administration.service.user.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.audit.model.mapper.AuditLogMapper;
import eu.europa.ec.mare.audit.logger.AuditLogger;
import eu.europa.ec.mare.audit.logger.AuditLoggerFactory;
import eu.europa.ec.mare.audit.logger.AuditRecord;
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
import eu.europa.ec.mare.usm.administration.domain.UnauthenticatedException;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;
import eu.europa.ec.mare.usm.administration.domain.UserAccount;
import eu.europa.ec.mare.usm.administration.domain.UserStatus;
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
import eu.europa.ec.mare.usm.information.entity.ChallengeEntity;
import eu.europa.ec.mare.usm.information.entity.OrganisationEntity;
import eu.europa.ec.mare.usm.information.entity.PasswordHistEntity;
import eu.europa.ec.mare.usm.information.entity.PersonEntity;
import eu.europa.ec.mare.usm.information.entity.UserEntity;

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
  AuthenticationService authService;
  
  @EJB
  DefinitionService definitionService;  
  

  
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

  private final AuditLogger auditLogger;

  /**
   * Creates a new instance
   */
  public ManageUserServiceBean() 
  {
    auditLogger = AuditLoggerFactory.getAuditLogger();
  }
  
  @Override
  public UserAccount createUser(ServiceRequest<UserAccount> request) 
  {
    LOGGER.info("createUser(" + request + ") - (ENTER)");

    validator.assertValidUser(request,USMFeature.manageUsers);

    UserEntity entity = userDao.read(request.getBody().getUserName());
    if (entity != null) {
      throw new IllegalArgumentException(USER_ALREADY_EXISTS);
    }

    entity = new UserEntity();
    update(entity, request.getBody());
    entity.setCreatedBy(request.getRequester());
    entity.setCreatedOn(new Date());

    UserEntity user=userDao.create(entity);
    
    AuditRecord auditRecord = new AuditRecord(USMApplication.USM.name(),
				AuditOperationEnum.CREATE.getValue(), AuditObjectTypeEnum.USER.getValue(), request.getRequester(),
					request.getBody().getUserName(), request.getBody().getNotes());
		auditLogger.logEvent(auditRecord);
	
    
    LOGGER.info("createUser() - (LEAVE)");
    return convert(user);
  }

  @Override
  public UserAccount updateUser(ServiceRequest<UserAccount> request) 
  {
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

    UserAccount ret = convert(updatedUser);
    
    UserAccount userAccount = request.getBody();

		AuditRecord auditRecord = new AuditRecord(USMApplication.USM.name(),
				AuditOperationEnum.UPDATE.getValue(), AuditObjectTypeEnum.USER.getValue(),
				request.getRequester(), userAccount.getUserName(), userAccount.getNotes());
		auditLogger.logEvent(auditRecord);
	
    
    LOGGER.info("updateUser() - (LEAVE)");
    return ret;
  }

  @Override
  public void changePassword(ServiceRequest<ChangePassword> request) 
  throws IllegalArgumentException, UnauthorisedException, UnauthenticatedException, RuntimeException 
  {
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
    
    AuditRecord auditRecord = new AuditRecord(USMApplication.USM.name(),
				AuditOperationEnum.UPDATE.getValue(), AuditObjectTypeEnum.PASSWORD.getValue(), requester,
					userName, userName);
		auditLogger.logEvent(auditRecord);
	

    LOGGER.info("changePassword() - (LEAVE)");
  }

  private void authenticateUser(String userName, String password) 
  throws IllegalArgumentException 
  {
    AuthenticationRequest sr = new AuthenticationRequest();
    sr.setUserName(userName);
    sr.setPassword(password);
    AuthenticationResponse ret = authService.authenticateUser(sr);
  
    if (!ret.isAuthenticated()) {
      String msg;
      
      switch (ret.getStatusCode()) {
        case AuthenticationResponse.ACCOUNT_DISABLED:
          msg = ACCOUNT_DISABLED;
          break;
        case AuthenticationResponse.ACCOUNT_LOCKED:
          msg = ACCOUNT_LOCKED;
          break;
        case AuthenticationResponse.INTERNAL_ERROR:
          msg = INTERNAL_ERROR;
          break;
        case AuthenticationResponse.INVALID_CREDENTIALS:
          msg = INVALID_CREDENTIALS;
          break;
        case AuthenticationResponse.INVALID_TIME:
          msg = INVALID_TIME;
          break;
        case AuthenticationResponse.OTHER:
          // Fall through
        default:
          msg = USER_UNAUTHENTICATED;
          break;
      }
      
      throw new IllegalArgumentException(msg);
    }
  }
  
  private void update(UserEntity user, UserAccount request) 
  {
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

 private UserAccount convert(UserEntity src)
 {
    UserAccount ret = null;
    if (src != null) {
      ret = new UserAccount();
      ret.setStatus(src.getStatus());
      ret.setUserName(src.getUserName());
      ret.setActiveFrom(src.getActiveFrom());
      ret.setActiveTo(src.getActiveTo());
      ret.setLastLogon(src.getLastLogon());
      ret.setLockoutReason(src.getLockoutReason());
      ret.setLockoutTo(src.getLockoutTo());
      ret.setNotes(src.getNotes());
	  ret.setPerson(convertPersonEntityToDomain(src.getPerson()));
	  ret.setOrganisation(convertOrgEntityToDomain(src.getOrganisation())); 
	  if(ret.getOrganisation() != null){
		  ret.setOrganisation_parent(ret.getOrganisation().getParent()+" / "+ret.getOrganisation().getName());
	  }
    }
   
    return ret;
  } 
 
 private Organisation convertOrgEntityToDomain(OrganisationEntity entity){
	 if (entity!=null){
		 Organisation org=new Organisation();
		 org.setName(entity.getName());
		 if (entity.getParentOrganisation()!=null){
			 org.setParent(entity.getParentOrganisation().getName());
		 }
		 org.setNation(entity.getIsoa3code());
		 return org;
	 }else{
		 return null;
	 }
 }
  
 private Person convertPersonEntityToDomain(PersonEntity entity){
	 if (entity!=null){
		 Person ret=new Person();
		  ret.setPersonId(entity.getPersonId());
		  ret.setFirstName(entity.getFirstName());
	      ret.setLastName(entity.getLastName());
	      ret.setFaxNumber(entity.getFaxNumber());
	      ret.setEmail(entity.getEMail());
	      ret.setMobileNumber(entity.getMobileNumber());
	      ret.setPhoneNumber(entity.getPhoneNumber());
		 return ret;
	 }else {
		 return null;
	 }
		 
 }
  private void auditAction(String actionName, ServiceRequest<UserAccount> request) 
  {
    AuditRecord ar = new AuditRecord();
    
    ar.setActionName(actionName);
    ar.setApplicationName(USMApplication.USM.name());
    ar.setComponentName("ManageUserService");
    ar.setFailure(false);
    ar.setUserId(request.getRequester());
    ar.setResourceName(request.getBody().getUserName());
    ar.setRawData(request.getBody().getNotes());
    
    auditLogger.logEvent(ar);
  }
  
  private void changePassword(UserEntity entity, ServiceRequest<ChangePassword> request, boolean isTemporaryPassword) 
  {
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
      entity.setPasswordHistList(new ArrayList<PasswordHistEntity> ());
      entity.getPasswordHistList().add(h);
    }

    // Change password
    entity.setPassword(hash);

    if(isTemporaryPassword){
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
    public ChallengeInformationResponse getChallengeInformation(ServiceRequest<String> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {
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
            
            // return exactly the number of messages needed 
            //trunkChallenges(challengeEntities);
            
            List<ChallengeInformation> challengeInformations = new ArrayList<ChallengeInformation>();
            
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


    private void trunkChallenges(List<ChallengeEntity> challengeEntities) {        
        if (challengeEntities.size() < NUMBER_OF_CHALLENGES) {
            while (challengeEntities.size() < NUMBER_OF_CHALLENGES) {
                challengeEntities.add(new ChallengeEntity());
                
            }
        }
        
        if (challengeEntities.size() > NUMBER_OF_CHALLENGES) {
            while (challengeEntities.size() > NUMBER_OF_CHALLENGES) {
                challengeEntities.remove(challengeEntities.size() - 1);
                
            }            
        }        
    }
    
    private void trunkChallengeInformation(List<ChallengeInformation> challengeInformations, boolean isGetOperation) {
        
        int numberOfChallenges = getNumberOfChallenges();
        
        if (challengeInformations.size() < numberOfChallenges && isGetOperation) {
            while (challengeInformations.size() < numberOfChallenges) {
                challengeInformations.add(new ChallengeInformation());
                
            }
        }
        
        if (challengeInformations.size() > numberOfChallenges) {
            while (challengeInformations.size() > numberOfChallenges) {
                challengeInformations.remove(challengeInformations.size() - 1);
                
            }            
        }        
    }

    private int getNumberOfChallenges() {
        int numberOfChallenges = NUMBER_OF_CHALLENGES;
        
        String policyProperty = definitionService.getPolicyProperty("Password", "password.numberOfChallenges");
        
        if (policyProperty != null) {
            try {
                numberOfChallenges = Integer.valueOf(policyProperty);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return numberOfChallenges;
    }    


    @Override
    public ChallengeInformationResponse setChallengeInformation(ServiceRequest<ChallengeInformationResponse> request, String userName)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException {

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
            
            String auditOperation = null;
            
            if (challengeEntity.getChallengeId() == null) {
                challengeJpaDao.create(challengeEntity);
                auditOperation = AuditOperationEnum.CREATE.getValue();
            } else {
                challengeJpaDao.update(challengeEntity);
                auditOperation = AuditOperationEnum.UPDATE.getValue();
            }
            
            AuditRecord auditRecord = new AuditRecord(USMApplication.USM.name(),
    					auditOperation, AuditObjectTypeEnum.CHALLENGE.getValue(),
    						request.getRequester(), userName, userName);
    			auditLogger.logEvent(auditRecord);
    		
            
            challengeInformation.setChallengeId(challengeEntity.getChallengeId());
        }

        LOGGER.info("setChallengeInformation() - (LEAVE)");
        
        return challengeInformationResponse;
    }

	@Override
	public void resetPassword(ServiceRequest<ResetPasswordQuery> request)
			throws IllegalArgumentException, UnauthorisedException, RuntimeException {
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
		if(verifySecurityAnswers(request.getBody().getChallenges(), challengeJpaDao.getChallenges(userName))){
			// answers are valid, reset password
			resetPassword(entity, request.getBody().getPassword(), request.getBody().isTemporaryPassword());
		} else {
			throw new IllegalArgumentException(INVALID_ANSWERS);
		}
		
		AuditRecord auditRecord = new AuditRecord(USMApplication.USM.name(),
					AuditOperationEnum.RESET.getValue(), AuditObjectTypeEnum.PASSWORD.getValue(),
						request.getRequester(), userName, userName);
			auditLogger.logEvent(auditRecord);
		
		
	    LOGGER.info("resetPassword() - (LEAVE)");
	}
	
	private boolean verifySecurityAnswers(List<ChallengeInformation> userChallenges, 
			List<ChallengeEntity> challengesStroredInDB){
		
		int correctAnswers = 0;
		
		for (ChallengeEntity challengeInDB : challengesStroredInDB) {
			for (ChallengeInformation userChallenge : userChallenges) {
				if (challengeInDB.getResponse().equals(userChallenge.getResponse())) {
					correctAnswers += 1;
					break;
				}
			}
		}
		 
		if(correctAnswers == challengesStroredInDB.size()){
			return true;
		} else {
			return false;
		}
	}
	
	
	private void resetPassword(UserEntity entity, String newPassword, boolean isTemporaryPassword){
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
  throws  IllegalArgumentException, UnauthorisedException 
  {
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

		AuditRecord auditRecord = new AuditRecord(USMApplication.USM.name(),
              AuditOperationEnum.RESET.getValue(), AuditObjectTypeEnum.PASSWORD.getValue(),
              request.getRequester(), userName, userName);
      auditLogger.logEvent(auditRecord);

    LOGGER.info("resetPasswordAndNotify() - (LEAVE)");
	}
	
	@Override
	public String getPasswordPolicy(ServiceRequest<String> request){
		 LOGGER.info("getPasswordPolicy(" + request + ") - (ENTER)");
		 String policy = policyEnforcer.getPasswordPolicy();
		 LOGGER.info("getPasswordPolicy() - (LEAVE)");
		 return policy;
	}
	
  
}
