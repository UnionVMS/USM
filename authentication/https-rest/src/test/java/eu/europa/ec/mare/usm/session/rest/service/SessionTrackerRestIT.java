package eu.europa.ec.mare.usm.session.rest.service;

import com.sun.jersey.api.client.ClientResponse;
import eu.europa.ec.mare.usm.BuildAuthenticationHttpsRestDeployment;
import eu.europa.ec.mare.usm.authentication.domain.StatusResponse;
import eu.europa.ec.mare.usm.session.domain.SessionIdWrapper;
import eu.europa.ec.mare.usm.session.domain.SessionInfo;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.InetAddress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class SessionTrackerRestIT extends BuildAuthenticationHttpsRestDeployment {
    private String userSite;

    public SessionTrackerRestIT() throws IOException {
        userSite = InetAddress.getLocalHost().getHostAddress();
    }

    @Test
    @OperateOnDeployment("normal")
    public void testStartSessionOneSite() {
        // Execute
        SessionInfo sessionId = createSessionInfo("testStartSession");

        String result = startSession(sessionId);

        // Verify
        assertNotNull("Unexpected null result", result);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetSession() {
        // Set-up
        SessionInfo sessionInfo = createSessionInfo("testGetSession");
        String uniqueId = startSession(sessionInfo);

        // Execute
        SessionInfo result = getSession(uniqueId);

        // Verify
        assertNotNull("Unexpected null result", result);
        assertEquals("Unexpected userName value", sessionInfo.getUserName(), result.getUserName());
        assertEquals("Unexpected userSite value", sessionInfo.getUserSite(), result.getUserSite());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testEndSession() {
        // Set-up
        SessionInfo sessionInfo = createSessionInfo("testEndSession");
        String uniqueId = startSession(sessionInfo);

        // Execute
        Response response = endSession(uniqueId);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    SessionInfo createSessionInfo(String userName) {
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setUserName(userName);
        sessionInfo.setUserSite(userSite);
        return sessionInfo;
    }

    private SessionInfo getSession(String userNameToken) {
        return getWebTargetInternal()
                .path("sessions")
                .path(userNameToken)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .get(SessionInfo.class);
    }

    private String startSession(SessionInfo request) {
        SessionIdWrapper response = getWebTargetInternal()
                .path("sessions")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(request), SessionIdWrapper.class);
        return response.getSessionId();
    }

    private Response endSession(String userNameToken) {
        return getWebTargetInternal()
                .path("sessions")
                .path(userNameToken)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .delete();
    }

    private void handleError(ClientResponse cr) throws RuntimeException {
        if (ClientResponse.Status.BAD_REQUEST == cr.getStatusInfo()) {
            StatusResponse sr = cr.getEntity(StatusResponse.class);
            throw new IllegalArgumentException(sr.getMessage());
        } else if (ClientResponse.Status.CONFLICT == cr.getStatusInfo()) {
            StatusResponse sr = cr.getEntity(StatusResponse.class);
            throw new IllegalStateException(sr.getMessage());
        } else if (ClientResponse.Status.INTERNAL_SERVER_ERROR == cr.getStatusInfo()) {
            StatusResponse sr = cr.getEntity(StatusResponse.class);
            throw new RuntimeException(sr.getMessage());
        }
    }

}
