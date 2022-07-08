import scala.util.Try
import java.nio.file.{Path => JPath}
import java.nio.file.Paths
import java.nio.file.Files
import java.nio.file.StandardCopyOption

Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / scalaVersion      := "3.1.2"
ThisBuild / organization      := "io.github.ilinandrii"
ThisBuild / version           := "0.1.0-SNAPSHOT"

lazy val sharedSettings = Seq(
  libraryDependencies ++= Seq(
    "dev.zio"                     %%% "zio-json"       % "0.3.0-RC8",
    "com.softwaremill.sttp.tapir" %%% "tapir-json-zio" % "1.0.0-RC3"
  )
)

lazy val model = project
  .in(file("model"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "click-model",
    sharedSettings
  )

lazy val frontend = project
  .in(file("frontend"))
  .dependsOn(model)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name                            := "click-client",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withSourceMap(true) },
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    sharedSettings,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.2.0",
      "com.raquo"    %%% "laminar"     % "0.14.2",
      "io.laminext"  %%% "fetch"       % "0.14.3",
      "io.laminext"  %%% "core"        % "0.14.3"
    )
  )

lazy val backend = project
  .in(file("backend"))
  .dependsOn(model)
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    sharedSettings,
    name := "click-server",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % "1.0.0-RC3",
      "io.d11"                      %% "zhttp"                 % "2.0.0-RC9",
      // "io.d11"                      %% "zhttp-test"            % "2.0.0-RC9" % Test,
      "dev.zio"                     %% "zio"                   % "2.0.0-RC6",
      // "dev.zio"                     %% "zio-test"              % "2.0.0"     % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

lazy val site = project
  .in(file("site"))
  .enablePlugins(GhpagesPlugin)
  .settings(
    siteMappings ++= Seq(
      file(s"${baseDirectory.value}/index.html") -> "index.html",
      file {
        val targetPath        = (frontend / Compile / target).value.toString
        val frontScalaVersion = (frontend / scalaVersion).value
        s"$targetPath/scala-$frontScalaVersion/click-client-opt/main.js"
      } -> "click-client.js"
    ),
    makeSite       := makeSite.dependsOn(frontend / Compile / fullOptJS).value,
    git.remoteRepo := "https://github.com/ilinandrii/click.git"
  )
