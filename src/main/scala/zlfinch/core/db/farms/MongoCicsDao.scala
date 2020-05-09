// Author: zhanglao

package zlfinch.core.db.farms

import java.util.UUID
import org.joda.time.DateTime
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter, BSONDocumentReader}
import reactivemongo.api.DefaultDB
////////import reactivemongo.bson._
import com.twitter.util.{Future => TFuture}
import scala.concurrent.{Future => SFuture}
import scala.concurrent.ExecutionContext
import zlfinch.core.db.MongoDao
import zlfinch.core.db.users.UserDao
import zlfinch.core.db.formats.CommonDbFormats._
import zlfinch.core.entities.{Cics, CicsID, UserID}

case class CicsDaoEntity(cicsId: UUID, name: String, location: String, onLine: Boolean, createTime: DateTime, info: String)

class MongoCicsDao(db: SFuture[DefaultDB], userDao: UserDao)(implicit ec: ExecutionContext) extends MongoDao[CicsDaoEntity, UUID](db, collectionName = "cics") with CicsDao {
  override protected implicit def reader: BSONDocumentReader[CicsDaoEntity] = new BSONDocumentReader[CicsDaoEntity] {
    override def read(bson: BSONDocument): CicsDaoEntity = {
      (for {
        cicsId     <- bson.getAs[UUID]("id")
        name       <- bson.getAs[String]("name")
        location   <- bson.getAs[String]("location")
        onLine     <- bson.getAs[Boolean]("onLine")
        createTime <- bson.getAs[DateTime]("createTime")
        info       <- bson.getAs[String]("info")
      } yield CicsDaoEntity(cicsId, name, location, onLine, createTime, info)).get
    }
  }

  override protected implicit def writer: BSONDocumentWriter[CicsDaoEntity] = new BSONDocumentWriter[CicsDaoEntity] {
    override def write(cics: CicsDaoEntity): BSONDocument = BSONDocument(
      "id"         -> cics.cicsId,
      "name"       -> cics.name,
      "location"   -> cics.location,
      "onLine"     -> cics.onLine,
      "createTime" -> cics.createTime,
      "info"       -> cics.info
    )
  }

  override def createCics(name: String, location: String, onLine: Boolean, info: String): TFuture[Cics] = {
    val id = UUID.randomUUID()
    val currentTime: DateTime = DateTime.now
    val cics = CicsDaoEntity(id, name, location, onLine, currentTime, info)

    insert(cics)
    .map(_ => Cics(CicsID(id.toString), name, location, onLine, currentTime, info))
  }

  override def findCicses(): TFuture[Seq[Cics]] = {
    findAll().map(_.map(toCics))
  }

  override def findCicsesByUserID(userId: UserID): TFuture[Seq[Cics]] = {
    val cicsIds: TFuture[Option[Set[String]]] = userDao.findCicsIds(userId)

    cicsIds.flatMap{
      case Some(ids) => findAll(BSONDocument("id" -> BSONDocument("$in" -> ids.map(UUID.fromString(_))))).map(_.map(toCics))
      case None      => TFuture.value(List[Cics]())
    }
  }

  override def findCicsByName(name: String): TFuture[Option[Cics]] = {
    findByName(name).map(_.map(toCics))
  }

  override def findCicsById(cicsId: CicsID): TFuture[Option[Cics]] = {
    val id: UUID = UUID.fromString(cicsId.id)
    findById(id).map(_.map(toCics))
  }

  override def updateCics(cicsId: CicsID, location: String, info: String): TFuture[Unit] = {
    val id: UUID = UUID.fromString(cicsId.id)
    val modified = BSONDocument(
      "$set" -> BSONDocument(
        "location" -> location,
        "info" -> info
      )
    )

    updateFieldsById(id, modified)
    .map(_ => {})
  }

  override def updateCicsOnLine(cicsId: CicsID, onLine: Boolean): TFuture[Unit] = {
    val id: UUID = UUID.fromString(cicsId.id)
    val modified = BSONDocument(
      "$set" -> BSONDocument(
        "onLine" -> onLine
      )
    )

    updateFieldsById(id, modified)
    .map(_ => {})
  }

  override def removeCics(cicsId: CicsID): TFuture[CicsID] = {
    val id: UUID = UUID.fromString(cicsId.id)
    
    removeById(id)
    .map(_ => cicsId)
  }


  private def toCics(ce: CicsDaoEntity): Cics = {
    Cics(CicsID(ce.cicsId.toString), ce.name, ce.location, ce.onLine, ce.createTime, ce.info)
  }
}
