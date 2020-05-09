//Author: zhanglao

package zlfinch.api.v1.http.insurance

import zlfinch.core.entities.Insurance
import io.circe.{Json, Encoder, Decoder}
import org.joda.time.DateTime

case class InsuranceModel(psnName: String, bbrName: String, prem: Double)

object InsuranceModel {
  val dateFormat = org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZoneUTC
  implicit val dateTimeEncoder: Encoder[DateTime] =
    Encoder[String].contramap { dateFormat.print}

  implicit val insuranceEncoder: Encoder[Insurance] =
    Encoder.forProduct5("id", "cnrtDate", "psnName", "bbrName", "prem")(i =>
      (i.insuranceId.id, i.cnrtDate, i.psnName, i.bbrName, i.prem)
    )

  implicit val insuranceModelDecoder: Decoder[InsuranceModel] =
    Decoder.forProduct3("psnName", "bbrName", "prem")(InsuranceModel.apply)

}
