name := "cdf"

scalaVersion := "2.11.7"

scalastyleFailOnError := true

val akkaVersion = "2.4.1"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "net.ruippeixotog" %% "scala-scraper" % "0.1.2",
  "org.scalatest" % "scalatest_2.11" % "2.2.5" % "test"
)

lazy val testScalastyle = taskKey[Unit]("testScalastyle")
testScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Test).toTask("").value
(test in Test) <<= (test in Test) dependsOn testScalastyle