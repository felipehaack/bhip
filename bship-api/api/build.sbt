enablePlugins(PlayScala, DockerPlugin)

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M1",
  "org.mockito" % "mockito-all" % "1.10.19"
)

libraryDependencies += filters