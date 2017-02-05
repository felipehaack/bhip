package services

import javax.inject.{Inject, Singleton}

import models.{Board, Fire, Game, Player}
import play.api.libs.json.Json
import play.api.libs.ws._

import scala.concurrent.duration._
import utils.{Linker, Rules}

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.util.{Random, Try}

@Singleton
class MatchService @Inject()(
                              ws: WSClient,
                              gameService: GameService,
                              implicit val context: ExecutionContext
                            ) extends Linker with Rules {

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

  private def isValidShots(shots: Array[String]): Boolean = {

    val result = for {
      i <- shots.indices
      if shots(i).length == 3 && isValidChar(shots(i)(0)) && isValidChar(shots(i)(2)) && shots(i)(1) == 'x'
    } yield i

    result.length match {
      case size if shots.length == size => true
      case _ => false
    }
  }

  private def shotBoard(shots: Array[String], player: Player): List[(String, String)] = {

    var status = List[(String, String)]()

    var i = 0

    while (i < shots.length) {

      val x = findPositionFromAscii(shots(i)(0))
      val y = findPositionFromAscii(shots(i)(2))

      player.board(y)(x) match {

        case c if c == Board.EMPTY =>
          player.board(y)(x) = Board.MISSED
          status ::= (shots(i), Board.MISS)

        case c if c == Board.MISSED || c == Board.KILLED =>
          status ::= (shots(i), Board.MISS)

        case Board.SHIP =>
          player.board(y)(x) = Board.KILLED
          status ::= (shots(i), Board.HIT)
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
              case true => localStatus ::= (shot, Board.KILL)
              case false => localStatus ::= (shot, Board.HIT)
            }
          }

        case _ => localStatus ::= status(i)
      }

      i += 1
    }

    localStatus
  }

  private def verifyReplicateShots(game: Game, shots: List[(String, String)]): List[(Int, Int, Char)] = {

    shots.map { shot =>

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

    game.autoPilot match {

      case true =>

        game.turn match {

          case game.me.userId =>

            val shots = for {
              i <- 0 until game.shots
              x = HEXADECIMAL(Random.nextInt(HEXADECIMAL.length))
              y = HEXADECIMAL(Random.nextInt(HEXADECIMAL.length))
              s = s"${x}x${y}"
            } yield s

            Future {

              delay(3.seconds.fromNow)

              fire(game.id, Fire.Create(shots.toArray)) map {

                case Some(result) => fireResult(game.id, result)
                case _ =>
              }
            }

          case _ =>
        }

      case _ =>
    }
  }

  def fire(gameId: String, shots: Fire.Create): Future[Option[Fire.Result]] = {

    (isValidShots(shots.shots), verifyPlayerTurn(gameId)) match {

      case (true, true) =>

        gameService.findGameByGameId(gameId) match {

          case Some(game) =>

            val totalShips = findTotalShipsAlive(game.me)

            verifyShots(shots.shots, game.rule, totalShips, game.shots) match {

              case true =>

                val json = Json.toJson(shots)

                val url = stringAsFire(game.connection.host)(game.connection.port)(gameId)

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

        game.turn = result.status._2

        result.status._1 match {

          case c if c == Board.WON => game.finish = true
          case _ =>
        }

        isIncrementer(game.rule) match {
          case true => game.shots += result.shots.count(_._2 == Board.KILL)
          case _ =>
        }

        val localSalvo = verifyReplicateShots(game, result.shots.toList)

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

            val shots = fire.shots

            val totalShips = findTotalShipsAlive(game.me)

            val statusBoard = shotBoard(shots, game.me)
            val statusShips = shotShips(statusBoard, game.me)

            game.turn = game.me.userId

            val turn = findTotalShipsAlive(game.me) match {

              case total if total > 0 =>

                isIncrementer(game.rule) match {

                  case v if v && totalShips != total =>

                    game.turn = game.opponent.userId

                    (Board.TURN, game.turn)

                  case _ =>

                    isDecrementer(game.rule) match {
                      case true => game.shots = total
                      case _ =>
                    }

                    (Board.TURN, game.turn)
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
