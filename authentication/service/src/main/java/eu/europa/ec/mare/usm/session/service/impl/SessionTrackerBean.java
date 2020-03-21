package eu.europa.ec.mare.usm.session.service.impl;

import eu.europa.ec.mare.usm.policy.service.impl.PolicyProvider;
import eu.europa.ec.mare.usm.service.impl.RequestValidator;
import eu.europa.ec.mare.usm.session.domain.SessionInfo;
import eu.europa.ec.mare.usm.session.service.SessionTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Stateless Session Bean implementation of the SessionTracker interface.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SessionTrackerBean implements SessionTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionTrackerBean.class);

    private static final String POLICY_SUBJECT = "Account";
    private static final long ONE_SECOND = 1000L;

    @EJB
    private PolicyProvider policyProvider;

    @Inject
    private RequestValidator validator;

    @EJB
    private SessionDao sessionDao;

    @Override
    public String startSession(SessionInfo sessionInfo)
			throws IllegalStateException, IllegalArgumentException, RuntimeException {

        LOGGER.debug("startSession(" + sessionInfo + ") - (ENTER)");
        String ret = null;
        validator.assertValid(sessionInfo);
        try {
            checkMaxSessionPolicy(sessionInfo);

            UserSession session = new UserSession();
            session.setCreationTime(new Date());
            session.setUserName(sessionInfo.getUserName());
            session.setUserSite(sessionInfo.getUserSite());

            ret = sessionDao.createSession(session);

            LOGGER.debug("startSession() - (LEAVE): " + ret);
        } catch (IllegalStateException ex) {
            LOGGER.warn("User exceeded the maximum number sessions!");
        }
        return ret;
    }

    @Override
    public SessionInfo getSession(String sessionId) throws IllegalArgumentException, RuntimeException {
        LOGGER.debug("getSession(" + sessionId + ") - (ENTER)");

        validator.assertNotEmpty("sessionId", sessionId);

        UserSession session = sessionDao.readSession(sessionId);
        SessionInfo ret = null;
        if (session != null) {
            ret = new SessionInfo();
            ret.setUserName(session.getUserName());
            ret.setUserSite(session.getUserSite());
        }

        LOGGER.debug("getSession() - (LEAVE): " + ret);
        return ret;
    }

    @Override
    public void endSession(String sessionId) throws IllegalArgumentException, RuntimeException {
        LOGGER.debug("endSession(" + sessionId + ") - (ENTER)");

        validator.assertNotEmpty("sessionId", sessionId);

        sessionDao.deleteSession(sessionId);

        LOGGER.debug("endSession() - (LEAVE)");
    }

    private void checkMaxSessionPolicy(SessionInfo sessionInfo) throws NumberFormatException {
        LOGGER.debug("checkMaxSessionPolicy(" + sessionInfo + ") - (ENTER)");
        boolean isAllowed = true;

        Properties policy = policyProvider.getProperties(POLICY_SUBJECT);
        int maxOneSite = policyProvider.getIntProperty(policy,
                "account.maxSessionOneSite", 0);
        int maxAnySite = policyProvider.getIntProperty(policy,
                "account.maxSessionAnySite", 0);
        int ttlSession = policyProvider.getIntProperty(policy,
                "account.maxSessionDuration", 0);

        if (maxOneSite > 0 || maxAnySite > 0) {
            int cntOneSite = 0;
            int cntAnySite = 0;
            Date earliestStartTime = new Date(System.currentTimeMillis()
                    - (ttlSession * ONE_SECOND));

            List<UserSession> lst = sessionDao.readSessions(
                    sessionInfo.getUserName(), earliestStartTime);
            for (UserSession session : lst) {
                if (sessionInfo.getUserSite().equals(session.getUserSite())) {
                    cntOneSite += 1;
                }
                cntAnySite += 1;
            }

            if ((maxOneSite > 0 && cntOneSite >= maxOneSite)) {
                isAllowed = false;
                LOGGER.warn("User " + sessionInfo.getUserName()
                        + " exceeded the maximum number (" + maxOneSite
                        + ") of user sessions for single site");
            }
            if ((maxAnySite > 0 && cntAnySite >= maxAnySite)) {
                isAllowed = false;
                LOGGER.warn("User " + sessionInfo.getUserName()
                        + " exceeded the maximum number (" + maxAnySite
                        + ") of user sessions for any site");
            }
        }
        if (!isAllowed) {
            throw new IllegalStateException(
                    "Maximum number of sessions exceeded");
        }

        LOGGER.debug("checkMaxSessionPolicy() - (LEAVE)");
    }
}
