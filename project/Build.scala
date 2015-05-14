import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "dimg"
  val appVersion      = "1.0"+"_"+System.getProperty("BUILD_NUMBER")
  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "com.typesafe.slick" %% "slick" % "1.0.0" withSources(),
    "com.h2database" % "h2" % "1.3.166" withSources(),
    "postgresql" % "postgresql" % "9.1-901.jdbc4",
    "c3p0" % "c3p0" % "0.9.1.2"
  )
  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
