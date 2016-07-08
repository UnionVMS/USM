/*
 * Developed by the European Commission - Directorate General for Maritime 
 * Affairs and Fisheries © European Union, 2015-2016.
 * 
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite.
 * The IFDM Suite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * The IFDM Suite is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details. You should have received a copy of the GNU General Public 
 * License along with the IFDM Suite. If not, see http://www.gnu.org/licenses/.
 */
package eu.europa.ec.mare.usm.session.service.impl;

import eu.europa.ec.mare.usm.policy.service.impl.PolicyProvider;
import eu.europa.ec.mare.usm.service.impl.RequestValidator;
import eu.europa.ec.mare.usm.session.domain.SessionInfo;
import eu.europa.ec.mare.usm.session.service.SessionTracker;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stateless Session Bean implementation of the SessionTracker interface.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SessionTrackerBean implements SessionTracker {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SessionTrackerBean.class);
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
			throws IllegalStateException, IllegalArgumentException,
			RuntimeException {
		LOGGER.info("startSession(" + sessionInfo + ") - (ENTER)");
		String ret = null;
		validator.assertValid(sessionInfo);
		try {
			checkMaxSessionPolicy(sessionInfo);

			UserSession session = new UserSession();
			session.setCreationTime(new Date());
			session.setUserName(sessionInfo.getUserName());
			session.setUserSite(sessionInfo.getUserSite());

			ret = sessionDao.createSession(session);

			LOGGER.info("startSession() - (LEAVE): " + ret);
		} catch (IllegalStateException ex) {
			LOGGER.warn("User exceeded the maximum number sessions!");
		}
		return ret;
	}

	@Override
	public SessionInfo getSession(String sessionId)
			throws IllegalArgumentException, RuntimeException {
		LOGGER.info("getSession(" + sessionId + ") - (ENTER)");

		validator.assertNotEmpty("sessionId", sessionId);

		UserSession session = sessionDao.readSession(sessionId);
		SessionInfo ret = null;
		if (session != null) {
			ret = new SessionInfo();
			ret.setUserName(session.getUserName());
			ret.setUserSite(session.getUserSite());
		}

		LOGGER.info("getSession() - (LEAVE): " + ret);
		return ret;
	}

	@Override
	public void endSession(String sessionId) throws IllegalArgumentException,
			RuntimeException {
		LOGGER.info("endSession(" + sessionId + ") - (ENTER)");

		validator.assertNotEmpty("sessionId", sessionId);

		sessionDao.deleteSession(sessionId);

		LOGGER.info("endSession() - (LEAVE)");
	}

	private void checkMaxSessionPolicy(SessionInfo sessionInfo)
			throws NumberFormatException {
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