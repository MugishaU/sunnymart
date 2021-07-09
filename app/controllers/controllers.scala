import play.api.mvc._
import play.api.libs.json._
import play.api.mvc.Results.BadRequest

package object controllers {
  def handleRequestBody[T](
      request: Request[AnyContent],
      response: T => Result
  )(implicit reads: Reads[T]): Result = {
    request.body.asJson
      .map { json =>
        json
          .validate[T]
          .map(x => response(x))
          .recoverTotal { _ =>
            BadRequest(Json.toJson(Map("error" -> "Malformed Request")))
          }
      }
      .getOrElse {
        BadRequest(Json.toJson(Map("error" -> "No JSON Request Body Found")))
      }
  }
}
