package io.github.ilinandrii.click

import org.scalajs.dom.*
import com.raquo.airstream.core.EventStream
import zio.json.*
import java.util.UUID
import scala.util.Try
import com.raquo.airstream.state.Var

object Users {
  private final val UserKey = "user"

  private def getUserId = for {
    idString <- Option(window.localStorage.getItem(UserKey))
    id       <- Try(UUID.fromString(idString)).toOption
  } yield id

  def getUser = getUserId match {
    case Some(id) => Controller.getUser(id)
    case None     => EventStream.empty
  }

  private def setUserId(id: UUID) =
    window.localStorage.setItem(UserKey, id.toString)

  def makeUser(name: User.Name): EventStream[User] =
    Controller.makeUser(User.Create(name)).map { user =>
      setUserId(user.id)
      user
    }

}
