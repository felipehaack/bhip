module App {

    let app = angular.module(Module, [
        'ngAnimate',
        'ngCookies',
        'ngSanitize',
        'ui.router',
        'ngMaterial',
        'ngMessages'
    ]);

    app.run((config: Config,
             $rootScope: ng.IRootScopeService,
             $state: angular.ui.IStateService) => {

    });
}