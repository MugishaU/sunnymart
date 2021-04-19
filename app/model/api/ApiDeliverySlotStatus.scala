package model.api
import play.api.libs.json.{Json, Reads}

case class ApiDeliverySlotStatus(deliverySlotStatus: String)

case object ApiDeliverySlotStatus {
  implicit val adssr: Reads[ApiDeliverySlotStatus] =
    Json.reads[ApiDeliverySlotStatus]
}
