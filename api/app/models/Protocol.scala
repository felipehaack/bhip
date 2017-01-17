package models

import play.api.libs.json.Json

case class Protocol(
                     hostname: String,
                     port: Int
                   )

object Protocol {

  implicit val ProtocolFormat = Json.format[Protocol]
}