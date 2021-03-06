lazy val akkaHttpVersion = "10.0.9"
lazy val akkaVersion = "2.4.19"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "ru.akatov",
      scalaVersion := "2.12.2"
    )),
    name := "Money Transfers",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "org.scalatest" %% "scalatest" % "3.0.1" % Test
    )
  )
