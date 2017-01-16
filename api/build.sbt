enablePlugins(PlayScala, DockerPlugin)

libraryDependencies ++= Seq(
  "com.wix" %% "accord-core" % "0.6",
  "org.specs2" %% "specs2-mock" % "3.8.3",
  "org.scalatest" %% "scalatest" % "2.2.6",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1"
)