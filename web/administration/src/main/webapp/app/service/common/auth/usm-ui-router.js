/*
Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
angular.module('auth.router', ['ui.bootstrap', 'auth.controllers', 'ui.router', 'auth.user-service'])
    .constant("ACCESS", {
        "PUBLIC": {ac: "public"},
        "AUTH": {ac: "auth"}
    })
/**
 * @ngdoc object
 * @name auth.router.authRouterProvider
 *
 * @requires ui.router.util.$urlMatcherFactoryProvider
 * @requires $stateProvider
 *
 * @description
 * `authRouterProvider` has the responsibility of watching for $state changes.
 * On `$state` change events it  checks ACCESS state data and whether a user is logged
 * in or not. Depending on this it redirects to the login page
 *
 */
    .provider('authRouter', ['$stateProvider', '$urlMatcherFactoryProvider', 'ACCESS',
        function ($stateProvider, $urlMatcherFactory, ACCESS) {

            // A state to be used for login
            var loginState,
                logoutState,
                accessErrorState,
                homeState = '';


            this.setHomeState = function (stateName) {
                homeState = stateName;
            };
            this.setLoginState = function (stateName) {
                loginState = stateName;
            };
            this.setLogoutState = function (stateName) {
                logoutState = stateName;
            };

            /**
             * @ngdoc function
             * @name auth.router.authRouterProvider#useLoginPage
             * @methodOf auth.router.authRouterProvider
             *
             * @description
             * Set's up a login state to be used instead of the pop up.
             *
             * Unless the authRouterProvider finds a state name 'login', it sets up a state named `loginpanel` that uses
             * a modal panel for login.
             * To replace this by a page instead call `authRouterProvider.useLoginState()` without any argument. This
             * will create a state named `login` using the same template as the login panel.
             * While using different controllers, they perform the same operations: Upon a successful login the
             * `userService.login()` method is called passing the authorisation token received and the state gets
             * transitioned to the provided toState $stateParams or otherwise to the homeState.
             *
             * If you have setup your own login state to be used instead of the default one you can pass the state name
             * to `authRouterProvider.useLoginState()`.
             *
             * You can also pass a complete state configuration obejct (including a state name) that will be passed to
             * `$stateProvider.state()` for setting it up and then used for login.
             *
             *  If no `loginStateDefinition` is provided a default
             *
             * @param {string|object=} loginStateDefinition An existing state name, e.g. "login", or a complete State configuration object.
             * if undefined a default login state, with name `login` will be created
             */
            this.useLoginPage = function (loginStateDefinition) {
              //default states
              $stateProvider
                  .state('login', {
                      url: '/login',
                      params: {
                          toState: {
                              value: function () {
                                  return homeState;
                              }
                          },
                          toParams: null,
                          message: "Login required"
                      },
                      data: {access: ACCESS.PUBLIC},
                      views: {
                          app: {
                              templateUrl: 'service/common/auth/templates/renewLogin.html',
                              controller: 'loginController'
                          }
                      }
                  })
                  .state('loginChoice', {
                      url: '/loginchoice',
                      params: {
                          toState: {
                              value: function () {
                                  return homeState;
                              }
                          },
                          toParams: null,
                          message: "Login required"
                      },
                      data: {access: ACCESS.PUBLIC},
                      views: {
                          app: {
                              templateUrl: 'service/common/auth/templates/LoginChoice.html',
                              controller: 'loginChoiceController'
                          }
                      }
                  })
                  .state('ecaslogin', {
                      url: '/ecaslogin',
                      params: {
                          toState: null,
                          toParams: null,
                          message: "Login required"
                      },
                      onEnter: ['$window', '$location', '$log', 'authRouter', function ($window, $location, $log, authRouter) {
                          var callback = $window.document.URL.split('#')[0] + '#/jwtcallback';
                          var _url = "/usm-administration/rest/ping?jwtcallback=" + callback;
                          //$location.url(_url);
                          $window.location.assign(_url);
                      }]
                  });

                if (loginStateDefinition) {
                    if (angular.isObject(loginStateDefinition)) {
                        //This should be a complete state object including a name
                        loginState = loginStateDefinition.name;
                        $stateProvider
                            .state(loginStateDefinition);
                    } else if (angular.isString(loginStateDefinition)) {
                        // loginStateDefinition is the name of an existing state
                        loginState = loginStateDefinition;

                    } else {
                        throw "loginStateDefinition should be a state name or a complete state configuration object";
                    }
                } else {
                    //define a default state to use
                    loginState = 'loginChoice';
                }

            };

            this.useLoginPanel = function (loginStateDefinition) {

              //define a default state to use
              $stateProvider
                  .state('loginpanel', {
                      url: '/loginpanel',
                      params: {
                          toState: {
                              value: function () {
                                  return homeState;
                              }
                          },
                          toParams: null,
                          message: "Login required"
                      },
                      data: {access: ACCESS.PUBLIC},
                      onEnter: ['$stateParams', '$state', '$modal', '$log', 'authRouter', function ($stateParams, $state, $modal, $log, authRouter) {
                          var toState = $stateParams.toState;
                          var toParams = $stateParams.toParams;
                          $modal.open({
                              templateUrl: 'service/common/auth/templates/renewLogin.html',

                              controller: 'modalLoginController',
                              backdrop: 'static'
                          }).result.finally(function () {
                                  $log.debug('loginpanel modal finally running');
                                  $log.debug('Going to state ', toState.name);
                                  $state.go(toState, toParams);
                              });
                      }]
                  })
                  .state('loginChoicePanel', {
                      url: '/loginchoicepanel',
                      params: {
                          toState: {
                              value: function () {
                                  return homeState;
                              }
                          },
                          toParams: null,
                          message: "Login required"
                      },
                      data: {access: ACCESS.PUBLIC},
                      onEnter: ['$stateParams', '$state', '$modal', '$log', 'authRouter', function ($stateParams, $state, $modal, $log, authRouter) {
                          var toState = $stateParams.toState;
                          var toParams = $stateParams.toParams;
                          $modal.open({
                              templateUrl: 'service/common/auth/templates/LoginChoice.html',

                              controller: 'modalLoginChoiceController',
                              backdrop: 'static'
                          }).result.finally(function () {
                                  $log.debug('loginChoicePanel finally running');
                                  $log.debug('Going to state ', toState.name);
                                  $state.go(toState, toParams);
                              });
                      }]
                  })
                  .state('ecasloginpanel', {
                        url: '/ecasloginpanel',
                        params: {
                            toState: {
                                value: function () {
                                    return homeState;
                                }
                            },
                            toParams: null,
                            message: "Login required"
                        },
                        onEnter: ['$stateParams', '$state', '$modal', '$log', 'authRouter', function ($stateParams, $state, $modal, $log, authRouter) {
                            var toState = $stateParams.toState;
                            var toParams = $stateParams.toParams;
                            $modal.open({
                              templateUrl: 'service/common/auth/templates/EcasLogin.html',
                              controller: 'ecaslogincontroller',
                                backdrop: 'static'
                            }).result.finally(function () {
                                    $log.debug('ecasloginpanel modal finally running');
                                    $log.debug('Going to state ', toState.name);
                                    $state.go(toState, toParams);
                                });
                        }]
                    });

                    if (loginStateDefinition) {
                        if (angular.isObject(loginStateDefinition)) {
                            //This should be a complete state object including a name
                            loginState = loginStateDefinition.name;
                            $stateProvider
                                .state(loginStateDefinition);
                        } else if (angular.isString(loginStateDefinition)) {
                            // loginStateDefinition is the name of an existing state
                            loginState = loginStateDefinition;

                        } else {
                            throw "loginStateDefinition should be a state name or a complete state configuration object";
                        }
                    } else {
                        //define a default state to use
                        loginState = 'loginChoicePanel';
                    }

            };
            this.setupLogoutState = function (stateName) {
                if (!stateName) {
                    //define a default state to use
                    $stateProvider
                        .state('logout', {
                            url: '/logout',
                            params: {
                                loginState: loginState,
                                logoutMessage: "You have been logged out."
                            },
                            data: {access: ACCESS.PUBLIC},
                            views: {
                                app: {
                                    templateUrl: 'service/common/auth/templates/logout.html',
                                    controller: 'logoutController'
                                }
                            }
                        });
                    stateName = 'logout';
                }
                logoutState = stateName;

            };
            this.setupAccessErrorState = function (stateName) {
                if (!stateName) {
                    //define a default state to use
                    $stateProvider
                        .state('access_error', {
                            url: '/access_error',
                            params: {
                                loginState: loginState,
                                message: "You do not have the required rights for this page"
                            },
                            data: {access: ACCESS.PUBLIC},
                            views: {
                                app: {
                                    templateUrl: 'service/common/auth/templates/accessError.html',
                                    controller: 'accessErrorController'
                                }
                            }
                        });
                    stateName = 'access_error';
                }
                accessErrorState = stateName;

            };


            var routerConfig = this;

            var $get = function ($state, $modal, $rootScope, $injector, $timeout, $location, $window, userService, $log, renewloginpanel, ACCESS, $urlRouter) {
                var preventedState = null;
                var allowedState = null;
                var expiredPasswordPanel = null;
                var anonRoute;
                var idm;


                var _setupStates = function () {
                    // if no loginState has been defined, look for a state named `login` and use it
                    // otherwise use the default login panel.
                    if (!loginState) {
                        var existingLoginState = $state.get('login');
                        if (existingLoginState) {
                            loginState = 'login';
                        } else {
                            routerConfig.useLoginPanel();
                        }
                    }
                    if (!logoutState) {
                        routerConfig.setupLogoutState();
                    }
                };

                var _getHomeState = function () {
                    return homeState;
                };

                var _getLoginState = function () {
                    return loginState;
                };


                $injector.invoke(['$state', function ($state) {
                    _setupStates();
                    /*
                     $rootScope.$on('AuthenticationSuccess', function () {
                     if($state.current.url !== "^") {
                     $state.reload();
                     }
                     });*/

                    /*
                     $rootScope.$on('ContextSwitch', function () {
                     $state.reload();
                     });*/

                    // if we get an authenticationNeeded event we go to the login state
                    $rootScope.$on('authenticationNeeded', function () {
                        $log.debug('on -> authenticationNeeded');
                        $log.debug('authenticationNeeded preventedState', preventedState);
                        // a state that got prevented when checking for access right might still raise an authentication
                        // error that the auth Interceptor would intercept
                        // Let's avoid going to the loginstate several times
                        if (!preventedState) {
                            //if we get here we got stopped in our tracks while trying to load data for an authorised state
                            // let's put the last allowedStata as the destination after the login
                            var toState = allowedState && allowedState.state ? allowedState.state : null;
                            var toParams = allowedState && allowedState.params ? allowedState.params : null;
                            /*$state.go(loginState, {'toState': toState, 'toParams': toParams}).then(
                                function(successTransition){},
                                function(transitionError){
                                    $log.debug("transistion to loginstate rejected with error ",transitionError);
                                }
                            );*/
                        }
                        //$state.reload();
                    });

					$rootScope.$on('NeedChangePassword', function (event, args) {
						$log.log("injecting changepasswordpanel");
						var status = args.status;
                        if(!expiredPasswordPanel){
                            expiredPasswordPanel = $injector.get('changepasswordpanel');
                            expiredPasswordPanel.show(status).finally(function () {
                                $log.log("changepasswordpanel Closed");
                                expiredPasswordPanel = null;
                                //init();
                            });
                        }


					});
					$rootScope.$on('WarningChangePassword', function () {
						$log.log("injecting warningpasswordpanel");
						var WarningPwd = $injector.get('warningpasswordpanel');
						WarningPwd.show().then(function () {
							$log.log("warningpasswordpanel Closed");
							//init();
						});
					});

          //credits: http://www.netlobo.com/url_query_string_javascript.html
          function gup(url, name) {
              name = name.replace(/[[]/, "[").replace(/[]]/, "]");
              var regexS = "[?&]" + name + "=([^&#]*)";
              var regex = new RegExp(regexS);
              var results = regex.exec(url);
              if (results == null) {
                  return "";
              } else {
                  return results[1];
              }
          }
          function rmgup(url, name) {
              name = name.replace(/[[]/, "[").replace(/[]]/, "]");
              var regexS = "[?&]" + name + "=([^&#]*)";
              var regex = new RegExp(regexS);
              return url.replace(regex,"");

          }


                    $rootScope.$on('$locationChangeSuccess', function (e) {
                        $log.debug('$locationChangeSuccess', arguments);
                        $log.debug('$locationChangeSuccess preventedState', preventedState);
                        $log.debug('$state.$current.self', $state.$current.self);
                        //if we have started a manual redirect below we will only
                        // allow the $locationChangeSuccess if the state matches the redirect
                        if (preventedState) {
                            if ($state.$current.self.name !== preventedState.state.name) {
                                e.preventDefault();

                            } else {
                                preventedState = null;
                            }
                        }
                        // UserService is an example service for managing user state
                        //if (UserService.isLoggedIn()) return;
                        //
                        //// Prevent $urlRouter's default handler from firing
                        //e.preventDefault();
                        //
                        //UserService.handleLogin().then(function() {
                        //    // Once the user has logged in, sync the current URL
                        //    // to the router:
                        //    $urlRouter.sync();
                        //});
                    });

                    // Configures $urlRouter's listener *after* your custom listener
                    $urlRouter.listen();

                    /*for debugging*/
                    //$rootScope.$on('$stateChangeStart',function(event, toState, toParams, fromState, fromParams){
                    //    $log.debug('$stateChangeStart to '+toState.to+'- fired when the transition begins. toState,toParams : \n',toState, toParams);
                    //});
                    $rootScope.$on('$stateChangeError', function (event, toState, toParams, fromState, fromParams, error) {
                        $log.debug('$stateChangeError - fired when an error occurs during transition.');
                        $log.debug(arguments);
                    });
                    $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
                        $log.debug('$stateChangeSuccess to ' + toState.name + '- fired once the state transition is complete.');
                    });

                    // $rootScope.$on('$viewContentLoading',function(event, viewConfig){
                    //   // runs on individual scopes, so putting it in "run" doesn't work.
                    //   $log.debug('$viewContentLoading - view begins loading - dom not rendered',viewConfig);
                    // });

                    $rootScope.$on('$viewContentLoaded', function (event) {
                        $log.debug('$viewContentLoaded - fired after dom rendered', event);
                    });
                    $rootScope.$on('$stateNotFound', function (event, unfoundState, fromState, fromParams) {
                        $log.debug('$stateNotFound ' + unfoundState.to + '  - fired when a state cannot be found by its name.');
                        $log.debug(unfoundState, fromState, fromParams);
                    });

                    $rootScope.$on("$stateChangeStart", function (event, toState, toParams, fromState, fromParams) {
                        $log.debug('$stateChangeStart > to ' + toState.name + '- fired when the transition begins. toState,toParams : \n', toState, toParams);
                        var preventedName = preventedState && preventedState.state ? preventedState.state.name : '';
                        $log.debug("$stateChangeStart > preventedState name :" + preventedName);
                        //check if user is logged in:
                        // do we have a jwt in the url:
                        var url = $location.absUrl();
                        if (url.indexOf("jwt=") > 0) {
                            var token = gup(url, 'jwt');
                            userService.login(token);
                            url = rmgup(url,'jwt');
                          }
                          if (url.indexOf("ticket=") > 0) {
                            url = rmgup(url,'ticket');
                            //index.html?ticket=ST-13911999-khbGOmIpmoSwrouPkNoEhzvQX5Xe3xqKAb6cBcRikbDYtNnXylezhTyOQH2dWuqfx6IVmabQznGql3VXGHrziEC-PHslUMVSXYCcIEi3lxzzJ9m-TS0owNI5tRMX2MRGCkDlgJztzXt2jSzoqwft4Ofm05AS
                          }
                          if (url.indexOf("index.html") > 0){
                            var regexS = "index.html(?=#)";
                            var regex = new RegExp(regexS);
                            url = url.replace(regex,"");
                          }
                          if(url!==$location.absUrl()){
                              $window.location.assign(url);
                              event.preventDefault();
                          }
                        var accessLevel = ('data' in toState) && ('access' in toState.data) && toState.data.access ? toState.data.access : null;
                        $log.debug('$stateChangeStart > Access level for this state is:',accessLevel);
                        var error;
                        // We override all restrictions if this is the logout state.
                        if (toState.name !== logoutState) {
                        	// let's check if this state needs to be protected

                        	if (!accessLevel || accessLevel === ACCESS.PUBLIC) {
                                $log.debug("$stateChangeStart > Access undefined or public for this state. Go ahead");
                                //in the future we could decide that we do not allow states with undefined access but for now...
                                preventedState = null;
                                // In case something goes wrong when fetching the data we could still be redirected to the login
                                // just for such a case lets store toState and toParams
                                allowedState = {state: toState, params: toParams};

                            } else {
                            	// this state has a form of restriction
                            	$log.debug('$stateChangeStart > This route has some form of non public access restriction:', accessLevel);
                            	// let's find the username
	                            userService.findCurrentUser().then(
	                                function (userName) {
	                                    $log.debug('$stateChangeStart > User resolution returned "' + userName + '"');

	                                    if (userName === "") {
	                                        $log.debug('$stateChangeStart > We are NOT logged in.');

	                                            //todo: detect previous state properly, here we only look for a parent
	                                            if (fromState.url === '^') {
	                                                $log.debug('$stateChangeStart > Looks like there is no previous state');
	                                                error = 'Please login with an authorised username and password.';
	                                                $log.debug('$stateChangeStart > '+error);
	                                                preventedState = preventedState || {state: toState, params: toParams};
	                                                event.preventDefault();
	                                            } else {
	                                                $log.debug('$stateChangeStart > Looks like there is a previous state: ', fromState);
	                                                error = 'Your session timed out. Please Login again';
	                                                $log.debug('$stateChangeStart > '+error);
	                                                preventedState = preventedState || {state: toState, params: toParams};
	                                                event.preventDefault();
	                                            }
	                                            //TODO: Maybe we don't want to automatically redirect but show an access error page
	                                            $state.go(loginState, {
	                                                'toState': toState,
	                                                'toParams': toParams,
	                                                message: error
	                                            }).then(
	                                                function(successTransition){},
	                                                function(transitionError){
	                                                    $log.error("$stateChangeStart > transistion to loginstate rejected with error ",transitionError);
	                                                }
	                                            );

	                                            /*
	                                             if(loginState){

	                                             $state.go(loginState, {'toState': toState, message: error});
	                                             }
	                                             else
	                                             {

	                                             //renewloginpanel.show().
	                                             userService.findSelectedContext().then(function () {
	                                             $log.log("retry request succeeded!");
	                                             //$state.go(toState);
	                                             $state.go(toState, toParams, {reload: true});
	                                             //return $http(rejection.config);
	                                             }, function () {
	                                             $log.log("retry request failed!");
	                                             userService.logout();
	                                             $rootScope.$broadcast('authenticationNeeded');
	                                             });
	                                             }*/

	                                    } else {
	                                        $log.debug('$stateChangeStart > We ARE logged in!!!! Username: ' + userService.getUserName());
	                                        $log.debug('$stateChangeStart > ',toState);
	                                        if (accessLevel === ACCESS.AUTH) {
	                                            $log.debug("$stateChangeStart > We only need to be authenticated for this state. Go ahead");
	                                            preventedState = null;
	                                            // In case something goes wrong when fetching the data we could still be redirected to the login
	                                            // just for such a case lets store toState and toParams
	                                            allowedState = {state: toState, params: toParams};
	                                            //in the future we could decide that we do not allow public pages but for now...
	                                        } else {
	                                            $log.debug('$stateChangeStart > This route has some form of non public access restriction:', accessLevel);
	                                            // We need to check features and this means we need a user's context.
	                                            userService.findSelectedContext().then(
													// findSelectedContext promise success handler
													function(selectedContext) {
														$log.debug('$stateChangeStart > User context resolved', selectedContext);
														//now check access auth
														if (!userService.isAllowed(accessLevel)) {
															$log.debug('$stateChangeStart > Preventing access to ' + toState.name + '. It requires ' + accessLevel);

															preventedState = preventedState || {
																state : toState,
																params : toParams
															};
															event.preventDefault();
															error = 'Your credentials do not allow access to this page. Switch context or login with different credentials';
															$log.debug(error);

															$state.go(accessErrorState, {
																'toState' : toState,
																'toParams' : toParams,
																message : error
															}).then(
																function(successTransition) {},
																function(transitionError) {
																	$log.debug("$stateChangeStart > transistion to accessErrorState rejected with error ", transitionError);
																}
															);

														} else {
															$log.debug('$stateChangeStart > The logged-in user is allowed to access: ', accessLevel);

															preventedState = null;
															allowedState = {
																state : toState,
																params : toParams
															};
														}
													},
													// findSelectedContext promise error handler
													function (error) {
	                                                    $log.error('$stateChangeStart > context Resolution failed');
	                                                    //Let's handle this like a user missing the required feature
	                                                    $log.debug('$stateChangeStart > Preventing access to ' + toState.name + '. It requires ' + accessLevel);

	                                                    preventedState = preventedState || {state: toState, params: toParams};
	                                                    event.preventDefault();
	                                                    error = 'No selected user context. Select context or login with different credentials';
	                                                    $log.debug('$stateChangeStart > '+error);

	                                                    $state.go(accessErrorState, {
	                                                        'toState': toState,
	                                                        'toParams': toParams,
	                                                        message: error
	                                                    }).then(
	                                                        function (successTransition) {
	                                                        },
	                                                        function (transitionError) {
	                                                            $log.debug("$stateChangeStart > transistion to accessErrorState rejected with error ", transitionError);
	                                                        }
	                                                    );
	                                                }

	                                            );
	                                        }
	                                    }
	                                },
	                                function (error) {
	                                    $log.error('$stateChangeStart > user Resolution failed');
	                                }
	                            );
                            } // check if this state needs to be protected
                        } //if (toState.name !== logoutState)
                    }); // stateChangeStart
                }]); // $injector.invoke(['$state'...


                return {
                    getHome: _getHomeState,
                    getLogin: _getLoginState,
                    getLogout: function () {
                        return logoutState;
                    },
                    getAnon: function () {
                        return anonRoute;
                    }
                };
            };
            $get.$inject = ['$state', '$modal', '$rootScope', '$injector', '$timeout', '$location', '$window', 'userService', '$log', 'renewloginpanel', 'ACCESS', '$urlRouter'];
            this.$get = $get;

        }])
    .config(['$urlRouterProvider', '$stateProvider', 'ACCESS', function ($urlRouterProvider, $stateProvider, ACCESS) {


        // Prevent $urlRouter from automatically intercepting URL changes;
        // this allows you to configure custom behavior in between
        // location changes and route synchronization:
        $urlRouterProvider.deferIntercept();

        $stateProvider
            .state('jwtcallback', {
                url: '/jwtcallback',
                params: {
                    jwt: null
                },
                access: {data: ACCESS.PUBLIC},
                views: {
                    app: {
                        template: '<body><h2>You have successfully logged in.</h2>' +
                        '<p>This page will close automatically. If not please close it.<p>',
                        controller: ['userService', '$stateParams', function (userService, $stateParams) {
                            if ($stateParams.jwt) {
                                userService.login($stateParams.jwt);
                            }
                        }]
                    }
                }
            });
    }]);
