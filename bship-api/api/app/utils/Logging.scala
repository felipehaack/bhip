package utils

import play.api.Logger

trait Logging {
  protected[this] lazy val log = Logger(getClass)
}
