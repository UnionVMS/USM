package eu.europa.ec.mare.usm.administration.service.user;

import eu.europa.ec.mare.usm.administration.domain.*;

import java.util.List;

/**
 * Provides operations for view users.
 */
public interface ViewUsersService {
    /**
     * Retrieves a specific user.
     *
     * @param request a GetUserQuery request.
     * @return a specific user
     * @throws IllegalArgumentException in case the provided input is null, empty
     *                                  or otherwise incomplete
     * @throws UnauthorisedException    in case the service requestor is not
     *                                  authorised to view user information
     * @throws RuntimeException         in case an internal error prevented fulfilling
     *                                  the request
     */
    public UserAccount getUser(ServiceRequest<GetUserQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Retrieves a list of users according to the request
     *
     * @param request a FindUsersQuery request.
     * @return PaginationResponse that includes a comprehensive user list and
     * the total number of results
     * @throws IllegalArgumentException in case the provided input is null, empty
     *                                  or otherwise incomplete
     * @throws UnauthorisedException    in case the service requestor is not
     *                                  authorised to view user information
     * @throws RuntimeException         in case an internal error prevented fulfilling
     *                                  the request
     */
    public PaginationResponse<UserAccount> findUsers(ServiceRequest<FindUsersQuery> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;

    /**
     * Gets the list of all userNames, regardless of the status
     * (active/inactive, enabled/disabled/locked-out) of the users.
     *
     * @param request holds the identity of the service requester
     * @return the possibly empty list of all user names
     * Retrieves the list of existing users
     * @throws IllegalArgumentException in case the provided input is null, empty
     *                                  or otherwise incomplete
     * @throws UnauthorisedException    in case the service requestor is not
     *                                  authorised to view user information
     * @throws RuntimeException         in case an internal error prevented fulfilling
     *                                  the request
     */
    public List<String> getUsersNames(ServiceRequest<String> request)
            throws IllegalArgumentException, UnauthorisedException, RuntimeException;
}
