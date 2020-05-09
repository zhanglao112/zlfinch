//Author: zhanglao

package zlfinch.api.v1.http.formats

import org.joda.time.DateTime
import io.circe.{Json, Encoder, Decoder, HCursor}

object CommonFormats {
  implicit val dateTimeFormat: Encoder[DateTime] with Decoder[DateTime] = new Encoder[DateTime] with Decoder[DateTime] {
    override def apply(dt: DateTime): Json = Encoder.encodeLong.apply(dt.getMillis)
    override def apply(c: HCursor): Decoder.Result[DateTime] = Decoder.decodeLong.map(l => new DateTime(l)).apply(c)
  }
}
