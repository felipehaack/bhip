package api

import play.api.mvc.Action
import javax.inject.{Inject, Singleton}

import models.Fire
import models.Player._

import services.MatchService

@Singleton
class MatchApi @Inject()(matchService: MatchService) extends Api {

  private def isValidChar(char: Char): Boolean = {

    char match {
      case c if c >= 48 && c <= 57 || c >= 65 && c <= 70 => true
      case _ => false
    }
  }

  private def isValidSalvos(salvos: Array[String]): Boolean = {

    val result = for {
      i <- salvos.indices
      if salvos(i).length == 3 && isValidChar(salvos(i)(0)) && isValidChar(salvos(i)(2)) && salvos(i)(1) == 'x'
    } yield i

    result.length match {
      case size if salvos.length == size => true
      case _ => false
    }
  }

  def fire(gameId: Int) = Action(json[Fire.Create]) { implicit request =>

    val fire = request.body

    isValidSalvos(fire.salvo) match {

      case true => matchService.verifyPlayerTurn(gameId, Turn.Me) match {

        case true =>

          matchService.damage(gameId, fire, Turn.Me) match {

            case Some(r) => Ok.asJson(r)
            case None => BadRequest
          }

        case false => BadRequest
      }

      case false => BadRequest
    }
  }

  def fired(gameId: Int) = Action(json[Fire.Create]) { implicit request =>

    val fire = request.body

    isValidSalvos(fire.salvo) match {

      case true =>

        matchService.verifyPlayerTurn(gameId, Turn.Opponent) match {

          case true =>

            matchService.damage(gameId, fire, Turn.Opponent) match {

              case Some(r) => Ok.asJson(r)
              case None => BadRequest
            }

          case false => BadRequest
        }

      case false => BadRequest
    }
  }
}
