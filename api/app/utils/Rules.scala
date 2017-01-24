package utils

import models.Ship

trait Rules {

  val regexShot = "[0-9]+".r

  object Rule extends Enumeration {

    val SHOT = Value("shot")
    val STANDARD = Value("standard")
    val DESPERATION = Value("desperation")
    val SUPER_CHARGER = Value("super-charge")
  }

  def findShotsByRules(rules: String): Option[Int] = {

    rules match {
      case r if r.indexOf(Rule.STANDARD.toString) > -1 => Some(Ship.All.length)
      case r if r.indexOf(Rule.SUPER_CHARGER.toString) > -1 => Some(Ship.All.length)
      case r if r.indexOf(Rule.DESPERATION.toString) > -1 => Some(1)
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
      case r if r.indexOf(Rule.STANDARD.toString) > -1 => shots.length <= shipsAlive
      case r if r.indexOf(Rule.SUPER_CHARGER.toString) > -1 => shots.length <= shipsAlive
      case r if r.indexOf(Rule.SHOT.toString) > -1 => shots.length <= maxShots
      case r if r.indexOf(Rule.DESPERATION.toString) > -1 => shots.length <= maxShots
    }
  }

  def verifyResultShots(rules: String): Int = {

    rules match {
      case r if r.indexOf(Rule.DESPERATION.toString) > -1 => 1
      case _ => 0
    }
  }

  def isDesperation(rules: String): Boolean = {

    rules match {
      case r if r.indexOf(Rule.DESPERATION.toString) > -1 => true
      case _ => false
    }
  }

  def isSuperChargerOrStandard(rules: String): Boolean = {

    rules match {
      case r if r.indexOf(Rule.SUPER_CHARGER.toString) > -1 => true
      case r if r.indexOf(Rule.STANDARD.toString) > -1 => true
      case _ => false
    }
  }
}