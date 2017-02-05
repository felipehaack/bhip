package config

import javax.inject.Inject
import play.api.Configuration
import utils.Configurable

class UserConfig @Inject()(
                            implicit configuration: Configuration
                          ) extends Configurable("user") {

  lazy val id: String = getAsString("id")
  lazy val fullName: String = getAsString("fullName")
  lazy val host: String = getAsString("host")
  lazy val port: Int = getAsString("port").toInt
}
