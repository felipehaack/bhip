package api

import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.OneAppPerTest
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

class GameApiTest extends WordSpec with OneAppPerTest with Matchers {

  val game = Json.parse(
    """
      |{
      | "user_id": "xebialabs-1",
      | "full_name": "XebiaLabs Opponent",
      | "spaceship_protocol": {
      |     "hostname": "127.0.0.1",
      |     "port": 9001
      |   }
      | }
    """.stripMargin)

  "create new game" should {

    "return 200 after create a game" in {

      val fakeRequest = FakeRequest(POST, "/xl-spaceship/protocol/game/new").withJsonBody(game)

      val result = await(route(app, fakeRequest).get)

      result.header.status shouldBe OK
    }
  }

  "try to create a new game" should {

    "and return 400 for bad json request" in {

      val parse = Json.parse(
        """
          |{
          | "user_id": "xebialabs-1",
          | "full_name": "XebiaLabs Opponent",
          | "spaceship_protocol": {
          |     "hostname": "127.0.0.1",
          |     "port": "9001"
          |   }
          | }
        """.
          stripMargin)

      val fakeRequest = FakeRequest(POST, "/xl-spaceship/protocol/game/new").withJsonBody(parse)

      val result = await(route(app, fakeRequest).get)

      result.header.status shouldBe BAD_REQUEST
    }

    "and return 400 for missed value on json" in {

      val parse = Json.parse(
        """
          |{
          | "user_id": "xebialabs-1",
          | "spaceship_protocol": {
          |     "hostname": "127.0.0.1",
          |     "port": 9001
          |   }
          | }
        """.stripMargin)

      val fakeRequest = FakeRequest(POST, "/xl-spaceship/protocol/game/new").withJsonBody(parse)

      val result = await(route(app, fakeRequest).get)

      result.header.status shouldBe BAD_REQUEST
    }
  }

  "get game progress" should {

    "return 200 when there's a game running" in {

      val fakeRequest = FakeRequest(POST, "/xl-spaceship/protocol/game/new").withJsonBody(game)
      await(route(app, fakeRequest).get)

      val fakeProgressRequest = FakeRequest(GET, "/xl-spaceship/user/game/1")
      val progressResult = await(route(app, fakeProgressRequest).get)

      progressResult.header.status shouldBe OK
    }

    "return 404 when there's no game running for specify ID" in {

      val fakeProgressRequest = FakeRequest(GET, "/xl-spaceship/user/game/")
      val progressResult = await(route(app, fakeProgressRequest).get)

      progressResult.header.status shouldBe NOT_FOUND
    }
  }

  "enable auto pilot" should {

    "return 200 when there's a game running" in {

      val fakeRequest = FakeRequest(POST, "/xl-spaceship/protocol/game/new").withJsonBody(game)
      await(route(app, fakeRequest).get)

      val fakeRequestAuto = FakeRequest(POST, "/xl-spaceship/user/game/1/auto").withJsonBody(game)
      val gameResultAuto = await(route(app, fakeRequestAuto).get)

      gameResultAuto.header.status shouldBe OK
    }

    "return 400 when there's no game running on specify ID" in {

      val fakeRequestAuto = FakeRequest(POST, "/xl-spaceship/user/game/10/auto").withJsonBody(game)
      val gameResultAuto = await(route(app, fakeRequestAuto).get)

      gameResultAuto.header.status shouldBe BAD_REQUEST
    }
  }
}
