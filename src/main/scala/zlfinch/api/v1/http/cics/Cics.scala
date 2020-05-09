//Author: zhanglao

package zlfinch.api.v1.http.cics

import zlfinch.core.entities.Cics
import io.circe.{Json, Encoder, Decoder}
//import io.circe._, io.circe.generic.semiauto._
import org.joda.time.DateTime

case class CicsModel(id: String, name: String, location: String, info: String)

object CicsModel {
  //implicit val cicsEncoder: Encoder[Cics] =
    // Encoder.instance{ cics =>
    // Json.obj("cics" -> Json.fromString(s"""{id:${cics.cicsId.id}, name:${cics.name}, location:${cics.location}, onLine:${cics.onLine}, createTime:${cics.createTime}, info:${cics.info}}"""))}

  //todo joda DateTime Encoder
  // val dateFormat = org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").withZoneUTC
  val dateFormat = org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZoneUTC
  implicit val dateTimeEncoder: Encoder[DateTime] =
    Encoder[String].contramap { dateFormat.print}

  implicit val cicsEncoder: Encoder[Cics] =
    Encoder.forProduct6("id", "name", "location", "onLine", "createTime", "info")(c =>
      (c.cicsId.id, c.name, c.location, c.onLine, c.createTime, c.info)
    )

  implicit val cicsModelDecoder: Decoder[CicsModel] =
    Decoder.forProduct4("id", "name", "location", "info")(CicsModel.apply)
  //implicit val cicsModelDeocder: Decoder[CicsModel] = deriveDecoder[CicsModel]
}


