package model.domain

import play.api.libs.json.{JsValue, Json, OWrites, Reads, Writes}
final case class ItemDetail(header: String, content: String)

final case class Item(
    id: String,
    name: String,
    category: ItemCategory,
    details: List[ItemDetail],
    price: Int,
    essentialStatus: Boolean,
    quantity: Int
)

final case class Inventory(items: List[Item])

sealed trait ItemCategory //todo extend all cases
case object BreadAndBakery extends ItemCategory
case object Breakfast extends ItemCategory
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

object ItemCategory {
  implicit object ItemCategoryJson extends Writes[ItemCategory] {
    def writes(category: ItemCategory): JsValue = category match {
      case BreadAndBakery   => Json.toJson("BreadAndBakery")
      case Breakfast        => Json.toJson("Breakfast")
      case Drinks           => Json.toJson("Drinks")
      case Dairy            => Json.toJson("Dairy")
      case CannedGoods      => Json.toJson("CannedGoods")
      case FruitAndVeg      => Json.toJson("FruitAndVeg")
      case MeatAndFish      => Json.toJson("MeatAndFish")
      case PersonalCare     => Json.toJson("PersonalCare")
      case Confectionary    => Json.toJson("Confectionary")
      case GrainsAndPasta   => Json.toJson("GrainsAndPasta")
      case CleaningProducts => Json.toJson("CleaningProducts")
      case Miscellaneous    => Json.toJson("Miscellaneous")
    }
  }

  def apply(maybeCategory: String): Option[ItemCategory] = {
    maybeCategory match {
      case "BreadAndBakery"   => Some(BreadAndBakery)
      case "Breakfast"        => Some(Breakfast)
      case "Drinks"           => Some(Drinks)
      case "Dairy"            => Some(Dairy)
      case "CannedGoods"      => Some(CannedGoods)
      case "FruitAndVeg"      => Some(FruitAndVeg)
      case "MeatAndFish"      => Some(MeatAndFish)
      case "PersonalCare"     => Some(PersonalCare)
      case "Confectionary"    => Some(Confectionary)
      case "GrainsAndPasta"   => Some(GrainsAndPasta)
      case "CleaningProducts" => Some(CleaningProducts)
      case "Miscellaneous"    => Some(Miscellaneous)
      case _                  => None
    }
  }
}

object ItemDetail {
  implicit val idw: OWrites[ItemDetail] = Json.writes[ItemDetail]
  implicit val idr: Reads[ItemDetail] = Json.reads[ItemDetail]
}

object Item {
  implicit val iw: OWrites[Item] = Json.writes[Item]
}

object Inventory {
  implicit val iw: OWrites[Inventory] = Json.writes[Inventory]
}
