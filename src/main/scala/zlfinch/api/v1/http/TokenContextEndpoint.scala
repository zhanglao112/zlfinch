//Author: zhanglao

package zlfinch.api.v1.http

import zlfinch.core.filter.AuthorizedRequest
import zlfinch.core.entities.AuthTokenContext

import io.finch._

object TokenContextEndpoint {
  def tcEndpoint: Endpoint[AuthTokenContext] = root.map{
    case AuthorizedRequest(_, context) => context
  }
}
