package io.github.ilinandrii.click

import org.scalajs.dom.document
import org.scalajs.dom.RequestMode
import scala.concurrent.duration.*
import org.scalajs.dom
import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.L
import io.laminext.fetch.*
import com.raquo.airstream.core.EventStream
import scala.util.control.NonFatal

@main def entrypoint =
  val UserUUIDKey = "userUUID"
  val user: EventStream[String] =
    val userUUID = document.cookie
      .split(";")
      .find(_.startsWith(UserUUIDKey))
      .flatMap(_.split("=").lastOption)

    userUUID match {
      case Some(uuid) => EventStream.fromValue(uuid)
      case None       => ClickClient.makeUser.debugLog()
    }

  ClickView.render(user)
