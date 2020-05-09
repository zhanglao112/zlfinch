// Author: zhanglao

package zlfinch.core.farms

import zlfinch.core.db.farms.CicsDao
import zlfinch.core.entities.{UserID, CicsID, Cics, AuthTokenContext}
import com.twitter.util.{Future => TFuture}
import org.joda.time.DateTime

class CicsService(cicsDao: CicsDao) {
  val ADMIN = "admin"

  def create(authContext: AuthTokenContext, cicsName: String, location: String, onLine: Boolean, info: String): TFuture[Option[Cics]] = {
    if (authContext.role == ADMIN) {
      cicsDao.findCicsByName(cicsName) flatMap {
        case Some(_) => TFuture.value(None)
        case None => cicsDao.createCics(cicsName, location, onLine, info).map(Some(_))
      }
    } else {
      TFuture.value(None)
    }
  }

  def get(authContext: AuthTokenContext): TFuture[Seq[Cics]] = {
    if (authContext.role == ADMIN) {
      cicsDao.findCicses();
    } else {
      cicsDao.findCicsesByUserID(authContext.userId)
    }
  }

  def update(authContext: AuthTokenContext, cicsId: CicsID, location: String, info: String): TFuture[Option[Unit]] = {
    if (authContext.role == ADMIN) {
      cicsDao.updateCics(cicsId, location, info).map(Some(_))
    } else {
      TFuture.value(None)
    }
  }

  // def updateCics(cicsId: CicsID, location: String, info: String): TFuture[Unit] = {
  //   cicsDao.updateCics(cicsId, location, info)
  // }

  def updateOnLine(authContext: AuthTokenContext, cicsId: CicsID, onLine: Boolean): TFuture[Option[Unit]] = {
    if (authContext.role == ADMIN) {
      cicsDao.updateCicsOnLine(cicsId, onLine).map(Some(_))
    } else {
      TFuture.value(None)
    }
  }

  def remove(authContext: AuthTokenContext, cicsId: CicsID): TFuture[Option[CicsID]] = {
    if (authContext.role == ADMIN) {
      cicsDao.removeCics(cicsId).map(Some(_))
    } else {
      TFuture.value(None)
    }
  }
}
