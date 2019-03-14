var LoginPage = require('../../../shared/page/loginPage');
var MenuPage = require('../../../shared/page/menuPage');
var UsersPage = require('../page/usersPage');
var PoliciesPage = require('../../../policies/page/policiesPage');

describe('Test manage ldap user', function() {
    var menuPage = new MenuPage();
    var loginPage = new LoginPage();
    var usersPage = new UsersPage();
    var policiesPage = new PoliciesPage();
    var initialUsersCount=0;
    var sleep=3000;
    var userName = '';

    beforeEach(function () {
        // login
        loginPage.visit();
        loginPage.login('usm_admin', 'password',"USM-UserManager - (no scope)");

        // select policies from menu
        menuPage.clickPolicies();

        // set the criteria and search
        policiesPage.search("ldap.enabled", "Authentication");

        var columns = policiesPage.getTableRow(0).$$('td');
        columns.get(4).element(by.id('editPolicy')).click();

        browser.waitForAngular();

        policiesPage.clickModalEditButton();
        policiesPage.setModalPolicyValue('true');
        policiesPage.clickModalSaveButton();

        browser.waitForAngular();


        // select users from menu
        menuPage.clickUsers();

        usersPage.getTableRows().count().then(function (rowCount) {
            initialUsersCount = rowCount;
            //console.log("initialUsersCount: " + initialUsersCount);
            expect(initialUsersCount > 0).toBeTruthy();
        });

        browser.driver.sleep(sleep);

    });

    it('should test fill in a new user by copy existing at ldap', function() {
        //To create a ldap known name
        userName = "ldap_enabled";

        //New User button
        usersPage.clickNewUserButton();

        usersPage.setUserName(userName);
        usersPage.clickCopyButton();

        browser.waitForAngular();
        expect(usersPage.getFirstName().getAttribute('value')).toMatch('ldap');
        expect(usersPage.getLastName().getAttribute('value')).toMatch('Enabled');
        expect(usersPage.getEmail().getAttribute('value')).toMatch(
            'LdapEnabled@mail.org');
        expect(usersPage.getPhoneNumber().getAttribute('value')).toMatch('2105566888');
        //usersPage.clickSaveButton();
        usersPage.refreshPage();
    });

    afterEach(function () {
        // select policies from menu
        menuPage.clickPolicies();

        // set the criteria and search
        policiesPage.search("ldap.enabled", "Authentication");

        var columns = policiesPage.getTableRow(0).$$('td');
        columns.get(4).element(by.id('editPolicy')).click();

        browser.waitForAngular();

        policiesPage.clickModalEditButton();
        policiesPage.setModalPolicyValue('false');
        policiesPage.clickModalSaveButton();

        browser.waitForAngular();


        loginPage.gotoHome();

        // logout
        menuPage.clickLogOut();

        browser.executeScript('window.sessionStorage.clear();');
        browser.executeScript('window.localStorage.clear();');

        browser.driver.sleep(sleep);

    });

});
