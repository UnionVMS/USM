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
import javax.inject.Inject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

/**
 * Stateless Session Bean implementation of the AuthenticationService.
 */
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

    @Override
    public boolean isLDAPEnabled() {
        LOGGER.info("isLDAPEnabled() - (ENTER)");

        Properties props = policyProvider.getProperties(AUTHENTICATION_SUBJECT);
        boolean ret = Boolean.parseBoolean(props.getProperty(LDAP_ENABLED, "false"));

        LOGGER.info("isLDAPEnabled() - (LEAVE): " + ret);
        return ret;
    }

    @Override
    public boolean isPasswordExpired(String userName) {
        LOGGER.info("isPasswordExpired() - (ENTER)");

        boolean passwordExpired = false;

        dao.recordLoginSuccess(userName);

        Date now = new Date();
        Date expiry = dao.getPasswordExpiry(userName);
        if (expiry != null) {
            if (expiry.before(now)) {
                passwordExpired = true;
            }
        }

        LOGGER.info("isPasswordExpired() - (LEAVE)");
        return passwordExpired;
    }

    @Override
    public boolean isPasswordAboutToExpire(String userName) {
        LOGGER.info("isPasswordAboutToExpire() - (ENTER)");

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

        LOGGER.info("isPasswordAboutToExpire() - (LEAVE)");
        return passwordAboutToExpire;
    }

    @Override
    public AuthenticationResponse authenticateUser(AuthenticationRequest request) {
        LOGGER.info("authenticateUser(" + request + ") - (ENTER)");
        validator.assertValid(request);

        AuthenticationResponse authenticationResponse;
        if (isLDAPEnabled()) {
            authenticationResponse = authenticateLdap(request);
        } else {
            authenticationResponse = authenticateLocal(request);
        }
        if (authenticationResponse.isAuthenticated()) {
            Date now = new Date();
            Date expiry = handleLoginSuccess(authenticationResponse.getUserMap(), request.getUserName());
            if (expiry != null) {
                if (expiry.before(now)) {
                    authenticationResponse.setStatusCode(AuthenticationResponse.PASSWORD_EXPIRED);
                } else {
                    Properties props = policyProvider.getProperties(AUTHENTICATION_SUBJECT);
                    int days = policyProvider.getIntProperty(props, RENEWAL_REMINDER, 0);
                    if (days != 0) {
                        Date reminder = new Date(expiry.getTime() - (days * ONE_DAY));
                        if (reminder.before(now)) {
                            authenticationResponse.setStatusCode(AuthenticationResponse.MUST_CHANGE_PASSWORD);
                        }
                    }
                }
            }
        } else {
            handleLoginFailure(request.getUserName());
        }
        LOGGER.info("authenticateUser() - (LEAVE): " + authenticationResponse);
        return authenticationResponse;
    }

    @Override
    public ChallengeResponse getUserChallenge(AuthenticationQuery query) {
        LOGGER.info("getUserChallenge(" + query + ") - (ENTER)");

        validator.assertValid(query);

        ChallengeResponse challengeResponse = null;
        try {
            List<ChallengeResponse> userChallenges = dao.getUserChallenges(query.getUserName());

            if (userChallenges != null && !userChallenges.isEmpty()) {
                Random randomGenerator = new Random();
                int index = randomGenerator.nextInt(userChallenges.size());
                challengeResponse = userChallenges.get(index);
            }
        } catch (Exception exc) {
            throw new RuntimeException("Problem: " + exc.getMessage(), exc);
        }

        LOGGER.info("getUserChallenge() - (LEAVE): " + challengeResponse);
        return challengeResponse;
    }

    @Override
    public AuthenticationResponse authenticateUser(ChallengeResponse request) {
        LOGGER.info("authenticateUser(" + request + ") - (ENTER)");
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

        LOGGER.info("authenticateUser(" + request.getUserName() + ") - (LEAVE): " + authenticationResponse);
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
        } catch (Exception exc) {
            LOGGER.error("Problem: " + exc.getMessage(), exc);
            authenticationResponse.setStatusCode(AuthenticationResponse.INTERNAL_ERROR);
        }

        LOGGER.debug("authenticateLocal() - (LEAVE): " + authenticationResponse);
        return authenticationResponse;
    }

    private AuthenticationResponse authenticateLdap(AuthenticationRequest request) {
        LOGGER.debug("authenticateLdap(" + request + ") - (ENTER)");
        AuthenticationResponse authenticationResponse = createResponse();

        LDAP ldap = new LDAP(policyProvider.getProperties(AUTHENTICATION_SUBJECT));
        Map<String, Object> userMap = ldap.authenticate(request.getUserName(), request.getPassword());
        LOGGER.debug("ldap.authenticate: " + userMap);

        if (userMap != null) {
            if (userMap.get(LDAP.STATUS_CODE) == null) {
                String status = dao.getUserStatus(request.getUserName());
                if (ENABLED.equals(status)) {
                    authenticationResponse.setAuthenticated(true);
                    authenticationResponse.setStatusCode(AuthenticationResponse.SUCCESS);
                } else if (DISABLED.equals(status)) {
                    authenticationResponse.setStatusCode(AuthenticationResponse.ACCOUNT_DISABLED);
                } else if (LOCKED.equals(status)) {
                    authenticationResponse.setStatusCode(AuthenticationResponse.ACCOUNT_LOCKED);
                } else {
                    authenticationResponse.setStatusCode(AuthenticationResponse.OTHER);
                }
            } else {
                authenticationResponse.setStatusCode(((Integer) userMap.get(LDAP.STATUS_CODE)));
            }
        }

        if (authenticationResponse.isAuthenticated()) {
            authenticationResponse.setUserMap(userMap);
        }

        LOGGER.debug("authenticateLdap() - (LEAVE): " + authenticationResponse);
        return authenticationResponse;
    }

    private AuthenticationResponse createResponse() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAuthenticated(false);
        authenticationResponse.setStatusCode(AuthenticationResponse.INVALID_CREDENTIALS);
        return authenticationResponse;
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        String hashedPassword = null;

        if (password != null) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());

            byte[] digest = md.digest();

            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : digest) {
                stringBuilder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            hashedPassword = stringBuilder.toString();
        }

        return hashedPassword;
    }

    private Date handleLoginSuccess(Map<String, Object> userMap, String userName) {
        LOGGER.debug("handleLoginSuccess(" + userName + ") - (ENTER)");

        dao.recordLoginSuccess(userName);

        Date passwordExpiry = dao.getPasswordExpiry(userName);

        if (isLDAPEnabled() && userMap != null && !userMap.isEmpty()) {
            handleSyncWithLDAP(userMap, userName);
        } else {
            LOGGER.debug("No handle sync with LDAP need");
        }

        LOGGER.info("handleLoginSuccess() - (LEAVE)");
        return passwordExpiry;
    }

    private void handleSyncWithLDAP(Map<String, Object> userMap, String userName) {
        int personId = dao.getPersonId(userName);
        if (personId == 0) {
            dao.createPersonForUser(userMap, userName);
        } else {
            dao.syncPerson(userMap, personId);
        }
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
                Date lockoutExpiry = new Date(System.currentTimeMillis() +
                        duration * ONE_MINUTE);
                dao.lockUser(userName, lockoutExpiry);
            }
        }

        LOGGER.info("handleLoginFailure() - (LEAVE)");
    }
}
