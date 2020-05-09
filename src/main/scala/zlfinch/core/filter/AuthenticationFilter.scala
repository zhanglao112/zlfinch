// Author: zhanglao

package zlfinch.core.filter

import com.twitter.finagle.{Service, Filter}
import com.twitter.finagle.http.{Response, Request, RequestProxy}
import com.twitter.finagle.http.Status.InternalServerError
import com.twitter.util.Future
//TODO
//import org.jboss.netty.handler.codec.http.HttpHeaders.Names
//import org.jboss.netty.handler.codec.http.HttpResponseStatus
import zlfinch.config.Config.AuthorisationHttpHeader
import zlfinch.core.entities.{AuthTokenContext, BearerToken, UserID}
import zlfinch.core.authentication.AuthenticationService
import zlfinch.util.error.Errors.authFailedError

case class AuthorizedRequest(request: Request, authTokenContext: AuthTokenContext) extends RequestProxy

class AuthenticationFilter(authService: AuthenticationService) extends Filter[Request, Response, Request, Response] {
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    // if (request.headers().contains(Names.AUTHORIZATION)) {
      //request.headers().get(Names.AUTHORIZATION)
    // val token = request.headerMap.get(AuthorisationHttpHeader)
    // if (token.isDefined) {
    //   val authTokenContext: Future[Option[AuthTokenContext]] = authService.authorize(BearerToken(token.get))

    //   authTokenContext.flatMap {
    //     case Some(context) => service(AuthorizedRequest(request, context))
    //     case None => println("1111");unauthorized(request)
    //   }
    // } else {
    //   unauthorized(request)
    // }

    if (request.uri == "/api/account/login" || request.uri == "/api/account/signup") {
      service(request)
      //service(AuthorizedRequest(request, AuthTokenContext(UserID("111"), "nobody", RoleID("222"))))
    } else {
      val token = request.headerMap.get(AuthorisationHttpHeader)

      if (token.isDefined) {
        val authTokenContext: Future[Option[AuthTokenContext]] = authService.authorize(BearerToken(token.get))

        authTokenContext.flatMap {
          case Some(context) => service(AuthorizedRequest(request, context))
          case None => unauthorized(request)
        }
      } else {unauthorized(request)}
    }
     //  FutureO(authTokenContext).flatMap(c => FutureO(AuthorizedRequest(request, c))).future.flatMap(service(_))
    // } else unauthorized(request)
  }

  def unauthorized(request: Request): Future[Response] =
    Future.exception(authFailedError("请先登陆！"))
    //Future.value(Response(request.version, InternalServerError))
    //Future.value(Response(request.getProtocolVersion(), HttpResponseStatus.UNAUTHORIZED))
}

