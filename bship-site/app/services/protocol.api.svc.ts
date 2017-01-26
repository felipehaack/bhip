module App {

    export class ProtocolApiService {

        static id = "protocolApiService";

        constructor() {

        }
    }

    angular.module(App.Module).service(ProtocolApiService.id, ProtocolApiService);
}