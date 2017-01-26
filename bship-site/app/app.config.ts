module App {

    let app = angular.module(Module);

    export class Config {

        static id: string = "config";

        protected name: string = "battleship";
        protected version: string = "1.0";

        constructor() {

        }
    }

    app.constant(Config.id, new Config());

    app.config(($logProvider: ng.ILogProvider, $locationProvider: ng.ILocationProvider) => {

        if ($logProvider.debugEnabled) {

            $logProvider.debugEnabled(true);
        }
    });
}