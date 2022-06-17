import scala.util.Try
import java.nio.file.{Path => JPath}
import java.nio.file.Paths
import java.nio.file.Files
import java.nio.file.StandardCopyOption

Global / onChangedBuildSource := ReloadOnSourceChanges
ThisProject / scalaVersion    := "3.1.2"
ThisProject / organization    := "io.github.ilinandrii"
ThisProject / version         := "0.1.0-SNAPSHOT"

lazy val frontend = project
  .in(file("./frontend"))
  .enablePlugins(ScalaJSPlugin, GhpagesPlugin)
  .settings(
    name                            := "zio-click-client",
    scalaVersion                    := "3.1.2",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withSourceMap(false) },
    siteMappings ++= Seq(
      file {
        val targetPath = (Compile / target).value.toString
        s"$targetPath/scala-${scalaVersion.value}/zio-click-client-opt/main.js"
      } -> "click-client.js"
    ),
    makeSite       := makeSite.dependsOn(Compile / fullOptJS).value,
    git.remoteRepo := "https://github.com/ilinandrii/zio-click.git",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.2.0",
      "com.raquo"    %%% "laminar"     % "0.14.2",
      "io.laminext"  %%% "fetch"       % "0.14.3",
      "io.laminext"  %%% "core"        % "0.14.3"
    )
  )

lazy val backend = project
  .in(file("./backend"))
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name         := "zio-click-server",
    scalaVersion := "3.1.2",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % "1.0.0-RC3",
      "io.d11"                      %% "zhttp"                 % "2.0.0-RC9",
      "io.d11"                      %% "zhttp-test"            % "2.0.0-RC9" % Test,
      "dev.zio"                     %% "zio"                   % "2.0.0-RC6",
      "dev.zio"                     %% "zio-test"              % "2.0.0-RC6" % Test,
      "dev.zio"                     %% "zio-json"              % "0.3.0-RC8",
      "com.softwaremill.sttp.tapir" %% "tapir-json-zio"        % "1.0.0-RC3"
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
