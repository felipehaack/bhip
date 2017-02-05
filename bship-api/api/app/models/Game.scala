package models

import utils.ImplicitJson._
import play.api.libs.json.Json

case class Game(
                 id: String,
                 var turn: String,
                 var finish: Boolean,
                 var autoPilot: Boolean,
                 var shots: Int,
                 rule: String,
                 me: Player,
                 opponent: Player,
                 connection: Connection
               )

object Game {

  case class Create(
                     userId: String,
                     fullName: String,
                     rule: String,
                     connection: Connection
                   )

  case class Result(
                     userId: String,
                     fullName: String,
                     gameId: String,
                     turn: String,
                     rule: String
                   )

  case class Status(
                     opponentId: String,
                     fullName: String,
                     gameId: String,
                     finished: Boolean,
                     autoPilot: Boolean,
                     shots: Int,
                     turn: String
                   )

  case class Challenge(
                        connection: Connection,
                        rule: String
                      )

  case class Progress(
                       me: ProgressPlayer,
                       opponent: ProgressPlayer,
                       turn: (String, String)
                     )

  case class ProgressPlayer(
                             userId: String,
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