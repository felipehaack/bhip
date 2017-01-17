package models

import play.api.libs.json.Json
import utils.ImplicitJsonWrites._

object Fire {

  case class Create(salvo: Array[String])

  case class Result(salvo: Seq[(String, String)], game: (String, String))

  implicit val CreateReads = Json.reads[Fire.Create]
  implicit val ResultWrites = Json.writes[Fire.Result]
}
