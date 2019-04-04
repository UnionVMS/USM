package eu.europa.ec.mare.usm.administration.service.role;

import java.util.List;

import eu.europa.ec.mare.usm.administration.domain.ComprehensiveRole;
import eu.europa.ec.mare.usm.administration.domain.Feature;
import eu.europa.ec.mare.usm.administration.domain.FindPermissionsQuery;
import eu.europa.ec.mare.usm.administration.domain.FindRolesQuery;
import eu.europa.ec.mare.usm.administration.domain.GetRoleQuery;
import eu.europa.ec.mare.usm.administration.domain.PaginationResponse;
import eu.europa.ec.mare.usm.administration.domain.Role;
import eu.europa.ec.mare.usm.administration.domain.RoleQuery;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;

/**
 * Provides operations for roles.
 */
public interface RoleService {

  /**
   * Retrieves the name of the roles matching the provided query.
   *
   * @param request the service request holding the role query
   *
   * @return the possibly-empty list of role names
   *
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public List<String> getRoleNames(ServiceRequest<RoleQuery> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Retrieves a specific role.
   *
   * @param request the service request holding the get role query
   *
   * @return a specific role
   *
   * @throws IllegalArgumentException in case the provided input is null, empty
   * or otherwise incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to view user information
   * @throws RuntimeException in case an internal error prevented fulfilling the
   * request
   */
  public Role getRole(ServiceRequest<GetRoleQuery> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Retrieves a list of roles according to the request's criteria
   *
   * @param request the service request holding the find roles query
   *
   * @return PaginationResponse response which includes a list of ComprehensiveRoles and the total value
   *
   * @throws IllegalArgumentException in case the provided input is null, empty
   * or otherwise incomplete
   * @throws UnauthorisedException in case the service requestor is not
   * authorised to view user information
   * @throws RuntimeException in case an internal error prevented fulfilling the
   * request
   */
  public PaginationResponse<ComprehensiveRole> findRoles(ServiceRequest<FindRolesQuery> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Creates a new role.
   *
   * @param request contains the details for the new role
   *
   * @return the role was successfully created, <i>null</i>
   * otherwise
   *
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete or if the provided application, name, status are requested and
   * the pair (name, application) must be unique
   *
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   *
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public ComprehensiveRole createRole(ServiceRequest<ComprehensiveRole> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Updates an existing role
   *
   * @param request holds the details of the role to be updated
   *
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete or if the provided application, name, status are requested and
   * the pair (name, application) must be unique
   *
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   *
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public void updateRole(ServiceRequest<ComprehensiveRole> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Deletes a role based on his identifier
   *
   * @param request the identifier of the role which must be deleted
   *
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   *
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   *
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public void deleteRole(ServiceRequest<Long> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;
  
  /**
   * Retrieves the list of available features for a given application 
   * name
   * 
   * @param request the name of the application for which features are searched 
   * 
   * @return the list of features associated directly to an application or 
   * for it's children, null if none is associated
   * 
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public List<Feature> findFeaturesByApplication(ServiceRequest<String> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Gets the group names matching the provided request.
   *
   * @param request the request
   * 
   * @return the group names
   */
  public List<String> getGroupNames(ServiceRequest<String> request);

  /**
   * Finds permissions matching the provided request.
   *
   * @param request the request
   * 
   * @return the list of features
   * 
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public List<Feature> findPermissions(ServiceRequest<FindPermissionsQuery> request) 
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;
  
  /**
   * Retrieves a list of comprehensive roles
   *
   * @return a list of comprehensive roles
   * 
   * @param request the service request holding the Role query
   *
   * @throws IllegalArgumentException in case the provided input is null, empty
   * or otherwise incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to view user information
   * @throws RuntimeException in case an internal error prevented fulfilling the
   * request
   */
  public List<ComprehensiveRole> getRoles(ServiceRequest<RoleQuery> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;
}
