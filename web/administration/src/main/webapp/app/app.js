var appModule = angular.module('app', ['ui.router', 'usm', 'auth','unionvmsWeb']);

//also configure the unionVMS module to be able to define the common directives
angular.module('unionvmsWeb', ['ui.bootstrap','ui.utils','ngRoute','ngAnimate']);

//Configure locale in momentjs (used to determine start of week)
//TODO: get locale from config or browser
moment.locale('en');

appModule.config(['$urlRouterProvider', '$stateProvider','authRouterProvider','ACCESS','$injector',
    function ($urlRouterProvider, $stateProvider,authRouterProvider,ACCESS,$injector) {

        var getGlobalSettingsPromise = function(globalSettingsService) {
            return globalSettingsService.setup();
        };
        getGlobalSettingsPromise.$inject = ['globalSettingsService'];


    $urlRouterProvider.when('','usm');
    $stateProvider
        .state('app', {
            abstract:true,

            views: {
                app: {
                    template: '<div ui-view name="modulenav"></div><div ui-view name="modulepage"></div>'
                },
                header: {
                    templateUrl: 'usm/ec-template/template/header.html'
                },
                footer: {
                    templateUrl: 'usm/ec-template/template/footer.html',
                    controller:"FooterController"
                }
            },
            resolve: {
                globalSettings : getGlobalSettingsPromise
            }
        });


        $urlRouterProvider.otherwise('/usm');
}]);

appModule.run(['$rootScope', '$location', '$log', '$http', '$localStorage', 'jwtHelper', '$modalStack', '$cookies', '$translate',
    function ($rootScope, $location, $log, $http, $localStorage, jwtHelper, $modalStack, $cookies, $translate) {

        $rootScope.safeApply = function (fn) {
            var phase = $rootScope.$$phase;
            if (phase === '$apply' || phase === '$digest') {
                if (fn && (typeof(fn) === 'function')) {
                    fn();
                }
            } else {
                this.$apply(fn);
            }
        };

    }]);

appModule.config(['$httpProvider', 'authInterceptorProvider','ENVCONFIG', function Config($httpProvider, authInterceptorProvider,ENVCONFIG) {
    // Please note we're annotating the function so that the $injector works when the file is minified
    authInterceptorProvider.injectPanel = false;
    if(_.isArray(ENVCONFIG.rebaseApiList)){
      authInterceptorProvider.rebaseApiList = ENVCONFIG.rebaseApiList;
    }
    authInterceptorProvider.authFilter = ['config', '$log', function (config, $log) {
        //myService.doSomething();

        var skipURL = /^(template|usm|assets|common).*?\.(html|json)$/i.test(config.url);
        var logmsg = skipURL?'SKIPPING':'setting auth';
        $log.debug('authFilter '+ logmsg +' on url :' + config.url);
        return skipURL;
    }];

    $httpProvider.interceptors.push('authInterceptor');
}]);

appModule.config(['$stateProvider','authRouterProvider','ENVCONFIG',function($stateProvider,authRouterProvider,ENVCONFIG){

    if(angular.isDefined(ENVCONFIG.idm)){
      if(ENVCONFIG.idm==="ECAS"){
        if(ENVCONFIG.loginmode === "Panel"){
          authRouterProvider.useLoginPanel('ecasloginpanel');
        } else {
          authRouterProvider.useLoginPage('ecaslogin');
        }
      } else if (ENVCONFIG.idm==="BuiltIn") {
        if(ENVCONFIG.loginmode === "Page"){
          authRouterProvider.useLoginPage('login');
        } else {
          authRouterProvider.useLoginPanel('loginpanel');
        }
      } else {
        authRouterProvider.useLoginPanel();
      }
    } else {
      authRouterProvider.useLoginPanel();
    }
    authRouterProvider.anonRoute = "/anon";
    authRouterProvider.setHomeState("app.usm.home");
    authRouterProvider.setLogoutState("app.usm.logout");
}]);

//Handle error durring app startup
function handleEnvironmentConfigurationError(error, $log){
    $log.error("Error loading environment configuration.", error);
    if(angular.isDefined(error.status) && angular.isDefined(error.statusText)){
        error = error.status + " : " +error.statusText;
    }
    if(typeof error !== 'string'){
        error = "";
    }
    $('#appLoading').remove();
    $("body").append('<div class="appErrorContainer"><i class="fa fa-exclamation-triangle"></i> Error loading environment configuration<div>' +error +'</div></div>');
}

///Bootstrap the application by getting the environment config that points out the REST api URL
var envConfigJsonPath = "usm/assets/EnvSettings.json?ts=" +(new Date()).getTime();

function getEnvironmentConfig(envConfig) {
    var initInjector = angular.injector(["ng"]);
    var $http = initInjector.get("$http");
    var $log = initInjector.get("$log");
    var $q = initInjector.get("$q");

    var deferred = $q.defer();

    $http.get(envConfigJsonPath).then(function(response) {
        $log.debug(response.data);
        var envConfig = response.data;
        appModule.constant('ENVCONFIG', envConfig);
        //Verify that rest_api_base is available in the config
        // if(angular.isUndefined(envConfig) || angular.isUndefined(envConfig.rest_api_base)){
        //     var error ="rest_api_base is missing from the configuration file.";
        //     handleEnvironmentConfigurationError(error, $log);
        //     deferred.reject(error);
        //     return;
        // }
        //verify we have an idm setting in the config
        if(angular.isUndefined(envConfig) || angular.isUndefined(envConfig.idm)){
            var error ="idm setting is missing from the configuration file.";
            handleEnvironmentConfigurationError(error, $log);
            deferred.reject(error);
            return;
        }
        $('#appLoading').remove();
        deferred.resolve();
    }, function(error) {
        handleEnvironmentConfigurationError(error, $log);
        deferred.reject(error);
    });

    return deferred.promise;
}


function bootstrapApplication() {
    angular.element(document).ready(function() {
        angular.bootstrap(document, ['app']);
    });
}
getEnvironmentConfig().then(bootstrapApplication);
