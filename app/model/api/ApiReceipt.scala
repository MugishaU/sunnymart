package model.api

import play.api.libs.json.{Json, OWrites}

final case class ApiReceiptItem(
    name: String,
    itemCost: Int,
    quantity: Int
)

final case class ApiReceipt(
    orderId: String,
    orderItems: List[ApiReceiptItem],
    deliveryCost: Int
)

object ApiReceiptItem {
  implicit val ariw: OWrites[ApiReceiptItem] = Json.writes[ApiReceiptItem]
}

object ApiReceipt {
  implicit val arw: OWrites[ApiReceipt] = Json.writes[ApiReceipt]
}
