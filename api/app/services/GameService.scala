package services

import javax.inject.Singleton

import models.{Game, Player, Ship, Board}

import scala.util.Random

@Singleton
class GameService {

  private val MATCH = "match"
  private val PLAYER = "player"
  private val FULLNAME = "Assessment Player"

  var matches = List[Game]()

  private def createBoard(ships: List[Ship]): Array[Array[Char]] = {

    val rowsSeq = for {
      i <- 0 until Board.MATRIX
      row = for {
        j <- 0 until Board.MATRIX
      } yield Board.Ship.DEFAULT
    } yield row.toArray

    val rows = rowsSeq.toArray

    ships.flatMap(b => b.positions).sorted.foreach { pos =>
      rows(pos._2)(pos._1) = Board.Ship.MARKER
    }

    rows
  }

  private def isOverlap(ships: List[Ship], attempt: Ship): Boolean = {

    ships.length match {

      case num if num > 0 =>

        val size = attempt.size
        val position = attempt.start

        var i = 0

        while (i < ships.length) {

          val localSize = ships(i).size
          val localPosition = ships(i).start

          if (position._1 < localPosition._1 + localSize._1 &&
            position._1 + size._1 > localPosition._1 &&
            position._2 < localPosition._2 + localSize._2 &&
            position._2 + size._2 > localPosition._2) {

            return true
          }

          i += 1
        }

        false
      case _ => false
    }
  }

  private def rotateShip(ship: Ship): Ship = {

    val result: (List[(Int, Int)], (Int, Int)) = Random.nextInt(Ship.MAX_POSE) match {
      case 0 => (ship.positions, ship.size)
      case 1 =>
        (ship.positions.map { pos =>
          ((ship.size._1 - 1) - pos._1, (ship.size._2 - 1) - pos._2)
        }, ship.size)
      case 2 =>
        (ship.positions.map { pos =>
          ((ship.size._2 - 1) - pos._2, pos._1)
        }, (ship.size._2, ship.size._1))
      case 3 =>
        (ship.positions.map { pos =>
          (pos._2, (ship.size._1 - 1) - pos._1)
        }, (ship.size._2, ship.size._1))
    }

    ship.copy(positions = result._1, size = result._2)
  }

  private def generateShips(): List[Ship] = {

    var ships = List[Ship]()

    while (ships.length < Ship.All.length) {

      val rotatedShip = rotateShip(Ship.All(ships.length))

      val x = Random.nextInt(Board.MATRIX - rotatedShip.size._1)
      val y = Random.nextInt(Board.MATRIX - rotatedShip.size._2)

      val newPosition = rotatedShip.positions.map(pos => (pos._1 + x, pos._2 + y))

      val attemptShip = rotatedShip.copy(start = (x, y), positions = newPosition)

      isOverlap(ships, attemptShip) match {

        case false =>

          ships :::= List(attemptShip)

        case true =>

          ships = List[Ship]()
      }
    }

    ships
  }

  def findGameByGameId(gameId: Int): Option[Game] = {

    val found = for {
      i <- matches.indices
      if matches(i).id == gameId
    } yield i

    found.length match {

      case 1 => Some(matches(found(0)))
      case _ => None
    }
  }

  def showBoardOnConsole(board: Array[Array[Char]]) = {

    board.foreach { row =>

      row.foreach(column => print(s"${column} "))

      println
    }

    println
  }

  def gameBoard(gameId: Int): Option[Game.Progress] = {

    findGameByGameId(gameId) match {

      case Some(game) =>

        val boardMe = game.me.board.map { row =>

          row.map(a => a.toString).reduce((a, b) => a + b)
        }
        val progressMe = Game.ProgressPlayer(game.me.userId, boardMe)

        val boardOpponent = game.opponent.board.map { row =>

          row.map(c => ".").reduce((a, b) => a + b)
        }
        val progressOpponent = Game.ProgressPlayer(game.opponent.userId, boardOpponent)

        val progress = Game.Progress(progressMe, progressOpponent, ("player_turn", game.turn))

        game.finish match {

          case true => Some(progress.copy(game = ("won", game.turn)))
          case false => Some(progress)
        }

      case _ => None
    }
  }

  def enableAutoPilot(gameId: Int): Boolean = {

    findGameByGameId(gameId) match {

      case Some(game) =>

        game.autopilot = true

        true

      case _ => false
    }
  }

  def register(game: Game.Create): Game.Result = {

    val id: Int = matches.isEmpty match {
      case true => 1
      case false => matches.head.id + 1
    }

    //Create Ship: rotate and position and generate the board with each ship
    val shipsOpponent = generateShips()
    val boardOpponent = createBoard(shipsOpponent)

    val opponent = Player(game.user_id, game.full_name, shipsOpponent, boardOpponent)

    //showBoardOnConsole(boardOpponent)

    //Create Ship: rotate and position and generate the board with each ship
    val shipsMe = generateShips()
    val boardMe = createBoard(shipsMe)

    val me = Player(PLAYER, FULLNAME, shipsMe, boardMe)

    //showBoardOnConsole(boardMe)

    //Add each player and the game configuration to the Game List that contain all current games
    val newGame = Game(id, s"$MATCH-$id", opponent.userId, false, false, me, opponent, game.spaceship_protocol)

    matches ::= newGame

    //Return the correct data to controller
    Game.Result(opponent.userId, opponent.fullName, newGame.name, newGame.turn)
  }
}
