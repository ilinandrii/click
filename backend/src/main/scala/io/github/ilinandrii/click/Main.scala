import zio.*
import zio.Console.printLine

object Main extends ZIOAppDefault:
  def run = for {
    port <- System
      .env("PORT")
      .map(_.flatMap(_.toIntOption))
      .someOrElse(9000).debug
    
    server <- ClickHttpServer
      .start(port)
      .provide(
        ClickController.live,
        ClickService.live,
        ClickHttpServer.live
      )
  } yield server
