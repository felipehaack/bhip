package utils

import models.Ship

trait Rules {

  private val regexShot = "[0-9]+".r

  object Rule extends Enumeration {

    val SHOT = Value("shot")
    val INCREMENTER = Value("incrementer")
    val DECREMENTER = Value("decrementer")
  }

  def findShotsByRules(rules: String): Option[Int] = {

    rules match {
      case r if r.indexOf(Rule.DECREMENTER.toString) > -1 => Some(Ship.All.length)
      case r if r.indexOf(Rule.INCREMENTER.toString) > -1 => Some(1)
      case r if r.indexOf(Rule.SHOT.toString) > -1 =>

        val shots = r.split("-")

        shots.length match {

          case 2 => regexShot.findFirstIn(shots(0)) match {
            case Some(shot) if shot.toInt > 0 && shot.toInt <= 10 => Some(shot.toInt)
            case _ => None
          }

          case _ => None
        }

      case _ => None
    }
  }

  def verifyShots(shots: Array[String], rules: String, shipsAlive: Int, maxShots: Int): Boolean = {

    rules match {
      case r if r.indexOf(Rule.DECREMENTER.toString) > -1 => shots.length <= shipsAlive
      case r if r.indexOf(Rule.SHOT.toString) > -1 => shots.length <= maxShots
      case r if r.indexOf(Rule.INCREMENTER.toString) > -1 => shots.length <= maxShots
    }
  }

  def isIncrementer(rules: String): Boolean = {

    rules.indexOf(Rule.INCREMENTER.toString) match {
      case -1 => false
      case _ => true
    }
  }

  def isDecrementer(rules: String): Boolean = {

    rules.indexOf(Rule.DECREMENTER.toString) match {
      case -1 => false
      case _ => true
    }
  }
}