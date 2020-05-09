// Author: zhanglao

package zlfinch.core.db.farms

import zlfinch.core.entities.{Reactor, ReactorID, CicsID}
import org.joda.time.DateTime
import com.twitter.util.Future

trait ReactorDao {
  def createReactor(reactorName: String, cicsId: CicsID, rType: Int, status: Int, info: String): Future[Reactor]

  def findReactors(): Future[Seq[Reactor]]

  def findReactors(cicsIds: Set[String]): Future[Seq[Reactor]]

  def findReactor(cics: CicsID, reactorName: String): Future[Option[Reactor]]

  def findReactor(reactorId: ReactorID, ids: Set[String]): Future[Option[Reactor]]

  // def findReactorByName(cicsId: CicsID, name: String): Future[Option[Reactor]]

  def findReactorById(reactorId: ReactorID): Future[Option[Reactor]]

  def updateReactor(reactorId: ReactorID, reactorName: String, rType: Int, totalTime: DateTime , info: String): Future[Unit]

  def updateReactorStatus(reactorId: ReactorID, status: Int): Future[Unit]

  def updateReactorTotalTime(reactorId: ReactorID, totalTime: DateTime): Future[Unit]

  def removeReactor(reactorId: ReactorID): Future[ReactorID]
}
