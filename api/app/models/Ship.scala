package models

case class Ship(
                 size: (Int, Int),
                 start: (Int, Int),
                 var positions: List[(Int, Int)]
               )

object Ship {

  val Winger = Ship((3, 5), (0, 0), List(
    (0, 0), (2, 0),
    (0, 1), (2, 1),
    (1, 2),
    (0, 3), (2, 3),
    (0, 4), (2, 4)
  ))

  val Angle = Ship((3, 4), (0, 0), List(
    (0, 0),
    (0, 1),
    (0, 2),
    (0, 3), (1, 3), (2, 3)
  ))

  val AClass = Ship((3, 4), (0, 0), List(
    (1, 0),
    (0, 1), (2, 1),
    (0, 2), (1, 2), (2, 2),
    (0, 3), (2, 3)
  ))

  val BClass = Ship((3, 5), (0, 0), List(
    (0, 0), (1, 0),
    (0, 1), (2, 1),
    (0, 2), (1, 2),
    (0, 3), (2, 3),
    (0, 4), (1, 4)
  ))

  val SClass = Ship((4, 5), (0, 0), List(
    (1, 0), (2, 0),
    (0, 1),
    (1, 2), (2, 2),
    (3, 3),
    (1, 4), (2, 4)
  ))

  val MAX_POSE = 4

  val All = Array[Ship](
    Angle,
    Winger,
    AClass,
    BClass,
    SClass
  )
}