package api

import play.api.mvc.Action
import javax.inject.{Inject, Singleton}

import models.Fire
import utils.Protocol
import services.MatchService

@Singleton
class MatchApi @Inject()(matchService: MatchService) extends Api with Protocol {

  def fire(gameId: String) = Action.async(json[Fire.Create]) { implicit request =>

    matchService.fire(gameId, request.body) map {

      case Some(result) =>

        matchService.fireResult(gameId, result)

        Ok.asJson(result)
      case None => BadRequest
    }
  }

  def fired(gameId: String) = Action(json[Fire.Create]) { implicit request =>

    matchService.fired(gameId, request.body) match {

      case Some(result) => Ok.asJson(result)
      case None => BadRequest
    }
  }
}
