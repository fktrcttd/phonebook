package models.ApiModels

import play.api.libs.json.Json

case class PhoneApiModel (title: String, number: String ) {

}
object PhoneApiModel {
  implicit val PhoneApiModel = Json.format[PhoneApiModel]
}
