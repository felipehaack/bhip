package models

case class Player(
                   userId: String,
                   fullName: String,
                   var ships: List[Ship],
                   var board: Array[Array[Char]],
                   var shots: List[(Int, Int, Char)]
                 )
