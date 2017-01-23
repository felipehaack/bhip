package services

import models.{Game, Player, Protocol, Ship}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.mockito.Mockito._

class GameServiceTest extends PlaySpec with MockitoSugar {

  /*val player = Player("", "", List(), Array(Array()))
  val protocol = Protocol("", 9000)
  val game = Game(1, "", "", false, false, player, player, protocol)

  val gameService = new GameService
  val gameServiceMock = mock[GameService]

  "create a board" should {

    "returns a array with length more then 1" in {

      val board = gameService.createBoard(Ship.All.toList)
      board.length must be > 1
    }
  }

  "rotate a ship" should {

    "return a new or same points position" in {

      val ship = gameService.rotateShip(Ship.All(0))
      ship.positions.length must be > 0
    }
  }

  "detect overlap" should {

    "return false when there's only one ship" in {

      val ships = List()

      val isOverlap = gameService.isOverlap(ships, Ship.All(0))

      isOverlap mustBe false
    }

    "return false when there's 2 ship, but the seconds ins't overlap" in {

      val ships = List(Ship.All(0))

      val attempt = Ship.All(1).copy(start = (10, 10))

      val isOverlap = gameService.isOverlap(ships, attempt)

      isOverlap mustBe false
    }

    "return true when there's 2 ship, but the seconds is overlap" in {

      val ships = List(Ship.All(0))

      val attempt = Ship.All(1).copy(start = (0, 0))

      val isOverlap = gameService.isOverlap(ships, attempt)

      isOverlap mustBe true
    }
  }*/
}
