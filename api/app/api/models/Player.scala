package api.models

case class Player(
                   userId: String,
                   fullName: String,
                   var ships: List[Ship],
                   var board: Array[Array[Char]]
                 )