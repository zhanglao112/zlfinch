// Author: zhanglao

package zlfinch.core.db

import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.indexes.Index
import reactivemongo.api.{Cursor, DefaultDB}
import reactivemongo.bson._
import scala.concurrent.{Future => SFuture, ExecutionContext}
import com.twitter.util.{Future => TFuture}
import zlfinch.util.async.RichFuture._

abstract class MongoDao[T, ID](db: SFuture[DefaultDB], val collectionName: String)(implicit idWriter: BSONWriter[ID, _ <: BSONValue], idReader: BSONReader[_ <: BSONValue, ID], ec: ExecutionContext) {
  protected lazy val collection: SFuture[BSONCollection] = db.map(_.collection(collectionName))

  protected implicit def reader: BSONDocumentReader[T]
  protected implicit def writer: BSONDocumentWriter[T]
  protected def requiredIndexes: Seq[Index] = Seq.empty

  protected def improvedStacktrace[A](method: => String): PartialFunction[Throwable, A] = {
    case e: Throwable => throw new RuntimeException(s"Exception in DAO $getClass.$method. ${e.getMessage}", e)
  }

  //Future.sequence(requiredIndexes.map(collection.flatMap(_.indexesManager.ensure)))

  def insert(obj: T): TFuture[T] =
    collection.flatMap(
    _.insert(obj)
     .map(_ => obj)
     .recover(improvedStacktrace(s"insert($obj)"))).asTwitter

  def updateById(id: ID, obj: T): TFuture[T] =
    collection.flatMap(
    _.update(BSONDocument("id" -> id), obj)
     .map(_ => obj)
     .recover(improvedStacktrace(s"updateById($id, $obj)"))).asTwitter

  def updateFieldsById(id: ID, modified: BSONDocument): TFuture[Unit] =
    collection.flatMap(
    _.update(BSONDocument("id" -> id), modified)
     .map(_ => {})
     .recover(improvedStacktrace(s"updateSomeFieldById($id, $modified)"))).asTwitter

  def findAll(query: BSONDocument = BSONDocument(), sort: BSONDocument = BSONDocument("id" -> 1)): TFuture[List[T]] =
    collection.flatMap(
    _.find(query)
     .sort(sort)
     .cursor[T]()
     .collect[List]()
     .recover(improvedStacktrace(s"findAll($query, $sort)"))).asTwitter

  def findOne(query: BSONDocument): TFuture[Option[T]] =
    collection.flatMap(
    _.find(query)
     .one[T]
     .recover(improvedStacktrace(s"findOne(${BSONDocument.pretty(query)})"))).asTwitter

  def findById(id: ID): TFuture[Option[T]] =
    findOne(BSONDocument("id" -> id))

  // def findFieldsById(id: ID, projection: BSONDocument): TFuture[BSONDocument] =
  //   collection.flatMap(
  //   _.find(BSONDocument("id" -> id), projection)
  //    .one[BSONDocument]
  //    .recover(improvedStacktrace(s"findFieldsById(${id}, ${BSONDocument.pretty(projection)})"))).asTwitter

  def findByName(name: String): TFuture[Option[T]] =
    findOne(BSONDocument("name" -> name))

  def removeById(id: ID): TFuture[WriteResult] =
    collection.flatMap(
    _.remove(BSONDocument("id" -> id))
     .recover(improvedStacktrace(s"removeById($id)"))).asTwitter

  def listIndexes: TFuture[List[Index]] =
    collection.flatMap(_.indexesManager.list()).asTwitter
}
