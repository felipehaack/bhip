package models

import play.api.libs.json.Json

case class Connection(
                     host: String,
                     port: Int
                   )

object Connection {

  implicit val ConnectionFormat = Json.format[Connection]
}