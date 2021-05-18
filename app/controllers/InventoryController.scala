package controllers

import play.api.mvc._
import play.api.libs.json.Json

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import Helpers.{getItemCategory, handleRequestBody}
import config.DynamoDb.getItem
import model.domain.{GrainsAndPasta, Inventory, Item, ItemDetail}
import model.api.ApiItem
import model.aws.AwsItem

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
        val maybeItem = getItem[AwsItem](
          primaryKeyName = "id",
          primaryKeyValue = id,
          tableName = "sunnymart-inventory"
        )
        maybeItem match {
          case Right(awsItem) =>
            val maybeCategory = getItemCategory(awsItem.category)
            maybeCategory match {
              case Some(category) =>
                val item = Item(
                  id = awsItem.id,
                  name = awsItem.name,
                  category = category,
                  details = awsItem.details,
                  price = awsItem.price,
                  essentialStatus = awsItem.essentialStatus,
                  quantity = awsItem.quantity
                )
                Ok(Json.toJson(item))
              case None =>
                InternalServerError(
                  Json.toJson(Map("error" -> "Failure to parse DynamoDB item"))
                )
            }

          case Left(error) =>
            Status(error("statusCode").toInt)(
              Json.toJson(Map("error" -> error("errorMessage")))
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
