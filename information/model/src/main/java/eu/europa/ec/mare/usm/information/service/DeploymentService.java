package eu.europa.ec.mare.usm.information.service;

import eu.europa.ec.mare.usm.information.domain.deployment.Application;


/**
 * Provides operations for the deployment of Applications.
 */
public interface DeploymentService {
  /**
   * Deploys the provided Application as a new Application.
   * 
   * @param request the service request holding the Application 
   * 
   * @throws IllegalArgumentException in case the service request is null,
   * empty or otherwise incomplete or if the application to be deployed already
   * exist
   * @throws UnauthorisedException in case the service requester is not 
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing 
   * the request
   */
  public void deployApplication(Application request)
  throws IllegalArgumentException,  RuntimeException;

  /**
   * Redeploys the provided Application.
   * 
   * @param request the service request holding the Application 
   * 
   * @throws IllegalArgumentException in case the service request is null,
   * empty or otherwise incomplete
   * @throws UnauthorisedException in case the service requester is not 
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing 
   * the request
   */
  public void redeployApplication(Application request)
  throws IllegalArgumentException,  RuntimeException;

  /**
   * Un-deploys the provided Application.
   * 
   * @param request the service request holding the name of the Application to 
   * be un-deployed.
   * 
   * @throws IllegalArgumentException in case the service request is null,
   * empty or otherwise incomplete or if the application does not exist
   * @throws UnauthorisedException in case the service requester is not 
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing 
   * the request
   */
  public void undeployApplication(String request)
  throws IllegalArgumentException,  RuntimeException;

  
  /**
   * Retrieves the deployment descriptor for the application with the 
   * provided name.
   * 
   * @param request the service request holding the name of the Application 
   * 
   * @return the deployment descriptor if the application exists, <i>null</i>
   * otherwise
   * 
   * @throws IllegalArgumentException in case the service request is null or
   * empty
   * @throws UnauthorisedException in case the service requester is not 
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing 
   * the request
   */
  public Application getDeploymentDescriptor(String request)
  throws IllegalArgumentException,  RuntimeException;

  /**
   * Deploys the provided Datasets to an existing Application.
   * 
   * @param request the service request holding the Datasets to be added to
   * the Application deployment
   * 
   * @throws IllegalArgumentException in case the service request is null,
   * empty or otherwise incomplete or if the application does not exist
   * @throws UnauthorisedException in case the service requester is not 
   * authorised to use the specific feature
   * @throws RuntimeException in case an internal problem prevents processing 
   * the request
   */
  public void deployDatasets(Application request)
  throws IllegalArgumentException, RuntimeException;
}
