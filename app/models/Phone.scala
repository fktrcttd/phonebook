package models

import play.api.libs.json.Json

case class Phone (id: Long, title: String, number: String)

object Phone {
  implicit val phoneFormat = Json.format[Phone]
}
