package api.controllers

import javax.inject.{Inject, Singleton}

import api.models.Game
import api.services.GameService
import play.api.mvc.Action

@Singleton
class GameApi @Inject()(matchService: GameService) extends Api {

  def register = Action(json[Game.Create]) { implicit request =>

    val result = matchService.register(request.body)

    Ok.asJson(result)
  }
}
