//Author: zhanglao

package zlfinch.core.authentication.tokenGenerators

import zlfinch.core.entities._
import zlfinch.util.time.Time
import com.twitter.util.{Try, Return, Throw}
import java.time.Instant
import pdi.jwt.{JwtCirce, JwtAlgorithm, JwtClaim}
import scala.util.{Try, Success, Failure}
import io.circe.Json
import io.circe.Json._

class JwtBearerTokenGenerator(keyGenerator: () => SecuredToken, timer: Time) extends BearerTokenGenerator {
  private val secretKey = new String(keyGenerator().token, "UTF-8")
  private val algo      = JwtAlgorithm.HS256

  def create(ctx: AuthTokenContext): Option[BearerToken] = {
    val claim = JwtClaim(
      issuer  = Some("zlfinch"),
      subject = Some("auth-bearer"),
      issuedAt = Some(Instant.now.getEpochSecond),
      expiration = Some(Instant.now.plusSeconds(157784760).getEpochSecond)) + ("userId", ctx.userId.id) + ("username", ctx.username) + ("role", ctx.role)

    val token = JwtCirce.encode(claim, secretKey, algo)
    Some(BearerToken(token))
  }

  def decode(bearer: BearerToken): Option[AuthTokenContext] = {
    //TODO
    val fromJson: Json => Option[AuthTokenContext] =
      (jwt) => {
        // downField("username")
        val hcursor  = jwt.hcursor
        val userId   = hcursor.get[String]("userId").toOption.get
        val username = hcursor.get[String]("username").toOption.get
        val role   = hcursor.get[String]("role").toOption.get

        Some(AuthTokenContext(UserID(userId), username, role))
      }

    JwtCirce.decodeJson(bearer.token, secretKey, Seq(algo)) match {
      case Success(jwt) => fromJson(jwt)
      case Failure(ex)  => /*TODO: log*/ None
    }
  }

  def isValid(bearer: BearerToken): Boolean = {
    JwtCirce.isValid(bearer.token, secretKey, Seq(algo))
  }
}
