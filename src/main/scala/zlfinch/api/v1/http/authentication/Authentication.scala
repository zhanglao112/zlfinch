// Author: zhanglao

package zlfinch.api.v1.http.authentication

import io.circe.{Json, Encoder, Decoder}

final case class Authentication(username: String, password: String)

object Authentication {
  implicit val authEncoder: Encoder[Authentication] =
    Encoder.instance(auth => Json.obj("auth" -> Json.fromString(s"""{username:${auth.username}, password:${auth.password}}""")))

  implicit val authDecoder: Decoder[Authentication] =
    Decoder.forProduct2("username", "password")(Authentication.apply)
}
