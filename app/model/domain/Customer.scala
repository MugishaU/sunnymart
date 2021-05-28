package model.domain

import play.api.libs.json.{Json, OWrites, Reads}

final case class Name(
    title: String,
    forename: String,
    surname: String
)

final case class Address(
    line1: String,
    line2: Option[String],
    county: Option[String],
    city: String,
    postcode: String
)

final case class Customer(
    id: String,
    name: Name,
    address: Address
)

final case class ExpiryDate(month: Int, year: Int)

final case class PaymentInfo(
    id: String,
    customerId: String,
    cardNumber: String, //todo library called refined to create card number regex
    expiryDate: ExpiryDate,
    securityCode: Int
)

object Name {
  implicit val nr: Reads[Name] = Json.reads[Name]
  implicit val nw: OWrites[Name] = Json.writes[Name]
}

object Address {
  implicit val ar: Reads[Address] = Json.reads[Address]
  implicit val aw: OWrites[Address] = Json.writes[Address]
}

object Customer {
  implicit val cr: Reads[Customer] = Json.reads[Customer]
  implicit val cw: OWrites[Customer] = Json.writes[Customer]
}

object ExpiryDate {
  implicit val edr: Reads[ExpiryDate] = Json.reads[ExpiryDate]
  implicit val edw: OWrites[ExpiryDate] = Json.writes[ExpiryDate]
}

object PaymentInfo {
  implicit val pir: Reads[PaymentInfo] = Json.reads[PaymentInfo]
  implicit val piw: OWrites[PaymentInfo] = Json.writes[PaymentInfo]
}
