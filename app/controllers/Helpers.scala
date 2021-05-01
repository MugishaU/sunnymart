package controllers

import play.api.mvc._
import play.api.libs.json._
import play.api.mvc.Results.BadRequest

case object Helpers {
  def handleRequestBody[T](
      request: Request[AnyContent],
      response: (T, List[String]) => Result,
      params: List[String] = Nil
  )(implicit reads: Reads[T]): Result = {
    request.body.asJson
      .map { json =>
        json
          .validate[T]
          .map(x => response(x, params))
          .recoverTotal { _ =>
            BadRequest(s"Malformed Request")
          }
      }
      .getOrElse {
        BadRequest("No JSON Request Body Found")
      }
  }
}
