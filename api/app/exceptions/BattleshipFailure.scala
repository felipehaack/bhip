package exceptions

import scala.util.control.NoStackTrace

class BattleshipFailure(val message: String) extends RuntimeException(message) with BattleshipException with NoStackTrace