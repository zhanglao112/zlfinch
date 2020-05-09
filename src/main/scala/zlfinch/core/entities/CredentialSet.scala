// Author: zhanglao

package zlfinch.core.entities

case class CredentialSet(password: Array[Byte], salt: Array[Byte], algorithm: String)
