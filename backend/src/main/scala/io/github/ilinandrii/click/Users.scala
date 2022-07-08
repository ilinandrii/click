package io.github.ilinandrii.click

import zio.*
import zio.ZLayer.*
import sttp.tapir.ztapir.*
import io.netty.handler.codec.http.HttpServerCodec
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zhttp.service.Server
import zhttp.service.*
import java.util.UUID

trait Users:
  def click(id: UUID): Task[Long]
  def clicks(id: UUID): Task[Long]
  def get(id: UUID): Task[User]
  def make(name: User.Name): Task[User]

object Users:

  def click(uuid: UUID)     = ZIO.serviceWithZIO[Users](_.click(uuid))
  def clicks(uuid: UUID)    = ZIO.serviceWithZIO[Users](_.clicks(uuid))
  def get(uuid: UUID)       = ZIO.serviceWithZIO[Users](_.get(uuid))
  def make(name: User.Name) = ZIO.serviceWithZIO[Users](_.make(name))

  val live = ZLayer.fromZIO {
    Ref.make(Map.empty[UUID, User]).map { db =>
      new Users {
        def make(name: User.Name) = for {
          uuid <- Random.nextUUID
          user = User(uuid, name, 0)
          _ <- db.update(users => users + (user.id -> user))
        } yield user

        def get(id: UUID) = db.get.map(_.get(id)).someOrFailException

        def clicks(id: UUID) = db.get
          .map(users => users.get(id).map(_.clicks))
          .someOrFailException

        def click(id: UUID) = db
          .updateAndGet(_.updatedWith(id)(_.map(_.click)))
          .map(_.get(id).map(_.clicks))
          .someOrFailException
      }
    }
  }
