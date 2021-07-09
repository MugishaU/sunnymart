package model.aws

import model.domain.Address
import play.api.libs.json.{Json, OWrites, Reads}

final case class AwsDeliverySlot(
    id: String,
    date: String,
    hour: Int,
    availability: String
)

final case class AwsDelivery(
    deliverySlot: AwsDeliverySlot,
    deliveryAddress: Option[Address]
)

object AwsDeliverySlot {
  implicit val adsr: Reads[AwsDeliverySlot] = Json.reads[AwsDeliverySlot]
  implicit val adsw: OWrites[AwsDeliverySlot] = Json.writes[AwsDeliverySlot]
}

object AwsDelivery {
  implicit val adr: Reads[AwsDelivery] = Json.reads[AwsDelivery]
}
