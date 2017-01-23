package models

import utils.ImplicitJson._
import play.api.libs.json._

object Fire {

  case class Create(salvo: Array[String])

  case class Result(salvo: Seq[(String, String)], game: (String, String))

  implicit val CreateWrites = Json.reads[Create]
  implicit val ResultWrites = Json.writes[Fire.Result]
  implicit val ResultReads = Json.reads[Fire.Result]
}

