// Author: zhanglao

package zlfinch.api.v1.hello

import io.circe.{Json, Encoder}

final case class Hello(name: String)

object Hello {
  implicit val helloEncode: Encoder[Hello] =
    Encoder.instance(h => Json.obj("Hello" -> Json.fromString(h.name)))
}
