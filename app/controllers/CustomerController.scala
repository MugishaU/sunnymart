package controllers

import db.DynamoDb.{PrimaryKey, SortKey, getDynamoItem}
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import model.api.{ApiCustomer, ApiPaymentInfo}
import model.domain.{Address, Customer, ExpiryDate, Name, PaymentInfo}
import play.api.libs.json.Json

@Singleton
class CustomerController @Inject() (
    cc: ControllerComponents
)(implicit ex: ExecutionContext)
    extends AbstractController(cc) {

  def getCustomer(customerId: String): Action[AnyContent] =
    Action {
      getCustomerFromDb(customerId)
    }

  def createCustomer: Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiCustomer](
        request,
        dummyCreateCustomer
      )
    }

  def updateCustomer(customerId: String): Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiCustomer](
        request,
        dummyUpdateCustomer(_, Map("customerId" -> customerId))
      )
    }

  def deleteCustomer(customerId: String): Action[AnyContent] =
    Action {
      dummyDeleteCustomer(customerId)
    }

  def getPaymentDetails(
      customerId: String,
      paymentId: String
  ): Action[AnyContent] =
    Action {
      getPaymentDetailsFromDb(customerId, paymentId)
    }

  def createPaymentDetails(customerId: String): Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiPaymentInfo](
        request,
        dummyCreatePaymentDetails(_, Map("customerId" -> customerId))
      )
    }

  def updatePaymentDetails(
      customerId: String,
      paymentId: String
  ): Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiPaymentInfo](
        request,
        dummyUpdatePaymentDetails(
          _,
          Map("customerId" -> customerId, "paymentId" -> paymentId)
        )
      )
    }

  def deletePaymentDetails(
      customerId: String,
      paymentId: String
  ): Action[AnyContent] =
    Action {
      dummyDeletePaymentDetails(customerId, paymentId)
    }

  val dummyName = Name(
    title = "Mr",
    forename = "John",
    surname = "Doe"
  )

  val dummyAddress = Address(
    line1 = "45 Pegasus Street",
    line2 = None,
    county = None,
    city = "London",
    postcode = "WE16 7GP"
  )

  val dummyCustomer = Customer(
    id = "1",
    name = dummyName,
    address = dummyAddress
  )

  val dummyExpiryDate = ExpiryDate(
    month = 12,
    year = 2025
  )

  val dummyPaymentInfo = PaymentInfo(
    id = "5",
    customerId = "12",
    cardNumber = "1234567891012134",
    expiryDate = dummyExpiryDate,
    securityCode = 123
  )

  def getCustomerFromDb(customerId: String): Result = {

    val maybeCustomer = getDynamoItem[Customer](
      primaryKey = PrimaryKey("id", customerId),
      tableName = "sunnymart-customers"
    )

    maybeCustomer match {
      case Right(customer) => Ok(Json.toJson(customer))
      case Left(error) =>
        Status(error("statusCode").toInt)(
          Json.toJson(Map("error" -> error("errorMessage")))
        )
    }
  }

  def dummyCreateCustomer(parsedBody: ApiCustomer): Result = {
    val customer =
      Customer(
        id = "new",
        name = dummyName.copy(
          title = parsedBody.name.title,
          forename = parsedBody.name.forename,
          surname = parsedBody.name.surname
        ),
        address = dummyAddress.copy(
          line1 = parsedBody.address.line1,
          line2 = parsedBody.address.line2,
          county = parsedBody.address.county,
          city = parsedBody.address.city,
          postcode = parsedBody.address.postcode
        )
      )
    Ok(Json.toJson(customer))
  }

  def dummyUpdateCustomer(
      parsedBody: ApiCustomer,
      params: Map[String, String]
  ): Result = {
    val customerId = params("customerId")
    val customer =
      Customer(
        id = customerId,
        name = dummyName.copy(
          title = parsedBody.name.title,
          forename = parsedBody.name.forename,
          surname = parsedBody.name.surname
        ),
        address = dummyAddress.copy(
          line1 = parsedBody.address.line1,
          line2 = parsedBody.address.line2,
          county = parsedBody.address.county,
          city = parsedBody.address.city,
          postcode = parsedBody.address.postcode
        )
      )
    Ok(Json.toJson(customer))
  }

  def dummyDeleteCustomer(
      customerId: String
  ): Result = {
    Ok(
      Json.toJson(
        Map("message" -> s"Successfully Deleted User Id: $customerId")
      )
    )
  }

  def getPaymentDetailsFromDb(customerId: String, paymentId: String): Result = {
    val maybePaymentDetails = getDynamoItem[PaymentInfo](
      primaryKey = PrimaryKey("id", paymentId),
      sortKey = Some(SortKey("customerId", customerId)),
      tableName = "sunnymart-payment-info"
    )

    maybePaymentDetails match {
      case Right(paymentDetails) => Ok(Json.toJson(paymentDetails))
      case Left(error) =>
        Status(error("statusCode").toInt)(
          Json.toJson(Map("error" -> error("errorMessage")))
        )
    }
  }

  def dummyCreatePaymentDetails(
      parsedBody: ApiPaymentInfo,
      params: Map[String, String]
  ): Result = {
    val customerId = params("customerId")

    val expiryDate = ExpiryDate(
      month = parsedBody.expiryDate.month,
      year = parsedBody.expiryDate.year
    )

    val paymentInfo = PaymentInfo(
      id = "new",
      customerId = customerId,
      cardNumber = parsedBody.cardNumber,
      expiryDate = expiryDate,
      securityCode = parsedBody.securityCode
    )

    Ok(Json.toJson(paymentInfo))
  }

  def dummyUpdatePaymentDetails(
      parsedBody: ApiPaymentInfo,
      params: Map[String, String]
  ): Result = {
    val customerId = params("customerId")
    val paymentId = params("paymentId")

    val expiryDate = ExpiryDate(
      month = parsedBody.expiryDate.month,
      year = parsedBody.expiryDate.year
    )

    val paymentInfo = PaymentInfo(
      id = paymentId,
      customerId = customerId,
      cardNumber = parsedBody.cardNumber,
      expiryDate = expiryDate,
      securityCode = parsedBody.securityCode
    )

    Ok(Json.toJson(paymentInfo))
  }

  def dummyDeletePaymentDetails(
      customerId: String,
      paymentId: String
  ): Result = {
    Ok(
      Json.toJson(
        Map(
          "message" -> s"Successfully Deleted Payment Details Id: $paymentId from Customer Id: $customerId"
        )
      )
    )
  }
}
