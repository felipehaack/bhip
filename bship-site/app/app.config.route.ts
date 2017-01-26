module App {

    angular.module(Module)
        .config(
            ["$stateProvider",
                "$urlRouterProvider",
                "$locationProvider",
                ($locationProvider: ng.ILocationProvider,
                 $stateProvider: angular.ui.IStateProvider) => {

                    $stateProvider.state("home", {
                        url: "/",
                        templateUrl: "views/home/home.html",
                        controller: HomeController.id,
                        controllerAs: "vm"
                    });

                    $stateProvider.state("game", {
                        url: "game",
                        templateUrl: "views/game/game.html",
                        controller: BasicFormController.id,
                        controllerAs: "vm"
                    });

                    //$urlRouterProvider.otherwise("");

                    $locationProvider.html5Mode(true)
                }
            ]
        );
}