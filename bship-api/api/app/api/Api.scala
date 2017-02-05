package api

import javax.inject.Inject

import models.Error
import play.api.i18n._
import play.api.libs.concurrent.Execution.defaultContext
import play.api.libs.json._
import play.api.mvc._
import utils.Logging

import scala.concurrent.Future

trait Api extends Controller with I18nSupport with JsonApi with ImplicityHelper with Logging {

  @Inject var messagesApi: MessagesApi = _
}

trait ImplicityHelper {

  protected implicit val execution = defaultContext
}

trait JsonApi extends ImplicityHelper {

  self: Controller with I18nSupport =>

  implicit val UnitWrites: Writes[Unit] = null

  implicit class ResultAsJson(status: Status)(implicit requestHeader: RequestHeader) {

    def asJson[T: Writes](o: T): Result = {
      o match {

        case true => NoContent
        case false | None => NotFound
        case () => status
        case _ => status(Json.toJson(o))
      }
    }

    def asJson[T: Writes](f: Future[T]): Future[Result] = f.map(asJson(_))
  }

  object json {

    private val inputInvalidCode = "error.input.invalid"

    def apply[T](implicit reader: Reads[T]): BodyParser[T] = {

      BodyParser("json input") { implicit request =>

        parse.json(request).mapFuture {

          case Left(simpleResult) =>
            Future.successful(Left(simpleResult))

          case Right(jsValue) =>

            jsValue.validate(reader).map { a =>

              Future.successful(Right(a))
            } recoverTotal { jsError =>

              val errors = JsError.toFlatForm(jsError).flatMap {

                case (code, error) => error.map(e => Error(code, e.message))
              }

              val error = Error(inputInvalidCode, inputInvalidCode, Some(errors))
              val result = BadRequest(Json.toJson(error))

              Future.successful(Left(result))
            }
        }
      }
    }
  }

}
