package eu.europa.ec.mare.usm.administration.rest.service.policy;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.mare.usm.administration.domain.FindPoliciesQuery;
import eu.europa.ec.mare.usm.administration.domain.NoBody;
import eu.europa.ec.mare.usm.administration.domain.Policy;
import eu.europa.ec.mare.usm.administration.domain.ServiceRequest;
import eu.europa.ec.mare.usm.administration.rest.ServiceArrayResponse;
import eu.europa.ec.mare.usm.administration.rest.common.ExceptionHandler;
import eu.europa.ec.mare.usm.administration.service.policy.PolicyService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;


/**
 * REST Web Service implementation of the Policy service
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Path("policies")
public class PolicyResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(PolicyResource.class);
	
	@EJB
	PolicyService service;
	
	@Context
	private HttpServletRequest servletRequest;	
	
	/**
	 * Creates a new instance.		
	 */
	public PolicyResource() {
	}
	
	/**
	 * Updates an existing policy property's value
	 *
	 * @param jwtToken the JWT token identifying the service requester, optional
	 * if the service requester is authenticated by the J2EE container
	 * @param policy the policy property which value will be updated
	 *
	 * @return Response with status OK (200) in case of success otherwise status
	 * NOT_MODIFIED or a BAD_REQUEST error code in case the provided input
	 * incomplete, with an INTERNAL_SERVER_ERROR error code in case an internal
	 * error prevented fulfilling the request or UnauthorisedException with an
	 * FORBIDDEN error code in case the end user is not authorised to perform the
	 * operation
	 */
	@PUT
	@Consumes("application/json")
	@Produces("application/json")
	public Response updatePolicy(@HeaderParam("authorization") String jwtToken, 
                                 Policy policy) 
  {
		LOGGER.info("updatePolicy(" + policy + ") - (ENTER)");
		
    ServiceRequest<Policy> req = new ServiceRequest<>();
    req.setRequester(servletRequest.getRemoteUser());
    req.setBody(policy);
		
		Response ret;
		try {
			Policy e = service.updatePolicy(req);

      ret = Response.status(Response.Status.OK).entity(e).build();
		} catch (Exception e) {
		  ret = ExceptionHandler.handleException(e);
		}
		
    LOGGER.info("updatePolicy() - (LEAVE)");
    return ret;
	}
	
	/**
	 * Retrieves a list of policy properties
	 *
	 * @param jwtToken the JWT token identifying the service requester, optional
	 * if the service requester is authenticated by the J2EE container
	 * @param name the requested policy property name
	 * @param subject the requested policy property subject
	 *
	 * @return an OK status and the list of policy properties that meet the criteria, or a
	 * BAD_REQUEST error code in case the provided input incomplete, with an
	 * INTERNAL_SERVER_ERROR error code in case an internal error prevented
	 * fulfilling the request or UnauthorisedException with an FORBIDDEN error
	 * code in case the end user is not authorised to perform the operation
	 */
  @GET
  @Produces("application/json")
  public Response findPolicies(@HeaderParam("authorization") String jwtToken,
                                 @QueryParam("name") String name,
                                 @QueryParam("subject") String subject) 
  {
    LOGGER.info("findPolicies() - (ENTER)");

    ServiceRequest<FindPoliciesQuery> req = new ServiceRequest<>();
    req.setRequester(servletRequest.getRemoteUser());
    FindPoliciesQuery query = new FindPoliciesQuery();
    query.setName(name);
    query.setSubject(subject);
    req.setBody(query);

    Response ret;
    try {
      List<Policy> resp = service.findPolicies(req);

      Status statusCode;
      if (resp == null || resp.isEmpty()) {
        statusCode = Response.Status.NO_CONTENT;
      } else {
        statusCode = Response.Status.OK;
      }
        ret = Response.status(statusCode).entity(resp).build();
    } catch (Exception exc) {
      ret = ExceptionHandler.handleException(exc);
    }

    LOGGER.info("findPolicies() - (LEAVE)");
    return ret;
	  }
	
	/**
	 * Retrieves the list of all subjects
	 * 
	 * @param jwtToken the JWT token identifying the service requester, optional
	 * if the service requester is authenticated by the J2EE container
	 * 
	 * @return the list of the all subjects with an OK status, or a BAD_REQUEST error
	 * code in case the provided input incomplete, with an INTERNAL_SERVER_ERROR error code
	 * in case an internal error prevented fulfilling the request or UnauthorisedException with
	 * an FORBIDEN error code in case the end user is not authorised to perform the operation
	 */
	@GET
	@Path("subjects")
	@Produces("application/json")
	public Response getSubjects(@HeaderParam("authorization") String jwtToken) 
  {
		LOGGER.info("getSubjects() - (ENTER)");
		
		ServiceRequest<NoBody> req = new ServiceRequest<>();
		req.setRequester(servletRequest.getRemoteUser());
		
		Response ret;
		try {
			List<String> lst = service.getSubjects(req);
		    Status status;
		    if (lst == null || lst.isEmpty()) {
		    	status = Response.Status.NO_CONTENT;
		    } else {
		    	status = Response.Status.OK;
		    }
		    
      ServiceArrayResponse<String> sar = new ServiceArrayResponse<>();
      sar.setResults(lst);
			ret = Response.status(status).entity(sar).build();
		} catch (Exception e) {
			ret = ExceptionHandler.handleException(e);
		}

		LOGGER.info("getSubjects() - (LEAVE)");
		return ret;
	}
}
