package models

import utils.ImplicitJson._
import play.api.libs.json._

object Fire {

  case class Create(salvo: Array[String])

  case class Result(salvo: Seq[(String, String)], game: (String, String))

  implicit val CreateFormat = Json.format[Fire.Create]
  implicit val ResultFormat = Json.format[Fire.Result]
}
