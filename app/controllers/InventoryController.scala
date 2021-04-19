package controllers

import play.api.mvc._
import model.api.ApiInventory
import play.api.libs.json._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class InventoryController @Inject() (
    cc: ControllerComponents
)(implicit ex: ExecutionContext)
    extends AbstractController(cc) {

  def createInventory: Action[AnyContent] = {
    Action { request =>
      handleRequestBody(request, _ => Ok("create inventory"))
    }
  }

  def updateInventoryItem(itemId: String): Action[AnyContent] =
    Action { request =>
      handleRequestBody(request, _ => Ok(s"UPDATE item-id: $itemId"))
    }

  def getInventoryItem(
      itemId: Option[String] = None
  ): Action[AnyContent] = {

    itemId match {
      case Some(value) =>
        Action {
          Ok(s"GET: item-id: $value")
        }
      case None =>
        Action {
          Ok(s"GET: all inventory")
        }
    }

  }
  def deleteInventoryItem(
      itemId: String
  ): Action[AnyContent] =
    Action {
      Ok(s"DELETE: item-id: $itemId")
    }

  def handleRequestBody(
      request: Request[AnyContent],
      successResponse: ApiInventory => Result
  ): Result = {
    request.body.asJson
      .map { json =>
        json
          .validate[ApiInventory]
          .map(x => successResponse(x))
          .recoverTotal { _ =>
            BadRequest(s"Malformed Request")
          }
      }
      .getOrElse {
        BadRequest("No JSON Request Body Found")
      }
  }
}
