package eu.europa.ec.mare.usm.authentication.service.impl;

import eu.europa.ec.mare.usm.authentication.domain.AuthenticationQuery;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;
import eu.europa.ec.mare.usm.authentication.service.AuthenticationService;
import eu.europa.ec.mare.usm.policy.service.impl.PolicyProvider;
import eu.europa.ec.mare.usm.service.impl.RequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AuthenticationServiceBean implements AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceBean.class);

    private static final String AUTHENTICATION_SUBJECT = "Authentication";
    private static final String RENEWAL_REMINDER = "password.renewalReminder";
    private static final String LOCKOUT_DURATION = "account.lockoutDuration";
    private static final String LOCKOUT_FRESHOLD = "account.lockoutFreshold";
    private static final String LDAP_ENABLED = "ldap.enabled";
    private static final long ONE_MINUTE = (1000L * 60L);
    private static final long ONE_DAY = (1000L * 60 * 60 * 24);
    private static final String LOCKED = "L";
    private static final String DISABLED = "D";
    private static final String ENABLED = "E";

    @EJB
    private AuthenticationDao dao;

    @EJB
    private PolicyProvider policyProvider;

    @Inject
    private RequestValidator validator;

    @Inject
    @CreateLdapUser
    private Event<CreateLdapUserEvent> ldapUserEventEvent;

    @Override
    public boolean isLDAPEnabled() {
        LOGGER.debug("isLDAPEnabled() - (ENTER)");

        Properties props = policyProvider.getProperties(AUTHENTICATION_SUBJECT);
        boolean ldapEnabled = Boolean.parseBoolean(props.getProperty(LDAP_ENABLED, "false"));

        LOGGER.debug("isLDAPEnabled() - (LEAVE): " + ldapEnabled);
        return ldapEnabled;
    }

    @Override
    public boolean isPasswordExpired(String userName) {
        LOGGER.debug("isPasswordExpired() - (ENTER)");

        boolean passwordExpired = false;

        dao.recordLoginSuccess(userName);

        Date now = new Date();
        Date expiry = dao.getPasswordExpiry(userName);
        if (expiry != null) {
            if (expiry.before(now)) {
                passwordExpired = true;
            }
        }

        LOGGER.debug("isPasswordExpired() - (LEAVE)");
        return passwordExpired;
    }

    @Override
    public boolean isPasswordAboutToExpire(String userName) {
        LOGGER.debug("isPasswordAboutToExpire() - (ENTER)");

        boolean passwordAboutToExpire = false;

        dao.recordLoginSuccess(userName);

        Date now = new Date();
        Date expiry = dao.getPasswordExpiry(userName);
        if (expiry != null) {
            Properties props = policyProvider.getProperties(AUTHENTICATION_SUBJECT);
            int days = policyProvider.getIntProperty(props, RENEWAL_REMINDER, 0);
            if (days != 0) {
                Date reminder = new Date(expiry.getTime() - (days * ONE_DAY));
                if (reminder.before(now)) {
                    passwordAboutToExpire = true;
                }
            }
        }

        LOGGER.debug("isPasswordAboutToExpire() - (LEAVE)");
        return passwordAboutToExpire;
    }

    @Override
    public AuthenticationResponse authenticateUser(AuthenticationRequest request) {
        LOGGER.debug("authenticateUser(" + request + ") - (ENTER)");
        validator.assertValid(request);

        AuthenticationResponse authenticationResponse;
        if (isLDAPEnabled()) {
            authenticationResponse = authenticateLdap(request);
        } else {
            authenticationResponse = authenticateLocal(request);
        }

        if (!authenticationResponse.isAuthenticated()) {
            handleLoginFailure(request.getUserName());
            LOGGER.debug("authenticateUser() - (LEAVE): " + authenticationResponse);
            return authenticationResponse;
        }

        Date now = new Date();
        Date passwordExpiryDate = handleLoginSuccess(authenticationResponse.getUserMap(), request.getUserName());

        if (passwordExpiryDate != null) {
            if (passwordExpiryDate.before(now)) {
                authenticationResponse.setStatusCode(AuthenticationResponse.PASSWORD_EXPIRED);
            } else {
                Properties properties = policyProvider.getProperties(AUTHENTICATION_SUBJECT);
                int days = policyProvider.getIntProperty(properties, RENEWAL_REMINDER, 0);
                if (days != 0) {
                    Date reminder = new Date(passwordExpiryDate.getTime() - (days * ONE_DAY));
                    if (reminder.before(now)) {
                        authenticationResponse.setStatusCode(AuthenticationResponse.MUST_CHANGE_PASSWORD);
                    }
                }
            }
        }
        LOGGER.debug("authenticateUser() - (LEAVE): " + authenticationResponse);
        return authenticationResponse;
    }

    @Override
    public ChallengeResponse getUserChallenge(AuthenticationQuery query) {
        LOGGER.debug("getUserChallenge(" + query + ") - (ENTER)");

        validator.assertValid(query);

        ChallengeResponse challengeResponse = null;
        try {
            List<ChallengeResponse> userChallenges = dao.getUserChallenges(query.getUserName());

            if (userChallenges != null && !userChallenges.isEmpty()) {
                Random randomGenerator = new Random();
                int index = randomGenerator.nextInt(userChallenges.size());
                challengeResponse = userChallenges.get(index);
            }
        } catch (Exception e) {
            throw new RuntimeException("Problem: " + e.getMessage(), e);
        }

        LOGGER.debug("getUserChallenge() - (LEAVE): " + challengeResponse);
        return challengeResponse;
    }

    @Override
    public AuthenticationResponse authenticateUser(ChallengeResponse request) {
        LOGGER.debug("authenticateUser(" + request + ") - (ENTER)");
        validator.assertValid(request);

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        try {
            Long uid = dao.getActiveUserId(request);

            authenticationResponse.setAuthenticated(uid != null);
            if (authenticationResponse.isAuthenticated()) {
                authenticationResponse.setStatusCode(AuthenticationResponse.SUCCESS);
            } else {
                authenticationResponse.setStatusCode(AuthenticationResponse.INVALID_CREDENTIALS);
            }
        } catch (Exception e) {
            LOGGER.error("Problem: " + e.getMessage(), e);
            authenticationResponse.setStatusCode(AuthenticationResponse.INTERNAL_ERROR);
        }

        LOGGER.debug("authenticateUser(" + request.getUserName() + ") - (LEAVE): " + authenticationResponse);
        return authenticationResponse;
    }

    private AuthenticationResponse authenticateLocal(AuthenticationRequest request) {
        LOGGER.debug("authenticateLocal(" + request + ") - (ENTER)");
        AuthenticationResponse authenticationResponse = createResponse();

        try {
            String password = hashPassword(request.getPassword());
            Long uid = dao.getActiveUserId(request.getUserName(), password);
            String lockoutReason = dao.getLockoutReason(request.getUserName());
            if (uid != null) {
                authenticationResponse.setAuthenticated(true);
                authenticationResponse.setStatusCode(AuthenticationResponse.SUCCESS);
            } else {
                String status = dao.getUserStatus(request.getUserName());
                if (ENABLED.equals(status)) {
                    // Invalid password
                    authenticationResponse.setStatusCode(AuthenticationResponse.INVALID_CREDENTIALS);
                } else if (status == null) {
                    // Invalid userName
                    authenticationResponse.setStatusCode(AuthenticationResponse.INVALID_CREDENTIALS);
                } else if (DISABLED.equals(status)) {
                    authenticationResponse.setStatusCode(AuthenticationResponse.ACCOUNT_DISABLED);
                } else if (LOCKED.equals(status)) {
                    authenticationResponse.setStatusCode(AuthenticationResponse.ACCOUNT_LOCKED);
                    authenticationResponse.setErrorDescription(lockoutReason);
                } else {
                    authenticationResponse.setStatusCode(AuthenticationResponse.OTHER);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Problem: " + e.getMessage(), e);
            authenticationResponse.setStatusCode(AuthenticationResponse.INTERNAL_ERROR);
        }

        LOGGER.debug("authenticateLocal() - (LEAVE): " + authenticationResponse);
        return authenticationResponse;
    }

    private AuthenticationResponse authenticateLdap(AuthenticationRequest request) {
        LOGGER.debug("authenticateLdap(" + request + ") - (ENTER)");
        AuthenticationResponse authenticationResponse = createResponse();
        String username = request.getUserName();

        LDAP ldap = new LDAP(policyProvider.getProperties(AUTHENTICATION_SUBJECT));
        Map<String, Object> userMap = ldap.authenticate(username, request.getPassword());
        LOGGER.debug("ldap.authenticate: " + userMap);

        if (userMap == null) {
            LOGGER.debug("authenticateLdap() - (LEAVE): " + authenticationResponse);
            return authenticationResponse;
        }

        if (hasLdapError(userMap)) {
            authenticationResponse.setStatusCode(((Integer) userMap.get(LDAP.STATUS_CODE)));
            LOGGER.debug("authenticateLdap() - (LEAVE): " + authenticationResponse);
            return authenticationResponse;
        }

//        if (userDoesNotExistInDatabase(username)) {
//            createUserInDatabaseFromLdap(username);
//        }
//
//        if (personDoesNotExistInDatabase(username)) {
//            createPersonInDatabaseFromLdap(username, userMap);
//        }

        String status = dao.getUserStatus(username);
        if (ENABLED.equals(status)) {
            authenticationResponse.setAuthenticated(true);
            authenticationResponse.setUserMap(userMap);
            authenticationResponse.setStatusCode(AuthenticationResponse.SUCCESS);
        } else if (DISABLED.equals(status)) {
            authenticationResponse.setStatusCode(AuthenticationResponse.ACCOUNT_DISABLED);
        } else if (LOCKED.equals(status)) {
            authenticationResponse.setStatusCode(AuthenticationResponse.ACCOUNT_LOCKED);
        } else {
            authenticationResponse.setStatusCode(AuthenticationResponse.OTHER);
        }

        LOGGER.debug("authenticateLdap() - (LEAVE): " + authenticationResponse);
        return authenticationResponse;
    }

    private boolean hasLdapError(Map<String, Object> userMap) {
        return userMap.get(LDAP.STATUS_CODE) != null;
    }

    private AuthenticationResponse createResponse() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAuthenticated(false);
        authenticationResponse.setStatusCode(AuthenticationResponse.INVALID_CREDENTIALS);
        return authenticationResponse;
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        if (password == null) {
            return null;
        }

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());

        byte[] digest = md.digest();

        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : digest) {
            stringBuilder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }

        return stringBuilder.toString();
    }

    private Date handleLoginSuccess(Map<String, Object> userMap, String userName) {
        LOGGER.debug("handleLoginSuccess(" + userName + ") - (ENTER)");

        dao.recordLoginSuccess(userName);

        Date passwordExpiryDate = dao.getPasswordExpiry(userName);

        if (isLDAPEnabled() && userMap != null && !userMap.isEmpty()) {
            handleSyncWithLDAP(userMap, userName);
        } else {
            LOGGER.debug("No handle sync with LDAP need");
        }

        LOGGER.debug("handleLoginSuccess() - (LEAVE)");
        return passwordExpiryDate;
    }

    private void handleSyncWithLDAP(Map<String, Object> userMap, String userName) {
        int personId = dao.getPersonId(userName);
        if (personId == 0) {
            dao.createPersonForUser(userMap, userName);
        } else {
            dao.syncPerson(userMap, personId);
        }
    }

    private boolean personDoesNotExistInDatabase(String username) {
        return dao.getPersonId(username) == 0;
    }

    private void createPersonInDatabaseFromLdap(String username, Map<String, Object> userMap) {
        dao.createPersonForUser(userMap, username);
    }

    private boolean userDoesNotExistInDatabase(String username) {
        return dao.getUserStatus(username) == null;
    }

    private void createUserInDatabaseFromLdap(String username) {
        CreateLdapUserEvent createLdapUserEvent = new CreateLdapUserEvent();
        createLdapUserEvent.username = username;
        ldapUserEventEvent.fire(createLdapUserEvent);
    }

    private void handleLoginFailure(String userName) {
        LOGGER.debug("handleLoginFailure(" + userName + ") - (ENTER)");

        dao.recordLoginFailure(userName);

        Properties props = policyProvider.getProperties(AUTHENTICATION_SUBJECT);
        int threshold = policyProvider.getIntProperty(props, LOCKOUT_FRESHOLD, 0);
        int duration = policyProvider.getIntProperty(props, LOCKOUT_DURATION, 0);
        if (threshold != 0 && duration != 0) {
            int failures = dao.getLoginFailures(userName);
            if (failures >= threshold) {
                Date lockoutExpiry = new Date(System.currentTimeMillis() + duration * ONE_MINUTE);
                dao.lockUser(userName, lockoutExpiry);
            }
        }

        LOGGER.debug("handleLoginFailure() - (LEAVE)");
    }
}
