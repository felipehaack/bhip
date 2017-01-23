package utils

trait Protocol {

  val http = "http://"
  val colon = ":"

  object Path {

    val game = "/xl-spaceship/user/game/"
    val protocol = "/xl-spaceship/protocol/game/"
    val challenge = "new"
  }

  def stringAsChallenge(host: String)(port: Int): String = {

    s"${http}${host}${colon}${port}${Path.protocol}${Path.challenge}"
  }

  def stringAsFire(host: String)(port: Int)(gameId: String): String = {

    s"${http}${host}${colon}${port}${Path.protocol}${gameId}"
  }

  def stringAsChallengeSeeOther(gameId: String): String = {

    s"${Path.game}${gameId}"
  }
}
