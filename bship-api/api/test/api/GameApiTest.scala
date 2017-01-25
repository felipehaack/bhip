package api

import common.ApiSpec
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import models.{Game, Protocol}
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}

class GameApiTest extends PlaySpec with ApiSpec with OneAppPerTest {

  val protocol = Protocol("192.168.1.2", 9000)
  val game = Game.Create("xebialabs", "xebialabs full", "standard", protocol)
  val gameResult = Game.Result("someone", "someone full", "somehash", "someone", "standard")

  "try to create a new game by Protocol API call" should {

    "return 200 after create a new game" in {

      val fakeRequest = FakeRequest(POST, "/xl-spaceship/protocol/game/new").withJsonBody(Json.toJson(game))

      val result = route(app, fakeRequest).get

      status(result) mustBe 200

      result.contentAs[Game.Result].rules mustBe "standard"
    }

    "return 400 when some fields are has invalid data" in {

      val localGame = game.copy(rules = "noexist")
      val fakeRequest = FakeRequest(POST, "/xl-spaceship/protocol/game/new").withJsonBody(Json.toJson(localGame))

      val result = route(app, fakeRequest).get

      result.isCompleted mustBe false
    }

    "return 400 when some fields are missed on json" in {

      val json =
        """
          {
          | "user_id": "xebialabs",
          |	"rules": "standard",
          |	"spaceship_protocol": {
          |		"hostname": "127.0.0.1",
          |		"port": 8000
          |	}
          }
        """.stripMargin

      val fakeRequest = FakeRequest(POST, "/xl-spaceship/protocol/game/new").withJsonBody(Json.toJson(json))

      val result = route(app, fakeRequest).get

      result.isCompleted mustBe false
    }
  }

  "try to get the progress status of the game" should {

    "return 200 when try to get an existing game status from game id" in {

      val fakeRequest = FakeRequest(POST, "/xl-spaceship/protocol/game/new").withJsonBody(Json.toJson(game))

      val result = route(app, fakeRequest).get

      status(result) mustBe 200

      val gameResult = result.contentAs[Game.Result]

      gameResult.rules mustBe game.rules

      val fakeRequestAux = FakeRequest(GET, s"/xl-spaceship/user/game/${gameResult.game_id}")

      val resultAux = route(app, fakeRequestAux).get

      status(resultAux) mustBe 200

      val gameProgress = resultAux.contentAs[Game.Progress]

      gameProgress.opponent.user_id mustBe game.user_id
    }

    "return 404 when try to get the game status from game id that doesn't exists" in {

      val fakeRequest = FakeRequest(GET, s"/xl-spaceship/user/game/${gameResult.game_id}")

      val result = route(app, fakeRequest).get

      status(result) mustEqual 404
    }
  }

  "try to get all game status" should {

    "return 200 with at least 2 game in progress" in {

      await(route(app, FakeRequest(POST, "/xl-spaceship/protocol/game/new").withJsonBody(Json.toJson(game))).get)
      await(route(app, FakeRequest(POST, "/xl-spaceship/protocol/game/new").withJsonBody(Json.toJson(game))).get)

      val fakeRequest = FakeRequest(GET, "/xl-spaceship/user/games")

      val result = route(app, fakeRequest).get

      val gameStatus = result.contentAs[List[Game.Status]]

      gameStatus.length must be > 1
    }

    "return 200 with no games in progress" in {

      val fakeRequest = FakeRequest(GET, "/xl-spaceship/user/games")

      val result = route(app, fakeRequest).get

      val gameStatus = result.contentAs[List[Game.Status]]

      gameStatus.length mustEqual 0
    }
  }

  "try to enable the autopilot feature" should {

    "return 200 when enable auto pilot with a existing game id" in {

      val fakeRequest = FakeRequest(POST, "/xl-spaceship/protocol/game/new").withJsonBody(Json.toJson(game))

      val result = route(app, fakeRequest).get

      status(result) mustBe 200

      val gameResult = result.contentAs[Game.Result]

      gameResult.rules mustBe game.rules

      val fakeRequestPilot = FakeRequest(POST, s"/xl-spaceship/user/game/${gameResult.game_id}/auto")

      val resultPilot = route(app, fakeRequestPilot).get

      status(resultPilot) mustBe 200
    }

    "return 404 when try to enable with not found game id" in {

      val fakeRequestPilot = FakeRequest(POST, s"/xl-spaceship/user/game/${gameResult.game_id}/auto")

      val resultPilot = route(app, fakeRequestPilot).get

      status(resultPilot) mustBe 404
    }
  }
}
