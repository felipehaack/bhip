name := "Battleship"

version := "1.0"

scalaVersion in ThisBuild := "2.11.8"

val api = Project("battleship-api", file("api"))

val root = Project("battleship", file(".")).aggregate(api)
