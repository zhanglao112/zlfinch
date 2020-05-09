//Author: zhanglao

package zlfinch.core.authentication

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import com.twitter.util.{Try, Return, Throw}
import zlfinch.core.entities.CredentialSet

/**
  * Generates a Hash array of bytes representing an encrypted sequence of Salt and Secret Keys
  */

object SecretKeyHashUtils {
  private val HASH_ALGORITHM: String = "PBKDF2WithHmacSHA512"
  private val ITERATIONS: Int        = 20
  private val SECRET_KEY_LENGTH: Int = 512

  def validate(storedCredentials: CredentialSet, secret: String): Boolean = {
    val given = calculateHash(secret.toCharArray, storedCredentials.salt)
    given match {
      case Some(givenPassword) => storedCredentials.password.sameElements(givenPassword)
      case None => false
    }
  }

  def generate(password: Array[Char], salt: Array[Byte]): Option[CredentialSet] = {
    val result = calculateHash(password, salt)
    result.map(pw => CredentialSet(pw, salt, HASH_ALGORITHM))
  }

  private def calculateHash(password: Array[Char],
                            salt: Array[Byte],
                            iterations: Int = ITERATIONS,
                            keyLength: Int = SECRET_KEY_LENGTH): Option[Array[Byte]] = {
    val hashCalculation = Try[Array[Byte]] {
      val keyFactory = SecretKeyFactory.getInstance(HASH_ALGORITHM)
      val keySpec    = new PBEKeySpec(password, salt, iterations, keyLength)
      val secretKey  = keyFactory.generateSecret(keySpec)
      secretKey.getEncoded()
    }

    hashCalculation match {
      case Return(value) => Some(value)
      case Throw(err)    => /*logger.error("Operation failed!", err);*/ None
    }
  }
}

