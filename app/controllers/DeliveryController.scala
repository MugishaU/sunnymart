package controllers

import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

import model.api.ApiDeliverySlotStatus
import Helpers.handleRequestBody

@Singleton
class DeliveryController @Inject() (
    cc: ControllerComponents
)(implicit ex: ExecutionContext)
    extends AbstractController(cc) {

  def getAvailableSlots: Action[AnyContent] =
    Action {
      Ok("available delivery slots")
    }

  def getDeliverySchedule(
      date: Option[String],
      location: Option[String]
  ): Action[AnyContent] = {

    (date, location) match {
      case (Some(dateValue), Some(locationValue)) =>
        Action {
          Ok(s"deliveries, date: $dateValue, location: $locationValue")
        }
      case (Some(dateValue), None) =>
        Action {
          Ok(s"deliveries, date only: $dateValue")
        }
      case (None, Some(locationValue)) =>
        Action {
          Ok(s"deliveries, location only: $locationValue")
        }
      case (None, None) =>
        Action {
          Ok(s"all deliveries")
        }
    }
  }

  def updateDeliverySlotStatus(
      slotId: String
  ): Action[AnyContent] =
    Action { request =>
      handleRequestBody[ApiDeliverySlotStatus](
        request,
        _ => Ok(s"UPDATE: slot-id: $slotId")
      )
    }
}
