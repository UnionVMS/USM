package eu.europa.ec.mare.usm.session.service.impl;

import java.util.Date;
import java.util.List;

/**
 * Data-access object for the UserSessions
 */
public interface SessionDao {

    /**
     * Creates (ore stores) the given user session.
     *
     * @param session the user session to be created
     * @return the session unique identifier
     * @throws RuntimeException in case an internal error prevented processing
     *                          the request
     */
    public String createSession(UserSession session) throws RuntimeException;

    /**
     * Reads the user session with the  provided unique identifier.
     *
     * @param uniqueId the session unique identifier
     * @return the user session if it (still) exists, null otherwise
     * @throws RuntimeException in case an internal error prevented processing
     *                          the request
     */
    public UserSession readSession(String uniqueId) throws RuntimeException;

    /**
     * Retrieves all existing sessions for the user with the provided
     * name, started after the provided date.
     *
     * @param userName     the user name
     * @param startedAfter the minimum session start date.
     * @return the possibly-empty list of user sessions matching the provided
     * criteria
     * @throws RuntimeException in case an internal error prevented processing
     *                          the request
     */
    public List<UserSession> readSessions(String userName, Date startedAfter) throws RuntimeException;

    /**
     * Deletes the user session with the provided unique identifier.
     *
     * @param uniqueId the session unique identifier
     * @throws RuntimeException in case an internal error prevented processing
     *                          the request
     */
    public void deleteSession(String uniqueId) throws RuntimeException;

    /**
     * Deletes all user sessions.
     *
     * @throws RuntimeException in case an internal error prevented processing
     *                          the request
     */
    public void deleteSessions() throws RuntimeException;
}
