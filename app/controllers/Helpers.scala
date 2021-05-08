package controllers

import play.api.mvc._
import play.api.libs.json._
import play.api.mvc.Results.BadRequest
import model.domain.{
  BreadAndBakery,
  Breakfast,
  CannedGoods,
  CleaningProducts,
  Confectionary,
  Dairy,
  Drinks,
  FruitAndVeg,
  GrainsAndPasta,
  Inventory,
  Item,
  ItemDetail,
  MeatAndFish,
  Miscellaneous,
  PersonalCare,
  ItemCategory
}

case object Helpers {
  def handleRequestBody[T](
      request: Request[AnyContent],
      response: (T, Map[String, String]) => Result,
      params: Map[String, String] = Map.empty
  )(implicit reads: Reads[T]): Result = {
    request.body.asJson
      .map { json =>
        json
          .validate[T]
          .map(x => response(x, params))
          .recoverTotal { _ =>
            BadRequest(Json.toJson(Map("error" -> "Malformed Request")))
          }
      }
      .getOrElse {
        BadRequest(Json.toJson(Map("error" -> "No JSON Request Body Found")))
      }
  }

  def getItemCategory(maybeCategory: String): Option[ItemCategory] = {
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
