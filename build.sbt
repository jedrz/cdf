name := "cdf"

scalaVersion := "2.11.7"

scalastyleFailOnError := true

val akkaVersion = "2.4.1"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scalatest" % "scalatest_2.11" % "2.2.5" % "test"
)