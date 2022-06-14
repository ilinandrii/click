import sttp.tapir.ztapir.*
import zio.Random
import zio.ZLayer
import ClickModel.*
import sttp.model.Header
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*

case class ClickController(clickService: ClickService):
  def makeUser = endpoint
    .name("register clicker")
    .post
    .in("users")
    .out(stringBody)
    .out(header(Header.accessControlAllowOrigin("*")))
    .zServerLogic[Any](_ => Random.nextUUID.map(_.toString))

  def getClicks = endpoint
    .name("get clicks")
    .get
    .in("users" / path[String]("uuid") / "clicks")
    .out(stringBody)
    .out(header(Header.accessControlAllowOrigin("*")))
    .zServerLogic[Any](uuid => clickService.clicks(User(uuid)).map(_.toString).orDie)

  def makeClick = endpoint
    .name("perform click")
    .post
    .in("users" / path[String]("uuid") / "clicks")
    .out(stringBody)
    .out(header(Header.accessControlAllowOrigin("*")))
    .zServerLogic[Any](uuid => clickService.click(User(uuid)).map(_.toString).orDie)

  def leaderboard = endpoint
    .name("get leaderboard")
    .get
    .out(jsonBody[List[LeaderboardRecord]])
    .out(header(Header.accessControlAllowOrigin("*")))
    .zServerLogic[Any](_ => clickService.leaderboard.orDie)

object ClickController:
  val live = ZLayer.fromFunction(ClickController.apply)
