package eu.europa.ec.mare.usm.administration.service.user;

import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.authentication.service.impl.CreateLdapUser;
import eu.europa.ec.mare.usm.authentication.service.impl.CreateLdapUserEvent;

import javax.enterprise.event.Observes;

/**
 * Provides operations for the management of users.
 */
public interface ManageUserService {

    /**
     * Creates a new user.
     *
     * @param request contains the details for the new user
     * @return <i>true</i> if the user was successfully creates, <i>false</i>
     * otherwise
     * @throws IllegalArgumentException in case the service request is null or
     *                                  incomplete or if the provided userName is already assigned
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing
     *                                  the request
     */
    UserAccount createUser(ServiceRequest<UserAccount> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    void createUserFromLdap(@Observes @CreateLdapUser CreateLdapUserEvent event);

    /**
     * Updates an existing user
     *
     * @param request holds the details of the user to be updated
     * @return the update user, what else?
     * @throws IllegalArgumentException in case the service request is null or
     *                                  incomplete
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing
     *                                  the request
     */
    UserAccount updateUser(ServiceRequest<UserAccount> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Changes the password of an existing user.<br/>
     * May be used either by an administrator to change the password of
     * other users or by a regular user to change his/her own password.
     * When used to change the password of another user the service requester
     * must have a right to manager-users, while when used to change a user own
     * password the user status may not be disabled and the current user password
     * must be provided in the request.
     *
     * @param request a request to change the password of a user
     * @throws IllegalArgumentException in case the service request is null,
     *                                  empty or otherwise incomplete; or if the target user does not exist;
     *                                  or if the password violates one or more of the applicable policies or
     *                                  in case the service requester has provided incorrectpassword
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature or in case the user status is
     *                                  disabled
     * @throws RuntimeException         in case an internal problem prevents processing
     *                                  the request
     */
    void changePassword(ServiceRequest<ChangePassword> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Get the challenge information of an existing user.<br/>
     * May be used by a regular user to change his/her own challenge/answer.
     *
     * @param request a request to get the challenge information of a user
     * @return the challenge information
     * @throws IllegalArgumentException in case the service request is null,
     *                                  empty or otherwise incomplete;
     *                                  in case the service requester has provided incorrect password
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing
     *                                  the request
     */
    ChallengeInformationResponse getChallengeInformation(ServiceRequest<String> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Set the challenge information of an existing user.<br/>
     * May be used by a regular user to change his/her own challenge/answer.
     *
     * @param request  a request to set the challenge information of a user
     * @param userName you are on your own here
     * @return you are on your own here as well
     * @throws IllegalArgumentException in case the service request is null,
     *                                  empty or otherwise incomplete;
     *                                  in case the service requester has provided incorrect password
     * @throws UnauthorisedException    in case the service requester is not
     *                                  authorised to use the specific feature
     * @throws RuntimeException         in case an internal problem prevents processing
     *                                  the request
     */
    ChallengeInformationResponse setChallengeInformation(ServiceRequest<ChallengeInformationResponse> request, String userName)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Resets the password of an existing user<br/>
     * This service will be called in case the "force user to fill the security
     * questions" policy is adapted.
     * Prerequisites: user status may not be disabled, the new user password
     * must be compatible with the password policy and the provided
     * answers are valid
     *
     * @param request a request to reset the password of a user
     * @throws IllegalArgumentException in case the service request is null,
     *                                  empty or otherwise incomplete; or if the target user does not exist;
     *                                  or if the password violates one or more of the applicable policies,
     *                                  or if the answers provided by the user are not valid
     * @throws UnauthorisedException    in case the user status is
     *                                  disabled
     * @throws RuntimeException         in case an internal problem prevents processing
     *                                  the request
     */
    void resetPassword(ServiceRequest<ResetPasswordQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Resets the password of an existing user with a new random expired password
     * and send it to the user's email<br/>
     * <p>
     * Prerequisites: user status may not be disabled
     *
     * @param request a request to reset the password of a user
     * @throws IllegalArgumentException in case the service request is null,
     *                                  empty or otherwise incomplete; or if the target user does not exist;
     * @throws UnauthorisedException    in case the user status is
     *                                  disabled
     * @throws RuntimeException         in case an internal problem prevents processing
     *                                  the request
     */
    void resetPasswordAndNotify(ServiceRequest<NotificationQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    String getPasswordPolicy(ServiceRequest<String> request);
}
