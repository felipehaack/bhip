module App {

    interface IGameStatus {
        opponent_id: string,
        full_name: string,
        game_id: string,
        finished: boolean,
        autopilot: boolean,
        shots: number
    }

    interface IGameProgress {
        self: IPlayerProgress,
        opponent: IPlayerProgress,
        game: any
    }

    interface IPlayerProgress {
        user_id: string,
        board: string[]
    }

    interface IChallenge {
        rules: string,
        spaceship_protocol: IProtocol
    }

    interface IProtocol {
        hostname: string,
        port: number
    }

    interface ISalvo {
        salvo: string[]
    }

    interface ISalvoResult {
        salvo: any[]
        game: any
    }

    export class UserApiService {

        static id = "userApiService";

        /*@ngInject*/
        constructor(private $http: ng.IHttpService) {

        }

        getAllGames(): ng.IPromise<IGameStatus> {

            return this.$http.get(App.URL + App.USER_API + `/games`).then(toData)
        }

        getGameProgress(gameId: string): ng.IPromise<IGameProgress> {

            return this.$http.get(App.URL + App.USER_API + `/game/${gameId}`).then(toData)
        }

        enableAutoPilot(gameId: string): ng.IPromise<any> {

            return this.$http.post(App.URL + App.USER_API + `/game/${gameId}/auto`, null).then(toData)
        }

        challenge(input: IChallenge): ng.IPromise<IGameProgress> {

            return this.$http.post(App.URL + App.USER_API + `/game/new`, input).then(toData)
        }

        fire(gameId: string, salvo: ISalvo): ng.IPromise<ISalvoResult> {

            return this.$http.put(App.URL + App.USER_API + `/game/${gameId}/fire`, salvo).then(toData)
        }
    }

    angular.module(App.Module).service(UserApiService.id, UserApiService);
}