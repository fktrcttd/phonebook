name := "PhoneBook"
 
version := "1.0" 
      
lazy val `phonebook` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies += guice

libraryDependencies += "com.typesafe.play" %% "play-slick" % "4.0.1"

libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "4.0.1"

libraryDependencies += "com.h2database" % "h2" % "1.4.197"

libraryDependencies += "org.postgresql" % "postgresql" % "42.2.1"

libraryDependencies += specs2 % Test
unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

      