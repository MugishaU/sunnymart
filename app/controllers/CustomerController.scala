package controllers

import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

import controllers.Helpers.handleRequestBody
import model.api.{ApiCustomer, ApiPaymentInfo}

@Singleton
class CustomerController @Inject() (
    cc: ControllerComponents
)(implicit ex: ExecutionContext)
    extends AbstractController(cc) {

  def createCustomer: Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiCustomer](
        request,
        (x, Nil) => Ok("customer created")
      )
    }

  def createPaymentDetails(customerId: String): Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiPaymentInfo](
        request,
        (x, Nil) => Ok(s"Payment Details, customer-id: $customerId")
      )
    }

  def editCustomer(customerId: String): Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiCustomer](
        request,
        (x, Nil) => Ok(s"EDIT: customer-id: $customerId")
      )
    }

  def deleteCustomer(
      customerId: String
  ): Action[AnyContent] =
    Action {
      Ok(s"DELETE: customer-id: $customerId")
    }
}
