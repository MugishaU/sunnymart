package model.api
import play.api.libs.json.{Json, Reads}

final case class ApiOrder(
    customerId: String,
    deliverySlotId: String,
    orderSelections: List[ApiItemSelection],
    billingAddress: Option[ApiAddress]
)

final case class ApiOrderStatus(orderStatus: String)

case object ApiOrder {
  implicit val apr: Reads[ApiOrder] = Json.reads[ApiOrder]
}

case object ApiOrderStatus {
  implicit val aosr: Reads[ApiOrderStatus] = Json.reads[ApiOrderStatus]
}
