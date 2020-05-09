//Author: zhanglao

package zlfinch.api.v1.http.reactor

import zlfinch.core.entities.Reactor
import org.joda.time.DateTime
import io.circe.{Json, Encoder, Decoder}
import zlfinch.api.v1.http.formats.CommonFormats._

// TODO cicsId: CicsID
// case class AddReactorModel(reactorName: String, cicsId: String, rType: Int, info: String)
case class AddReactorModel(reactorName: String, cicsId: String)

case class UpdateReactorModel(reactorId: String, reactorName: String, rType: Int, totalTime: DateTime, info: String)

object ReactorModel {
  // implicit val reactorEncoder: Encoder[Reactor] =
  //   Encoder.instance{ r =>
  //     Json.obj("reactor" -> Json.fromString(s"""{id:${r.reactorId.id}, cicsId:${r.cicsId.id}, name:${r.name}, rType:${r.rType}, status:${r.status}, createTime:${r.createTime}, totalTime:${r.totalTime}, info:${r.info}}"""))}


  // OK
  // val dateFormat = org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZoneUTC

  // implicit val dateTimeEncoder: Encoder[DateTime] =
  //   Encoder[String].contramap { dateFormat.print}
  // END Ok

  implicit val reactorEncoder: Encoder[Reactor] =
    Encoder.forProduct8("id", "reactorName", "cicsId", "rType", "status", "createTime", "totalTime", "info")(r =>
      (r.reactorId.id, r.reactorName, r.cicsId.id, r.rType, r.status, r.createTime, r.totalTime, r.info)
    )

  implicit val addReactorDecoder: Decoder[AddReactorModel] =
    Decoder.forProduct2("reactorName", "cicsId")(AddReactorModel.apply)

  implicit val updateReactorDecoder: Decoder[UpdateReactorModel] =
    Decoder.forProduct5("id", "reactorName", "rType", "totalTime", "info")(UpdateReactorModel.apply)
}
