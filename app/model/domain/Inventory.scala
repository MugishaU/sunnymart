package model.domain

final case class Item(
    id: String,
    name: String,
    category: ItemCategory,
    details: List[ItemDetail],
    price: Int,
    essentialStatus: Boolean,
    quantity: Int
)

final case class ItemDetail(key: String, value: String)

final case class Inventory(items: List[Item])

sealed trait ItemCategory //todo extend all cases
case object BreadAndBakery extends ItemCategory
case object BreakfastAndCereal extends ItemCategory
case object Drinks extends ItemCategory
case object Dairy extends ItemCategory
case object CannedGoods extends ItemCategory
case object FruitAndVeg extends ItemCategory
case object MeatAndFish extends ItemCategory
case object PersonalCare extends ItemCategory
case object Confectionary extends ItemCategory
case object GrainsAndPasta extends ItemCategory
case object CleaningProducts extends ItemCategory
case object Miscellaneous extends ItemCategory
