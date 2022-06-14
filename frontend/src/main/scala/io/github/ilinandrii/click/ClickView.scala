package io.github.ilinandrii.click

import com.raquo.laminar.api.L._
import com.raquo.laminar.api.L
import zio.*
import org.scalajs.dom
import com.raquo.laminar.nodes.ReactiveHtmlElement
import scala.collection.mutable
import zio.Console.*

object ClickView:
  def render(initialClickCount: Int, clickHandler: => Task[Int]): Task[Unit] = {

    val $clickCounter = Var(initialClickCount)

    class ClickButton private():
      private val $display = Var(false)
      private val $button = button(
        "CLICK ME",
        typ       := "button",
        styleAttr := "font-size:5rem;", // border-radius:10%;
        cls.toggle("d-none") <-- $display.signal.map(!_).debugLogEvents(),
        cls("btn btn-success btn-lg"),
        onClick.mapTo(()) --> ClickButton.clickObserver
      )
      def view = $button
      def isShown = $display.now()
      def show: ClickButton =
        $display.set(true)
        this
      def hide: ClickButton =
        $display.set(false)
        this

    object ClickButton:
      private val buttons = Var(List.empty[ClickButton])
      private def randomButton = ZIO.randomWith { random =>
        val shown = buttons.now().filter(_.isShown)
        val hidden = buttons.now().filterNot(_.isShown)
        
        shown.map(_.hide)

        random
          .shuffle(hidden)
          .map(_.headOption.map(_.show))
      }

      def nextButton =
        val button = new ClickButton
        buttons.update(button :: _)
        button

      val clickObserver = Observer[Unit] { _ =>
        val effect = for {
          clicks <- clickHandler
          _      <- printLine("Buttons: " + buttons.now().mkString("\n"))
          random <- randomButton
          _      <- printLine("Random button: " + random)
        } yield clicks

        Runtime.default.unsafeRunAsyncWith(effect) { exit =>
          exit.toEither match {
            case Left(e)       => println(e.getMessage)
            case Right(clicks) => $clickCounter.set(clicks)
          }
        }
      }

    val leftTop, leftBottom, center, rightTop, rightBottom = ClickButton.nextButton

    val $view = div(
      cls("container-fluid"),
      div(
        cls("row row-cols-3"),
        div(
          cls("col-4 mx-auto text-center"),
          leftTop.view
        ),
        div(
          cls("col-4 mx-auto text-center"),
          img(
            src := "https://c.tenor.com/xzjlrhYq_lQAAAAi/cat-nyan-cat.gif",
            alt := "nyan-cat"
          )
        ),
        div(
          cls("col-4 mx-auto text-center"),
          rightTop.view
        )
      ),
      div(
        cls("row row-cols-1"),
        div(
          cls("col text-center"),
          h1(
            cls("display-1"),
            color := "white",
            child.text <-- $clickCounter.toObservable
              .map(count => s"YOU'VE CLICKED $count TIMES")
          )
        )
      ),
      div(
        cls("row row-cols-3"),
        div(
          cls("col-4 mx-auto text-center"),
          leftBottom.view
        ),
        div(
          cls("col-4 mx-auto text-center"),
          center.show.view
        ),
        div(
          cls("col-4 mx-auto text-center"),
          rightBottom.view
        )
      )
    )

    for {
      _ <- ZIO.attempt(center.show)
      _ <- ZIO.attempt {
        L.render(container = dom.document.getElementById("root"), rootNode = $view)
      }
    } yield ()
  }
