package controllers

import java.nio.file.Paths

import akka.actor.ActorSystem
import com.google.inject.ImplementedBy
import javax.inject.Inject
import models.ApiModels.PhoneApiModel
import models.{Phone, PhoneRepository, PhoneValidator}
import play.api.Play
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, BaseController, ControllerComponents, MessagesAbstractController, MessagesControllerComponents}
import services.FileAsyncIO

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.concurrent.CustomExecutionContext

@ImplementedBy(classOf[PhoneExecutionContextImpl])
trait PhoneExecutionContext extends ExecutionContext

class PhoneExecutionContextImpl @Inject() (system: ActorSystem)
  extends CustomExecutionContext(system, "phones.executor")
    with PhoneExecutionContext

class PhoneController @Inject() (myExecutionContext: PhoneExecutionContext, repo: PhoneRepository, cc: ControllerComponents)
  extends AbstractController(cc) {


  def list (): Action[AnyContent] = Action {
    Ok(Json.toJson(repo.list()))
  }
  
  def getById (id: Long): Action[AnyContent] = Action {
    var json = Json.toJson(repo.findById(id))
    Ok(json)
  }

  def searchByTitle (term: String): Action[AnyContent] = Action {
    val list = repo.searchByName(term)
    val json = Json.toJson(list)
    Ok(json)
  }

  def searchByNumber (term: String): Action[AnyContent] = Action {
    val json = Json.toJson(repo.searchByNumber(term))
    Ok(json)
  }

  def create (): Action[AnyContent] = Action { implicit request =>
    // this will fail if the request body is not a valid json value
    val json = request.body.asJson.get
    json.validate[PhoneApiModel] match {
      case success: JsSuccess[PhoneApiModel] =>
        val telApiModel: PhoneApiModel = success.get
        val telIsValid  = PhoneValidator.telIsValid(telApiModel)
        val alreadyExist = repo.getByNumber(telApiModel.number) != null
        
        if (!telIsValid || alreadyExist){
          BadRequest("Bad phone format or title is empty")
        }else{
          repo.create(new Phone(0, telApiModel.title, telApiModel.number))
          Ok("Successfully created!")  
        }
      case JsError(_) => BadRequest("Validation failed!")
    }
  }

  def edit (id: Long): Action[AnyContent] = Action { implicit request =>
    val json = request.body.asJson.get
    json.validate[PhoneApiModel] match {
      case success: JsSuccess[PhoneApiModel] =>
        val phoneApiModel = success.get
        val telIsValid  = PhoneValidator.telIsValid(phoneApiModel)
        if (!telIsValid){
          BadRequest("Bad phone format or title is empty")
        }
        repo.edit(id, phoneApiModel.title, phoneApiModel.number)
        Ok("Successfully updated!")
      case JsError(_) => BadRequest("Invalid json body!")
    }
  }

  def delete (id: Long): Action[AnyContent] = Action {
    repo.delete(id)
    Ok("Successfully deleted")
  }
  
  def writeAll: Action[AnyContent] = Action.async {
    val future: Future[Unit] = FileAsyncIO.writeText("public/writeAll.txt",Json.toJson(repo.list()).toString())(myExecutionContext)
    future.map(_ => Ok("Success"))(myExecutionContext)
  }
    
}
