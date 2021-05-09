package model.api
import play.api.libs.json.{Json, Reads}

final case class ApiShoppingCart(
    customerId: String,
    cartSelections: List[ApiItemSelection]
)

final case class ApiItemSelection(
    itemId: String,
    quantity: Int
)

final case class ApiUpdateQuantity(
    quantity: Int
)

case object ApiShoppingCart {
  implicit val anscr: Reads[ApiShoppingCart] = Json.reads[ApiShoppingCart]
}

case object ApiItemSelection {
  implicit val aisr: Reads[ApiItemSelection] = Json.reads[ApiItemSelection]
}

case object ApiUpdateQuantity {
  implicit val auqr: Reads[ApiUpdateQuantity] = Json.reads[ApiUpdateQuantity]
}
