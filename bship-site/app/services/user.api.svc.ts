module App {

    export class UserApiService2 {

        static id = "userApiService";

        constructor() {

        }
    }

    angular.module(App.Module).service(UserApiService.id, UserApiService);
}