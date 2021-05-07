package controllers

import play.api.mvc._
import play.api.libs.json._
import play.api.mvc.Results.BadRequest

case object Helpers {
  def handleRequestBody[T](
      request: Request[AnyContent],
      response: (T, Map[String, String]) => Result,
      params: Map[String, String] = Map.empty
  )(implicit reads: Reads[T]): Result = {
    request.body.asJson
      .map { json =>
        json
          .validate[T]
          .map(x => response(x, params))
          .recoverTotal { _ =>
            BadRequest(Json.toJson(Map("error" -> "Malformed Request")))
          }
      }
      .getOrElse {
        BadRequest(Json.toJson(Map("error" -> "No JSON Request Body Found")))
      }
  }
}
