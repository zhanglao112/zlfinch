// Author: zhanglao

package zlfinch.core.entities

import io.circe.{Json, Encoder}


abstract class Token

abstract class AuthToken extends Token

case class AuthTokenContext(userId: UserID, username: String, role: String)

object AuthTokenContext {
  def fromUser(user: User): AuthTokenContext = AuthTokenContext(user.userId, user.name, user.role)
}

case class BearerToken(token: String) extends AuthToken

object BearerToken {
  implicit val bearerTokenEncoder: Encoder[BearerToken] =
    Encoder.instance(bear => Json.obj("data" -> Json.fromString(bear.token)))
}

case class SecuredToken(token: Array[Byte]) extends Token
