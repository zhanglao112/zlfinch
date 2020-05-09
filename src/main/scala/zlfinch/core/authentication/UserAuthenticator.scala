//Author: zhanglao

package zlfinch.core.authentication

import com.twitter.util.{Future => TFuture}
import zlfinch.core.authentication.tokenGenerators.BearerTokenGenerator
//import zlfinch.core.db.users.UserCredentialsDao
import zlfinch.core.db.users.UserDao
import zlfinch.core.entities._
import zlfinch.util.async.FutureO

class UserAuthenticator(secretValidator: (CredentialSet, UserSecret) => Boolean, tokenGenerator: BearerTokenGenerator, userDao: UserDao) {
  def authenticate(user: User, secret: UserSecret): TFuture[Option[AuthToken]] = {
    // for {
    //   creds <- FutureO(credentialsDao.findUserCredentials(user.userId))
    //   token <- if (secretValidator(creds, secret)) tokenGerator.create(AuthTokenContext.fromUser(user)) else None
    // } yiled token
    FutureO(userDao.findUserCredentials(user.userId)).flatMap(creds => FutureO(TFuture{if (secretValidator(creds, secret)) tokenGenerator.create(AuthTokenContext.fromUser(user)) else None})).future
  }

  def validateToken(token: AuthToken): Option[AuthTokenContext] = token match {
    case (bearer: BearerToken) => tokenGenerator.decode(bearer)
    case _                     => None
  }
}

