package config

import javax.inject.Inject
import play.api.Configuration
import utils.Configurable

class UserConfig @Inject()(
                            implicit configuration: Configuration
                          ) extends Configurable("user") {

  lazy val userId = getAsString("user_id")
  lazy val fullName = getAsString("full_name")
  lazy val ip = getAsString("ip")
  lazy val port = getAsString("port")
}
