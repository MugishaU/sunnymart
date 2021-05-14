package controllers

import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import model.api.{ApiOrder, ApiOrderStatus, ApiReceipt, ApiReceiptItem}
import Helpers.{getOrderStatus, handleRequestBody}
import model.domain.{
  Address,
  Delivery,
  DeliverySlot,
  ItemSelection,
  Order,
  OrderComplete,
  OrderPlaced,
  Unavailable
}
import play.api.libs.json.Json

import java.util.Date

@Singleton
class OrderController @Inject() (
    cc: ControllerComponents
)(implicit ex: ExecutionContext)
    extends AbstractController(cc) {

  def createOrder: Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiOrder](
        request,
        dummyCreateOrder
      )
    }

  def getOrder(orderId: String): Action[AnyContent] =
    Action {
      dummyGetOrder(orderId)
    }

  def updateOrder(
      orderId: String
  ): Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiOrder](
        request,
        dummyUpdateOrder(_, Map("orderId" -> orderId))
      )
    }

  def updateOrderStatus(
      orderId: String
  ): Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiOrderStatus](
        request,
        dummyUpdateOrderStatus(_, Map("orderId" -> orderId))
      )
    }

  def updateOrderComplete(
      orderId: String
  ): Action[AnyContent] =
    Action {
      dummyUpdateOrderComplete(orderId)
    }

  def deleteOrder(
      orderId: String
  ): Action[AnyContent] =
    Action {
      dummyDeleteOrder(orderId)
    }

  def getReceipt(
      orderId: String
  ): Action[AnyContent] =
    Action {
      dummyGetReceipt(orderId)
    }

  def dummyCreateOrder(parsedBody: ApiOrder): Result = {
    val deliverySlot = DeliverySlot(
      id = parsedBody.deliverySlotId,
      date = new Date(),
      hour = 7,
      availability = Unavailable
    )

    val address = Address(
      line1 = "133 Park Road",
      line2 = None,
      county = None,
      city = "Exeter",
      postcode = "EX4 3GT"
    )

    val delivery = Delivery(
      deliverySlot = deliverySlot,
      deliveryAddress = Some(address)
    )

    val billingAddress = if (parsedBody.billingAddress.isDefined) {
      parsedBody.billingAddress.map(address =>
        Address(
          line1 = address.line1,
          line2 = address.line2,
          county = address.county,
          city = address.city,
          postcode = address.postcode
        )
      )
    } else {
      None
    }

    val order = Order(
      orderStatus = OrderPlaced,
      customerId = parsedBody.customerId,
      delivery = delivery,
      orderItems = parsedBody.orderSelections.map(item =>
        ItemSelection(itemId = item.itemId, quantity = item.quantity)
      ),
      totalCost = 0,
      billingAddress = billingAddress
    )

    Ok(Json.toJson(order))
  }

  def dummyGetOrder(orderId: String): Result = {
    val deliverySlot = DeliverySlot(
      id = "id",
      date = new Date(),
      hour = 7,
      availability = Unavailable
    )

    val address = Address(
      line1 = "133 Park Road",
      line2 = None,
      county = None,
      city = "Exeter",
      postcode = "EX4 3GT"
    )

    val delivery = Delivery(
      deliverySlot = deliverySlot,
      deliveryAddress = Some(address)
    )

    val order = Order(
      orderStatus = OrderPlaced,
      customerId = "customerId",
      delivery = delivery,
      orderItems = Nil,
      totalCost = 0,
      billingAddress = None
    )

    Ok(Json.toJson(order))
  }

  def dummyUpdateOrder(
      parsedBody: ApiOrder,
      params: Map[String, String]
  ): Result = {

    val orderId = params("orderId")

    val deliverySlot = DeliverySlot(
      id = parsedBody.deliverySlotId,
      date = new Date(),
      hour = 7,
      availability = Unavailable
    )

    val address = Address(
      line1 = "133 Park Road",
      line2 = None,
      county = None,
      city = "Exeter",
      postcode = "EX4 3GT"
    )

    val delivery = Delivery(
      deliverySlot = deliverySlot,
      deliveryAddress = Some(address)
    )

    val billingAddress = if (parsedBody.billingAddress.isDefined) {
      parsedBody.billingAddress.map(address =>
        Address(
          line1 = address.line1,
          line2 = address.line2,
          county = address.county,
          city = address.city,
          postcode = address.postcode
        )
      )
    } else {
      None
    }

    val order = Order(
      orderStatus = OrderPlaced,
      customerId = parsedBody.customerId,
      delivery = delivery,
      orderItems = parsedBody.orderSelections.map(item =>
        ItemSelection(itemId = item.itemId, quantity = item.quantity)
      ),
      totalCost = 0,
      billingAddress = billingAddress
    )

    Ok(Json.toJson(order))
  }

  def dummyUpdateOrderStatus(
      parsedBody: ApiOrderStatus,
      params: Map[String, String]
  ): Result = {
    val orderId = params("orderId")
    val orderStatus = getOrderStatus(parsedBody.orderStatus)

    orderStatus match {
      case Some(value) =>
        val deliverySlot = DeliverySlot(
          id = "id",
          date = new Date(),
          hour = 7,
          availability = Unavailable
        )

        val address = Address(
          line1 = "133 Park Road",
          line2 = None,
          county = None,
          city = "Exeter",
          postcode = "EX4 3GT"
        )

        val delivery = Delivery(
          deliverySlot = deliverySlot,
          deliveryAddress = Some(address)
        )

        val order = Order(
          orderStatus = orderStatus.get,
          customerId = "customerId",
          delivery = delivery,
          orderItems = Nil,
          totalCost = 0,
          billingAddress = None
        )

        Ok(Json.toJson(order))
      case None =>
        BadRequest(Json.toJson(Map("error" -> "No Such Order Status")))
    }
  }

  def dummyUpdateOrderComplete(
      orderId: String
  ): Result = {
    val deliverySlot = DeliverySlot(
      id = "id",
      date = new Date(),
      hour = 7,
      availability = Unavailable
    )

    val address = Address(
      line1 = "133 Park Road",
      line2 = None,
      county = None,
      city = "Exeter",
      postcode = "EX4 3GT"
    )

    val delivery = Delivery(
      deliverySlot = deliverySlot,
      deliveryAddress = Some(address)
    )

    val order = Order(
      orderStatus = OrderComplete,
      customerId = "customerId",
      delivery = delivery,
      orderItems = Nil,
      totalCost = 0,
      billingAddress = None
    )

    Ok(Json.toJson(order))
  }

  def dummyDeleteOrder(orderId: String): Result = {
    Ok(Json.toJson(Map("message" -> s"Order $orderId deleted successfully")))
  }

  def dummyGetReceipt(orderId: String): Result = {
    val apiReceiptItem = ApiReceiptItem(
      name = "rice",
      quantity = 3,
      itemCost = 500
    )

    val receiptItemList =
      List(apiReceiptItem, apiReceiptItem.copy(name = "bread"))

    val apiReceipt = ApiReceipt(
      orderId = orderId,
      orderItems = receiptItemList,
      deliveryCost = 499
    )

    Ok(Json.toJson(apiReceipt))
  }
}
