package eu.europa.ec.mare.usm.administration.rest.service.user;

import eu.europa.ec.mare.usm.administration.domain.*;
import eu.europa.ec.mare.usm.administration.rest.service.AdministrationRestClient;
import eu.europa.ec.mare.usm.administration.rest.service.BuildAdministrationDeployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ws.rs.core.Response;
import java.util.Date;

import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class UserProfileResourceIT extends BuildAdministrationDeployment {

    private static final String USM_ADMIN = "usm_admin";
    private static final String PASSWORD = "password";

    @EJB
    private AdministrationRestClient restClient;

    @Test
    @OperateOnDeployment("normal")
    public void testChangePasswordByAdministrator() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        UserAccount testUserAccount = createUser();

        Response response = restClient.createUser(auth.getJwtoken(), testUserAccount);
        assertEquals(OK.getStatusCode(), response.getStatus());

        ChangePassword changePassword = new ChangePassword();
        changePassword.setUserName(testUserAccount.getUserName());
        changePassword.setNewPassword("p@3$w0rdD");

        response = restClient.changePassword(auth.getJwtoken(), changePassword);
        assertEquals(OK.getStatusCode(), response.getStatus());

        auth = restClient.authenticateUser(testUserAccount.getUserName(), changePassword.getNewPassword());
        assertNotNull(auth);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testChangePasswordByEndUser() {
        AuthenticationJwtResponse auth = restClient.authenticateUser(USM_ADMIN, PASSWORD);
        String initialPassword = "p@3$w0rdD";

        // 1: Administrator creates test user
        UserAccount userAccount = createUser();
        Response response = restClient.createUser(auth.getJwtoken(), userAccount);
        assertEquals(OK.getStatusCode(), response.getStatus());

        // 2: Administrator sets the test user initial password
        ChangePassword changePassword = new ChangePassword();
        changePassword.setUserName(userAccount.getUserName());
        changePassword.setNewPassword(initialPassword);

        response = restClient.changePassword(auth.getJwtoken(), changePassword);
        assertEquals(OK.getStatusCode(), response.getStatus());

        // Execute: test user logs in and changes his own password
        AuthenticationJwtResponse user = restClient.authenticateUser(userAccount.getUserName(), initialPassword);
        String updatedPassword = "P@3$w0rdD";

        changePassword = new ChangePassword();
        changePassword.setUserName(userAccount.getUserName());
        changePassword.setCurrentPassword(initialPassword);
        changePassword.setNewPassword(updatedPassword);

        response = restClient.changePassword(user.getJwtoken(), changePassword);
        assertEquals(OK.getStatusCode(), response.getStatus());

        // Verify: test user logs-in with changed password
        user = restClient.authenticateUser(userAccount.getUserName(), updatedPassword);
        assertNotNull(user);
    }

    private UserAccount createUser() {
        UserAccount account = new UserAccount();
        account.setUserName("testRestUser" + System.currentTimeMillis());
        account.setStatus("E");
        Organisation org = new Organisation();
        account.setOrganisation(org);
        org.setName("DG-MARE");

        Person person = new Person();
        account.setPerson(person);
        person.setFirstName("Test-Rest");
        person.setLastName("User");
        person.setEmail(account.getUserName() + "@email.com");

        account.setActiveFrom(new Date(System.currentTimeMillis() - 3600000));
        account.setActiveTo(new Date(System.currentTimeMillis() + 36000000));
        return account;
    }
}
