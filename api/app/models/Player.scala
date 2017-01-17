package models

case class Player(
                   userId: String,
                   fullName: String,
                   var ships: List[Ship],
                   var board: Array[Array[Char]]
                 )

object Player {

  object Turn extends Enumeration {
    type PlayerStatus = Value
    val Me, Opponent = Value
  }

}