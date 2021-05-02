package model.domain

import java.util.Date

final case class DeliverySlot(
    id: String,
    date: Date,
    hour: Int,
    availability: DeliverySlotStatus
)

final case class Delivery(
    deliverySlot: DeliverySlot,
    deliveryAddress: Option[Address]
)

final case class DeliverySchedule(
    orders: List[Order]
)

final case class Order(
    orderStatus: OrderStatus,
    customerId: String,
    delivery: Delivery,
    orderItems: List[ItemSelection],
    totalCost: Int,
    billingAddress: Option[Address]
)

sealed trait OrderStatus //todo extend all options
case object OrderPlaced extends OrderStatus
case object OrderReady extends OrderStatus
case object OutForDelivery extends OrderStatus
case object OrderDelivered extends OrderStatus
case object OrderCollected extends OrderStatus
case object OrderFailed extends OrderStatus
case object OrderCancelled extends OrderStatus

sealed trait DeliverySlotStatus //todo extend all options
case object Available extends DeliverySlotStatus
case object Unavailable extends DeliverySlotStatus
