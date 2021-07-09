package controllers

import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import model.api.ApiDeliverySlotStatus
import db.DynamoDb.scanDynamoTable
import db.{Equals, FilterExpression}
import model.aws.AwsDeliverySlot
import model.domain.{
  Address,
  Available,
  Delivery,
  DeliverySchedule,
  DeliverySlot,
  DeliverySlotStatus,
  ItemSelection,
  Order,
  OrderPlaced
}
import play.api.libs.json.Json

@Singleton
class DeliveryController @Inject() (
    cc: ControllerComponents
)(implicit ex: ExecutionContext)
    extends AbstractController(cc) {

  def getAvailableSlots: Action[AnyContent] =
    Action {
      getAvailableSlotsFromDb
    }

  def getDeliverySchedule(
      date: Option[String],
      location: Option[String]
  ): Action[AnyContent] = {
    Action {
      dummyGetDeliverySchedule(date, location)
    }
  }

  def updateDeliverySlotStatus(
      slotId: String
  ): Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiDeliverySlotStatus](
        request,
        dummyUpdate(_, Map("slotId" -> slotId))
      )
    }

  def getAvailableSlotsFromDb: Result = {
    val filterExpression = FilterExpression(
      filterKey = "availability",
      filterValue = "Available",
      filterOperator = Equals
    )
    val scanResult = scanDynamoTable[AwsDeliverySlot](
      "sunnymart-delivery-slots",
      Some(filterExpression)
    )
    Ok(Json.toJson(scanResult))
  }

  def dummyGetDeliverySchedule(
      date: Option[String],
      location: Option[String]
  ): Result = {
    val deliverySlot = DeliverySlot(
      id = "id",
      date = "new Date()",
      hour = 16,
      availability = Available
    )

    val delivery = Delivery(
      deliverySlot = deliverySlot,
      deliveryAddress = None
    )

    val order = Order(
      id = "id",
      orderStatus = OrderPlaced,
      customerId = "customerId",
      delivery = delivery,
      orderItems = List.empty[ItemSelection],
      totalCost = 1234,
      billingAddress = None
    )

    val deliverySchedule = DeliverySchedule(
      orders = List(
        order,
        order.copy(
          customerId = "customerId2",
          orderItems = List(ItemSelection(itemId = "item1", quantity = 5))
        ),
        order.copy(
          customerId = "customerId3",
          billingAddress = Some(
            Address(
              line1 = "line1",
              line2 = None,
              county = None,
              city = "London",
              postcode = "NW13 K46"
            )
          )
        )
      )
    )

    (date, location) match {
      case (Some(dateValue), Some(locationValue)) =>
        Ok(Json.toJson(deliverySchedule))
      case (Some(dateValue), None) =>
        Ok(s"deliveries, date only: $dateValue")
      case (None, Some(locationValue)) =>
        Ok(s"deliveries, location only: $locationValue")
      case (None, None) =>
        Ok(s"all deliveries")
    }
  }

  def dummyUpdate(
      parsedBody: ApiDeliverySlotStatus,
      params: Map[String, String]
  ): Result = {
    val slotId = params("slotId")
    val deliverySlotStatus = DeliverySlotStatus(
      parsedBody.deliverySlotStatus
    )

    deliverySlotStatus match {
      case Some(value) =>
        val deliverySlot = DeliverySlot(
          id = slotId,
          date = "new Date()",
          hour = 15,
          availability = value
        )
        Ok(Json.toJson(deliverySlot))
      case None =>
        BadRequest(Json.toJson(Map("error" -> "Invalid Delivery Slot Status")))
    }
  }

}
