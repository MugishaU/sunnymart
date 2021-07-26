package model.aws

import model.domain.{Address, ItemSelection}
import play.api.libs.json.{Json, OWrites, Reads}

final case class AwsOrder(
    id: String,
    orderStatus: String,
    customerId: String,
    delivery: AwsDelivery,
    orderItems: List[ItemSelection],
    totalCost: Int,
    billingAddress: Option[Address]
)

object AwsOrder {
  implicit val aor: Reads[AwsOrder] = Json.reads[AwsOrder]
  implicit val aow: OWrites[AwsOrder] = Json.writes[AwsOrder]
}
