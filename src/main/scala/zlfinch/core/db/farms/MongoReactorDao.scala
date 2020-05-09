// Author: zhanglao

package zlfinch.core.db.farms

import java.util.UUID
import org.joda.time.DateTime
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter, BSONDocumentReader}
import reactivemongo.api.DefaultDB
import com.twitter.util.{Future => TFuture}
import scala.concurrent.{Future => SFuture}
import scala.concurrent.ExecutionContext
import zlfinch.core.db.MongoDao
import zlfinch.core.db.formats.CommonDbFormats._
import zlfinch.core.entities.{Reactor, ReactorID, CicsID}

// case class ReactorDaoEntity(reactorId: UUID, cicsId: UUID, cicsName: String, name: String, rType: Int, status: Int, createTime: DateTime, totalTime: DateTime, info: String)
case class ReactorDaoEntity(reactorId: UUID, reactorName: String , cicsId: UUID, rType: Int, status: Int, createTime: DateTime, totalTime: DateTime, info: String)

class MongoReactorDao(db: SFuture[DefaultDB])(implicit ec: ExecutionContext) extends MongoDao[ReactorDaoEntity, UUID](db, collectionName = "reactor") with ReactorDao {
  override protected implicit def reader: BSONDocumentReader[ReactorDaoEntity] = new BSONDocumentReader[ReactorDaoEntity] {
    override def read(bson: BSONDocument): ReactorDaoEntity = {
      (for {
        reactorId  <- bson.getAs[UUID]("id")
        reactorName       <- bson.getAs[String]("reactorName")
        cicsId     <- bson.getAs[UUID]("cicsId")
        // cicsName   <- bson.getAs[String]("cicsName")
        rType      <- bson.getAs[Int]("type")
        status     <- bson.getAs[Int]("status")
        createTime <- bson.getAs[DateTime]("createTime")
        totalTime  <- bson.getAs[DateTime]("totalTime")
        info       <- bson.getAs[String]("info")
      } yield ReactorDaoEntity(reactorId, reactorName, cicsId, rType, status, createTime, totalTime, info)).get
    }
  }

  override protected implicit def writer: BSONDocumentWriter[ReactorDaoEntity] = new BSONDocumentWriter[ReactorDaoEntity] {
    override def write(reactor: ReactorDaoEntity): BSONDocument = BSONDocument(
      "id"          -> reactor.reactorId,
      "reactorName" -> reactor.reactorName,
      "cicsId"      -> reactor.cicsId,
      // "cicsName"    -> reactor.cicsName,
      "type"        -> reactor.rType,
      "status"      -> reactor.status,
      "createTime"  -> reactor.createTime,
      "totalTime"   -> reactor.totalTime,
      "info"        -> reactor.info
    )
  }

  override def createReactor(reactorName: String, cicsId: CicsID, rType: Int, status: Int, info: String): TFuture[Reactor] = {
    val id = UUID.randomUUID()
    val currentTime: DateTime = DateTime.now
    val reactor = ReactorDaoEntity(id, reactorName, UUID.fromString(cicsId.id), rType, status, currentTime, currentTime, info)

    insert(reactor)
    .map(_ => Reactor(ReactorID(id.toString), reactorName, cicsId, rType, status, currentTime, currentTime, info))
  }

  override def findReactors(): TFuture[Seq[Reactor]] = {
    findAll().map(_.map(toReactor))
  }

  override def findReactors(cicsIds: Set[String]): TFuture[Seq[Reactor]] = {
    val cicsIdSet = cicsIds.map(UUID.fromString(_))
    val query = BSONDocument("cicsId" -> BSONDocument("$in" -> cicsIdSet))
    //val query = BSONDocument("cicsId" -> UUID.fromString(cicsId.id))
    findAll(query).map(_.map(toReactor))
  }

  override def findReactor(cicsId: CicsID, reactorName: String): TFuture[Option[Reactor]] = {
    val query = BSONDocument(
      "cicsId" -> UUID.fromString(cicsId.id),
      "reactorName" -> reactorName)
    findOne(query).map(_.map(toReactor))
  }

  // isExisted
  override def findReactor(reactorId: ReactorID, ids: Set[String]): TFuture[Option[Reactor]] = {
    val query = BSONDocument(
      "id" -> UUID.fromString(reactorId.id),
      "cicsId" -> BSONDocument (
        "$in" -> ids
      )
    )
    findOne(query).map(_.map(toReactor))
  }

  override def findReactorById(reactorId: ReactorID): TFuture[Option[Reactor]] = {
    val id: UUID = UUID.fromString(reactorId.id)
    findById(id).map(_.map(toReactor))
  }

  override def updateReactor(reactorId: ReactorID, reactorName: String, rType: Int, totalTime: DateTime , info: String): TFuture[Unit] = {
    val id: UUID = UUID.fromString(reactorId.id)
    val modified = BSONDocument(
      "$set" -> BSONDocument(
        "reactorName" -> reactorName,
        // "cicsId" -> UUID.fromString(cicsId.id),
        "type" -> rType,
        // "status" -> status,
        "totalTime" -> totalTime,
        "info" -> info
      )
    )

    updateFieldsById(id, modified)
    .map(_ => {})
  }

  override def updateReactorStatus(reactorId: ReactorID, status: Int): TFuture[Unit] = {
    val id: UUID = UUID.fromString(reactorId.id)
    val modified = BSONDocument(
      "$set" -> BSONDocument(
        "status" -> status
      )
    )

    updateFieldsById(id, modified)
    .map(_ => {})
  }

  override def updateReactorTotalTime(reactorId: ReactorID, totalTime: DateTime): TFuture[Unit] = {
    //TODO check reactorId is UUID
    val id: UUID = UUID.fromString(reactorId.id)
    val modified = BSONDocument(
      "$set" -> BSONDocument(
        "totalTime" -> totalTime
      )
    )

    updateFieldsById(id, modified)
    .map(_ => {})
  }

  override def removeReactor(reactorId: ReactorID): TFuture[ReactorID] = {
    val id: UUID = UUID.fromString(reactorId.id)

    removeById(id)
    .map(_ => reactorId)
  }

  private def toReactor(re: ReactorDaoEntity): Reactor = {
    Reactor(
      ReactorID(re.reactorId.toString),
      re.reactorName,
      CicsID(re.cicsId.toString),
      re.rType,
      re.status,
      re.createTime,
      re.totalTime,
      re.info)
  }

}
