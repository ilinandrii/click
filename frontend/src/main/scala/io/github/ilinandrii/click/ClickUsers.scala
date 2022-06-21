package io.github.ilinandrii.click

import org.scalajs.dom.*
import com.raquo.airstream.core.EventStream

object ClickUser {
  private final val UserUUIDKey = "userUUID"

  private def getUUID: Option[String] = document.cookie
    .split(";")
    .find(_.startsWith(UserUUIDKey))
    .flatMap(_.split("=").lastOption)

  private def setUUID(uuid: String) =
    document.cookie = s"$UserUUIDKey=$uuid"
    uuid

  def getUser: EventStream[String] = getUUID match {
    case Some(uuid) => EventStream.fromValue(uuid)
    case None       => ClickAPI.makeUser.map(setUUID)
  }
}
