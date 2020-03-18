package eu.europa.ec.mare.usm.administration.service.userContext;

import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.service.DeploymentFactory;
import eu.europa.ec.mare.usm.administration.service.user.ManageUserService;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class UserContextServiceTest extends DeploymentFactory {

    private static final String USM_ADMIN = "usm_admin";

    @EJB
    private UserContextService testSubject;

    @Test
    public void testGetUserContextsProvidingName() {
        // Setup
        FindUserContextsQuery query = new FindUserContextsQuery();
        query.setUserName("quota_usr_grc");
        ServiceRequest<FindUserContextsQuery> sRequest = new ServiceRequest<>();
        sRequest.setRequester("vms_admin_com");
        sRequest.setBody(query);
        String expected = "MS Quota User";

        // Execute
        UserContextResponse response = testSubject.getUserContexts(sRequest);

        // Verify
        assertNotNull("Unexpected null result", response);
        assertEquals("Unexpected 'roleName' value", expected, getRoleName(response.getResults(), expected));
    }

    private String getRoleName(List<ComprehensiveUserContext> cUserContexts, String expected) {
        for (ComprehensiveUserContext userContext : cUserContexts) {
            if (userContext.getRole().equals(expected)) {
                return expected;
            }
        }
        return null;
    }

    private ComprehensiveUserContext getUserContext(List<ComprehensiveUserContext> cUserContexts, String expected) {
        if (cUserContexts != null && !cUserContexts.isEmpty()) {
            for (ComprehensiveUserContext userContext : cUserContexts) {
                if (userContext != null && userContext.getRole() != null && userContext.getRole().equals(expected)) {
                    return userContext;
                }
            }
        }
        return null;
    }

    @Test
    public void testCUDUserContextOperations() {
        // Set-up
        ServiceRequest<UserContext> userContextRequest = createRequest();

        FindUserContextsQuery query = new FindUserContextsQuery();
        query.setUserName(userContextRequest.getBody().getUserName());
        ServiceRequest<FindUserContextsQuery> sRequest = new ServiceRequest<>();
        sRequest.setRequester(userContextRequest.getRequester());
        sRequest.setBody(query);

        // Execute
        UserContextResponse response = testSubject.getUserContexts(sRequest);
        String expected = "Administrator";
        Long userContextId = null;

        if (response == null || getUserContext(response.getResults(), expected) == null) {
            UserContext result = testSubject.createUserContext(userContextRequest);
            assertNotNull(result);
            userContextId = result.getUserContextId();
        } else {
            userContextId = getUserContext(response.getResults(), expected).getUserContextId();
        }
        // Execute
        UserContext userContext = userContextRequest.getBody();
        userContext.setRoleId(2L);
        userContext.setScopeId(2L);
        userContext.setUserContextId(userContextId);
        userContextRequest.setBody(userContext);

        userContext = testSubject.updateUserContext(userContextRequest);

        // verify
        assertNotNull(userContext);
		assertEquals("Scope was not updated", 2L, (long) userContext.getScopeId());

        //delete the user context as well
        ServiceRequest<String> deleteRequest = new ServiceRequest<>();
        deleteRequest.setBody(userContextId.toString());
        deleteRequest.setRequester(USM_ADMIN);
        testSubject.deleteUserContext(deleteRequest);

        //test the deletion result
        response = testSubject.getUserContexts(sRequest);

        // verify
        assertNull("unexpected record was found", getUserContextId(response.getResults(), userContextId));
    }

    private ServiceRequest<UserContext> createRequest() {
        ServiceRequest<UserContext> request = new ServiceRequest<>();
        UserContext userContext = new UserContext();
        userContext.setUserName("guest");

        userContext.setUserContextId(null);
        userContext.setRoleId(2L);
        userContext.setScopeId(1L);

        request.setBody(userContext);
        request.setRequester(USM_ADMIN);
        return request;
    }

    private Long getUserContextId(List<ComprehensiveUserContext> cUserContexts, Long expected) {
        if (cUserContexts != null) {
            for (ComprehensiveUserContext userContext : cUserContexts) {
                if (userContext != null && userContext.getUserContextId().equals(expected)) {
                    return userContext.getUserContextId();
                }
            }
        }
        return null;
    }

    /**
     * It is testing the functionality by copying the profile of usm_admin to guest
     * and then restoring the original state
     */
    @Test
    public void testCopyUserProfiles() {
        //set-up
        FindUserContextsQuery query = new FindUserContextsQuery();
        query.setUserName("vms_admin_com");
        ServiceRequest<FindUserContextsQuery> sRequest = new ServiceRequest<>();
        sRequest.setRequester("vms_admin_com");
        sRequest.setBody(query);
        UserContextResponse responseUsmUser = testSubject.getUserContexts(sRequest);

        query.setUserName("vms_user_com");
        UserContextResponse responseGuestInitial = testSubject.getUserContexts(sRequest);

        ServiceRequest<UserContextResponse> requestCpy = new ServiceRequest<>();
        requestCpy.setRequester(USM_ADMIN);
        requestCpy.setBody(responseUsmUser);

        testSubject.copyUserProfiles(requestCpy, "vms_user_com");
        UserContextResponse responseGuestFinal = testSubject.getUserContexts(sRequest);

        assertTrue("Was the profile copied?",
				checkUserContexts(responseUsmUser.getResults(), responseGuestFinal.getResults()));

        ServiceRequest<String> contextRequest = new ServiceRequest<>();
        contextRequest.setRequester(USM_ADMIN);
        //tear down

        //delete copied contexts
        for (ComprehensiveUserContext element : responseGuestFinal.getResults()) {
            contextRequest.setBody(element.getUserContextId().toString());
            testSubject.deleteUserContext(contextRequest);
        }

        responseGuestFinal = testSubject.getUserContexts(sRequest);
        assertTrue("Some contexts were not deleted", responseGuestFinal.getResults().isEmpty());

        //add original contexts
        ServiceRequest<UserContext> addedRequest = new ServiceRequest<>();
        addedRequest.setRequester(USM_ADMIN);
        UserContext uc = new UserContext();
        uc.setUserName("vms_user_com");
        addedRequest.setBody(uc);
        for (ComprehensiveUserContext element : responseGuestInitial.getResults()) {
            uc.setRoleId(element.getRoleId());
            uc.setScopeId(element.getScopeId());
            testSubject.createUserContext(addedRequest);
        }

        responseGuestFinal = testSubject.getUserContexts(sRequest);
        assertTrue("Some contexts could not be added", responseGuestFinal.getResults().size() > 0);
    }

    private boolean checkUserContexts(List<ComprehensiveUserContext> fromList, List<ComprehensiveUserContext> toList) {
        boolean contentStatus = true;
        int i = 0;
        for (ComprehensiveUserContext element : fromList) {
            if (!toList.contains(element)) {
                contentStatus = false;
            } else {
                i++;
            }
        }

        //check not to append the contexts to the existing lists but to have
        contentStatus = contentStatus && (i == toList.size());

        return contentStatus;
    }
}
