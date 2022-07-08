package io.github.ilinandrii.click
import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.L
import io.laminext.fetch.*
import zio.json.*
import java.util.UUID

object Controller:
  val baseUrl = "https://io-github-ilinandrii-click.herokuapp.com"

  def getClicks(id: UUID): EventStream[Int] = {
    val url = s"$baseUrl/users/$id/clicks"
    Fetch.get(url).text.collect {
      case response if response.ok =>
        response.data.toIntOption.getOrElse {
          throw new RuntimeException("Failed to get clicks")
        }
    }
  }

  def makeClick(id: UUID): EventStream[Int] = {
    val url = s"$baseUrl/users/$id/clicks"
    Fetch.post(url).text.collect {
      case response if response.ok =>
        response.data.toIntOption.getOrElse {
          throw new RuntimeException("Failed to make a click")
        }
    }
  }

  def makeUser(user: User.Create): EventStream[User] = {
    val url = s"$baseUrl/users"
    Fetch.post(url).body(user.toJson).text.collect {
      case response if response.ok =>
        response.data.fromJson[User].getOrElse {
          throw new RuntimeException("Failed to create user")
        }
    }
  }

  def getUser(id: UUID): EventStream[User] = {
    println("getting user by id: " + id)
    val url = s"$baseUrl/users/$id"

    Fetch.get(url).text.collect {
      case response if response.ok =>
        response.data.fromJson[User].toOption.getOrElse {
          throw new RuntimeException("Failed to get user")
        }
    }

  }
