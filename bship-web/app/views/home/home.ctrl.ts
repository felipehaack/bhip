module App {

    'use strict'

    export class HomeController {

        static id = 'homeController'

        protected letters = new Array("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F")

        protected allGames: IGameStatus[]

        protected currentGame: IGameStatus
        protected currentBoard: string[]
        protected currentBoardShoot: string[]

        protected currentGamesTimer: any
        protected currentGameTimer: any

        protected currentPlayer: boolean = false
        protected currentShots: string[] = new Array()

        protected searchingPlayer: boolean
        protected shootingPlayer: boolean = true

        /*@ngInject*/
        constructor(private $scope,
                    private $mdToast,
                    private $interval,
                    private $mdDialog,
                    private $mdSidenav,
                    private playerApiService: PlayerApiService,
                    private translationService: TranslationService) {

            this.findNewGames()
            this.bindTimeMethods()
        }

        bindTimeMethods() {

            this.$interval(function () {
                this.findNewGames()
            }.bind(this), 10 * 1000)

            this.$interval(function () {
                this.findCurrentGame()
            }.bind(this), 5 * 1000)
        }

        findNewGames() {

            this.playerApiService.getAllGames().then(data => {

                this.allGames = data

                if (this.currentGame) {

                    this.currentGame = this.allGames.filter(game => game.game_id === this.currentGame.gameId)[0]
                }
            })
        }

        findCurrentGame() {

            if (this.currentGame) {

                this.playerApiService.getGameProgress(this.currentGame.gameId)
                    .then((data: IGameProgress) => {
                        this.createBoard(data)
                    })
            }
        }

        createBoard(boardInfo: IGameProgress) {

            let key = Object.keys(boardInfo.turn)[0]
            let userId = boardInfo.turn[key]

            this.currentPlayer = userId === boardInfo.me.userId
            this.currentBoard = boardInfo.me.board
            this.currentBoardShoot = boardInfo.opponent.board
        }

        setCurrentGame(game: IGameStatus) {

            this.currentGame = game
            this.currentShots = new Array()

            this.toggleMenu()
            this.findCurrentGame()
        }

        setShot(x: number, y: number) {

            if (this.currentGame.turn !== this.currentGame.opponentId) {

                let localShot = `${x},${y}`

                if (this.currentShots.length < this.currentGame.shots) {

                    let exist = this.currentShots.filter(shot => shot === localShot).length

                    if (exist === 0) {

                        this.currentShots.push(localShot)

                        this.showSimpleToast(this.currentShots.length + ' / ' + this.currentGame.shots)
                    } else {

                        this.currentShots = this.currentShots.filter(shot => shot !== localShot)

                        this.showSimpleToast(this.translationService.shotsRemoved)
                    }
                } else {

                    let exist = this.currentShots.filter(shot => shot === localShot).length

                    if (exist === 1) {

                        this.currentShots = this.currentShots.filter(shot => shot !== localShot)

                        this.showSimpleToast(this.translationService.shotsRemoved)
                    } else {

                        this.showSimpleToast(this.translationService.shotsOverflow)
                    }

                }
            }
        }

        isShotSelected(x: number, y: number): boolean {

            let localShot = `${x},${y}`

            return this.currentShots.filter(shot => shot === localShot).length === 1
        }

        sendShots() {

            this.shootingPlayer = false

            let newShots = this.currentShots.map(shot => {

                let localShot = shot.split(",")

                let x = this.letters[parseInt(localShot[0], 10)]
                let y = this.letters[parseInt(localShot[1], 10)]

                return `${x}x${y}`
            })

            let salvo: IShots = {
                shots: newShots
            }

            this.playerApiService.fire(this.currentGame.gameId, salvo)
                .then(() => {

                    this.findNewGames()
                    this.findCurrentGame()

                    this.currentShots = new Array()

                    this.alertDialog(null,
                        this.translationService.fireTitle,
                        this.translationService.fireMessage,
                        this.translationService.ok)
                }).finally(() => this.shootingPlayer = true)
        }

        enableAutoPilot(game: IGameStatus) {

            if (!game.autoPilot) {
                this.setCurrentGame(game)

                this.playerApiService.enableAutoPilot(game.gameId).then(data => {

                    this.findNewGames()

                    let message = this.currentGame.opponentId.indexOf(this.currentGame.turn) > -1
                        ? this.translationService.autoPilotOpponent
                        : this.translationService.autoPilotTitleMe

                    this.alertDialog(null,
                        this.translationService.autoPilotTitle,
                        message,
                        this.translationService.ok)
                })
            } else {

                this.alertDialog(null,
                    this.translationService.autoPilotTitle,
                    this.translationService.autoPilotEnabled,
                    this.translationService.ok)
            }
        }

        challengePlayer(challenge: IChallenge) {

            this.searchingPlayer = true

            this.playerApiService.challenge(challenge)
                .then((data: IGameStatus) => {

                    this.alertDialog(null,
                        this.translationService.gameWasCreatedTitle,
                        this.translationService.gameWasCreatedMessage,
                        this.translationService.ok)

                    this.setCurrentGame(data)
                    this.findCurrentGame()
                })
                .catch(() => {
                        this.alertDialog(null,
                            this.translationService.playerNotFoundTitle,
                            this.translationService.playerNotFoundMessage,
                            this.translationService.ok)
                    }
                ).finally(() => this.searchingPlayer = false)
        }

        toggleMenu() {

            this.$mdSidenav('left').toggle();
        }

        alertDialog(ev, title, content, ok) {

            this.$mdDialog.show(
                this.$mdDialog.alert()
                    .parent(angular.element(document.querySelector('#popupContainer')))
                    .clickOutsideToClose(true)
                    .title(title)
                    .textContent(content)
                    .ok(ok)
                    .targetEvent(ev)
            );
        }

        challengeDialog(ev) {

            this.$mdDialog.show({
                controllerAs: 'vm',
                controller: this.ChallengeController,
                templateUrl: 'views/templates/challenge.tmpl.html',
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose: true,
                fullscreen: this.$scope.customFullscreen
            }).then(function (result: any) {

                let challenge: IChallenge = <IChallenge>{
                    rule: '',
                    connection: {}
                }

                if (result.shots) {

                    challenge.rule = result.shots + result.challenge.rules.replace('x', '')
                } else {

                    challenge.rule = result.challenge.rules
                }

                let connection: IConnection = <IConnection>{}

                connection.port = parseInt(result.challenge.connection.port, 10)
                connection.host = result.challenge.connection.host

                challenge.connection = connection

                this.challengePlayer(challenge)
            }.bind(this));
        }

        showSimpleToast(message) {

            this.$mdToast.show(
                this.$mdToast.simple()
                    .textContent(message)
                    .position('bottom right')
                    .hideDelay(500)
            );
        };

        /*@ngInject*/
        ChallengeController($scope, $mdDialog) {

            $scope.vm.challenge = <IChallenge>{}

            $scope.cancel = function () {

                $mdDialog.cancel();
            };

            $scope.challenge = function (challenge: IChallenge, shots: number) {

                let result = {
                    challenge: challenge,
                    shots: shots
                }

                $mdDialog.hide(result);
            };
        }
    }

    angular.module(Module).controller(HomeController.id, HomeController);
}