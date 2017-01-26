module App {

    let app = angular.module(Module, [
        "ngAnimate",
        "ngCookies",
        "ngTouch",
        "ngSanitize",
        "ui.router",
        "ui.bootstrap"
    ]);

    app.run(($rootScope: ng.IRootScopeService,
             config: Config,
             loggerFactory: ILoggerFactory,
             $state: angular.ui.IStateService) => {

    });
}