package controllers

import play.api.mvc._
import play.api.libs.json.Json

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import Helpers.{getItemCategory, handleRequestBody}
import config.DynamoDb.getItem
import model.domain.{GrainsAndPasta, Inventory, Item, ItemDetail}
import model.api.ApiItem

import java.util.UUID

@Singleton
class InventoryController @Inject() (
    cc: ControllerComponents
)(implicit ex: ExecutionContext)
    extends AbstractController(cc) {

  def createInventoryItem: Action[AnyContent] = {
    Action { request =>
      handleRequestBody[ApiItem](
        request,
        dummyCreateInventoryItem
      )
    }
  }

  def updateInventoryItem(itemId: String): Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiItem](
        request,
        dummyUpdateInventoryItem(_, Map("itemId" -> itemId))
      )
    }

  def getInventoryItem(itemId: Option[String] = None): Action[AnyContent] =
    Action {
      dummyGetInventory(itemId)
    }

  def deleteInventoryItem(itemId: String): Action[AnyContent] =
    Action {
      dummyDeleteInventoryItem(itemId)
    }

  def dummyGetInventory(itemId: Option[String]): Result = {
    val item = Item(
      id = "id",
      name = "rice",
      category = GrainsAndPasta,
      details = List(ItemDetail(header = "weight", content = "500g")),
      price = 100,
      essentialStatus = true,
      quantity = 500
    )

    itemId match {
      case Some(id) =>
        val maybeItem = getItem(
          primaryKeyName = "id",
          primaryKeyValue = id,
          tableName = "sunnymart-inventory"
        )
        maybeItem match {
          case Some(value) => Ok(Json.toJson(value))
          case None =>
            NotFound(
              Json.toJson(Map("message" -> "No item, See logs for detail"))
            )
        }
      case None =>
        Ok(
          Json.toJson(
            Inventory(List(item, item.copy(id = "id2"), item.copy(id = "id3")))
          )
        )
    }

  }

  def dummyCreateInventoryItem(parsedBody: ApiItem): Result = {

    val itemCategory = getItemCategory(parsedBody.category)

    itemCategory match {
      case Some(category) =>
        val item = Item(
          id = UUID.randomUUID.toString,
          name = parsedBody.name,
          category = category,
          details = parsedBody.details.map(detail =>
            ItemDetail(detail.header, detail.content)
          ),
          price = parsedBody.price,
          essentialStatus = parsedBody.essentialStatus,
          quantity = parsedBody.quantity
        )
        Ok(Json.toJson(item))
      case None =>
        BadRequest(Json.toJson(Map("error" -> "No Such Item Category")))
    }
  }

  def dummyUpdateInventoryItem(
      parsedBody: ApiItem,
      params: Map[String, String]
  ): Result = {
    val itemId = params("itemId")
    val itemCategory = getItemCategory(parsedBody.category)

    itemCategory match {
      case Some(category) =>
        val item = Item(
          id = itemId,
          name = parsedBody.name,
          category = category,
          details = parsedBody.details.map(detail =>
            ItemDetail(detail.header, detail.content)
          ),
          price = parsedBody.price,
          essentialStatus = parsedBody.essentialStatus,
          quantity = parsedBody.quantity
        )
        Ok(Json.toJson(item))
      case None =>
        BadRequest(Json.toJson(Map("error" -> "No Such Item Category")))
    }
  }

  def dummyDeleteInventoryItem(itemId: String): Result =
    Ok(
      Json.toJson(Map("message" -> s"Successful deletion of item-id: $itemId"))
    )
}
