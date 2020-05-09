//Author: zhanglao
//
package zlfinch.core.authentication

import zlfinch.core.db.clients.ConnectedClientsStore
import zlfinch.core.db.users.UserDao
import zlfinch.core.entities._
import zlfinch.api.v1.http.entities.ClientInformation
import zlfinch.util.async.FutureO
import scala.concurrent.ExecutionContext
import com.twitter.util.{Future => TFuture}
import java.util.UUID

class AuthenticationService(authenticator: UserAuthenticator, dao: UserDao, connectedClients: ConnectedClientsStore)/*(implicit ec: ExecutionContext)*/ {
  def signIn(username: String, password: UserSecret, info: ClientInformation): TFuture[Option[AuthToken]] = {
    val fo = for {
      user <- FutureO(dao.findUserByName(username))
      token <- FutureO(authenticator.authenticate(user, password))
    } yield (user.userId, token)

    fo.future map {
      case Some((userId, token)) => connectedClients.update(userId, info); Some(token)
      case None => None
    }
    // or
    //FutureO(dao.findUserByName(username)).flatMap(u => FutureO(authenticator.authenticate(u, password))).future
  }

  def signUp(username: String, secret: UserSecret, info: ClientInformation): TFuture[Option[AuthToken]] = {
    val createUser: (String, UserSecret, String) => TFuture[Option[AuthToken]] =
      (unm, pss, roleId) => {
        val user: TFuture[User] = dao.createUser(unm, pss, roleId)
        val token = user.flatMap(authenticator.authenticate(_, secret))
        //TODO
        // user.map(connectedClients.update(_.userId, info))
        token
      }

    //TODO GroupID, RoleID
    dao.findUserByName(username) flatMap {
      case Some(_) => TFuture.value(None)
      case None => createUser(username, secret, "admin")
    }
  }

  def authorize(token: AuthToken): TFuture[Option[AuthTokenContext]] = TFuture {
    authenticator.validateToken(token).filter {
      case AuthTokenContext(id, _, _) => connectedClients.isOnline(id)
    }
  }

  def updateCredentials(userId: UserID, newSecret: UserSecret): TFuture[Option[AuthToken]] = {
    val updateUser: (User, UserSecret) => TFuture[Option[AuthToken]] =
      (user, secret) => {
        dao.updateUser(user.userId, secret)
        authenticator.authenticate(user, secret)
      }

    dao.findUserById(userId) flatMap {
      case Some(user) => updateUser(user, newSecret)
      case None => TFuture.value(None)
    }
  }
}
