name := "bship"

version := "1.0"

scalaVersion in ThisBuild := "2.11.8"

val api = Project("bship-api", file("api"))

val root = Project("bship", file(".")).aggregate(api)
