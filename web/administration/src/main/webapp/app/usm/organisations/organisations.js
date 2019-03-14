var organisationsModule = angular.module('organisations', [
	'ui.bootstrap',
	'ui.utils',
	'ngRoute',
	'ngAnimate',
	'organisationsService',
    'usersService',
	'personsService',
    'auth'
]);

 organisationsModule.config(['$urlRouterProvider', '$stateProvider', 'ACCESS',
  function ($urlRouterProvider, $stateProvider, ACCESS) {

 
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
	        .state('app.usm.organisations', {
	            url: '/organisations?{page:int}&{sortColumn}&{sortDirection}&{name}&{nation}&{status}',
				//data: {
				//	access: ACCESS.AUTH
				//},
                params:{
                    page:1,
                    sortColumn:'name',
                    sortDirection:'asc',
                    name:'',
                    nation:'',
                    status:'all'
                },
	            views: {
	            	"page@app.usm": {
	                    templateUrl: 'usm/organisations/organisationsList.html',
	                    controller: "organisationsListCtrl"
	                }
	            },
                ncyBreadcrumb: {
                    label: 'Organisations'
                },
                resolve:{

                	orgNations:orgNationsPromise,
                	orgNames:orgNamesPromise
                }
	        })
	        .state('app.usm.organisations.organisation', {
	            url: '/{organisationId}',
                params:{
                    organisationId:null
                },
	            views: {
                    "page@app.usm": {
	                    templateUrl: 'usm/organisations/partial/organisationDetails.html'
	                }
	            },
                ncyBreadcrumb: {
                    label: 'Organisation Details'
                }
	        })

	    .state('app.usm.organisations.organisation.endpoint', {
            url: '/endpoint/{endPointId}',
            params:{
                    endPointId:null
                },
            views: {
                "page@app.usm": {
                    templateUrl: 'usm/organisations/partial/endPointsDetails.html'
                }
            },
                ncyBreadcrumb: {
                    label: 'Endpoint'
                }
        });

	}]);


	organisationsModule.factory('organisationsCache', ['$cacheFactory', function($cacheFactory){
	    return $cacheFactory('organisationsCache');
	}]);
