// Author: zhanglao

package zlfinch.core.db.farms

import zlfinch.core.entities.{Cics, CicsID, UserID}
import org.joda.time.DateTime
import com.twitter.util.Future

trait CicsDao {
  def createCics(name: String, location: String, onLine: Boolean, info: String): Future[Cics]

  def findCicses(): Future[Seq[Cics]]

  def findCicsesByUserID(userId: UserID): Future[Seq[Cics]]

  def findCicsByName(name: String): Future[Option[Cics]]

  def findCicsById(cicsId: CicsID): Future[Option[Cics]]

  def updateCics(cicsId: CicsID, location: String, info: String): Future[Unit]

  def updateCicsOnLine(cicsId: CicsID, onLine: Boolean): Future[Unit]

  def removeCics(cicsId: CicsID): Future[CicsID]
}
