package utils

import exceptions.BattleshipFailure
import play.api.Configuration

class Configurable(path: String) {

  def getAsString(key: String)(implicit configuration: Configuration): String = {

    configuration.getObject(path) match {

      case Some(configObject) =>

        configObject.containsKey(key) match {

          case true => configObject.get(key).unwrapped().toString
          case false => throw new BattleshipFailure(s"${path} -> ${key} not found")
        }
      case None => throw new BattleshipFailure(s"${path} -> ${key} not found")
    }
  }
}
