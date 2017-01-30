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
        protected currentSalvo: string[] = new Array()

        protected searchingPlayer: boolean
        protected shootingPlayer: boolean = true

        /*@ngInject*/
        constructor(private $scope,
                    private $mdToast,
                    private $interval,
                    private $mdDialog,
                    private $mdSidenav,
                    private userApiService: UserApiService,
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

            this.userApiService.getAllGames().then(data => {

                this.allGames = data

                if (this.currentGame) {

                    this.currentGame = this.allGames.filter(game => game.game_id === this.currentGame.game_id)[0]
                }
            })
        }

        findCurrentGame() {

            if (this.currentGame) {

                this.userApiService.getGameProgress(this.currentGame.game_id)
                    .then((data: IGameProgress) => {
                        this.createBoard(data)
                    })
            }
        }

        createBoard(boardInfo: IGameProgress) {

            let key = Object.keys(boardInfo.game)[0]
            let userId = boardInfo.game[key]

            this.currentPlayer = userId === boardInfo.self.user_id
            this.currentBoard = boardInfo.self.board
            this.currentBoardShoot = boardInfo.opponent.board
        }

        setCurrentGame(game: IGameStatus) {

            this.currentGame = game
            this.currentSalvo = new Array()

            this.toggleMenu()
            this.findCurrentGame()
        }

        setShot(x: number, y: number) {

            if (this.currentGame.turn !== this.currentGame.opponent_id) {

                let localShot = `${x},${y}`

                if (this.currentSalvo.length < this.currentGame.shots) {

                    let exist = this.currentSalvo.filter(shot => shot === localShot).length

                    if (exist === 0) {

                        this.currentSalvo.push(localShot)

                        this.showSimpleToast(this.currentSalvo.length + ' / ' + this.currentGame.shots)
                    } else {

                        this.currentSalvo = this.currentSalvo.filter(shot => shot !== localShot)

                        this.showSimpleToast(this.translationService.salvoRemoved)
                    }
                } else {

                    let exist = this.currentSalvo.filter(shot => shot === localShot).length

                    if (exist === 1) {

                        this.currentSalvo = this.currentSalvo.filter(shot => shot !== localShot)

                        this.showSimpleToast(this.translationService.salvoRemoved)
                    } else {

                        this.showSimpleToast(this.translationService.salvoOverflow)
                    }

                }
            }
        }

        isShotSelected(x: number, y: number): boolean {

            let localShot = `${x},${y}`

            return this.currentSalvo.filter(shot => shot === localShot).length === 1 ? true : false
        }

        sendSalvo() {

            this.shootingPlayer = false

            let newSalvo = this.currentSalvo.map(shot => {

                let localShot = shot.split(",")

                let x = this.letters[parseInt(localShot[0], 10)]
                let y = this.letters[parseInt(localShot[1], 10)]

                return `${x}x${y}`
            })

            let salvo: ISalvo = {
                salvo: newSalvo
            }

            this.userApiService.fire(this.currentGame.game_id, salvo)
                .then(() => {

                    this.findNewGames()
                    this.findCurrentGame()

                    this.currentSalvo = new Array()

                    this.alertDialog(null,
                        this.translationService.fireTitle,
                        this.translationService.fireMessage,
                        this.translationService.ok)
                }).finally(() => this.shootingPlayer = true)
        }

        enableAutoPilot(game: IGameStatus) {

            this.setCurrentGame(game)

            this.userApiService.enableAutoPilot(game.game_id).then(data => {

                this.findNewGames()

                let message = this.currentGame.opponent_id.indexOf(this.currentGame.turn) > -1
                    ? this.translationService.autoPilotOpponent
                    : this.translationService.autoPilotTitleMe

                this.alertDialog(null,
                    this.translationService.autoPilotTitle,
                    message,
                    this.translationService.ok)
            })
        }

        challengePlayer(challenge: IChallenge) {

            this.searchingPlayer = true

            this.userApiService.challenge(challenge)
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
                    rules: '',
                    spaceship_protocol: {}
                }

                if (result.shots) {

                    challenge.rules = result.shots + result.challenge.rules.replace('x', '')
                } else {

                    challenge.rules = result.challenge.rules
                }

                let protocol: IProtocol = <IProtocol>{}

                protocol.port = parseInt(result.challenge.spaceship_protocol.port, 10)
                protocol.hostname = result.challenge.spaceship_protocol.hostname

                challenge.spaceship_protocol = protocol

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