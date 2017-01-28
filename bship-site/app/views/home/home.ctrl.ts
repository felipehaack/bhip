module App {

    'use strict';

    export class HomeController {

        static id = 'homeController'

        /*@ngInject*/
        constructor(private $state,
                    private $mdDialog,
                    private $scope,
                    private $mdSidenav,
                    private userApiService: UserApiService) {

        }

        openMenu() {

            this.$mdSidenav('left').toggle().then(function () {

            });
        }

        alertDialog(ev, title, message) {

            this.$mdDialog.show(
                this.$mdDialog.alert()
                    .parent(angular.element(document.querySelector('#popupContainer')))
                    .clickOutsideToClose(true)
                    .title('This is an alert title')
                    .textContent('You can specify some description text in here.')
                    .ariaLabel('Alert Dialog Demo')
                    .ok('Got it!')
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
            }).then(function (challengeModel) {

            }, function () {

            });
        }

        /*@ngInject*/
        ChallengeController($scope, $mdDialog) {

            $scope.hide = function () {

                $mdDialog.hide();
            };

            $scope.cancel = function () {

                $mdDialog.cancel();
            };

            $scope.letsChallenge = function (data) {

                $mdDialog.hide(data);
            };
        }
    }

    angular.module(Module).controller(HomeController.id, HomeController);
}