package io.github.ilinandrii.click

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import com.raquo.laminar.nodes.ReactiveHtmlElement
import io.laminext.syntax.core.*
import com.raquo.airstream.core.Source.EventSource
import com.raquo.airstream.eventbus.EventBus

object View:
  def render =
    val $user           = Var(Option.empty[User])
    val $userWriter     = $user.someWriter
    val $userExists     = $user.signal.isDefined
    val $userClicks     = EventBus[Int]()
    val $userName       = Var("")
    val $userInputFocus = EventBus[Boolean]()

    val $view = div(
      cls("container-fluid"),
      div(
        cls("row row-cols-1"),
        div(
          cls("col text-center"),
          img(
            cls("img-fluid"),
            src := "https://c.tenor.com/xzjlrhYq_lQAAAAi/cat-nyan-cat.gif",
            alt := "nyan-cat"
          )
        )
      ),
      onMountCallback { ctx =>
        Users.getUser.foreach(user => $user.set(Some(user)))(ctx.owner)
      },
      $userExists.childWhenTrue {
        div(
          cls("row row-cols-1"),
          div(
            cls("col text-center"),
            h1(
              cls("display-1"),
              color := "white",
              child.text <-- $user.signal.changes
                .collect { case Some(user) => user }
                .map(user => s"${user.name} CLICKED ${user.clicks} TIMES")
            )
          ),
          div(
            div(
              cls("col mx-auto text-center"),
              button(
                typ := "button",
                cls("btn btn-success btn-lg"),
                thisEvents(onClick.preventDefault)
                  .sample($user.signal)
                  .collect { case Some(user) => user }
                  .debugLogEvents()
                  .flatMap { user =>
                    Controller
                      .makeClick(user.id)
                      .map(clicks => user.copy(clicks = clicks))
                  }
                  .foreach(user => $userWriter.onNext(user)),
                span(
                  "CLICK ME",
                  cls("display-2")
                )
              )
            )
          )
        )
      },
      $userExists.childWhenFalse {
        div(
          cls("row row-cols-1"),
          div(
            cls("col-auto text-center mx-auto"),
            input(
              cls("form-control form-control-lg text-center shadow-none border-0 name-input"),
              maxLength       := 20,
              styleAttr       := "caret-color: transparent;",
              backgroundColor := "transparent",
              color           := "white",
              `type`          := "text",
              autoFocus       := true,
              placeholder     := "TYPE YOUR NAME",
              focus <-- $userInputFocus.events,
              onBlur.mapToTrue --> $userInputFocus.writer.delay(1),
              thisEvents(onKeyPress)
                .filter(_.keyCode == 13 /* enter */ )
                .sample($userName.signal)
                .map(name => User.Name(name))
                .flatMap(Users.makeUser)
                .foreach(user => $userWriter.onNext(user)),
              controlled(
                value <-- $userName,
                onInput.mapToValue.map(_.toUpperCase) --> $userName
              )
            )
          )
        )
      }
    )

    renderOnDomContentLoaded(dom.document.getElementById("root"), $view)
