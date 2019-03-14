package eu.europa.ec.mare.usm.administration.service.policy;

import eu.europa.ec.mare.usm.administration.domain.PolicyDefinition;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.domain.UnauthorisedException;

/**
 * Provides operations for the retrieval and storage of policy 
 * configuration/definition properties. 
 */
public interface DefinitionService {
  /**
   * Retrieves the definition of the policy with the given subject.
   * 
   * @param subject the policy subject 
   * 
   * @return the policy definition if it exists, <i>null</i> otherwise
   * 
   * @throws IllegalArgumentException in case the provided subject is null or
   * empty
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public PolicyDefinition getDefinition(String subject)
  throws IllegalArgumentException, RuntimeException;
  
  /**
   * 
   * Retrieves the definition of a policy property with the given subject and name.
   * 
   * @param subject the policy subject
   * @param property the policy property
   * @return the policy property value if tie exists, <i>null</i> otherwise
   * @throws IllegalArgumentException in case the provided subject is null or
   * empty
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
 */
  public String getPolicyProperty(String subject, String property)
  throws IllegalArgumentException, RuntimeException;
  
  /**
   * Sets the configuration/definition properties for a specific policy.
   * 
   * @param request the policy definition
   * 
   * @throws IllegalArgumentException in case the provided request is null,  
   * empty or otherwise invalid
   * @throws UnauthorisedException in case the service requester is not
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing
   * the request
   */
  public void setDefinition(ServiceRequest<PolicyDefinition> request)
  throws IllegalArgumentException, UnauthorisedException, RuntimeException;
  
  /**
   * Evicts expired cached version
   * 
   *  @param subject 
   */
  public void evictDefinition(String subject);
}
