package common

import scala.concurrent.{ExecutionContext, Future}
import akka.stream.Materializer
import akka.util.{ByteString, Timeout}
import play.api.http._
import play.api.libs.json._
import play.api.mvc._
import play.api.test._

trait ApiSpec {

  val executionContext  = ExecutionContext.global

  implicit class RickFakeRequest[A](request: FakeRequest[A]) {
    def withInput[T: Writes](value: T) = {
      request.withJsonBody(Json.toJson(value))
    }
  }

  implicit class RickAction(action: EssentialAction) {
    def call[T](req: Request[T])(implicit w: Writeable[T], mat: Materializer): Future[Result] = {
      Helpers.call(action, req)
    }
  }

  implicit class RickResultExtractors(of: Future[Result]) {
    def contentType(implicit timeout: Timeout = Helpers.defaultAwaitTimeout): Option[String] = {
      Helpers.contentType(of)(timeout)
    }

    def charset(implicit timeout: Timeout = Helpers.defaultAwaitTimeout) = {
      Helpers.charset(of)(timeout)
    }

    def contentAsString(implicit timeout: Timeout = Helpers.defaultAwaitTimeout): ByteString = {
      Helpers.contentAsBytes(of)(timeout)
    }

    def contentAsBytes(implicit timeout: Timeout = Helpers.defaultAwaitTimeout): ByteString = {
      Helpers.contentAsBytes(of)(timeout)
    }

    def contentAsJson(implicit timeout: Timeout = Helpers.defaultAwaitTimeout): JsValue = {
      Helpers.contentAsJson(of)(timeout)
    }

    def status(implicit timeout: Timeout = Helpers.defaultAwaitTimeout): Int = {
      Helpers.status(of)(timeout)
    }

    def cookies(implicit timeout: Timeout = Helpers.defaultAwaitTimeout): Cookies = {
      Helpers.cookies(of)(timeout)
    }

    def flash(implicit timeout: Timeout = Helpers.defaultAwaitTimeout): Flash = {
      Helpers.flash(of)(timeout)
    }

    def session(implicit timeout: Timeout = Helpers.defaultAwaitTimeout): Session = {
      Helpers.session(of)(timeout)
    }

    def redirectLocation(implicit timeout: Timeout = Helpers.defaultAwaitTimeout): Option[String] = {
      Helpers.redirectLocation(of)(timeout)
    }

    def header(header: String)(implicit timeout: Timeout = Helpers.defaultAwaitTimeout): Option[String] = {
      Helpers.header(header, of)(timeout)
    }

    def headers(implicit timeout: Timeout = Helpers.defaultAwaitTimeout): Map[String, String] = {
      Helpers.headers(of)(timeout)
    }

    def contentAs[T: Reads](implicit timeout: Timeout = Helpers.defaultAwaitTimeout): T = {
      Json.fromJson[T](Helpers.contentAsJson(of)(timeout)).get
    }
  }

}
