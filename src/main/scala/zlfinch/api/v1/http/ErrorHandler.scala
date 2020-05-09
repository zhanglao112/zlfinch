//Author: zhanglao

package zlfinch.api.v1.http

import com.twitter.finagle.CancelledRequestException
import com.twitter.finagle.http.Status.{BadRequest => _, InternalServerError => _, NotFound => _, Unauthorized => _}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
//import zlfinch.http.ResponseOps
import zlfinch.util.error.ErrorReporter._
import zlfinch.util.error.{AuthenticationFailedError, NotFoundError}
import zlfinch.util.log.Logger._
import io.finch.Error._
import io.finch._

import zlfinch.util.error.ErrorResponseEncoders._
import io.circe.Json
import io.circe.Encoder
import io.circe.syntax._
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._


trait ErrorHandler {
  def apiErrorHandler: PartialFunction[Throwable, Output[Nothing]] = {
    case e: NotPresent => BadRequest(e)
    case e: NotParsed => println("jjjjj");BadRequest(e)
    case e: NotValid => BadRequest(e)
    // case e: RequestErrors => BadRequest(e)
    case e: AuthenticationFailedError => Unauthorized(e)
    case e: NotFoundError => NotFound(e)
    case e: Exception => InternalServerError(e)
  }

  def topLevelErrorHandler[REQUEST <: Request](request: REQUEST): PartialFunction[Throwable, Future[Response]] = {
    case e: AuthenticationFailedError => respond(request, Status.Unauthorized, e)
    case e: CancelledRequestException => respond(request, Status.ClientClosedRequest, e)
    case t: Throwable => unhandledException(request, t)
  }

  private def unhandledException[REQUEST <: Request](request: REQUEST, t: Throwable): Future[Response] ={
    try {
      log.info(s"Unhandled exception on URI ${request.uri} with message $t")
      errorReporter.error(t)
      respond(request, Status.InternalServerError, t)
    } catch {
      case e: Throwable => {
        Console.err.println(s"Unable to log unhandled exception: $e")
        throw e
      }
    }
  }

  private def respond[REQUEST <: Request](request: REQUEST, status: Status, t: Throwable): Future[Response] = {
    val rep = request.response
    rep.status = status
    rep.setContentTypeJson()
    val data = exceptionEncoder.apply(t).noSpaces
    rep.write(data)
    //val response = jsonResponse(request, status, t)
    rep.cacheControl = "no-cache"
    Future.value(rep)
  }
}

object ErrorHandler extends ErrorHandler
