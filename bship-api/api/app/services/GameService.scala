package services

import config.UserConfig

import scala.util.Random
import javax.inject.Singleton

import models._
import play.api.libs.json.Json
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import play.api.libs.ws._
import java.security.MessageDigest

import utils.{Linker, Rules}

@Singleton
class GameService @Inject()(
                             ws: WSClient,
                             userConfig: UserConfig,
                             implicit val executionContext: ExecutionContext
                           ) extends Linker with Rules {

  var matches = List[Game]()

  private def createBoard(ships: List[Ship]): Array[Array[Char]] = {

    val rowsSeq = for {
      i <- 0 until Board.SIZE
      row = for {
        j <- 0 until Board.SIZE
      } yield Ship.Marker.DEFAULT
    } yield row.toArray

    val rows = rowsSeq.toArray

    ships.flatMap(b => b.positions).sorted.foreach { pos =>
      rows(pos._2)(pos._1) = Ship.Marker.MARKER
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

      val x = Random.nextInt(Board.SIZE - rotatedShip.size._1)
      val y = Random.nextInt(Board.SIZE - rotatedShip.size._2)

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

  private def getGameIdHashed(str: String): String = {

    MessageDigest.getInstance("MD5").digest(str.getBytes()).foldLeft("")(_ + "%02x".format(_))
  }

  def findGameByGameId(gameId: String): Option[Game] = {

    val found = for {
      i <- matches.indices
      if matches(i).id == gameId
    } yield i

    found.length match {

      case 1 => Some(matches(found(0)))
      case _ => None
    }
  }

  def status(): List[Game.Status] = {

    val seq = for {
      i <- matches.indices
      game = matches(i)
      id = game.id
      user = game.opponent.userId
      name = game.opponent.fullName
      finished = game.finish
      autoPilot = game.autoPilot
      shots = game.shots
      turn = game.turn
    } yield {
      Game.Status(
        opponentId = user,
        fullName = name,
        gameId = id,
        finished = finished,
        autoPilot = autoPilot,
        shots = shots,
        turn = turn
      )
    }

    seq.toList
  }

  def enableAutoPilot(gameId: String): Boolean = {

    findGameByGameId(gameId) match {

      case Some(game) =>

        game.autoPilot = true

        true

      case None => false
    }
  }

  def gameBoard(gameId: String): Option[Game.Progress] = {

    findGameByGameId(gameId) match {

      case Some(game) =>

        val boardMe = game.me.board.map { row =>

          row.map(c => c.toString).reduce((a, b) => a + b)
        }
        val progressMe = Game.ProgressPlayer(game.me.userId, boardMe)

        val boardFire = game.me.board.map(row => row.map(c => Board.EMPTY))

        game.me.shots.foreach { shot =>

          boardFire(shot._2)(shot._1) = shot._3
        }

        val boardFireNew = boardFire.map { row =>

          row.map(c => c.toString).reduce((a, b) => a + b)
        }

        val progressFire = Game.ProgressPlayer(game.opponent.userId, boardFireNew)

        val progress = Game.Progress(progressMe, progressFire, (Board.TURN, game.turn))

        game.finish match {

          case true => Some(progress.copy(turn = (Board.WON, game.turn)))
          case false => Some(progress)
        }

      case _ => None
    }
  }

  def challenge(challenge: Game.Challenge): Future[Option[Game.Result]] = {

    findShotsByRules(challenge.rule) match {

      case Some(_) =>

        val url = stringAsChallenge(challenge.connection.host)(challenge.connection.port)

        val create = Game.Create(userConfig.id, userConfig.fullName, challenge.rule, Connection(userConfig.host, userConfig.port))

        val json = Json.toJson(create)

        ws.url(url).withRequestTimeout(8000.millis).post(json).map { response =>

          response.json.validate[Game.Result].asOpt
        } recover {

          case _ => None
        }

      case None => Future(None)
    }
  }

  def register(game: Game.Create): Game.Result = {

    //Create Ship: rotate and position and generate the board with each ship
    val shipsMe = generateShips()
    val boardMe = createBoard(shipsMe)

    val me = Player(userConfig.id, userConfig.fullName, shipsMe, boardMe, List())

    //Create Ship: rotate and position and generate the board with each ship
    val opponent = Player(game.userId, game.fullName, null, null, null)

    //Add each player and the game configuration to the Game List that contain all current games
    val turn = Random.nextInt(20) match {
      case r if r % 2 == 0 => me.userId
      case r if r % 2 == 1 => opponent.userId
    }

    val shots = findShotsByRules(game.rule)

    val id = getGameIdHashed(s"${game.connection.host}${System.currentTimeMillis()}")

    val newGame = Game(id, turn, false, false, shots.get, game.rule, me, opponent, game.connection)

    matches ::= newGame

    //Return the correct data to controller
    Game.Result(me.userId, me.fullName, newGame.id, newGame.turn, game.rule)
  }

  def registerChallenge(game: Game.Result, connection: Connection) = {

    //Create Ship: rotate and position and generate the board with each ship
    val shipsMe = generateShips()
    val boardMe = createBoard(shipsMe)

    val me = Player(userConfig.id, userConfig.fullName, shipsMe, boardMe, List())

    //Create Ship: rotate and position and generate the board with each ship
    val opponent = Player(game.userId, game.fullName, null, null, null)

    val shots = findShotsByRules(game.rule)

    val newGame = Game(game.gameId, game.turn, false, false, shots.get, game.rule, me, opponent, connection)

    matches ::= newGame
  }
}
