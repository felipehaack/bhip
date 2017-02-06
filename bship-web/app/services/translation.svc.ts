module App {

    'use strict'

    export class TranslationService {

        static id = "translationService";

        ok = 'Got it!'

        playerNotFoundTitle = 'Player not found :/'
        playerNotFoundMessage = 'The player was not found, maybe the host ins\'t correct?'

        gameWasCreatedTitle = 'The match has been created!'
        gameWasCreatedMessage = 'The match was created with success! Good luck!'

        autoPilotTitle = 'Auto pilot enabled!'
        autoPilotTitleMe = 'For auto pilot to work properly, make the first move!'

        autoPilotEnabled = 'The auto pilot is already enabled!'
        autoPilotOpponent = 'Now, you can\'t interact anymore with the current game! :('

        fireTitle = 'The shots => flies => kabum!'
        fireMessage = 'The shots reached the enemy with success! Oh yeah!!!'

        shotsOverflow = 'No more shots!'
        shotsRemoved = 'Shot was removed!'
        
        constructor() {

        }
    }

    angular.module(Module).service(TranslationService.id, TranslationService);
}