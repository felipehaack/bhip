package models

import utils.ImplicitJson._
import play.api.libs.json._

object Fire {

  case class Create(shots: Array[String])

  case class Result(shots: Seq[(String, String)], status: (String, String))

  implicit val CreateFormat = Json.format[Fire.Create]
  implicit val ResultFormat = Json.format[Fire.Result]
}
