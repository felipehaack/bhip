package api

import common.ApiSpec
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import models.{Connection, Game}
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import org.scalatestplus.play.PlaySpec

class GameApiTest extends PlaySpec with ApiSpec with GuiceOneAppPerTest {

  val connection = Connection("192.168.1.2", 9000)
  val game = Game.Create("nickname", "fullName", "incrementer", connection)
  val gameResult = Game.Result("someone", "someone full", "someHash", "someone", "incrementer")

  "try to create a new game by Link API call" should {

    "return 200 after create a new game" in {

      val fakeRequest = FakeRequest(POST, "/bship/link/game/new").withJsonBody(Json.toJson(game))

      val result = route(app, fakeRequest).get

      status(result) mustBe 200

      result.contentAs[Game.Result].rule mustBe "incrementer"
    }

    "return 400 when some fields are has invalid data" in {

      val localGame = game.copy(rule = "noExist")
      val fakeRequest = FakeRequest(POST, "/bship/link/game/new").withJsonBody(Json.toJson(localGame))

      val result = route(app, fakeRequest).get

      result.isCompleted mustBe false
    }

    "return 400 when some fields are missed on json" in {

      val json =
        """
          {
          | "userId": "nick name",
          |	"rule": "incrementer",
          |	"connection": {
          |		"host": "127.0.0.1",
          |		"port": 8000
          |	}
          }
        """.stripMargin

      val fakeRequest = FakeRequest(POST, "/bship/link/game/new").withJsonBody(Json.toJson(json))

      val result = route(app, fakeRequest).get

      result.isCompleted mustBe false
    }
  }

  "try to get the progress status of the game" should {

    "return 200 when try to get an existing game status from game id" in {

      val fakeRequest = FakeRequest(POST, "/bship/link/game/new").withJsonBody(Json.toJson(game))

      val result = route(app, fakeRequest).get

      status(result) mustBe 200

      val gameResult = result.contentAs[Game.Result]

      gameResult.rule mustBe game.rule

      val fakeRequestAux = FakeRequest(GET, s"/bship/player/game/${gameResult.gameId}")

      val resultAux = route(app, fakeRequestAux).get

      status(resultAux) mustBe 200

      val gameProgress = resultAux.contentAs[Game.Progress]

      gameProgress.opponent.userId mustBe game.userId
    }

    "return 404 when try to get the game status from game id that doesn't exists" in {

      val fakeRequest = FakeRequest(GET, s"/bship/player/game/${gameResult.gameId}")

      val result = route(app, fakeRequest).get

      status(result) mustEqual 404
    }
  }

  "try to get all game status" should {

    "return 200 with at least 2 game in progress" in {

      await(route(app, FakeRequest(POST, "/bship/link/game/new").withJsonBody(Json.toJson(game))).get)
      await(route(app, FakeRequest(POST, "/bship/link/game/new").withJsonBody(Json.toJson(game))).get)

      val fakeRequest = FakeRequest(GET, "/bship/player/games")

      val result = route(app, fakeRequest).get

      val gameStatus = result.contentAs[List[Game.Status]]

      gameStatus.length must be > 1
    }

    "return 200 with no games in progress" in {

      val fakeRequest = FakeRequest(GET, "/bship/player/games")

      val result = route(app, fakeRequest).get

      val gameStatus = result.contentAs[List[Game.Status]]

      gameStatus.length mustEqual 0
    }
  }

  "try to enable the autopilot feature" should {

    "return 200 when enable auto pilot with a existing game id" in {

      val fakeRequest = FakeRequest(POST, "/bship/link/game/new").withJsonBody(Json.toJson(game))

      val result = route(app, fakeRequest).get

      status(result) mustBe 200

      val gameResult = result.contentAs[Game.Result]

      gameResult.rule mustBe game.rule

      val fakeRequestPilot = FakeRequest(POST, s"/bship/player/game/${gameResult.gameId}/auto")

      val resultPilot = route(app, fakeRequestPilot).get

      status(resultPilot) mustBe 200
    }

    "return 404 when try to enable with not found game id" in {

      val fakeRequestPilot = FakeRequest(POST, s"/bship/player/game/${gameResult.gameId}/auto")

      val resultPilot = route(app, fakeRequestPilot).get

      status(resultPilot) mustBe 404
    }
  }
}
