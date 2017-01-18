package api

import javax.inject.{Inject, Singleton}

import models.Game
import play.api.mvc.Action
import services.GameService

@Singleton
class GameApi @Inject()(gameService: GameService) extends Api {

  def register = Action(json[Game.Create]) { implicit request =>

    val result = gameService.register(request.body)

    Ok.asJson(result)
  }

  def progress(gameId: Int) = Action { implicit request =>

    gameService.gameBoard(gameId) match {

      case Some(result) => Ok.asJson(result)
      case None => NotFound
    }
  }

  def enableAutoPilot(gameId: Int) = Action { implicit request =>

    gameService.enableAutoPilot(gameId) match {

      case true => Ok
      case false => BadRequest
    }
  }
}
