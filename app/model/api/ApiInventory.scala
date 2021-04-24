package model.api
import play.api.libs.json.{Json, Reads}

final case class ApiInventory(items: List[ApiItem])

final case class ApiItem(
    name: String,
    category: String,
    details: List[ApiItemDetail],
    price: Int,
    essentialStatus: Boolean,
    quantity: Int
)

final case class ApiItemDetail(key: String, value: String)

object ApiInventory {
  implicit val ir: Reads[ApiInventory] = Json.reads[ApiInventory]
}

object ApiItem {
  implicit val ir: Reads[ApiItem] = Json.reads[ApiItem]
}

object ApiItemDetail {
  implicit val idr: Reads[ApiItemDetail] = Json.reads[ApiItemDetail]
}
