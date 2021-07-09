package controllers

import play.api.mvc._
import play.api.libs.json.{Json, OWrites}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import Helpers.handleRequestBody
import config.DynamoDb.{PrimaryKey, getDynamoItem, scanDynamoTable}
import model.domain.{GrainsAndPasta, Inventory, Item, ItemCategory, ItemDetail}
import model.api.ApiItem
import model.aws.AwsItem

import java.util.UUID
import scala.collection.mutable

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

  def getInventoryItem(itemId: String): Action[AnyContent] =
    Action {
      getInventoryItemFromDb(itemId)
    }

  def getAllInventoryItems: Action[AnyContent] = {
    Action{
      getAllInventoryItemsDummy
    }
  }

  def deleteInventoryItem(itemId: String): Action[AnyContent] =
    Action {
      dummyDeleteInventoryItem(itemId)
    }

  def getAllInventoryItemsDummy: Result = {
    val c = scanDynamoTable[AwsItem]("sunnymart-inventory")
    Ok(c)
  }

  def getInventoryItemFromDb(itemId: String): Result = {
    val maybeItem = getDynamoItem[AwsItem](
      primaryKey = PrimaryKey("id", itemId),
      tableName = "sunnymart-inventory"
    ) //TODO Move this to repository layer method
    maybeItem match {
      case Right(awsItem) =>
        val maybeCategory = ItemCategory(awsItem.category)
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
  }

  def dummyCreateInventoryItem(parsedBody: ApiItem): Result = {

    val itemCategory = ItemCategory(parsedBody.category)

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
    val itemCategory = ItemCategory(parsedBody.category)

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
