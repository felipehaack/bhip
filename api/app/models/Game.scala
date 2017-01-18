package models

import play.api.libs.json.Json
import utils.ImplicitJsonWrites._

case class Game(
                 id: Int,
                 name: String,
                 var turn: String,
                 var finish: Boolean,
                 var autopilot: Boolean,
                 me: Player,
                 opponent: Player,
                 spaceshipProtocol: Protocol
               )

object Game {

  case class Create(
                     user_id: String,
                     full_name: String,
                     spaceship_protocol: Protocol
                   )

  case class Result(
                     user_id: String,
                     full_name: String,
                     game_id: String,
                     starting: String
                   )

  case class Progress(
                       self: ProgressPlayer,
                       opponent: ProgressPlayer,
                       game: (String, String)
                     )

  case class ProgressPlayer(
                             user_id: String,
                             board: Array[String]
                           )

  implicit val GameCreateReads = Json.reads[Game.Create]
  implicit val GameResultWrites = Json.writes[Game.Result]

  implicit val GameProgressPlayer = Json.writes[ProgressPlayer]
  implicit val GameProgress = Json.writes[Progress]
}