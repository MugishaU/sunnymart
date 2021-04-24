package model.api
import play.api.libs.json.{Json, Reads}

final case class ApiCustomer(
    name: ApiName,
    address: ApiAddress
)

final case class ApiName(
    title: String,
    forename: String,
    surname: String
)

final case class ApiAddress(
    line1: String,
    line2: Option[String],
    county: Option[String],
    city: String,
    postcode: String
)

final case class ApiPaymentInfo(
    cardNumber: String,
    expiryDate: ApiExpiryDate,
    securityCode: Int
)

final case class ApiExpiryDate(month: Int, year: Int)

object ApiCustomer {
  implicit val acr: Reads[ApiCustomer] = Json.reads[ApiCustomer]
}

object ApiName {
  implicit val anr: Reads[ApiName] = Json.reads[ApiName]
}

object ApiAddress {
  implicit val aar: Reads[ApiAddress] = Json.reads[ApiAddress]
}

object ApiPaymentInfo {
  implicit val apir: Reads[ApiPaymentInfo] = Json.reads[ApiPaymentInfo]
}

object ApiExpiryDate {
  implicit val aedr: Reads[ApiExpiryDate] = Json.reads[ApiExpiryDate]
}
