package model

import java.util.Date

final case class ItemSelection(
    itemId: String,
    quantity: Int
)

final case class ShoppingCart(
    id: String,
    customerId: String,
    cartSelections: List[ItemSelection],
    cartCost: Int,
    lastActive: Date
)
