package io.github.ilinandrii.click
import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.L
import io.laminext.fetch.*

object ClickAPI:
  val baseUrl = "https://zio-click.herokuapp.com"

  def getClicks(userUUID: String): EventStream[Int] = {
    val url = s"$baseUrl/users/$userUUID/clicks"
    Fetch.get(url).text.collect {
      case response if response.ok => response.data.toIntOption.getOrElse(0)
    }
  }

  def makeClick(userUUID: String): EventStream[Int] = {
    val url = s"$baseUrl/users/$userUUID/clicks"
    Fetch.post(url).text.collect {
      case response if response.ok => response.data.toIntOption.getOrElse(0)
    }
  }

  def makeUser: EventStream[String] = {
    val url = s"$baseUrl/users"
    Fetch.post(url).text.collect {
      case response if response.ok => response.data
    }
  }
