package eu.europa.ec.mare.usm.administration.service.organisation;

import eu.europa.ec.mare.usm.administration.domain.*;

import java.util.List;

/**
 * Provides operations for the administration of Organisations.
 */
public interface OrganisationService {
  /**
   * Retrieves the name of the organisations matching the provided query.
   *
   * @param request the service request holding the Organisation query
   * @return the possibly-empty list of organisation names
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public List<OrganisationNameResponse> getOrganisationNames(ServiceRequest<OrganisationQuery> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Gets the name of all possible parent organisations for a given
   * organisation.
   *
   * @param request the service request holding the id of the candidate child 
   * organisation, or -1 case of the new (transient) organisation
   *
   * @return the possibly empty list of organisation names
   * 
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public List<String> getOrganisationParentNames(ServiceRequest<Long> request)
  throws RuntimeException;

  /**
   * Retrieves the (distinct) names of nations for which organisations 
   * exist.
   *
   * @param request the service request holding the Organisation query
   * @return the possibly-empty list of nation names
   * 
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public List<String> getNationNames(ServiceRequest<OrganisationQuery> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Creates (or persists) the provided Organisation.
   *
   * @param request the service request holding the Organisation to be created
   * @return the Organisation if it was successfully created, <i>null</i>
   * otherwise
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public Organisation createOrganisation(ServiceRequest<Organisation> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Reads the Organisation with the provided (internal) unique identifier
   *
   * @param request the service request holding the Organisation (internal)
   * unique identifier
   * @return the matching Organisation if it exists, null otherwise
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public Organisation getOrganisation(ServiceRequest<Long> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Reads the Organisation with the provided name
   *
   * @param request the service request holding the Organisation name
   * @return the matching Organisation if it exists, null otherwise
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public Organisation getOrganisationByName(ServiceRequest<String> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Updates the provided Organisation
   *
   * @param request the service request holding the Organisation to be updated
   * @return the updated Organisation
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public Organisation updateOrganisation(ServiceRequest<Organisation> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Deletes the Organisation with the provided (internal) unique identifier
   *
   * @param request the service request holding the Organisation (internal)
   * unique identifier
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public void deleteOrganisation(ServiceRequest<Long> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Retrieves a list of organisations according to the request's criteria
   *
   * @param request the service request holding the find organisations query
   *
   * @return PaginationResponse response which includes a list of Organisation
   * and the total value
   *
   * @throws IllegalArgumentException in case the provided input is null, empty
   * or otherwise incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to view scope information
   * @throws RuntimeException in case an internal error prevented fulfilling the
   * request
   */
  public PaginationResponse<Organisation> findOrganisations(ServiceRequest<FindOrganisationsQuery> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Reads the Endpoint with the provided (internal) unique identifier
   *
   * @param request the service request holding the EndPoint (internal) unique
   * identifier
   * @return the matching Endpoint if it exists, null otherwise
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public EndPoint getEndPoint(ServiceRequest<Long> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Creates (or persists) the provided EndPoint.
   *
   * @param request the service request holding the EndPoint to be created
   * @return the EndPoint if it was successfully created, <i>null</i>
   * otherwise
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public EndPoint createEndPoint(ServiceRequest<EndPoint> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Updates the provided EndPoint
   *
   * @param request the service request holding the EndPoint to be updated
   * @return the updated EndPoint
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public EndPoint updateEndPoint(ServiceRequest<EndPoint> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Deletes the EndPoint with the provided (internal) unique identifier
   *
   * @param request the service request holding the EndPoint (internal) unique
   * identifier
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public void deleteEndPoint(ServiceRequest<Long> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Reads the Channel with the provided (internal) unique identifier
   *
   * @param request the service request holding the Channel (internal) unique
   * identifier
   * @return the matching Channel if it exists, null otherwise
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public Channel getChannel(ServiceRequest<Long> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Creates (or persists) the provided Channel.
   *
   * @param request the service request holding the Channel to be created
   * @return the Channel if it was successfully created, <i>null</i>
   * otherwise
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public Channel createChannel(ServiceRequest<Channel> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Updates the provided Channel
   *
   * @param request the service request holding the Channel to be updated
   * @return the updated Channel
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public Channel updateChannel(ServiceRequest<Channel> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Delete the Channel with the provided (internal) unique identifier
   *
   * @param request the service request holding the Channel (internal) unique
   * identifier
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public void deleteChannel(ServiceRequest<Long> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Reads the EndPointContact with the provided (internal) unique identifier
   *
   * @param request the service request holding the EndPointContact (internal)
   * unique identifier
   * @return the matching EndPointContact if it exists, null otherwise
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public EndPointContact getContact(ServiceRequest<Long> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Assigns Person to the endpoint provided the EndPointContact.
   *
   * @param request the service request holding the EndPointContact to be
   * created
   * @return the EndPointContact if it was successfully created, <i>null</i>
   * otherwise
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public EndPointContact assignContact(ServiceRequest<EndPointContact> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;

  /**
   * Remove the EndPointContact with the provided (internal) unique identifier
   *
   * @param request the service request holding the EndPointContact (internal)
   * unique identifier
   * @throws IllegalArgumentException in case the service request is null or
   * incomplete
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
  *
   */
  public void removeContact(ServiceRequest<EndPointContact> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;
  
 }
