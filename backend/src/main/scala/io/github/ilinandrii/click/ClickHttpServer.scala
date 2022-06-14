import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zhttp.service.Server
import zio.ZLayer
import zio.ZIO
import zio.Console

case class ClickHttpServer(clickController: ClickController):
  def start(port: Int) =
    val endpoints = List(
      clickController.makeUser,
      clickController.makeClick,
      clickController.getClicks,
      clickController.leaderboard
    )
    val http = ZioHttpInterpreter().toHttp(endpoints)
    Server.start(port, http)

object ClickHttpServer:
  val live             = ZLayer.fromFunction(ClickHttpServer.apply)
  def start(port: Int) = ZIO.serviceWithZIO[ClickHttpServer](_.start(port))
