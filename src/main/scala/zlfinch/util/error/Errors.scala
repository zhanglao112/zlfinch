// Author: zhanglao

package zlfinch.util.error

import com.twitter.finagle.http.Response
import scala.util.control.NoStackTrace
// import io.finch.{Error => FinchError}

// abstract class ZLFinchError extends FinchError
abstract class ZLFinchError extends Exception with NoStackTrace

final case class GenericError(reason: String, cause: Option[Throwable]) extends ZLFinchError {
  override def getMessage: String = reason

  override def getCause: Throwable = cause.orNull
}

final case class AuthenticationFailedError(reason: String, cause: Option[Throwable] = None) extends ZLFinchError {
  override def getMessage: String = reason

  override def getCause: Throwable = cause.orNull
}

final case class NotFoundError(reason: String, cause: Option[Throwable] = None) extends ZLFinchError {
  override def getMessage: String = reason

  override def getCause: Throwable = cause.orNull
}

final case class TooManyRedirectsError(redirects: Int) extends ZLFinchError {
  override def getMessage: String = s"Too many redirects ($redirects)"
}

abstract class UpstreamError(val response: Response) extends ZLFinchError

final case class UpstreamAuthenticationError(override val response: Response) extends UpstreamError(response) {
  override def getMessage: String = "Client error (authentication failed) while talking to upstream server"
}

final case class UpstreamClientError(override val response: Response) extends UpstreamError(response) {
  override def getMessage: String = "Client error (bad request) while talking to upstream server"
}

final case class UpstreamServerError(override val response: Response) extends UpstreamError(response) {
  override def getMessage: String = "Server error while talking to upstream server"
}

object Errors {
  def error(message: String): ZLFinchError = GenericError(message, None)

  def error(message: String, cause: Throwable): ZLFinchError = GenericError(message, Some(cause))

  def authFailedError(message: String): ZLFinchError = AuthenticationFailedError(message, None)

  def authFailedError(message: String, cause: Throwable): ZLFinchError = AuthenticationFailedError(message, Some(cause))

  def tooManyRedirects(redirects: Int): ZLFinchError = TooManyRedirectsError(redirects)

  def upstreamAuthenticationError(response: Response): ZLFinchError = UpstreamAuthenticationError(response)

  def notFoundError(message: String): ZLFinchError = NotFoundError(message, None)

  def notFoundError(message: String, cause: Throwable): ZLFinchError = NotFoundError(message, Some(cause))

  def upstreamClientError(response: Response): ZLFinchError = UpstreamClientError(response)

  def upstreamServerError(response: Response): ZLFinchError = UpstreamServerError(response)
}
