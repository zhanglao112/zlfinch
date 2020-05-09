// Author: zhanglao

package zlfinch.core.db.users

import zlfinch.core.entities.{User, UserID, UserSecret, CredentialSet}
import org.joda.time.DateTime
import com.twitter.util.Future

trait UserDao {
  def createUser(name: String, secret: UserSecret, role: String): Future[User]

  def findUsers(): Future[Seq[User]]

  def findUserByName(name: String): Future[Option[User]]

  def findUserById(userId: UserID): Future[Option[User]]

  def findCicsIds(userId: UserID): Future[Option[Set[String]]]

  def findUserCredentials(userId: UserID): Future[Option[CredentialSet]]

  def updateUser(userId: UserID, secret: UserSecret): Future[Unit]

  def updateUserLastSeen(userId: UserID, timestamp: DateTime): Future[Unit]

  def updateUserCicsIds(userId: UserID, cicsIds: Set[String]): Future[Unit]

  def removeUser(userId: UserID): Future[UserID]
}
