package models

case class Ship(
                 size: (Int, Int),
                 start: (Int, Int),
                 var positions: List[(Int, Int)]
               )

object Ship {

  val Killer = Ship((3, 5), (0, 0), List(
    (0, 0), (2, 0),
    (0, 1), (2, 1),
    (1, 2),
    (0, 3), (2, 3),
    (0, 4), (2, 4)
  ))

  val Eagle = Ship((3, 4), (0, 0), List(
    (0, 0),
    (0, 1),
    (0, 2),
    (0, 3), (1, 3), (2, 3)
  ))

  val Luminux = Ship((3, 4), (0, 0), List(
    (1, 0),
    (0, 1), (2, 1),
    (0, 2), (1, 2), (2, 2),
    (0, 3), (2, 3)
  ))

  val TheForce = Ship((3, 5), (0, 0), List(
    (0, 0), (1, 0),
    (0, 1), (2, 1),
    (0, 2), (1, 2),
    (0, 3), (2, 3),
    (0, 4), (1, 4)
  ))

  val Invincible = Ship((4, 5), (0, 0), List(
    (1, 0), (2, 0),
    (0, 1),
    (1, 2), (2, 2),
    (3, 3),
    (1, 4), (2, 4)
  ))

  val MAX_POSE = 4

  val All = Array[Ship](
    Eagle,
    Killer,
    Luminux,
    TheForce,
    Invincible
  )

  object Marker {

    val DEFAULT = '.'
    val MARKER = '*'
    val MISS = '-'
  }

}