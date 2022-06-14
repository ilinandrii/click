import scala.util.Try

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val frontend = project
  .in(file("./frontend"))
  .enablePlugins(ScalaJSBundlerPlugin, ScalaJSPlugin)
  .settings(
    name                            := "zio-click-client",
    scalaVersion                    := "3.1.2",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withSourceMap(false) },
    webpackDevServerPort                                    := 8000,
    webpackDevServerExtraArgs                               := Seq("--mode development"),
    Compile / npmDependencies += "abortcontroller-polyfill" -> "1.7.3",
    libraryDependencies ++= Seq(
      "org.scala-js"                  %%% "scalajs-dom"     % "2.2.0",
      "com.raquo"                     %%% "laminar"         % "0.14.2",
      "io.github.cquiroz"             %%% "scala-java-time" % "2.4.0",
      "com.softwaremill.sttp.client3" %%% "core"            % "3.6.2",
      "dev.zio"                       %%% "zio"             % "2.0.0-RC6",
      "dev.zio"                       %%% "zio-test"        % "2.0.0-RC6" % Test,
      "dev.zio"                       %%% "zio-json"        % "0.3.0-RC8",
      "com.softwaremill.sttp.tapir"    %% "tapir-json-zio"  % "1.0.0-RC3"
    )
  )

lazy val backend = project
  .in(file("./backend"))
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name         := "zio-click-server",
    dockerExposedPorts += sys.env.get("PORT")
    .flatMap(portString => Try(portString.toInt).toOption)
    .getOrElse(9000),
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
