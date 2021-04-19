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

final case class ApiAddress(
    line1: String,
    line2: Option[String],
    county: Option[String],
    city: String,
    postcode: String
)

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

case object ApiAddress {
  implicit val aar: Reads[ApiAddress] = Json.reads[ApiAddress]
}

case object ApiOrderStatus {
  implicit val aosr: Reads[ApiOrderStatus] = Json.reads[ApiOrderStatus]
}
