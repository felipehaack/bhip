package models

import play.api.libs.json.Json

case class Game(
                 id: Int,
                 name: String,
                 turn: String,
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

  implicit val GameCreateFormat = Json.reads[Game.Create]
  implicit val GameResultFormat = Json.writes[Game.Result]
}