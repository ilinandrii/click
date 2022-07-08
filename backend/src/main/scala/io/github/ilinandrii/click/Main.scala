package io.github.ilinandrii.click

import zio.*
import zio.Console.printLine

object Main extends ZIOAppDefault:
  def run = for {
    port <- System
      .env("PORT")
      .map(_.flatMap(_.toIntOption))
      .someOrElse(9000)

    server <- Server
      .start(port)
      .provide(
        Controller.live,
        Server.live,
        Users.live,
      )
  } yield server
