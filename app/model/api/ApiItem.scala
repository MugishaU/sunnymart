package model.api
import play.api.libs.json.{Json, Reads}

final case class ApiItemDetail(header: String, content: String)

final case class ApiItem(
    name: String,
    category: String,
    details: List[ApiItemDetail],
    price: Int,
    essentialStatus: Boolean,
    quantity: Int
)

object ApiItem {
  implicit val ir: Reads[ApiItem] = Json.reads[ApiItem]
}

object ApiItemDetail {
  implicit val idr: Reads[ApiItemDetail] = Json.reads[ApiItemDetail]
}
