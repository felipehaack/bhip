package services

import common.ApiSpec
import config.UserConfig
import models.{Game, Player, Connection, Ship}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.ws.WSClient

class GameServiceTest extends PlaySpec with ApiSpec with MockitoSugar {

  val gameId = "AE2G45E29"

  val gameServiceMock = mock[GameService]

  val connection = Connection("192.168.1.3", 9000)

  val board = Array(Array('.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.'))

  val player = Player("", "", Ship.All.toList, board, List())

  val game = Game(gameId, "someone", false, false, 5, "incrementer", player, player, connection)

  val wsClientMock = mock[WSClient]
  val userConfigMock = mock[UserConfig]

  val gameService = new GameService(wsClientMock, userConfigMock, executionContext)

  "create a board with existing game id" should {

    "return a some of game progress" in {

      gameService.matches = List(game)

      val result = gameService.gameBoard(gameId)

      result.isDefined mustBe true
    }

    "return a none of game progress because the game id doesn't exists" in {

      val result = gameService.gameBoard("HASH")

      result.isDefined mustBe false
    }
  }
}
