exports.config = {
		//seleniumAddress: 'http://localhost:4444/wd/hub',
		capabilities: {
		    'browserName': 'firefox'//'chrome' //'firefox'
		},
		jasmineNodeOpts: {
			displayStacktrace: true,     // display stacktrace for each failed assertion
			displayFailuresSummary: true, // display summary of all failures after execution
			displaySuccessfulSpec: true,  // display each successful spec
			displayFailedSpec: true,      // display each failed spec
			displayPendingSpec: false,    // display each pending spec
			displaySpecDuration: true,   // display each spec duration
			displaySuiteNumber: true,    // display each suite number (hierarchical)
			defaultTimeoutInterval: 700000, // Default time to wait in ms before a test fails.

			showColors: false,
			print: function() {}
		},
		specs: ['tests/**/e2e/*-spec.js','tests/**/e2e/**/*.js'],
		//specs: ['**/e2e/applications-spec.js'],
		//specs: ['**/e2e/organisations-spec.js'],
		//specs: ['**/e2e/policies-spec.js'],
		//specs: ['**/e2e/roles-spec.js'],
		//specs: ['**/e2e/login-spec.js'],
		//specs: ['**/e2e/**/account-spec.js'],
		//specs: ['**/e2e/**/changePassword-spec.js'],
		//specs: ['**/e2e/**/duplicateUserProfile-spec.js'],
		//specs: ['**/e2e/**/setPassword-spec.js'],
		//specs: ['**/e2e/**/changePassword-spec.js','**/e2e/**/duplicateUserProfile-spec.js','**/e2e/**/setPassword-spec.js'],
		//specs: [''**/e2e/**/userRoles-spec.js'],
		//specs: ['**/e2e/**/duplicateUserProfile-spec.js'],
		//specs: ['**/e2e/**/userRoles-spec.js'],
		//specs: ['**/e2e/**/changes-spec.js'],
        chromeDriver: 'node_modules/protractor/selenium/chromedriver_2.21win32.zip',
        seleniumServerJar: 'node_modules/protractor/selenium/selenium-server-standalone-2.52.0.jar',
        baseUrl: 'http://localhost:9001/app/', //default test port with Yeoman
		onPrepare: function() {
			var SpecReporter = require('jasmine-spec-reporter');
			var jasmineReporters = require('jasmine-reporters');
			//browser.driver.manage().window().maximize();
			browser.driver.manage().window().setSize(1024, 900);
			// add jasmine spec reporter
			jasmine.getEnv().addReporter(new SpecReporter({displayStacktrace: true}));
			require('jasmine-reporters');
			jasmine.getEnv().addReporter(new jasmineReporters.JUnitXmlReporter('testresults', true, true, 'xmloutput', true)  );
		}
};
