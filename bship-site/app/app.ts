module App {

    let app = angular.module(Module, [
        'ngAnimate',
        'ngCookies',
        'ngSanitize',
        'ui.router',
        'ngMaterial',
        'ngMessages'
    ]);

    app.run(($rootScope: ng.IRootScopeService,
             config: Config,
             loggerFactory: ILoggerFactory,
             $state: angular.ui.IStateService) => {

    });
}