package api.controllers

import org.scalatest.{Matchers, WordSpec}
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import org.scalatestplus.play.OneAppPerTest

class GameApiTest extends WordSpec with OneAppPerTest with Matchers {

  "Game Api" should {

    "return 200 after create a game" in {

      val parse = Json.parse(
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

      val fakeRequest = FakeRequest(POST, "/xl-spaceship/protocol/game/new").withJsonBody(parse)

      val result = await(route(app, fakeRequest).get)

      result.header.status shouldBe OK
    }
  }

  "and return 400 for bad json structure" in {

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
      """.stripMargin)

    val fakeRequest = FakeRequest(POST, "/xl-spaceship/protocol/game/new").withJsonBody(parse)

    val result = await(route(app, fakeRequest).get)

    result.header.status shouldBe BAD_REQUEST
  }

  "and return 400 for miss field on json structure" in {

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
