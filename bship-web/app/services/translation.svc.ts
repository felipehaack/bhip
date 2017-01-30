module App {

    'use strict'

    export class TranslationService {

        static id = "translationService";

        ok = 'Got it!'

        playerNotFoundTitle = 'Player not found :/'
        playerNotFoundMessage = 'The player was not found, maybe the hostname ins\'t correct?'

        gameWasCreatedTitle = 'The match has been created!'
        gameWasCreatedMessage = 'The match was created with success! Good luck!'

        autoPilotTitle = 'Auto pilot enabled!'
        autoPilotTitleMe = 'For auto pilot to work properly, make the first move!'

        autoPilotOpponent = 'Now, you can\'t interact anymore with the current game! :('

        fireTitle = 'The salvo => flies => kabum!'
        fireMessage = 'The salvo reached the enemy with success! Oh yeah!!!'

        salvoOverflow = 'No more shots!'
        salvoRemoved = 'Shot was removed!'
        
        constructor() {

        }
    }

    angular.module(Module).service(TranslationService.id, TranslationService);
}