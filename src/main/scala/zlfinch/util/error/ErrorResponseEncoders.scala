// Author: zhanglao

package zlfinch.util.error

import com.twitter.finagle.http.Message._
import com.twitter.io.Buf._
//import zlfinch.util.http.HttpOps
import io.circe.Encoder
import io.circe.syntax._

trait ErrorResponseEncoders {
  implicit val exceptionEncoder = Encoder.instance[Throwable]{ e =>
    val base = Map(
      "message" -> e.getMessage,
      "type" -> e.getClass.getSimpleName
    )
    val withCause = base.++(Option(e.getCause).map(cause => Map("cause" -> cause.getMessage)).getOrElse(Map()))
    val error = Map("error" -> withCause)
    error.asJson
  }
}

object ErrorResponseEncoders extends ErrorResponseEncoders
