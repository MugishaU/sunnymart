package model.api
import play.api.libs.json._

final case class Item(
    id: String,
    name: String,
    category: String,
    details: List[ItemDetail],
    price: Int,
    essentialStatus: Boolean,
    quantity: Int
)

final case class ItemDetail(key: String, value: String)

final case class Inventory(items: List[Item])

object ItemDetail {
  implicit val idr = Json.reads[ItemDetail]
}

object Item {
  implicit val ir = Json.reads[Item]
}

object Inventory {
  implicit val ir = Json.reads[Inventory]
}
