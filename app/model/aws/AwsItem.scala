package model.aws

import model.domain.ItemDetail
import play.api.libs.json.{Json, OWrites, Reads}

final case class AwsItem(
    id: String,
    name: String,
    category: String,
    details: List[ItemDetail],
    price: Int,
    essentialStatus: Boolean,
    quantity: Int
)

object AwsItem {
  implicit val ir: Reads[AwsItem] = Json.reads[AwsItem]
  implicit val iw: OWrites[AwsItem] = Json.writes[AwsItem]
}
