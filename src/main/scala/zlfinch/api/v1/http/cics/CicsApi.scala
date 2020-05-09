//Author: zhanglao

package zlfinch.api.v1.http.cics

import zlfinch.core.entities.{Cics, CicsID}
import zlfinch.core.farms.CicsService
import zlfinch.core.entities.AuthTokenContext
import zlfinch.api.v1.http.TokenContextEndpoint.tcEndpoint
import zlfinch.api.v1.http.formats.CommonFormats._
import zlfinch.api.v1.http.cics.CicsModel._
import io.finch._
import io.finch.circe._
//import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

object CicsApi {

  def cicsApi(cicsService: CicsService) = getCics(cicsService) :+: postCics(cicsService) :+: putCics(cicsService) :+: deleteCics(cicsService)

  def getCics(cicsService: CicsService): Endpoint[Seq[Cics]] =
    get("v1" :: "cics" :: tcEndpoint) { context: AuthTokenContext =>
      cicsService.get(context).map(Ok)
    }


  def postCics(cicsService: CicsService): Endpoint[String] =
    post("v1" :: "cics" :: tcEndpoint :: param("name")) { (context: AuthTokenContext, name: String) =>
      cicsService.create(context, name, "www.zlfinch.com", false, "newCics") map {
        case Some(_) => Ok("cics created")
        case None => Ok("create cics error")
      }
    }

  def putedCics: Endpoint[CicsModel] = jsonBody[CicsModel]
  def putCics(cicsService: CicsService): Endpoint[String] =
    put("v1" :: "cics" :: tcEndpoint :: putedCics) { (context: AuthTokenContext, m: CicsModel) =>
      cicsService.update(context, CicsID(m.id), m.location, m.info) map {
        case Some(_) => Ok("cics updated")
        case None => Ok("update cics error")
      }
    }
  // def postedCics: Endpoint[CicsModel] = jsonBody[CicsModel]
  // //TODO
  // def postCics(cicsService: CicsService): Endpoint[String] =
  //   post("v1" :: "cics" :: postedCics) { m: CicsModel =>
  //     cicsService.create(m.name, m.location, false, m.info) map {
  //       case Some(_) => Ok("Ok:cics post")
  //       case None    => Ok("Error: cisc post")
  //     }
  //   }

  def deleteCics(cicsService: CicsService): Endpoint[String] =
    delete("v1" :: "cics" :: tcEndpoint :: string) { (context: AuthTokenContext, id: String) =>
      cicsService.remove(context, CicsID(id)) map {
        case Some(_) => Ok("cics deleted")
        case None => Ok("delete cics error")
      }
    }
}
