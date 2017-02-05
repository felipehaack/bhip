package utils

trait Linker {

  val http = "http://"
  val colon = ":"

  object Path {

    val default = "/bship"
    val game = s"$default/player/game/"
    val link = s"$default/link/game/"
    val challenge = "new"
  }

  def stringAsChallenge(host: String)(port: Int): String = {

    s"$http$host$colon$port${Path.link}${Path.challenge}"
  }

  def stringAsFire(host: String)(port: Int)(gameId: String): String = {

    s"$http$host$colon$port${Path.link}$gameId"
  }
}
