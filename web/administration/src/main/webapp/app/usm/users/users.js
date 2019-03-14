angular.module('users', [
    'ui.bootstrap',
    'ui.utils',
    'ngRoute',
    'ngAnimate',
    'account',
    'userContexts',
    'preferences',
    'usersService',
    'auth',
    'changes',
    'shared']);

angular.module('users').config(['$urlRouterProvider', '$stateProvider', 'ACCESS', 'policyValuesProvider',
    function ($urlRouterProvider, $stateProvider, ACCESS, policyValues) {
        policyValues.setPolicyName("ldap.enabled");
        policyValues.setPolicySubject("Authentication");

//        var currentContextPromise = function (userService) {
//            return userService.findSelectedContext();
//        };
//        currentContextPromise.$inject = ['userService'];


        var orgNationsPromise = function(organisationsService,userService){
  		  //we need user context first
  	      return userService.findSelectedContext().then(
  	    		  function (context) {
  		    		  return organisationsService.getNations().then(
  		                 function (response) {
  		                     return response.nations;
  		                 },
  		                 function (error) {
  		                     return [error];
  		                 }
  		    		  );
  	    		  },
  	    		  function (error) {
  	                     return [error];
  	                 }
  	    		  );
  	           };

  	  orgNationsPromise.$inject =    ['organisationsService','userService'];
        
	  var orgNamesPromise = function(organisationsService,userService){
		//we need user context first
	      return userService.findSelectedContext().then(
	    		  function (context) {
	    		         return organisationsService.get().then(
	    		                 function (response) {
	    		                     return response.organisations;
	    		                 },
	    		                 function (error) {
	    		                     return [error];
	    		                 }
	    		             );
	    		  },
	    		  function (error) {
	                     return [error];
	                 }
	    		  );
	   };
	   orgNamesPromise.$inject =    ['organisationsService','userService'];

        $stateProvider
            .state('app.usm.users', {
                url: '/users?{page:int}&{sortColumn}&{sortDirection}&{user}&{nation}&{organisation}&{status}&{activeFrom}&{activeTo}',
                data: {
                    access: ACCESS.AUTH
                },
                params: {
                    page: 1,
                    sortColumn: 'userName',
                    sortDirection: 'desc',
                    status: '',
                    user: '',
                    nation: '',
                    organisation: '',
                    activeFrom: '',
                    activeTo: ''
                },
                views: {
                    "page@app.usm": {
                        templateUrl: 'usm/users/usersList.html',
                        controller: "usersListController"
                    }
                },
                ncyBreadcrumb: {
                    label: 'Users'
                },
                resolve: {
//                    currentContext: currentContextPromise,
                    orgNations: orgNationsPromise,
                    orgNames: orgNamesPromise

                }
            })
            .state('app.usm.users.contactDetails', {
                url: '/{userName}/contactDetails',
                views: {
                    "page@app.usm": {
                        templateUrl: 'usm/users/contactDetails/partial/contactDetails.html',
                        controller: 'contactDetailsTabsCtrl'
                    }
                }
            })
            .state('app.usm.users.userDetails', {
                url: '/{userName}',
                params: {
                    userName: ''
                },
                views: {
                    "page@app.usm": {
                        templateUrl: 'usm/users/partial/userDetails.html',
                        controller: 'userDetailsCtlr'
                    }
                },
                resolve: {
                    userDetailsService: 'userDetailsService'
                },
                ncyBreadcrumb: {
                    label: 'User Details'
                }
            });
    }]);
