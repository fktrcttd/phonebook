package controllers

import javax.inject.Inject
import models.ApiModels.PhoneApiModel
import models.{Phone, PhoneRepository}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{AbstractController, BaseController, ControllerComponents, MessagesAbstractController, MessagesControllerComponents}

import scala.concurrent.ExecutionContext

class PhoneController @Inject()(repo: PhoneRepository,cc: ControllerComponents
                                )
  extends AbstractController(cc) {
  
  
  def list() = Action{
    Ok( Json.toJson(repo.list()))
  }
  
  //search
  def getById(id:Long) = Action{
    var json = Json.toJson(repo.readById(id))
    Ok(json)
  }

  def searchByTitle (title: String) = Action{
    val json = Json.toJson(repo.searchByName(title))
    Ok(json)
  }

  def searchByNumber (number: String) = Action{
    val json = Json.toJson(repo.searchByNumber(number))
    Ok(json)
  }
  
  def create() = Action { implicit request =>
    // this will fail if the request body is not a valid json value
    val json = request.body.asJson.get
    json.validate[PhoneApiModel] match {
      case success: JsSuccess[PhoneApiModel] => {
        val title = success.get.title
        val number = success.get.number
        repo.create(new Phone(0,title, number))
        Ok("Successfully created!")
      }
      case JsError(_) => BadRequest("Validation failed!")
    }
  }
  
  def edit(id: Long) = Action{ implicit request => 
    val json = request.body.asJson.get
    json.validate[PhoneApiModel] match {
      case success: JsSuccess[PhoneApiModel] => {
        val title = success.get.title
        val number = success.get.number
        repo.edit(id, title, number)
        Ok("Successfully updated!")
      }
      case JsError(_) => BadRequest("Invalid json body!")
    }
  }
  
  def delete(id:Long) = Action{
    repo.delete(id)
    Ok("Successfully deleted")
  }
}
