module App {
  'use strict';

  export class HomeController {

    static id = "homeController"

    private title = "Battleship Heroes";
    private language: string;

    /*@ngInject*/
    constructor(private $state,
                private translationService: ITranslationService,
                private loggerFactory: ILoggerFactory,
                private config: Config) {

      this.title = "eu guys"
      this.language = "blabla"
    }

    bla(){
      this.$state.go("game")
    }
  }

  angular.module(Module).controller(HomeController.id, HomeController);
}