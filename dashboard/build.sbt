name := "dashboard"
 
version := "1.0" 
      
lazy val `dashboard` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.11.8"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.18"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.5.3"

libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.10.0"

libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.10.0"


libraryDependencies ++= Seq(
  "org.apache.ignite" % "ignite-core" % "2.3.0",
  "org.apache.ignite" % "ignite-web" % "2.3.0",
  "org.apache.ignite" % "ignite-slf4j" % "2.3.0",
  "org.apache.ignite" % "ignite-scalar" % "2.3.0",
  "org.apache.ignite" % "ignite-spring" % "2.3.0",
  "org.apache.ignite" % "ignite-indexing" % "2.3.0"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )