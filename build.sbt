name := """social"""

version := "1.0-SNAPSHOT"

lazy val common = (project in file("modules/common")).enablePlugins(PlayJava)

lazy val root = (project in file(".")).enablePlugins(PlayJava).dependsOn(common).aggregate(common)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "org.webjars" % "jquery" % "2.1.4",
  "org.webjars" % "bootstrap" % "3.3.5",
  "org.webjars" % "momentjs" % "2.8.3",
  "org.webjars" % "jquery-cookie" % "1.4.1",
  "org.webjars" % "requirejs-text" % "2.0.10-3",
  "org.webjars" % "angularjs" % "1.4.8",
  "org.webjars" % "ui-grid" % "3.0.7",
  "org.webjars" % "angular-ui-bootstrap" % "0.14.3",
  "org.webjars" % "angular-ui-router" % "0.2.15",
  "org.apache.commons" % "commons-lang3" % "3.4"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
