package services

import models.Ship
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec

class GameServiceTest extends PlaySpec with MockitoSugar {

  val gameService = new GameService
  val gameServiceMock = mock[GameService]

  "Game Service" should {

    "create board that returns a array with length more then 1" in {

      val board = gameService.createBoard(Ship.All.toList)
      board.length must be > 1
    }
  }

  "Game Service" should {

    "rotate a ship to new or same points position" in {

      val ship = gameService.rotateShip(Ship.All(0))
      ship.positions.length must be > 0
    }
  }

  "Game Service" should {

    "create a new ship and detect if it was overlap" in {

      val ships = List()

      val isOverlap = gameService.isOverlap(ships, Ship.All(0))

      isOverlap mustBe false
    }
  }

  "Game Service" should {

    "create a new ship with 2 ship on list and detect if it was overlap" in {

      val ships = List(Ship.All(0))

      val attempt = Ship.All(1).copy(start = (10, 10))

      val isOverlap = gameService.isOverlap(ships, attempt)

      isOverlap mustBe false
    }
  }
}
