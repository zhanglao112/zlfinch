//Author: zhanglao

package zlfinch.api.v1.http.authentication

import zlfinch.core.authentication.AuthenticationService
import zlfinch.api.v1.http.entities.ClientInformation
import zlfinch.core.entities.{UserSecret, BearerToken}
import zlfinch.api.v1.Errors._
import io.finch.{Endpoint, _}
import io.finch.circe._

object AuthenticationApi {
  def authApi(authService: AuthenticationService) = signIn(authService) :+: signUp(authService)

  def postedAuth: Endpoint[Authentication] = jsonBody[Authentication]

  def signIn(authService: AuthenticationService): Endpoint[BearerToken] =
    post("api" :: "account" :: "login" :: postedAuth) { auth: Authentication =>
      //TODO
      val info = ClientInformation("1.0", "192.168.1.1")
      authService.signIn(auth.username, UserSecret(auth.password), info).map{
        case Some(token) => token match {
          //case BearerToken(token) => Ok(token)
          case b@BearerToken(_) => Ok(b)
          case _ => BadRequest(new Exception("auth invalid"))
        }
        case None => BadRequest(new Exception("auth invalid"))
      }
    }

  def signUp(authService: AuthenticationService): Endpoint[String] =
    post("api" :: "account" :: "signup" :: postedAuth) { auth: Authentication =>
      //TODO
      val info = ClientInformation("1.0", "192.168.1.1")
      authService.signUp(auth.username, UserSecret(auth.password), info).map {
        case Some(token) => token match {
          case BearerToken(token) => Ok(token)
          case _ => Ok("nothing")
        }
        case None => Ok("Error: signUp")
      }
    }

/*
  def signOut(authService: AuthenticationService)(implicit apiCtx: ApiContext): Endpont[String] =
    post("v1" :: header("AuthHeader")) { token: String =>
      val ctxFuture = apiCtx.authenticate(BearerToken(token))
      ctxFuture.flatMap(authService.signOut(_.userId)).map(Ok)
    }
 */

}
