package io.github.ilinandrii.click

import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zhttp.service.{Server => ZServer}
import zio.ZLayer
import zio.ZIO
import zio.Console

case class Server(controller: Controller):
  def start(port: Int) =
    val endpoints = List(
      controller.makeUser,
      controller.makeClick,
      controller.getClicks,
      controller.getUser
    )
    val http = ZioHttpInterpreter().toHttp(endpoints)
    ZServer.start(port, http)

object Server:
  val live             = ZLayer.fromFunction(Server.apply)
  def start(port: Int) = ZIO.serviceWithZIO[Server](_.start(port))
