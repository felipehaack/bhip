module App {

    angular.module(Module)
        .config(["$locationProvider", "$stateProvider",
                ($locationProvider: ng.ILocationProvider, $stateProvider: angular.ui.IStateProvider) => {

                    $stateProvider.state("home", {
                        url: "/",
                        templateUrl: "views/home/home.html",
                        controller: HomeController.id,
                        controllerAs: "vm"
                    });

                    $locationProvider.html5Mode(true)
                }
            ]
        );
}