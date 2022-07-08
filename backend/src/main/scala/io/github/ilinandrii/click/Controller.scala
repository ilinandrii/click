package io.github.ilinandrii.click

import sttp.model.Header
import sttp.tapir.Schema
import sttp.tapir.json.zio.*
import sttp.tapir.ztapir.*
import zio.Random
import zio.ZLayer
import zio.json.DeriveJsonCodec
import zio.json.DeriveJsonDecoder
import zio.json.JsonCodec
import zio.json.JsonDecoder

import java.util.UUID

import Controller.*

case class Controller(users: Users):

  def makeUser = endpoint
    .description("creates a new user")
    .post
    .in("users")
    .in(jsonBody[User.Create])
    .out(jsonBody[User])
    .out(header(Header.accessControlAllowOrigin("*")))
    .zServerLogic[Any](request => users.make(request.name).orDie)

  def getUser = endpoint
    .description("gets an existing user")
    .get
    .in("users" / path[UUID]("id"))
    .out(jsonBody[User])
    .out(header(Header.accessControlAllowOrigin("*")))
    .zServerLogic[Any](id => users.get(id).orDie)

  def getClicks = endpoint
    .description("gets user's clicks")
    .get
    .in("users" / path[UUID]("id") / "clicks")
    .out(stringBody)
    .out(header(Header.accessControlAllowOrigin("*")))
    .zServerLogic[Any](id => users.get(id).map(_.clicks.toString).orDie)

  def makeClick = endpoint
    .description("makes a click by a user")
    .post
    .in("users" / path[UUID]("id") / "clicks")
    .out(stringBody)
    .out(header(Header.accessControlAllowOrigin("*")))
    .zServerLogic[Any](id => users.click(id).map(_.toString).orDie)

object Controller:
  val live = ZLayer.fromFunction(Controller.apply)
