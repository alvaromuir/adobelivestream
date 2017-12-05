name := "dashboard"
 
version := "1.0" 
      
lazy val `dashboard` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.11.8"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.18"

libraryDependencies += "org.apache.hbase" % "hbase-client" % "1.2.6"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
