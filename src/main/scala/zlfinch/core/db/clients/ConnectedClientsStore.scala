//Author: zhanglao

package zlfinch.core.db.clients

import zlfinch.core.entities.UserID
import zlfinch.api.v1.http.entities.ClientInformation
import scala.collection.mutable

class ConnectedClientsStore() {
  private val usersData: mutable.Map[UserID, ClientInformation] = mutable.Map().empty

  def find(userId: UserID): Option[ClientInformation] = {
    usersData.get(userId)
  }

  def update(userId: UserID, info: ClientInformation): Unit = {
    usersData.update(userId, info)
  }

  def remove(userId: UserID): Unit = {
    usersData.remove(userId)
  }

  def isOnline(userId: UserID): Boolean = {
    usersData.contains(userId)
  }
}
