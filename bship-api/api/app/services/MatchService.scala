package services

import javax.inject.{Inject, Singleton}

import models.{Board, Fire, Game, Player}
import play.api.libs.json.Json
import play.api.libs.ws._

import scala.concurrent.duration._
import utils.{Protocoler, Rules}

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.{Random, Try}

@Singleton
class MatchService @Inject()(
                              ws: WSClient,
                              gameService: GameService,
                              implicit val context: ExecutionContext
                            ) extends Protocoler with Rules {

  private val HEXADECIMAL = "0123456789ABCDEF"

  private def delay(dur: Deadline) = {

    Try(Await.ready(Promise().future, dur.timeLeft))
  }

  private def verifyPlayerTurn(gameId: String): Boolean = {

    gameService.findGameByGameId(gameId) match {
      case Some(game) => game.turn == game.me.userId && !game.finish
      case None => false
    }
  }

  private def findPositionFromAscii(char: Char): Int = {

    char match {
      case c if c >= 48 && c <= 57 => c - 48
      case c if c >= 65 && c <= 70 => (c - 65) + 10
    }
  }

  private def findTotalShipsAlive(player: Player): Int = {

    player.ships.map { ship =>

      ship.positions.length match {
        case 0 => 0
        case _ => 1
      }
    }.sum
  }

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

  private def shotBoard(salvo: Array[String], player: Player): List[(String, String)] = {

    var status = List[(String, String)]()

    var i = 0

    while (i < salvo.length) {

      val x = findPositionFromAscii(salvo(i)(0))
      val y = findPositionFromAscii(salvo(i)(2))

      player.board(y)(x) match {
        case c if c == Board.EMPTY =>
          player.board(y)(x) = Board.MISSED
          status ::=(salvo(i), Board.MISS)

        case c if c == Board.MISSED || c == Board.KILLED =>
          status ::=(salvo(i), Board.MISS)

        case Board.UNCHANGED =>
          player.board(y)(x) = Board.KILLED
          status ::=(salvo(i), Board.HIT)
      }

      i += 1
    }

    status
  }

  private def shotShips(status: List[(String, String)], player: Player): List[(String, String)] = {

    var localStatus = List[(String, String)]()

    var i = 0

    while (i < status.length) {

      status(i)._2 match {

        case Board.HIT =>

          val shot = status(i)._1

          val x = findPositionFromAscii(shot(0))
          val y = findPositionFromAscii(shot(2))

          player.ships.foreach { ship =>

            ship.positions = ship.positions.filter { pos =>
              pos._1 != x || pos._2 != y
            }

            ship.positions.isEmpty match {
              case true => localStatus ::=(shot, Board.KILL)
              case false => localStatus ::=(shot, Board.HIT)
            }
          }
        case _ => localStatus ::= status(i)
      }

      i += 1
    }

    localStatus
  }

  private def verifyReplicateShots(game: Game, salvo: List[(String, String)]): List[(Int, Int, Char)] = {

    salvo.map { shot =>

      shot._2 match {
        case r if r == Board.KILL || r == Board.HIT => (shot._1, shot._1, Board.KILLED)
        case _ => (shot._1, shot._1, Board.MISSED)
      }
    } map { shot =>

      val x = findPositionFromAscii(shot._1(0))
      val y = findPositionFromAscii(shot._2(2))

      (x, y, shot._3)
    } filter { shot =>

      var i = 0
      var isReplicate = false

      while (i < game.me.shots.length && !isReplicate) {

        val localShot = game.me.shots(i)

        (localShot._1, localShot._2) match {
          case (shot._1, shot._2) => isReplicate = true
          case _ =>
        }

        i += 1
      }

      isReplicate match {
        case false => true
        case true => false
      }
    }
  }

  private def autoPilotGenerator(game: Game) = {

    game.autopilot match {

      case true =>

        game.turn match {

          case game.me.userId =>

            val salvos = for {
              i <- 0 until game.shots
              x = HEXADECIMAL(Random.nextInt(HEXADECIMAL.length))
              y = HEXADECIMAL(Random.nextInt(HEXADECIMAL.length))
              s = s"${x}x${y}"
            } yield s

            Future {

              delay(3.seconds.fromNow)

              fire(game.id, Fire.Create(salvos.toArray)) map {

                case Some(result) => fireResult(game.id, result)
                case _ =>
              }
            }

          case _ =>
        }

      case _ =>
    }
  }

  def fire(gameId: String, salvos: Fire.Create): Future[Option[Fire.Result]] = {

    (isValidSalvos(salvos.salvo), verifyPlayerTurn(gameId)) match {

      case (true, true) =>

        gameService.findGameByGameId(gameId) match {

          case Some(game) =>

            val totalShips = findTotalShipsAlive(game.me)

            verifyShots(salvos.salvo, game.rules, totalShips, game.shots) match {

              case true =>

                val json = Json.toJson(salvos)

                val url = stringAsFire(game.protocol.hostname)(game.protocol.port)(gameId)

                ws.url(url).withRequestTimeout(8000.millis).put(json).map { response =>

                  response.json.validate[Fire.Result].asOpt
                } recover {

                  case _ => None
                }

              case false => Future(None)
            }

          case None => Future(None)
        }

      case _ => Future(None)
    }
  }

  def fireResult(gameId: String, result: Fire.Result): Any = {

    gameService.findGameByGameId(gameId) match {

      case Some(game) =>

        game.turn = result.game._2

        result.game._1 match {

          case c if c == Board.WON => game.finish = true
          case _ =>
        }

        isDesperation(game.rules) match {
          case true => game.shots += result.salvo.count(_._2 == Board.KILL)
          case _ =>
        }

        val localSalvo = verifyReplicateShots(game, result.salvo.toList)

        game.me.shots ++= localSalvo

        autoPilotGenerator(game)

      case None =>
    }
  }

  def fired(gameId: String, fire: Fire.Create): Option[Fire.Result] = {

    gameService.findGameByGameId(gameId) match {

      case Some(game) =>

        game.finish match {

          case false =>

            val salvo = fire.salvo

            val totalShips = findTotalShipsAlive(game.me)

            val statusBoard = shotBoard(salvo, game.me)
            val statusShips = shotShips(statusBoard, game.me)

            game.turn = game.me.userId

            //showOnConsolePlayersBoard(game)

            val turn = findTotalShipsAlive(game.me) match {

              case total if total > 0 =>

                isDesperation(game.rules) match {

                  case v if v && totalShips != total =>

                    game.turn = game.opponent.userId

                    (Board.PLAYER_TURN, game.turn)

                  case _ =>

                    isSuperChargerOrStandard(game.rules) match {
                      case true => game.shots = total
                      case _ =>
                    }

                    (Board.PLAYER_TURN, game.turn)
                }

              case _ =>

                game.finish = true
                game.turn = game.opponent.userId

                (Board.WON, game.turn)
            }

            autoPilotGenerator(game)

            Some(Fire.Result(statusShips, turn))

          case true => None
        }

      case _ => None
    }
  }
}
