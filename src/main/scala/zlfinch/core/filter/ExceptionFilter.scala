// Author: zhanglao

package zlfinch.core.filter

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.{Future, NonFatal}
import zlfinch.api.v1.http.ErrorHandler

class ExceptionFilter[REQUEST <: Request] extends SimpleFilter[REQUEST, Response] {
  def apply(request: REQUEST, service: Service[REQUEST, Response]): Future[Response] = {
    val finalResponse = {
      try {
        service(request)
      } catch {
        case NonFatal(e) => Future.exception(e)
      }
    }

    finalResponse.rescue(ErrorHandler.topLevelErrorHandler(request))
  }
}
