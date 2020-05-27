name := "test sbt project"
version := "0.1"
scalaVersion := "2.11.7"

resolvers += "Lucene" at "https://mvnrepository.com/artifact/org.apache.lucene/lucene-core"
libraryDependencies += "org.apache.lucene" % "lucene-core" % "6.6.3"
