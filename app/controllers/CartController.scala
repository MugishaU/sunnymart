package controllers

import javax.inject.Inject
import javax.inject.Singleton

import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class CartController @Inject() (
    cc: ControllerComponents
)(implicit ex: ExecutionContext)
    extends AbstractController(cc) {

  def createCart: Action[AnyContent] =
    Action {
      Ok("cart created")
    }

  def addItemSelection(cartId: String): Action[AnyContent] =
    Action {
      Ok(s"cart-id: $cartId")
    }

  def updateItemSelection(
      cartId: String,
      cartItemId: String
  ): Action[AnyContent] =
    Action {
      Ok(s"UPDATE: cart-id: $cartId, cart-item-id: $cartItemId")
    }

  def deleteItemSelection(
      cartId: String,
      cartItemId: String
  ): Action[AnyContent] =
    Action {
      Ok(s"DELETE: cart-id: $cartId, cart-item-id: $cartItemId")
    }
}
