package models

import utils.ImplicitJson._
import play.api.libs.json.Json

case class Game(
                 id: String,
                 var turn: String,
                 var finish: Boolean,
                 var autopilot: Boolean,
                 var shots: Int,
                 rules: String,
                 me: Player,
                 opponent: Player,
                 protocol: Protocol
               )

object Game {

  case class Create(
                     user_id: String,
                     full_name: String,
                     rules: String,
                     spaceship_protocol: Protocol
                   )

  case class Result(
                     user_id: String,
                     full_name: String,
                     game_id: String,
                     starting: String,
                     rules: String
                   )

  case class Status(
                     opponent_id: String,
                     full_name: String,
                     game_id: String,
                     finished: Boolean,
                     autopilot: Boolean,
                     shots: Int
                   )

  case class Challenge(
                        spaceship_protocol: Protocol,
                        rules: String
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

  implicit val GameCreateFormat = Json.format[Create]
  implicit val GameResultFormat = Json.format[Result]

  implicit val GameChallengeReads = Json.reads[Challenge]

  implicit val GameProgressPlayer = Json.writes[ProgressPlayer]
  implicit val GameProgressPlayerReads = Json.reads[ProgressPlayer]
  implicit val GameProgress = Json.writes[Progress]
  implicit val GameProgressReads = Json.reads[Progress]

  implicit val GameStatusWrites = Json.writes[Status]
  implicit val GameStatusReads = Json.reads[Status]
}