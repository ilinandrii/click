package io.github.ilinandrii.click

import com.raquo.laminar.api.L._
import com.raquo.laminar.api.L
import org.scalajs.dom
import com.raquo.laminar.nodes.ReactiveHtmlElement

object ClickView:
  def render(user: EventStream[String]) = {

    val clickBus = EventBus[Unit]
    val clickStream = user.flatMap { user =>
      val clickEvents = clickBus.events
        .flatMap(_ => ClickClient.makeClick(user).debugLogEvents())
      EventStream.merge(ClickClient.getClicks(user), clickEvents)
    }

    class ClickButton:
      def view = button(
        typ := "button",
        cls("btn btn-success btn-lg"),
        onClick.mapTo(()) --> clickBus.writer,
        span(
          "CLICK ME",
          cls("display-2")
        )
      )

    val center = new ClickButton()

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
      div(
        cls("row row-cols-1"),
        div(
          cls("col text-center"),
          h1(
            cls("display-1"),
            color := "white",
            child.text <-- clickStream.toObservable
              .map(count => s"YOU'VE CLICKED $count TIMES")
          )
        )
      ),
      div(
        cls("row row-cols-1"),
        div(
          cls("col mx-auto text-center"),
          center.view
        )
      )
    )

    renderOnDomContentLoaded(dom.document.getElementById("root"), $view)
  }
