import zio.json.*
import sttp.tapir.Schema

object ClickModel {
  opaque type User = String

  object User:
    def apply(uuid: String): User = uuid

  extension (user: User) def uuid: String = user

  case class LeaderboardRecord(user: User, clicks: Long)
  object LeaderboardRecord {
    given schema: Schema[LeaderboardRecord] = Schema.derived[LeaderboardRecord]
    given encoder: JsonEncoder[LeaderboardRecord] = DeriveJsonEncoder.gen
    given decoder: JsonDecoder[LeaderboardRecord] = DeriveJsonDecoder.gen
  }
}
