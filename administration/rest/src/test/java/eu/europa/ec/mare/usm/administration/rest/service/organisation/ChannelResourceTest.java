package eu.europa.ec.mare.usm.administration.rest.service.organisation;

import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.rest.service.AdministrationRestClient;
import eu.europa.ec.mare.usm.administration.rest.service.BuildAdministrationDeployment;
import eu.europa.ec.mare.usm.administration.rest.service.TestHelper;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class ChannelResourceTest extends BuildAdministrationDeployment {

    private static final String ORGANISATION_GRC = "GRC";
    private static final String ENDPOINT_NAME_GRC_BACK = "FLUX.GRC_backup";
    private static final String USM_ADMIN = "usm_admin";
    private static final String PASSWORD = "password";

    @EJB
    private AdministrationRestClient restClient;

    @EJB
    private TestHelper testHelper;

    @Test
    @OperateOnDeployment("normal")
    public void testCreateChannel() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        Channel channel = createChannelRequest(auth.getJwtoken());
        Response response = restClient.createChannel(auth.getJwtoken(), channel);
        assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());
        channel = response.readEntity(Channel.class);
        assertNotNull("Unexpected null channel", channel);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetChannel() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        List<EndPoint> endPointIds = testHelper.findOrganisationEndPoints(auth.getJwtoken(), ORGANISATION_GRC);
        Long endpointId = endPointIds.get(1).getEndpointId();

        Response response = restClient.getEndPoint(auth.getJwtoken(), String.valueOf(endpointId));
        EndPoint endPoint = response.readEntity(EndPoint.class);

        assertNotNull("Unexpected null result", response);
        assertEquals("Unexpected 'scopeName' value", ENDPOINT_NAME_GRC_BACK, endPoint.getName());

        Long channelId = endPoint.getChannelList().get(0).getChannelId();
        Response channelResponse = restClient.getChannel(auth.getJwtoken(), String.valueOf(channelId));
        Channel channel = channelResponse.readEntity(Channel.class);

        assertNotNull("Unexpected null result", channelResponse);
        assertEquals("Unexpected 'channelId' value", channel.getChannelId(), channelId);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpdateChannel() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        Channel channel = createChannelRequest(auth.getJwtoken());
        Response response = restClient.createChannel(auth.getJwtoken(), channel);
        assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

        channel = response.readEntity(Channel.class);
        channel.setPriority(2);

        response = restClient.updateChannel(auth.getJwtoken(), channel);
        assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

        Channel updated = response.readEntity(Channel.class);

        assertNotNull("Unexpected returned endpoint is null", updated);
        assertEquals("Unexpected priority", 2, (int) updated.getPriority());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testDeleteChannel() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        Channel channel = createChannelRequest(auth.getJwtoken());

        Response response = restClient.createChannel(auth.getJwtoken(), channel);
        assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());

        channel = response.readEntity(Channel.class);

        response = restClient.deleteChannel(auth.getJwtoken(), String.valueOf(channel.getChannelId()));
        assertEquals("Unexpected Response.Status", Response.Status.OK.getStatusCode(), response.getStatus());
    }

    private Channel createChannelRequest(String jwtToken) {
        EndPoint endPoint = testHelper.findOrganisationEndPoint(ORGANISATION_GRC, ENDPOINT_NAME_GRC_BACK, jwtToken);
        Channel channel = new Channel();
        channel.setDataflow("testDataflow" + System.currentTimeMillis());
        channel.setService("service");
        channel.setPriority(1);
        channel.setEndpointId(endPoint.getEndpointId());
        return channel;
    }
}
