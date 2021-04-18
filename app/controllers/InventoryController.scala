package controllers

import play.api.mvc._
import model.api.Inventory
import play.api.libs.json._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class InventoryController @Inject() (
    cc: ControllerComponents
)(implicit ex: ExecutionContext)
    extends AbstractController(cc) {

  def handle(request: Request[AnyContent]): Result = {
    request.body.asJson
      .map { json =>
        json
          .validate[Inventory]
          .map(_ => Ok("create inventory"))
          .recoverTotal { _ =>
            BadRequest(s"Malformed Request")
          }
      }
      .getOrElse {
        BadRequest("No Request Body Found")
      }
  }
  //todo Make JSON to be validated insertable (i.e. not all Inventory validation)
//todo Be able to add a custom function on the "okay" route to do whatever you want
// rename handle method
  def createInventory: Action[AnyContent] = {
    Action { request => handle(request) }
  }

  def updateInventoryItem(itemId: String): Action[AnyContent] =
    Action {
      Ok(s"UPDATE item-id: $itemId")
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
}
