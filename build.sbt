name := """scala-fedex"""

organization := "com.gilt"

version := "git describe --tags --always --dirty".!!.trim.replaceFirst("^v","")



javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"
)

publishMavenStyle := true

bintrayOrganization := Some("giltgroupe")


publishArtifact in Test := false

pomIncludeRepository := { _ => false }

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

homepage := Some(url("https://github.com/gilt/scala-fedex"))

pomExtra := <scm>
    <url>https://github.com/gilt/scala-fedex.git</url>
    <connection>scm:git:git@github.com:gilt/scala-fedex.git</connection>
  </scm>
  <developers>
    <developer>
      <id>rcaloras</id>
      <name>Ryan Caloras</name>
      <url>https://github.com/rcaloras</url>
    </developer>
  </developers>
