exports.config = {
		//seleniumAddress: 'http://localhost:4444/wd/hub',
		capabilities: {
		    'browserName': 'firefox',
            'loggingPrefs': {"driver": "INFO", "server": "OFF", "browser": "ALL"},
            shardTestFiles: true,
            maxInstances: 2
		},
		jasmineNodeOpts: {
			displayStacktrace: true,     // display stacktrace for each failed assertion
			displayFailuresSummary: true, // display summary of all failures after execution
			displaySuccessfulSpec: true,  // display each successful spec
			displayFailedSpec: true,      // display each failed spec
			displayPendingSpec: false,    // display each pending spec
			displaySpecDuration: true,   // display each spec duration
			displaySuiteNumber: true,    // display each suite number (hierarchical)
			defaultTimeoutInterval: 60000, // Default time to wait in ms before a test fails.

			showColors: true,
			print: function() {}
		},
		specs: ['**/e2e/*-spec.js','app/**/e2e/**/*.js'],
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
        chromeDriver: 'node_modules/protractor/selenium/chromedriver.exe',
        seleniumServerJar: 'node_modules/protractor/selenium/selenium-server-standalone-2.45.0.jar',
        seleniumArgs:[''],
        baseUrl: 'http://localhost:9001/app/', //default test port with Yeoman
		onPrepare: function() {
			var SpecReporter = require('jasmine-spec-reporter');
			//browser.driver.manage().window().maximize();
			browser.driver.manage().window().setSize(1024, 900);
			// add jasmine spec reporter
			jasmine.getEnv().addReporter(new SpecReporter({displayStacktrace: true}));
			require('jasmine-reporters');
			jasmine.getEnv().addReporter(new jasmine.JUnitXmlReporter('testresults', true, true, 'xmloutput', true)  );
		}
};
