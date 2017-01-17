package api

import javax.inject.{Inject, Singleton}

import models.Game
import play.api.mvc.Action
import services.GameService

@Singleton
class GameApi @Inject()(matchService: GameService) extends Api {

  def register = Action(json[Game.Create]) { implicit request =>

    val result = matchService.register(request.body)

    Ok.asJson(result)
  }
}
