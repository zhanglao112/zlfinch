// Author: zhanglao

package zlfinch.api.v1.hello

import io.finch.{ Endpoint, _}
import zlfinch.api.v1.hello._

object HelloApi {
  def helloApi() = hello

  def hello: Endpoint[Hello] =
    get("v1" :: "hello" :: string("name")) { (name: String) =>
      Ok(Hello(name))
    }
}
