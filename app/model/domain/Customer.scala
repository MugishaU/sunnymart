package model.domain

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

final case class ExpiryDate(month: Int, year: Int)

final case class PaymentInfo(
    customerId: String,
    cardNumber: String, //todo library called refined to create card number regex
    expiryDate: ExpiryDate,
    securityCode: Int
)

final case class Customer(
    id: String, //todo UUID?
    name: Name,
    address: Address
)
