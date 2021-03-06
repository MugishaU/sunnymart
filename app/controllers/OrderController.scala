package controllers

import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import model.api.{ApiOrder, ApiOrderStatus, ApiReceipt, ApiReceiptItem}
import Helpers.handleRequestBody
import config.DynamoDb.{PrimaryKey, SortKey, getDynamoItem}
import model.aws.AwsOrder
import model.domain.{
  Address,
  Delivery,
  DeliverySlot,
  DeliverySlotStatus,
  ItemSelection,
  Order,
  OrderComplete,
  OrderPlaced,
  OrderStatus,
  Unavailable
}
import play.api.libs.json.Json

import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

  def getOrder(customerId: String, orderId: String): Action[AnyContent] =
    Action {
      dummyGetOrder(customerId, orderId)
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
      date = "date",
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
      id = "id",
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

  def dummyGetOrder(customerId: String, orderId: String): Result = {
    val maybeOrder = getDynamoItem[AwsOrder](
      primaryKey = PrimaryKey("id", orderId),
      sortKey = Some(SortKey("customerId", customerId)),
      tableName = "sunnymart-orders"
    )

    maybeOrder match {
      case Left(error) =>
        Status(error("statusCode").toInt)(
          Json.toJson(Map("error" -> error("errorMessage")))
        )
      case Right(awsOrder) =>
        val maybeOrderStatus = OrderStatus(awsOrder.orderStatus)
        val maybeDeliverySlotStatus = DeliverySlotStatus(
          awsOrder.delivery.deliverySlot.availability
        )

        (maybeOrderStatus, maybeDeliverySlotStatus) match {
          case (Some(orderStatus), Some(deliverySlotStatus)) =>
            val deliverySlot = DeliverySlot(
              id = awsOrder.delivery.deliverySlot.id,
              date = awsOrder.delivery.deliverySlot.date,
              hour = awsOrder.delivery.deliverySlot.hour,
              availability = deliverySlotStatus
            )
            val delivery = Delivery(
              deliverySlot = deliverySlot,
              deliveryAddress = awsOrder.delivery.deliveryAddress
            )
            val order = Order(
              id = awsOrder.id,
              orderStatus = orderStatus,
              customerId = awsOrder.customerId,
              delivery = delivery,
              orderItems = awsOrder.orderItems,
              totalCost = awsOrder.totalCost,
              billingAddress = awsOrder.billingAddress
            )

            Ok(Json.toJson(order))
          case (_, _) =>
            InternalServerError(
              Json.toJson(Map("error" -> "Failure to parse DynamoDB item"))
            )
        }
    }
  }

  def dummyUpdateOrder(
      parsedBody: ApiOrder,
      params: Map[String, String]
  ): Result = {

    val orderId = params("orderId")

    val deliverySlot = DeliverySlot(
      id = parsedBody.deliverySlotId,
      date = "date",
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
      id = "id",
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
    val maybeOrderStatus = OrderStatus(parsedBody.orderStatus)
    val date = LocalDate.now()

    maybeOrderStatus match {
      case Some(orderStatus) =>
        val deliverySlot = DeliverySlot(
          id = "id",
          date = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
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
          id = "id",
          orderStatus = orderStatus,
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
      date = "date",
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
      id = "id",
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
