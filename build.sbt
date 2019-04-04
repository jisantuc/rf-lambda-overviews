name := "lambda-overviews"

version := "0.0.1"

scalaVersion := "2.11.12"

lazy val awsVersion = "1.1.0"
lazy val catsVersion = "1.6.0"
lazy val catsEffectVersion = "1.2.0"
lazy val circeVersion = "0.11.1"
lazy val gtVersion = "2.2.0"
lazy val gtcVersion = "0.10.2"
lazy val logbackClassicVersion = "1.2.3"
lazy val scalaLoggingVersion = "3.9.2"
lazy val scalatestVersion = "3.0.5"
lazy val scalacacheVersion = "0.27.0"
lazy val sttpVersion = "1.5.11"

scalacOptions ++= Seq(
  "-encoding", "UTF-8",   // source files are in UTF-8
  "-deprecation",         // warn about use of deprecated APIs
  "-unchecked",           // warn about unchecked type parameters
  "-feature",             // warn about misused language features
  "-language:higherKinds",// allow higher kinded types without `import scala.language.higherKinds`
  "-Xlint",               // enable handy linter warnings
  "-Xfatal-warnings",     // turn compiler warnings into errors
  "-Ypartial-unification",// allow the compiler to unify type constructors of different arities
  "-target:jvm-1.8"
)

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-lambda-java-core" % awsVersion,
  "com.azavea.geotrellis" %% "geotrellis-contrib-vlm" % gtcVersion,
  "com.github.cb372" %% "scalacache-core" % scalacacheVersion,
  "com.github.cb372" %% "scalacache-memcached" % scalacacheVersion,
  "com.softwaremill.sttp" %% "core" % sttpVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.locationtech.geotrellis" %% "geotrellis-raster" % gtVersion,
  "org.locationtech.geotrellis" %% "geotrellis-vector" % gtVersion,
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion % "test"
)

externalResolvers := Seq(
  DefaultMavenRepository,
  "locationtech-releases" at "https://repo.locationtech.org/content/groups/releases",
  Resolver.bintrayRepo("azavea", "maven"),
  Resolver.bintrayRepo("azavea", "geotrellis")
)

assemblyMergeStrategy in assembly := {
  case "reference.conf"                       => MergeStrategy.concat
  case "application.conf"                     => MergeStrategy.concat
  case n if n.startsWith("META-INF/services") => MergeStrategy.concat
  case n if n.endsWith(".SF") || n.endsWith(".RSA") || n.endsWith(".DSA") =>
    MergeStrategy.discard
  case "META-INF/MANIFEST.MF" => MergeStrategy.discard
  case _                      => MergeStrategy.first
}

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4")
