import zio.*
import zio.ZLayer.*
import sttp.tapir.ztapir.*
import io.netty.handler.codec.http.HttpServerCodec
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zhttp.service.Server
import zhttp.service.*
import ClickModel.*

trait ClickService:
  def click(user: User): Task[Long]

  def clicks(user: User): Task[Long]

  def leaderboard: Task[List[LeaderboardRecord]]

object ClickService:
  def click(user: User) = ZIO.serviceWithZIO[ClickService](_.click(user))

  def clicks(user: User) = ZIO.serviceWithZIO[ClickService](_.clicks(user))

  def leaderboard = ZIO.serviceWithZIO[ClickService](_.leaderboard)

  val live = ZLayer.fromZIO {
    Ref.make(Map.empty[User, Long]).map { db =>
      new ClickService {
        private val users = db

        def clicks(user: User) = users.get.map(map => map.getOrElse(user, 0L))
        def click(user: User) = for {
          users <- users.updateAndGet { users =>
            val userClicks = users.getOrElse(user, 0L) + 1
            users.updated(user, userClicks)
          }
        } yield users.getOrElse(user, 0)

        def leaderboard = users.get.map { users =>
          users.map((user, clicks) => LeaderboardRecord(user, clicks)).toList
        }
      }
    }
  }
