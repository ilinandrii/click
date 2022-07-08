package io.github.ilinandrii.click

import java.util.UUID
import zio.json.JsonCodec
import zio.json.DeriveJsonCodec
import zio.json.JsonEncoder
import zio.json.JsonDecoder
import sttp.tapir.Schema

final case class User(id: UUID, name: User.Name, clicks: Long) {
  def click = copy(clicks = clicks + 1)
}

object User:
  given JsonCodec[User] = DeriveJsonCodec.gen
  given Schema[User] = Schema.derived

  opaque type Name = String
  object Name:
    def apply(name: String): Name = name
    given JsonCodec[Name]         = JsonCodec.string
    given Schema[User.Name]       = Schema.string

  case class Create(name: User.Name)
  object Create:
    given JsonCodec[Create] = DeriveJsonCodec.gen
    given Schema[Create]    = Schema.derived

