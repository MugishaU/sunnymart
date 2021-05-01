package controllers

import play.api.mvc._
import model.api.ApiInventory

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

import Helpers.handleRequestBody

@Singleton
class InventoryController @Inject() (
    cc: ControllerComponents
)(implicit ex: ExecutionContext)
    extends AbstractController(cc) {

  def createInventory: Action[AnyContent] = {
    Action { request =>
      handleRequestBody[ApiInventory](
        request,
        (x, Nil) => Ok("create inventory")
      )
    }
  }

  def updateInventoryItem(itemId: String): Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiInventory](
        request,
        (x, Nil) => Ok(s"UPDATE item-id: $itemId")
      )
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
