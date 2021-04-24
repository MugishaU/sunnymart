package controllers

import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CustomerController @Inject() (
    cc: ControllerComponents
)(implicit ex: ExecutionContext)
    extends AbstractController(cc) {

  def createCustomer: Action[AnyContent] =
    Action {
      Ok("customer created")
    }

  def createPaymentDetails(customerId: String): Action[AnyContent] =
    Action {
      Ok(s"Payment Details, customer-id: $customerId")
    }

  def editCustomer(customerId: String): Action[AnyContent] =
    Action {
      Ok(s"EDIT: customer-id: $customerId")
    }

  def deleteCustomer(
      customerId: String
  ): Action[AnyContent] =
    Action {
      Ok(s"DELETE: customer-id: $customerId")
    }
}
