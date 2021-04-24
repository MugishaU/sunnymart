package controllers

import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

import model.api.{ApiOrder, ApiOrderStatus}
import Helpers.handleRequestBody

@Singleton
class OrderController @Inject() (
    cc: ControllerComponents
)(implicit ex: ExecutionContext)
    extends AbstractController(cc) {

  def createOrder: Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiOrder](request, _ => Ok("order created"))
    }

  def getOrder(orderId: String): Action[AnyContent] =
    Action {
      Ok(s"order-id: $orderId")
    }

  def updateOrder(
      orderId: String
  ): Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiOrder](
        request,
        _ => Ok(s"UPDATE: order-id: $orderId")
      )
    }

  def updateOrderStatus(
      orderId: String
  ): Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiOrderStatus](
        request,
        _ => Ok(s"UPDATE: order status, order-id: $orderId")
      )
    }

  def updateOrderComplete(
      orderId: String
  ): Action[AnyContent] =
    Action {
      Ok(s"UPDATE: order complete, order-id: $orderId")
    }

  def deleteOrder(
      orderId: String
  ): Action[AnyContent] =
    Action {
      Ok(s"DELETE: order-id: $orderId")
    }

  def getReceipt(
      orderId: String
  ): Action[AnyContent] =
    Action {
      Ok(s"GET: receipt, order-id: $orderId")
    }

}
