//package eu.europa.ec.mare.usm.administration.rest.service;
//
//import eu.europa.ec.mare.usm.administration.domain.AuthenticationJwtResponse;
//import eu.europa.ec.mare.usm.authentication.domain.AuthenticationRequest;
//import eu.europa.ec.mare.usm.authentication.domain.AuthenticationResponse;
//
//import javax.ejb.EJB;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//
//import static org.junit.Assert.*;
//
//public class AuthWrapper {
//
//    private static final String PASSWORD = "password";
//    protected final String endPoint;
//    protected final Properties props;
//    private String authToken = "";
//
//    protected String getAuthToken() {
//        return authToken;
//    }
//
//    protected void setAuthToken(String authToken) {
//        this.authToken = authToken;
//    }
//
//    @EJB
//    private AdministrationRestClient restClient;
//
//    public AuthWrapper() throws IOException {
//        InputStream is = getClass().getResourceAsStream("/test.properties");
//        props = new Properties();
//        props.load(is);
//        endPoint = props.getProperty("rest.endpoint");
//
//    }
//
//    public AuthWrapper(String testUser) throws IOException {
//        InputStream is = getClass().getResourceAsStream("/test.properties");
//        props = new Properties();
//        props.load(is);
//        endPoint = props.getProperty("rest.endpoint");
//        authenticate(testUser);
//    }
//
//    protected String authenticate(String testUser) {
//        AuthenticationRequest request = new AuthenticationRequest();
//        request.setUserName(testUser);
//        request.setPassword(PASSWORD);
//        AuthenticationJwtResponse result = restClient.authenticateUser(testUser, PASSWORD);
//
//        assertNotNull(result);
//        assertTrue(result.isAuthenticated());
//        assertEquals(AuthenticationResponse.SUCCESS, result.getStatusCode());
//        authToken = result.getJwtoken();
//        return authToken;
//    }
//}
