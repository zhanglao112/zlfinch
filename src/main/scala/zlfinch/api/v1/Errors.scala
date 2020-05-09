// Author : zhanglao

package zlfinch.api.v1

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
import io.circe.Json

object Errors {
  def illegalArgs[A](msg: String): Future[A] = Future.exception(new IllegalArgumentException(msg))

  final val errorFilter = new SimpleFilter[Request, Response] {
    def apply(req: Request, service: Service[Request, Response]): Future[Response] =
      service(req).handle {
        case (t: Throwable) =>
          val data = Json.obj(
            "type" -> Json.fromString(t.getClass.getSimpleName),
            "error" -> Json.fromString(Option(t.getMessage).getOrElse("Internal Server Error"))
          )
          val rep = Response(Status.InternalServerError)
          rep.setContentTypeJson()
          rep.write(data.noSpaces)
          rep
      }
  }
}
