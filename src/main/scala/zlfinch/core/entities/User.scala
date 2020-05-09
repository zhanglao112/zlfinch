// Author: zhanglao

package zlfinch.core.entities

import org.joda.time.DateTime


case class User(userId: UserID, name: String, credentials: CredentialSet, role: String, lastSeen: DateTime, cicsIds: Set[String])

case class UserID(id: String)

// case class GroupID(id: String)

//case class RoleID(id: String)

