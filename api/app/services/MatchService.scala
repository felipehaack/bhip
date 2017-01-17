package services

import javax.inject.{Inject, Singleton}

import models.{Fire, Game, Player}
import models.Player._

@Singleton
class MatchService @Inject()(gameService: GameService) {

  def verifyPlayerTurn(gameId: Int, turn: Turn.Value): Boolean = {

    val matches = gameService.matches

    val found = for {
      i <- matches.indices
      if matches(i).id == gameId && (turn match {
        case Turn.Me => matches(i).turn == matches(i).me.userId
        case Turn.Opponent => matches(i).turn == matches(i).opponent.userId
      })
    } yield i

    found.nonEmpty
  }

  private def getPositionFromAscii(char: Char): Int = {

    char match {
      case c if c >= 48 && c <= 57 => c - 48
      case c if c >= 65 && c <= 70 => (c - 65) + 10
    }
  }

  private def getTotalShipsAlive(player: Player): Int = {

    var i = 0

    player.ships.foreach { ship =>

      if (ship.positions.nonEmpty) {
        i += 1
      }
    }

    i
  }

  private def changeTurn(index: Int, turn: Turn.Value): (Player, Player) = {

    val matches = gameService.matches(index)

    turn match {
      case Turn.Me =>
        gameService.matches(index).turn = matches.opponent.userId
        (matches.opponent, matches.me)
      case Turn.Opponent =>
        gameService.matches(index).turn = matches.me.userId
        (matches.me, matches.opponent)
    }
  }

  private def shotBoard(salvo: Array[String], player: Player, totalShips: Int): List[(String, String)] = {

    var status = List[(String, String)]()

    var i = 0

    while (i < salvo.length) {

      if (i < totalShips) {

        val x = getPositionFromAscii(salvo(i)(0))
        val y = getPositionFromAscii(salvo(i)(2))

        player.board(y)(x) match {
          case c if c == '.' =>
            player.board(y)(x) = '-'
            status ::=(salvo(i), "miss")

          case c if c == '-' || c == 'X' =>
            status ::=(salvo(i), "miss")

          case '*' =>
            player.board(y)(x) = 'X'
            status ::=(salvo(i), "hit")
        }
      } else {

        status ::=(salvo(i), "miss")
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

        case "hit" =>

          val shot = status(i)._1

          val x = getPositionFromAscii(shot(0))
          val y = getPositionFromAscii(shot(2))

          player.ships.foreach { ship =>

            ship.positions = ship.positions.filter { pos =>
              pos._1 != x || pos._2 != y
            }

            ship.positions.isEmpty match {
              case true => localStatus ::=(shot, "kill")
              case false => localStatus ::=(shot, "hit")
            }
          }
        case _ => localStatus ::= status(i)
      }

      i += 1
    }

    localStatus
  }

  private def showOnConsolePlayersBoard(game: Game) = {

    gameService.showBoardOnConsole(game.me.board)
    gameService.showBoardOnConsole(game.opponent.board)
  }

  def damage(gameId: Int, salvos: Fire.Create, turn: Turn.Value): Option[Fire.Result] = {

    val salvo = salvos.salvo

    val matches = gameService.matches

    val found = for {
      i <- matches.indices
      if matches(i).id == gameId
    } yield i

    found.isEmpty match {

      case false =>

        val index = found(0)
        val game = matches(index)

        game.finish match {

          case false =>

            val (toDamage, fromDamage) = changeTurn(index, turn)

            val totalShips = getTotalShipsAlive(toDamage)

            val statusBoard = shotBoard(salvo, toDamage, totalShips)
            val statusShips = shotShips(statusBoard, toDamage)

            showOnConsolePlayersBoard(game)

            getTotalShipsAlive(toDamage) match {

              case t if t > 0 =>

                Some(Fire.Result(statusShips, ("player_turn", toDamage.userId)))

              case _ =>

                game.finish = true
                game.turn = fromDamage.userId

                Some(Fire.Result(statusShips, ("won", fromDamage.userId)))
            }

          case true => None
        }

      case true => None
    }
  }
}
