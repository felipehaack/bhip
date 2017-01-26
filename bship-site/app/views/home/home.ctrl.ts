module App {

    'use strict';

    export class HomeController {

        static id = "homeController"

        /*@ngInject*/
        constructor(private $state,
                    private userApiService: UserApiService) {

        }
    }

    angular.module(Module).controller(HomeController.id, HomeController);
}