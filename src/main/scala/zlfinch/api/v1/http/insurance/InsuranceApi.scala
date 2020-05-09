//Author: zhanglao

package zlfinch.api.v1.http.insurance

import zlfinch.core.entities.{Insurance, InsuranceID}
import zlfinch.core.life.InsuranceService
import zlfinch.core.entities.AuthTokenContext
import zlfinch.api.v1.http.TokenContextEndpoint.tcEndpoint
import zlfinch.api.v1.http.formats.CommonFormats._
import zlfinch.api.v1.http.insurance.InsuranceModel._
import io.finch._
import io.finch.circe._

object InsuranceApi {

  def insuranceApi(insuranceService: InsuranceService) = getInsurance(insuranceService) :+: postInsurance(insuranceService)

  def getInsurance(insuranceService: InsuranceService): Endpoint[Seq[Insurance]] =
    get("v1" :: "chinalife" :: tcEndpoint) { context: AuthTokenContext =>
      insuranceService.get(context).map(Ok)
    }

  def postedInsurance: Endpoint[InsuranceModel] = jsonBody[InsuranceModel]

  def postInsurance(insuranceService: InsuranceService): Endpoint[String] =
    post("v1" :: "chinalife" :: tcEndpoint :: postedInsurance) { (context: AuthTokenContext, m: InsuranceModel) =>
      insuranceService.create(context, m.psnName, m.bbrName, m.prem) map {
        case Some(_) => Ok("new insurance created")
        case None => Ok("create insurance error")
      }


    }
}

