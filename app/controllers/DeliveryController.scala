package controllers

import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import model.api.ApiDeliverySlotStatus
import Helpers.handleRequestBody
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
      dummyGetSlots()
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

  def dummyGetSlots(): Result = {
    val slot = DeliverySlot(
      id = "id",
      date = "new Date()",
      hour = 16,
      availability = Available
    )

    Ok(
      Json.toJson(
        List(
          slot,
          slot.copy(id = "id2", hour = 19),
          slot.copy(id = "id3", hour = 8)
        )
      )
    )
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
