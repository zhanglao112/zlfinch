//Author: zhanglao

package zlfinch.core.authentication

import zlfinch.core.entities.{CredentialSet, SecuredToken, UserSecret}

object UserSecretUtils {
  def encrypt(secret: UserSecret, securedTokenGenerator: () => SecuredToken): Option[CredentialSet] = {
    val salt = securedTokenGenerator().token
    val password = secret.password.toCharArray
    SecretKeyHashUtils.generate(password, salt)
  }

  def validate(storedCredentials: CredentialSet, secret: UserSecret): Boolean = {
    SecretKeyHashUtils.validate(storedCredentials, secret.password)
  }
}
