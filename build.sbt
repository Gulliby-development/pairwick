ThisBuild / scalaVersion := "3.3.7"
ThisBuild / version := "0.1.0"
ThisBuild / scalacOptions ++= Seq("-deprecation", "-feature", "-Werror", "-Wunused:all")

lazy val root = (project in file("."))
  .settings(
    name := "pairwick",
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.4" % Test
  )
