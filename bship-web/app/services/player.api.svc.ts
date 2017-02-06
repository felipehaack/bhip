module App {

    'use strict'

    export interface IGameStatus {
        opponentId: string,
        fullName: string,
        gameId: string,
        finished: boolean,
        autoPilot: boolean,
        shots: number
        turn: string
    }

    export interface IGameProgress {
        me: IPlayerProgress,
        opponent: IPlayerProgress,
        turn: any
    }

    export interface IPlayerProgress {
        userId: string,
        board: string[]
    }

    export interface IChallenge {
        rule: string,
        connection: IConnection
    }

    export interface IConnection {
        host: string,
        port: number
    }

    export interface IShots {
        shots: string[]
    }

    export interface IShotsResult {
        shots: any[]
        status: any
    }

    export class PlayerApiService {

        static id = "playerApiService";

        /*@ngInject*/
        constructor(private $http: ng.IHttpService) {

        }

        getAllGames(): ng.IPromise<IGameStatus[]> {

            return this.$http.get(App.URL + App.PLAYER_API + `/games`).then(toData)
        }

        getGameProgress(gameId: string): ng.IPromise<IGameProgress> {

            return this.$http.get(App.URL + App.PLAYER_API + `/game/${gameId}`).then(toData)
        }

        enableAutoPilot(gameId: string): ng.IPromise<any> {

            return this.$http.post(App.URL + App.PLAYER_API + `/game/${gameId}/auto`, {}).then(toData)
        }

        challenge(input: IChallenge): ng.IPromise<IGameStatus> {

            return this.$http.post(App.URL + App.PLAYER_API + `/game/new`, input).then(toData)
        }

        fire(gameId: string, shots: IShots): ng.IPromise<IShotsResult> {

            return this.$http.put(App.URL + App.PLAYER_API + `/game/${gameId}/fire`, shots).then(toData)
        }
    }

    angular.module(Module).service(PlayerApiService.id, PlayerApiService);
}