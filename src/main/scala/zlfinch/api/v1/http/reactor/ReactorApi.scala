//Author: zhanglao

package zlfinch.api.v1.http.reactor

import zlfinch.core.entities.Reactor
import zlfinch.core.farms.ReactorService
import zlfinch.core.entities.{AuthTokenContext, CicsID, ReactorID}
import zlfinch.api.v1.http.TokenContextEndpoint.tcEndpoint
import zlfinch.api.v1.http.formats.CommonFormats._
import zlfinch.api.v1.http.reactor.ReactorModel._
import io.finch._
import io.finch.circe._

object ReactorApi {
  def reactorApi(reactorService: ReactorService) = getReactor(reactorService) :+: postReactor(reactorService) :+: putReactor(reactorService) :+: putStatus(reactorService) :+: deleteReactor(reactorService)

  def getReactor(reactorService: ReactorService): Endpoint[Seq[Reactor]] = {
    get("v1" :: "reactor" :: tcEndpoint) { context: AuthTokenContext =>
      reactorService.get(context).map(Ok)
    }
  }

  def postedReactor: Endpoint[AddReactorModel] = jsonBody[AddReactorModel]
  def postReactor(reactorService: ReactorService): Endpoint[String] =
    post("v1" :: "reactor" :: tcEndpoint :: postedReactor) { (context: AuthTokenContext, m: AddReactorModel) =>
      reactorService.create(context, m.reactorName, CicsID(m.cicsId), 0, 0, "new reactor") map {
        case Some(_) => Ok("new reactor created")
        case None => Ok("create reactor error")
      }
    }

  def putedReactor: Endpoint[UpdateReactorModel] = jsonBody[UpdateReactorModel]
  def putReactor(reactorService: ReactorService): Endpoint[String] =
    put("v1" :: "reactor" :: tcEndpoint :: putedReactor) { (context: AuthTokenContext, m: UpdateReactorModel) =>
      reactorService.update(context, ReactorID(m.reactorId), m.reactorName, m.rType, m.totalTime, m.info) map {
        case Some(_) => Ok("reactor updated")
        case None => Ok("update reactor error")
      }

    }

  def putStatus(reactorService: ReactorService): Endpoint[String] =
    put("v1" :: "reactor" :: tcEndpoint :: string :: param("status")) { (context: AuthTokenContext, reactorId: String, status: String) =>
      reactorService.updateStatus(context, ReactorID(reactorId), status.toInt) map { /* toInt check*/
        case Some(_) => Ok("reactor status updated")
        case None => Ok("update reactor status error")
      }

    }

  def deleteReactor(reactorService: ReactorService): Endpoint[String] =
    delete("v1" :: "reactor" :: tcEndpoint :: string) { (context: AuthTokenContext, reactorId: String) =>
      reactorService.remove(context, ReactorID(reactorId)) map {
        case Some(_) => Ok("reactor deleted")
        case None => Ok("delete reactor error")
      }
    }
}


