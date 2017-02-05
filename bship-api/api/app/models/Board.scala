package models

object Board {

  val SIZE = 16

  val WON = "won"
  val TURN = "turn"

  val EMPTY = '.'
  val KILLED = 'X'
  val MISSED = '-'
  val SHIP = '*'

  val KILL = "kill"
  val MISS = "miss"
  val HIT = "hit"
}