package model.domain

import play.api.libs.json.{JsValue, Json, OWrites, Writes}

final case class DeliverySlot(
    id: String,
    date: String,
    hour: Int,
    availability: DeliverySlotStatus
)

final case class Delivery(
    deliverySlot: DeliverySlot,
    deliveryAddress: Option[Address]
)

final case class Order(
    id: String,
    orderStatus: OrderStatus,
    customerId: String,
    delivery: Delivery,
    orderItems: List[ItemSelection],
    totalCost: Int,
    billingAddress: Option[Address]
)

final case class DeliverySchedule(
    orders: List[Order]
)

sealed trait OrderStatus
case object OrderPlaced extends OrderStatus
case object OrderOutForDelivery extends OrderStatus
case object OrderComplete extends OrderStatus
case object OrderFailed extends OrderStatus
case object OrderCancelled extends OrderStatus

sealed trait DeliverySlotStatus
case object Available extends DeliverySlotStatus
case object Unavailable extends DeliverySlotStatus

object DeliverySlotStatus {
  implicit object DeliverySlotStatusJson extends Writes[DeliverySlotStatus] {
    def writes(status: DeliverySlotStatus): JsValue = status match {
      case Available   => Json.toJson("Available")
      case Unavailable => Json.toJson("Unavailable")
    }
  }

  def apply(maybeStatus: String): Option[DeliverySlotStatus] = {
    maybeStatus match {
      case "Available"   => Some(Available)
      case "Unavailable" => Some(Unavailable)
      case _             => None
    }
  }
}

object DeliverySlot {
  implicit val dsw: OWrites[DeliverySlot] = Json.writes[DeliverySlot]
}

object Delivery {
  implicit val dw: OWrites[Delivery] = Json.writes[Delivery]
}

object OrderStatus {
  implicit object OrderStatusJson extends Writes[OrderStatus] {
    def writes(status: OrderStatus): JsValue = status match {
      case OrderPlaced         => Json.toJson("OrderPlaced")
      case OrderOutForDelivery => Json.toJson("OrderOutForDelivery")
      case OrderComplete       => Json.toJson("OrderComplete")
      case OrderFailed         => Json.toJson("OrderFailed")
      case OrderCancelled      => Json.toJson("OrderCancelled")
    }
  }

  def apply(maybeStatus: String): Option[OrderStatus] = {
    maybeStatus match {
      case "OrderPlaced"         => Some(OrderPlaced)
      case "OrderOutForDelivery" => Some(OrderOutForDelivery)
      case "OrderComplete"       => Some(OrderComplete)
      case "OrderFailed"         => Some(OrderFailed)
      case "OrderCancelled"      => Some(OrderCancelled)
      case _                     => None
    }
  }
}

object Order {
  implicit val ow: OWrites[Order] = Json.writes[Order]
}

object DeliverySchedule {
  implicit val dsw: OWrites[DeliverySchedule] = Json.writes[DeliverySchedule]
}
