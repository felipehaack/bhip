package models

import play.api.libs.json.Json

case class Game(
                 id: Int,
                 name: String,
                 var turn: String,
                 var finish: Boolean,
                 player1: Player,
                 player2: Player,
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

  implicit val GameCreateReads = Json.reads[Game.Create]
  implicit val GameResultWrites = Json.writes[Game.Result]
}