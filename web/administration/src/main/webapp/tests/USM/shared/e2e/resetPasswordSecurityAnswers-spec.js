var LoginPage = require('../../shared/page/loginPage');
var MenuPage = require('../../shared/page/menuPage');
var AccountDetailsPage  = require('../../users/account/page/accountDetailsPage');

var Panel = require('../../../elements/panel');

describe('Test the panel of changing the Security Answer', function() {
    var menuPage = new MenuPage();
    var loginPage = new LoginPage();
    var accountDetailsPage = new AccountDetailsPage();

    //Variables panel Change Security answer
    var setNewPasswordSecurityAnswers = new Panel();
    var resetPasswordSecurityAnswerPanel = new Panel();
    var resetPasswordPanel = new Panel();

    //Test data
    var question1test = 'What is your favourite movie?';
    var answer1test = 'White';
    var question2test = 'What was the name of your first pet?';
    var answer2test = 'Churrina';
    var question3test = 'What was the model of your first car?';
    var answer3test = 'Seat Panda';
    var initialPassword = 'Password@100';
    var finalPassword = 'Password@101';

    //Test users
    var testUser1 = 'USM_TEST_1';
    var testUser2 = 'USM_TEST_2';
    var testUser3 = 'USM_TEST_3';

    //Methods
    var resetPassword = function (userName) {
        resetPasswordPanel.setFieldValue('userName', userName);
        resetPasswordPanel.clickResetButton();
    };

    var resetPasswordSuccess = function (userName) {
        resetPasswordPanel.setFieldValue('userName', userName);
        resetPasswordPanel.clickResetButtonSuccess();

    };

    //This method fills the panel of Change Security questions/answer for a certain user
    var setChangeSecurityAnswers = function (question1, answer1, question2, answer2, question3, answer3, password) {

        var securityAnswerPanel = new Panel();

        securityAnswerPanel.setSelectValue('challenge1', question1);
        securityAnswerPanel.setFieldValue('response1', answer1);
        securityAnswerPanel.setSelectValue('challenge2', question2);
        securityAnswerPanel.setFieldValue('response2', answer2);
        securityAnswerPanel.setSelectValue('challenge3', question3);
        securityAnswerPanel.setFieldValue('response3', answer3);

        securityAnswerPanel.applyCurrentPassword(password);

        securityAnswerPanel.clickSaveButtonSuccess();

    };

    //This method fills the panel of Change Security questions/answer for a certain user
    var restorePassword = function ( ) {

        var previousPassword = finalPassword;

        for(var i = 0 ; i < 2 ; i++) {
            //console.log("previousPassword: " + previousPassword);
            menuPage.clickChangePassword();
            menuPage.informSetPasswordPanel(previousPassword, finalPassword+i, finalPassword+i);
            menuPage.clickSaveButton();

            browser.wait(function() {
                var deferred = protractor.promise.defer();
                element(by.id('btn-success')).isPresent()
                    .then(function (isPresent) {
                        deferred.fulfill(!isPresent);
                    });
                return deferred.promise;
            });

            previousPassword = finalPassword + i;
        }

        //Restore password
        menuPage.clickChangePassword();
        menuPage.informSetPasswordPanel(previousPassword, initialPassword, initialPassword);
        menuPage.clickSaveButton();

        browser.wait(function() {
            var deferred = protractor.promise.defer();
            element(by.id('btn-success')).isPresent()
                .then(function (isPresent) {
                    deferred.fulfill(!isPresent);
                });
            return deferred.promise;
        });
    };

    //This method fills the panel of Change Security questions/answer for a certain user
    var setNewPasswordSecurityAnswers = function ( answer1,  answer2, answer3, password) {
        resetPasswordSecurityAnswerPanel.setFieldValue('response1', answer1);
        resetPasswordSecurityAnswerPanel.setFieldValue('response2', answer2);
        resetPasswordSecurityAnswerPanel.setFieldValue('response3', answer3);

        resetPasswordSecurityAnswerPanel.applyUserPassword(password);
        resetPasswordSecurityAnswerPanel.applyRepeatPassword(password);

    };

    beforeEach(function () {

       // loginPage.gotoHome();
       // menuPage.clickUserHome();
        loginPage.visit();

       // menuPage.clickUserLogin();

    });


    it('Test 1 - should test set the security questions of a certain user', function () {

        loginPage.login(testUser1,initialPassword);

        //Open the panel of setting Security Questions
        menuPage.clickChangeSecurityQuestions();

        //To inspect the title of the panel
        expect(resetPasswordSecurityAnswerPanel.getTitlePanel()).toBe('Change Your Security Answers');

        //Fill out the panel
        setChangeSecurityAnswers(question1test, answer1test, question2test, answer2test, question3test, answer3test, initialPassword);

    });


    it('Test 2 - should test the values of the labels/titles in the panels of Reset password and Set new password', function () {

        loginPage.clickForgottenPassword();

        //To inspect the title of the panel
        expect(resetPasswordPanel.getTitlePanel('setResetPassword')).toBe('Reset password');

        //To inspect the values of the label
        expect(resetPasswordPanel.getLabelText('setResetPassword',0)).toBe('Username');

        //reset the password menu
        resetPassword(testUser1);

        //Fill out the panel
        setNewPasswordSecurityAnswers(answer1test,answer2test,answer3test,'aa');

        //To inspect the title of the panel
        expect(resetPasswordSecurityAnswerPanel.getTitlePanel('setMySecurityAnswer')).toBe('Set new password');

        //To inspect the values of the different labels
        expect(resetPasswordPanel.getLabelText('setMySecurityAnswer',0)).toBe('Security questions:');
        expect(resetPasswordPanel.getLabelText('setMySecurityAnswer',1)).toBe('Answer:');
        expect(resetPasswordPanel.getLabelText('setMySecurityAnswer',2)).toBe('Answer:');
        expect(resetPasswordPanel.getLabelText('setMySecurityAnswer',3)).toBe('Answer:');
        expect(resetPasswordPanel.getLabelText('setMySecurityAnswer',4)).toBe('New Password');
        expect(resetPasswordPanel.getLabelText('setMySecurityAnswer',5)).toBe('Confirm Password');

        resetPasswordSecurityAnswerPanel.clickSaveButton();

        //To inspect the error message in the panel
        expect(resetPasswordSecurityAnswerPanel.getPanelMessage('setMySecurityAnswer')).toMatch('Password must contain at least 8 characters');

    });



    it('Test 3 - should test the Reset password with security questions and a wrong answer', function () {

        //Login with the test user credentials
        loginPage.clickForgottenPassword();

        //To inspect the title of the panel
        expect(resetPasswordPanel.getTitlePanel('setResetPassword')).toBe('Reset password');

        //reset the password menu
        resetPassword(testUser1);

        //Fill out the panel
        setNewPasswordSecurityAnswers(answer1test,answer2test,'Mercedes',finalPassword);

        //To inspect the title of the panel
        expect(resetPasswordSecurityAnswerPanel.getTitlePanel('setMySecurityAnswer')).toBe('Set new password');

        resetPasswordSecurityAnswerPanel.clickSaveButton();

        //To inspect the error message in the panel
        expect(resetPasswordSecurityAnswerPanel.getPanelMessage('setMySecurityAnswer')).toMatch('Invalid security answers');

    });

    it('Test 4 - should test the Reset password with security questions', function () {

        loginPage.clickForgottenPassword();

        //reset the password menu
        resetPassword(testUser1);

        //Fill out the panel
        setNewPasswordSecurityAnswers(answer1test,answer2test,answer3test,finalPassword);

        //To click the save button and wait for the success element
        resetPasswordSecurityAnswerPanel.clickSaveButtonSuccess();

    });

    it('Test 5 - should test to enter with a new password changed and restore the initial password', function () {

        //To enter with the new password
        loginPage.login(testUser1,finalPassword);

        restorePassword();

    });

    it('Test 6 - should test set a new password with a user without questions ', function () {

        loginPage.clickForgottenPassword();

        //To inspect the title of the panel
        expect(resetPasswordPanel.getTitlePanel('setResetPassword')).toBe('Reset password');

        //reset the password menu
        resetPasswordSuccess(testUser2);

    });


    it('Test 7 - should test an error message for a disable user', function () {

        //Option of forgotten password in the Login panel
        loginPage.clickForgottenPassword();

        //To inspect the title of the panel
        expect(resetPasswordPanel.getTitlePanel('setResetPassword')).toBe('Reset password');

        //reset the password menu
        resetPassword(testUser3);

        //To inspect the error message in the panel
        expect(resetPasswordPanel.getPanelMessage('setResetPassword')).toMatch('User status is disabled');

    });
    
    it('Test 8 - should test an error message for an unknow user', function () {
    	
    	//Option of forgotten password in the Login panel
    	loginPage.clickForgottenPassword();
    	
    	//To inspect the title of the panel
    	expect(resetPasswordPanel.getTitlePanel('setResetPassword')).toBe('Reset password');
    	
    	//reset the password menu
    	resetPassword("UNKNOWN_USERNAME");
    	
    	//To inspect the error message in the panel
    	expect(resetPasswordPanel.getPanelMessage('setResetPassword')).toMatch('We encountered an error. Please try again later.');
    	
    });

    afterEach(function () {
        loginPage.gotoHome();

        browser.executeScript('window.sessionStorage.clear();');
        browser.executeScript('window.localStorage.clear();');
    });

});
