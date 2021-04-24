package model.api
import play.api.libs.json.{Json, Reads}
import java.util.Date

final case class ApiOrder(
    customerId: String,
    delivery: ApiDeliverySlotStatus,
    orderSelections: List[ApiItemSelection],
    billingAddress: Option[ApiAddress]
)

final case class ApiDelivery(
    deliverySlot: ApiDeliverySlot,
    deliveryAddress: Option[ApiAddress]
)

final case class ApiDeliverySlot(
    date: Date,
    hour: Int,
    availability: String
)

final case class ApiOrderStatus(orderStatus: String)

case object ApiOrder {
  implicit val apr: Reads[ApiOrder] = Json.reads[ApiOrder]
}

case object ApiDelivery {
  implicit val adr: Reads[ApiDeliverySlotStatus] =
    Json.reads[ApiDeliverySlotStatus]
}

case object ApiDeliverySlot {
  implicit val adsr: Reads[ApiDeliverySlot] = Json.reads[ApiDeliverySlot]
}

case object ApiOrderStatus {
  implicit val aosr: Reads[ApiOrderStatus] = Json.reads[ApiOrderStatus]
}
