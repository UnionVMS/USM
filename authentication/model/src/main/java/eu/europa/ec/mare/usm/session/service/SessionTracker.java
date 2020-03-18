package eu.europa.ec.mare.usm.session.service;

import eu.europa.ec.mare.usm.session.domain.SessionInfo;

/**
 * Provides operations for monitoring user sessions; enforcing
 * the applicable policies (maximum number of sessions per user).
 */
public interface SessionTracker {

    /**
     * Informs the tracker that a (new) user session is starting.
     *
     * @param sessionInfo the identification of the starting user session
     * @return the unique identifier of the starting user session
     * @throws IllegalStateException    in case the creation of new session
     *                                  violates the applicable policies
     * @throws IllegalArgumentException in case the provided input is null,
     *                                  empty or incomplete
     * @throws RuntimeException         in case an internal error prevented processing
     *                                  the request
     */
    public String startSession(SessionInfo sessionInfo)
            throws IllegalStateException, IllegalArgumentException, RuntimeException;

    /**
     * Retrieves (identification) information about the user session
     * with the provided identifier.
     *
     * @param sessionId the unique identifier of the user session
     * @return session identification information if the session (still) exists,
     * null otherwise
     * @throws IllegalArgumentException in case the provided input is null,
     *                                  empty or incomplete
     * @throws RuntimeException         in case an internal error prevented processing
     *                                  the request
     */
    public SessionInfo getSession(String sessionId) throws IllegalArgumentException, RuntimeException;

    /**
     * Informs the tracker that a user session is terminating.
     *
     * @param sessionId the unique identifier of the terminating user session
     * @throws IllegalArgumentException in case the provided input is null,
     *                                  empty or incomplete
     * @throws RuntimeException         in case an internal error prevented processing
     *                                  the request
     */
    public void endSession(String sessionId) throws IllegalArgumentException, RuntimeException;
}
