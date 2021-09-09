package controllers

import javax.inject.Inject
import javax.inject.Singleton
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext
import model.api.{ApiItemSelection, ApiShoppingCart, ApiUpdateQuantity}
import model.domain.{ItemSelection, ShoppingCart}
import db.DynamoDb.deleteItem

import java.util.UUID
import java.time.LocalDateTime
import scala.util.{Failure, Success}

@Singleton
class CartController @Inject() (
    cc: ControllerComponents
)(implicit ex: ExecutionContext)
    extends AbstractController(cc) {

  val allItems = Nil

  def deleteApi(): Result = {
    val item = "R20210907992DAC61B"

    allItems.foreach(deleteItem)

    Ok("done")

//    v match {
//      case Failure(exception) => InternalServerError(s"Failed to Delete $item")
//      case Success(value) =>Ok(s"deleted $item")
//    }
  }

  def refundDelete: Action[AnyContent] = {
    Action {
      deleteApi()
    }
  }

  def createCart: Action[AnyContent] = {
    Action { request =>
      handleRequestBody[ApiShoppingCart](
        request,
        dummyCreate
      )
    }
  }

  def addItemSelection(cartId: String): Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiItemSelection](
        request,
        dummyAdd(_, Map("cartId" -> cartId))
      )
    }

  def updateItemSelection(
      cartId: String,
      itemId: String
  ): Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiUpdateQuantity](
        request,
        dummyUpdate(_, Map("cartId" -> cartId, "itemId" -> itemId))
      )
    }

  def deleteItemSelection(
      cartId: String,
      itemId: String
  ): Action[AnyContent] =
    Action {
      dummyDelete(cartId, itemId)
    }

  def dummyCreate(parsedBody: ApiShoppingCart): Result = {
    val cart = ShoppingCart(
      id = UUID.randomUUID.toString,
      customerId = parsedBody.customerId,
      cartSelections = parsedBody.cartSelections.map(selection =>
        ItemSelection(selection.itemId, selection.quantity)
      ),
      cartCost = 0,
      lastActive = LocalDateTime.now()
    )
    Ok(Json.toJson(cart))
  }

  def dummyAdd(
      parsedBody: ApiItemSelection,
      params: Map[String, String]
  ): Result = {
    val cartId = params("cartId")

    val cart = ShoppingCart(
      id = cartId,
      customerId = "customerId",
      cartSelections = List(
        ItemSelection(
          parsedBody.itemId,
          parsedBody.quantity
        )
      ),
      cartCost = 0,
      lastActive = LocalDateTime.now()
    )
    Ok(Json.toJson(cart))
  }

  def dummyUpdate(
      parsedBody: ApiUpdateQuantity,
      params: Map[String, String]
  ): Result = {
    val cartId = params("cartId")
    val itemId = params("itemId")

    val cart = ShoppingCart(
      id = cartId,
      customerId = "customerId",
      cartSelections = List(
        ItemSelection(
          itemId,
          parsedBody.quantity
        )
      ),
      cartCost = 0,
      lastActive = LocalDateTime.now()
    )
    Ok(Json.toJson(cart))
  }

  def dummyDelete(
      cartId: String,
      itemId: String
  ): Result = {

    val cart = ShoppingCart(
      id = cartId,
      customerId = "customerId",
      cartSelections = List(
        ItemSelection(
          itemId,
          0
        )
      ),
      cartCost = 0,
      lastActive = LocalDateTime.now()
    )
    Ok(Json.toJson(cart))
  }

}
