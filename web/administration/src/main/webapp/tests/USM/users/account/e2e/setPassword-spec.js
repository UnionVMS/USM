
var LoginPage = require('../../../shared/page/loginPage');
var MenuPage = require('../../../shared/page/menuPage');
var UsersPage = require('../page/usersPage');
var AccountDetailsPage = require('../page/accountDetailsPage');

describe('User Set Password', function() {
    var menuPage = new MenuPage();
    var loginPage = new LoginPage();
    var usersPage = new UsersPage();
    var accountDetailsPage = new AccountDetailsPage();

    var userName = '';
    var passwordRandom = '';

    beforeEach(function ()  {
        //    browser.driver.manage().window().setSize(1280, 1280);
        // login
        loginPage.visit();
        loginPage.login('usm_admin', 'password',"USM-UserManager - (no scope)");

        // select users from menu
        menuPage.clickUsers();

        usersPage.getTableRows().count().then(function (rowCount) {
            initialUsersCount = rowCount;
            expect(initialUsersCount > 0).toBeTruthy();
        });

    });

    it('should test change a password of a user', function () {

        userName='vms_user_fra';
        //To create a random name
        passwordRandom = Math.random().toString(36).replace(/[^a-z]+/g, '').substr(0, 8);
        passwordRandom = passwordRandom + '/*' + 'L' + '124';

        usersPage.search(userName);

        usersPage.clickDetailViewButton(0);

        //inspect that the page is Account Details
        expect(accountDetailsPage.getPageUrl()).toBe(browser.baseUrl +'#/usm/users/'+ userName);

        accountDetailsPage.setPasswordButton();

        //Test 1. Same fields, but less than 8 characters
        accountDetailsPage.informSetPasswordPanel('pas*1','pas*1');
        accountDetailsPage.savePasswordButton();
        expect(accountDetailsPage.getPanelMessage()).toEqual('Error: Password must contain at least 8 characters');
        accountDetailsPage.cancelPasswordButton();

        //Test 2. Successful password
        accountDetailsPage.setPasswordButton();
        accountDetailsPage.informSetPasswordPanel(passwordRandom,passwordRandom);
        accountDetailsPage.clickSaveButtonSuccess();

        //To inspect that it is possible to enter with the userName updated with the new password
		loginPage.gotoHome();
        menuPage.clickLogOut();

		loginPage.visit();
       // loginPage.login(userName,passwordRandom);
        loginPage.login(userName,passwordRandom,"User - FRA Quotas");

        //to inspect that the user has successfully entered in USM with the new password
        expect(accountDetailsPage.getPageUrl()).toBe(browser.baseUrl +'#/usm/home');
    });


    afterEach(function () {

        loginPage.gotoHome();

		// logout
        menuPage.clickLogOut();

		browser.executeScript('window.sessionStorage.clear();');
		browser.executeScript('window.localStorage.clear();');
    });

});
