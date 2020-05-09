// Author: zhanglao

package zlfinch.core.entities

abstract class AuthChallenge

case class UserSecret(password: String) extends AuthChallenge
