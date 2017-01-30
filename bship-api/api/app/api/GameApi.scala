package api

import javax.inject.{Inject, Singleton}

import models.Game
import play.api.mvc.Action
import services.GameService
import utils.Protocoler

@Singleton
class GameApi @Inject()(gameService: GameService) extends Api with Protocoler {

  def challenge = Action.async(json[Game.Challenge]) { implicit request =>

    gameService.challenge(request.body) map {

      case Some(result) =>

        gameService.registerChallenge(result, request.body.spaceship_protocol)

        Ok.asJson(result)
      case None => BadRequest
    }
  }

  def register = Action(json[Game.Create]) { implicit request =>

    val result = gameService.register(request.body)

    Ok.asJson(result)
  }

  def progress(gameId: String) = Action { implicit request =>

    gameService.gameBoard(gameId) match {

      case Some(result) => Ok.asJson(result)
      case None => NotFound
    }
  }

  def status = Action { implicit request =>

    Ok.asJson(gameService.status())
  }

  def enableAutoPilot(gameId: String) = Action { implicit request =>

    gameService.enableAutoPilot(gameId) match {

      case true => Ok
      case false => NotFound
    }
  }
}
