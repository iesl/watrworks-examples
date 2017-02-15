
resolvers in ThisBuild ++= List(
  "IESL Public Releases" at "https://dev-iesl.cs.umass.edu/nexus/content/groups/public",
  // "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases"),
  Resolver.jcenterRepo
)

scalaVersion := "2.11.8"

val scalazVersion       = "7.2.8"
val matryoshkaCoreV     = "0.16.5"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "com.slamdata" %% "matryoshka-core" % matryoshkaCoreV,
  "com.typesafe.play" %% "play-json" % "2.5.12",
  "net.sf.jsi" % "jsi" % "1.1.0-SNAPSHOT"
)
