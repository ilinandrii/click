package io.github.ilinandrii.click

import zio.ZIOAppDefault
import zio.ZIO
import sttp.client3.*
import zio.Console.*
import org.scalajs.dom.document
import org.scalajs.dom.RequestMode
import scala.concurrent.duration._
import zio.Task
import zio.*
import org.scalajs.dom
import com.raquo.laminar.api.L._
import com.raquo.laminar.api.L

object Main extends ZIOAppDefault:
  def run =
    val UserUUIDKey = "userUUID"
    val baseUrl     = "https://zio-click.herokuapp.com"
    type FetchRequest  = Request[Either[String, String], Any]
    type FetchResponse = Response[Either[String, String]]

    def sendRequest(request: FetchRequest): Task[FetchResponse] =
      val fetchOptions = FetchOptions.Default.copy(mode = Some(RequestMode.cors))
      val backend      = FetchBackend(fetchOptions)
      ZIO.fromFuture { implicit ec => backend.send(request) }

    def makeUser = for {
      request  <- ZIO.succeed(basicRequest.post(uri"$baseUrl/users"))
      response <- sendRequest(request)
      userUUID <- ZIO
        .fromEither(response.body)
        .orElseFail(new RuntimeException("Failed to register user"))
      _ <- ZIO.attempt(document.cookie = s"userUUID=$userUUID")
    } yield userUUID

    def getUser = for {
      cookies <- ZIO.attempt(document.cookie)
      userUUID <- ZIO
        .getOrFail {
          cookies
            .split(";")
            .find(_.startsWith(UserUUIDKey))
            .flatMap(_.split("=").lastOption)
        }
    } yield userUUID

    def makeClick(userUUID: String) = for {
      request  <- ZIO.succeed(basicRequest.post(uri"$baseUrl/users/$userUUID/clicks"))
      response <- sendRequest(request)
      clickCount <- ZIO
        .fromEither(response.body)
        .orElseFail(new RuntimeException("Failed to click"))
        .mapAttempt(_.toInt)
    } yield clickCount

    def getClicks(userUUID: String) = for {
      request  <- ZIO.succeed(basicRequest.get(uri"$baseUrl/users/$userUUID/clicks"))
      response <- sendRequest(request)
      clickCount <- ZIO
        .fromEither(response.body)
        .orElseFail(new RuntimeException("Failed to click"))
        .mapAttempt(_.toInt)
    } yield clickCount

    for {
      userUUID   <- getUser.orElse(makeUser)
      _          <- printLine(userUUID)
      clickCount <- getClicks(userUUID)
      _          <- printLine(clickCount)
      _ <- ClickView.render(initialClickCount = clickCount, clickHandler = makeClick(userUUID))
    } yield ()

    
