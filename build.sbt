name := "zlfinch"

version := "1.0"

scalaVersion := "2.12.1"

//resolvers += "Sonatype Snapshots" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % "0.14.0",
  "com.github.finagle" %% "finch-circe" % "0.14.0",
  "io.circe" %% "circe-core" % "0.7.0",
  //"io.circe" %% "circe-generic" % "0.7.0",
 // "io.circe" %% "circe-parser" % "0.7.0",
  //"com.twitter" %% "bijection-core" % "0.9.5",
  //"com.twitter" %% "bijection-util" % "0.9.5",
  "joda-time" % "joda-time" % "2.9.3",
  "org.joda" % "joda-convert" % "1.8",
  "com.github.benhutchison" %% "mouse" % "0.6",
  "org.reactivemongo" %% "reactivemongo" % "0.12.1",
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "com.pauldijou" %% "jwt-circe" % "0.12.0",
  "org.fusesource.mqtt-client" % "mqtt-client" % "1.12",
  "org.eclipse.paho" % "org.eclipse.paho.client.mqttv3" % "1.0.2",

  // Monitoring
  "com.rollbar" % "rollbar" % "0.5.2"
)
