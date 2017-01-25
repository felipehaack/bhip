module App {

  var app = angular.module(Module, [
    "ngAnimate",
    "ngCookies",
    "ngTouch",
    "ngSanitize",
    "ui.router",
    "ui.bootstrap",
    ngCommand.ModuleName
  ]);

  app.run(($rootScope: ng.IRootScopeService,
           config: Config,
           loggerFactory: ILoggerFactory,
           $state: angular.ui.IStateService) => {

    $rootScope.$on(config.events.uiRouter.$stateChangeError, (event: ng.IAngularEvent, toState: angular.ui.IState, toParams: any, fromState: angular.ui.IState, fromParams: any, error: Error) => {

    });
  });
}