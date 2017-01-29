module App {

    'use strict'

    export class TranslationService {

        static id = "translationService";

        ok = 'Got it!'

        playerNotFoundTitle = 'Player not found :/'
        playerNotFoundMessage = 'The player was not found, may the hostname ins\'t correct?'

        gameWasCreatedTitle = 'The match created!'
        gameWasCreatedMessage = 'The match was created with success! Good lucky!'

        autoPilotTitle = 'Auto pilot enabled!'
        autoPilotTitleMe = 'To auto pilot works properly, make the first move!'
        autoPilotOpponent = 'Now, you can\'t anymore interact with the current game! :('

        fireTitle = 'The salvo => fly => kabum!'
        fireMessage = 'The salvo was reached the enemy with success! Oh yeah!!!'

        salvoOverflow = 'No more shots!'
        salvoRemoved = 'Shot was removed!'

        constructor() {

        }
    }

    angular.module(Module).service(TranslationService.id, TranslationService);
}