package eu.europa.ec.mare.usm.administration.rest.service;

import com.sun.jersey.api.client.UniformInterfaceException;
import eu.europa.ec.fisheries.uvms.rest.security.InternalRestTokenHandler;
import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.rest.service.person.ContactDetailsRequest;
import eu.europa.ec.mare.usm.administration.service.JsonBConfiguratorExtended;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
import eu.europa.ec.mare.usm.authentication.domain.ChallengeResponse;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static eu.europa.ec.mare.usm.administration.rest.common.ExceptionHandler.handleException;

@Stateless
public class AdministrationRestClient {

    @Inject
    private InternalRestTokenHandler tokenHandler;

    private static final String INTERNAL_TARGET_URL = "http://localhost:8080/test/rest/";
    private static final String EXTERNAL_TARGET_URL = "http://localhost:28080/test/rest/";

    protected WebTarget getWebTargetInternal() {
        Client client = ClientBuilder.newClient();
        client.register(JsonBConfiguratorExtended.class);
        return client.target(INTERNAL_TARGET_URL);
    }

    protected String getTokenInternalRest() {
        return tokenHandler.createAndFetchToken("user");
    }

    public AuthenticationJwtResponse authenticateUser(String user, String password) {
        AuthenticationRequest request = createAuthenticationRequest(user, password);

        return getWebTargetInternal()
                .path("authenticate")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(request), AuthenticationJwtResponse.class);
    }

    private AuthenticationRequest createAuthenticationRequest(String user, String password) {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setUserName(user);
        request.setPassword(password);
        return request;
    }

    public Response findOrganisations(String jwtToken, String orgName) {
        return getWebTargetInternal()
                .path("organisations")
                .queryParam("limit", "8")
                .queryParam("offset", "0")
                .queryParam("name", orgName == null ? "" : orgName)
                .queryParam("sortColumn", "name")
                .queryParam("sortDirection", "ASC")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getOrganisationById(String jwtToken, String orgId) {
        return getWebTargetInternal()
                .path("organisations")
                .path(orgId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response createOrganisation(String jwtToken, Organisation organisation) {
        return getWebTargetInternal()
                .path("organisations")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .post(Entity.json(organisation));
    }

    public Response updateOrganisation(String jwtToken, Organisation organisation) {
        return getWebTargetInternal()
                .path("organisations")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .put(Entity.json(organisation));
    }

    public Response deleteOrganisation(String jwtToken, String organisationId) {
        return getWebTargetInternal()
                .path("organisations")
                .path(organisationId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .delete();
    }

    public Response getOrganisationNames(String jwtToken) {
        return getWebTargetInternal()
                .path("organisations")
                .path("names")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getOrganisationParentNames(String organisationId, String jwtToken) {
        return getWebTargetInternal()
                .path("organisations")
                .path(organisationId)
                .path("parent")
                .path("names")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getNationNames(String jwtToken) {
        return getWebTargetInternal()
                .path("organisations")
                .path("nations")
                .path("names")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getEndPoint(String jwtToken, String endpointId) {
        return getWebTargetInternal()
                .path("organisations")
                .path("endpoint")
                .path(endpointId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getEndPointContact(String jwtToken, String endpointContactId) {
        return getWebTargetInternal()
                .path("endpoint")
                .path("contact")
                .path(endpointContactId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response findApplications(String jwtToken, String applicationName) {
        return getWebTargetInternal()
                .path("applications")
                .queryParam("name", applicationName)
                .queryParam("limit", "8")
                .queryParam("offset", "0")
                .queryParam("sortColumn", "name")
                .queryParam("sortDirection", "desc")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getApplicationDetails(String jwtToken, String applicationName) {
        return getWebTargetInternal()
                .path("applications")
                .path(applicationName)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getParentApplicationNames(String jwtToken) {
        return getWebTargetInternal()
                .path("applications")
                .path("parent/names")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getApplicationNames(String jwtToken) {
        return getWebTargetInternal()
                .path("applications")
                .path("names")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getApplicationFeatures(String jwtToken, String applicationName) {
        return getWebTargetInternal()
                .path("applications")
                .path(applicationName)
                .path("features")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getAllFeatures(String jwtToken) {
        return getWebTargetInternal()
                .path("applications")
                .path("features")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response createEndPoint(String jwtToken, EndPoint endpoint) {
        return getWebTargetInternal()
                .path("endpoint")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .post(Entity.json(endpoint));
    }

    public Response updateEndpoint(String jwtToken, EndPoint endpoint) {
        return getWebTargetInternal()
                .path("endpoint")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .put(Entity.json(endpoint));
    }

    public Response deleteEndpoint(String jwtToken, String endpointId) {
        return getWebTargetInternal()
                .path("endpoint")
                .path(endpointId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .delete();
    }

    public ChallengeResponse getUserChallenge(String token) {
        return getWebTargetInternal()
                .path("challenge")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(ChallengeResponse.class);
    }

    public AuthenticationResponse authenticateByChallenge(ChallengeResponse request) {
        return getWebTargetInternal()
                .path("challengeauth")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                .post(Entity.json(request), AuthenticationResponse.class);
    }

    public Response assignContact(String jwtToken, EndPointContact endPointContact) {
        return getWebTargetInternal()
                .path("endpointcontact")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .post(Entity.json(endPointContact));
    }

    public Response removeContact(String jwtToken, String endPointContactId) {
        return getWebTargetInternal()
                .path("endpointcontact")
                .path(endPointContactId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .delete();
    }

    public Response getChannel(String jwtToken, String channelId) {
        return getWebTargetInternal()
                .path("channel")
                .path(channelId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response createChannel(String jwtToken, Channel channel) {
        return getWebTargetInternal()
                .path("channel")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .post(Entity.json(channel));
    }

    public Response updateChannel(String jwtToken, Channel channel) {
        return getWebTargetInternal()
                .path("channel")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .put(Entity.json(channel));
    }

    public Response deleteChannel(String jwtToken, String channelId) {
        return getWebTargetInternal()
                .path("channel")
                .path(channelId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .delete();
    }

    public Response getPersons(String jwtToken) {
        return getWebTargetInternal()
                .path("persons")
                .path("names")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getPerson(String jwtToken, String personId) {
        return getWebTargetInternal()
                .path("persons")
                .path(personId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response isUpdateContactDetailsEnabled(String jwtToken) {
        return getWebTargetInternal()
                .path("persons")
                .path("isUpdateContactDetailsEnabled")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getContactDetails(String jwtToken, String username) {
        return getWebTargetInternal()
                .path("persons")
                .path("contactDetails")
                .path(username)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response updateContactDetails(String jwtToken, ContactDetailsRequest request) {
        return getWebTargetInternal()
                .path("persons")
                .path("contactDetails")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .put(Entity.json(request));
    }

    public Response isReviewContactDetailsEnabled(String jwtToken) {
        return getWebTargetInternal()
                .path("persons")
                .path("isReviewContactDetailsEnabled")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response findPendingContactDetails(String jwtToken) {
        return getWebTargetInternal()
                .path("persons")
                .path("pendingContactDetails")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getPendingContactDetails(String jwtToken, String username) {
        return getWebTargetInternal()
                .path("persons")
                .path("pendingContactDetails")
                .path(username)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response rejectPendingContactDetails(String jwtToken, String username) {
        return getWebTargetInternal()
                .path("persons")
                .path("pendingContactDetails")
                .path(username)
                .path("reject")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response acceptPendingContactDetails(String jwtToken, String username) {
        return getWebTargetInternal()
                .path("persons")
                .path("pendingContactDetails")
                .path(username)
                .path("accept")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response findUsers(String jwtToken, String offset, String limit,
                              String sc, String sd, String user, String org, String roleName) {
        Invocation.Builder builder = getWebTargetInternal()
                .path("users")
                .queryParam("offset", offset)
                .queryParam("limit", limit)
                .queryParam("sortColumn", sc)
                .queryParam("sortDirection", sd)
                .queryParam("user", user)
                .queryParam("organisation", org)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken);

        if (roleName != null)
            builder.header("roleName", roleName);

        return builder.get();
    }

    public Response getUser(String jwtToken, String username) {
        return getWebTargetInternal()
                .path("users")
                .path(username)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getUserNames(String jwtToken) {
        return getWebTargetInternal()
                .path("users")
                .path("names")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response createUser(String jwtToken, UserAccount user) {
        return getWebTargetInternal()
                .path("users")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .post(Entity.json(user));
    }

    public Response updateUser(String jwtToken, UserAccount user) {
        return getWebTargetInternal()
                .path("users")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .put(Entity.json(user));
    }

    public Response changePassword(String jwtToken, ChangePassword request)
            throws UniformInterfaceException {
        Response response;
        try {
            response = getWebTargetInternal()
                    .path("users")
                    .path("password")
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, jwtToken)
                    .put(Entity.json(request));
        } catch (UniformInterfaceException e) {
            response = handleException(e);
        }
        return response;
    }

    public Response getUserContexts(String jwtToken, String username) {
        return getWebTargetInternal()
                .path("users")
                .path(username)
                .path("userContexts")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response createUserContext(String jwtToken, UserContext userContext) {
        return getWebTargetInternal()
                .path("users")
                .path(userContext.getUserName())
                .path("userContexts")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .post(Entity.json(userContext));
    }

    public Response updateUserContext(String jwtToken, UserContext userContext) {
        return getWebTargetInternal()
                .path("users")
                .path(userContext.getUserName())
                .path("userContexts")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .put(Entity.json(userContext));
    }

    public Response deleteUserContext(String jwtToken, String username, String userContextId) {
        return getWebTargetInternal()
                .path("users")
                .path(username)
                .path("userContexts")
                .path(userContextId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .delete();
    }

    public Response copyUserProfiles(String jwtToken, String to, List<ComprehensiveUserContext> userContextList) {
        return getWebTargetInternal()
                .path("users")
                .path(to)
                .path("userPreferences")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .put(Entity.json(userContextList));
    }

    public Response getChallenges(String jwtToken, String username) {
        return getWebTargetInternal()
                .path("users")
                .path(username)
                .path("challenges")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response setChallenges(String jwtToken, String username, ChallengeInformationResponse request) {
        return getWebTargetInternal()
                .path("users")
                .path(username)
                .path("challenges")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .put(Entity.json(request));
    }

    public Response getUserPreferences(String jwtToken, String username, String groupName) {
        return getWebTargetInternal()
                .path("users")
                .path(username)
                .path("userPreferences")
                .queryParam("groupName", groupName)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response findPolicies(String jwtToken, String name, String subject) {
        return getWebTargetInternal()
                .path("policies")
                .queryParam("name", name)
                .queryParam("subject", subject)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response updatePolicy(String jwtToken, Policy policy) {
        return getWebTargetInternal()
                .path("policies")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .put(Entity.json(policy));
    }

    public Response getSubjects(String jwtToken) {
        return getWebTargetInternal()
                .path("policies")
                .path("subjects")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getRoleNames(String jwtToken) {
        return getWebTargetInternal()
                .path("roles")
                .path("names")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response findRoles(String jwtToken, FindRolesQuery query) {
        return getWebTargetInternal()
                .path("roles")
                .queryParam("offset", query.getPaginator().getOffset())
                .queryParam("limit", query.getPaginator().getLimit())
                .queryParam("sortColumn", query.getPaginator().getSortColumn())
                .queryParam("sortDirection", query.getPaginator().getSortDirection())
                .queryParam("role", query.getRoleName())
                .queryParam("application", query.getApplicationName())
                .queryParam("status", query.getStatus())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getRole(String jwtToken, String roleId) {
        return getWebTargetInternal()
                .path("roles")
                .path(roleId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response createRole(String jwtToken, ComprehensiveRole role) {
        return getWebTargetInternal()
                .path("roles")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .post(Entity.json(role));
    }

    public Response updateRole(String jwtToken, ComprehensiveRole role) {
        return getWebTargetInternal()
                .path("roles")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .put(Entity.json(role));
    }

    public Response deleteRole(String jwtToken, String roleId) {
        return getWebTargetInternal()
                .path("roles")
                .path(roleId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .delete();
    }

    public Response getFeatureGroupNames(String jwtToken) {
        return getWebTargetInternal()
                .path("roles")
                .path("features/group/names")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response findPermissions(String jwtToken, String application, String group) {
        return getWebTargetInternal()
                .path("roles")
                .queryParam("application", application)
                .queryParam("group", group)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getRoles(String jwtToken) {
        return getWebTargetInternal()
                .path("roles")
                .path("comprehensives")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response findScopes(String jwtToken, FindScopesQuery query) {
        return getWebTargetInternal()
                .path("scopes")
                .queryParam("offset", query.getPaginator().getOffset())
                .queryParam("limit", query.getPaginator().getLimit())
                .queryParam("sortColumn", query.getPaginator().getSortColumn())
                .queryParam("sortDirection", query.getPaginator().getSortDirection())
                .queryParam("name", query.getScopeName())
                .queryParam("application", query.getApplicationName())
                .queryParam("status", query.getStatus())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getScope(String jwtToken, String scopeId) {
        return getWebTargetInternal()
                .path("scopes")
                .path(scopeId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response createScope(String jwtToken, Scope scope) {
        return getWebTargetInternal()
                .path("scopes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .post(Entity.json(scope));
    }

    public Response updateScope(String jwtToken, Scope scope) {
        return getWebTargetInternal()
                .path("scopes")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .put(Entity.json(scope));
    }

    public Response findDatasets(String jwtToken, String application, String category) {
        return getWebTargetInternal()
                .path("scopes")
                .path("datasets")
                .queryParam("application", application)
                .queryParam("category", category)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response deleteScope(String jwtToken, String scopeId) {
        return getWebTargetInternal()
                .path("scopes")
                .path(scopeId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .delete();
    }

    public Response getContexts(String jwtToken) {
        return getWebTargetInternal()
                .path("userContexts")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

    public Response getPing(String jwtToken) {
        return getWebTargetInternal()
                .path("ping")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .get();
    }

}
