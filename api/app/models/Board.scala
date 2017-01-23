package models

object Board {

  val SIZE = 16

  val WON = "won"
  val PLAYER_TURN = "player_turn"

  val EMPTY = '.'
  val KILLED = 'X'
  val MISSED = '-'
  val UNCHANGED = '*'

  val KILL = "kill"
  val MISS = "miss"
  val HIT = "hit"
}