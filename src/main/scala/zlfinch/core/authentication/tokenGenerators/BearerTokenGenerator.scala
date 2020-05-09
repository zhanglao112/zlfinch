//Author: zhanglao

package zlfinch.core.authentication.tokenGenerators

import zlfinch.core.entities.{AuthTokenContext, BearerToken}

trait BearerTokenGenerator {
  def create(ctx: AuthTokenContext): Option[BearerToken]

  def decode(bearer: BearerToken): Option[AuthTokenContext]

  def touch(bearer: BearerToken): Option[BearerToken] = ???

  def isValid(bearer: BearerToken): Boolean
}

