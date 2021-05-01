package model.domain
import play.api.libs.json.{Json, OWrites}

import java.time.LocalDateTime

final case class ItemSelection(
    itemId: String,
    quantity: Int
)

final case class ShoppingCart(
    id: String,
    customerId: String,
    cartSelections: List[ItemSelection],
    cartCost: Int,
    lastActive: LocalDateTime
)

object ItemSelection {
  implicit val isw: OWrites[ItemSelection] = Json.writes[ItemSelection]
}

object ShoppingCart {
  implicit val scw: OWrites[ShoppingCart] = Json.writes[ShoppingCart]
}
