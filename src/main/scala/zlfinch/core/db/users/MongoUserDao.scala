// Author: zhanglao

package zlfinch.core.db.users

import java.util.UUID
import org.joda.time.DateTime
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter, BSONDocumentReader}
import reactivemongo.api.DefaultDB
import com.twitter.util.{Future => TFuture}
import scala.concurrent.{Future => SFuture}
import scala.concurrent.ExecutionContext
import zlfinch.core.db.MongoDao
import zlfinch.core.db.formats.CommonDbFormats._
import zlfinch.core.entities.{User, UserID, CredentialSet, UserSecret, CicsID}

case class UserDaoEntity(userId: UUID, name: String, credentials: CredentialSet, role: String, lastSeen: DateTime, cicsIds: Set[String])

class MongoUserDao(db: SFuture[DefaultDB], credentialsGenerator: UserSecret => Option[CredentialSet])(implicit ec: ExecutionContext) extends MongoDao[UserDaoEntity, UUID](db, collectionName = "user") with UserDao {
  override protected implicit def reader: BSONDocumentReader[UserDaoEntity] = new BSONDocumentReader[UserDaoEntity] {
    override def read(bson: BSONDocument): UserDaoEntity = {
      (for {
        userId      <- bson.getAs[UUID]("id")
        name        <- bson.getAs[String]("name")
        credentials <- bson.getAs[BSONDocument]("credentials")
        role        <- bson.getAs[String]("role")
        lastSeen    <- bson.getAs[DateTime]("lastSeen")
        cicsIds     <- bson.getAs[Set[String]]("cicsIds")
      } yield UserDaoEntity(userId, name, CredentialSet(credentials.getAs[Array[Byte]]("password").get, credentials.getAs[Array[Byte]]("salt").get, credentials.getAs[String]("algorithm").get), role, lastSeen, cicsIds)).get
    }
  }

  override protected implicit def writer: BSONDocumentWriter[UserDaoEntity] = new BSONDocumentWriter[UserDaoEntity] {
    override def write(ue: UserDaoEntity): BSONDocument = BSONDocument(
      "id"          -> ue.userId,
      "name"        -> ue.name,
      "credentials" -> BSONDocument(
        "password"  -> ue.credentials.password,
        "salt"      -> ue.credentials.salt,
        "algorithm" -> ue.credentials.algorithm
      ),
      "role"        -> ue.role,
      "lastSeen"    -> ue.lastSeen,
      "cicsIds"     -> ue.cicsIds
    )
  }

  override def createUser(name: String, secret: UserSecret, role: String): TFuture[User] = {
    val id = UUID.randomUUID()
    val currentTime: DateTime = DateTime.now
    // TODO future.exception???
    val credentials = credentialsGenerator(secret).getOrElse(throw new RuntimeException("Failed to generate credential."))
    val cicsIds = Set[String]()
    val entity = UserDaoEntity(id, name, credentials, role, currentTime, cicsIds)

    insert(entity)
    .map(_ => User(UserID(id.toString), name, credentials, role, currentTime, cicsIds))
  }

  override def findUsers(): TFuture[Seq[User]] = {
    findAll().map(_.map(toUser))
  }

  override def findUserByName(name: String): TFuture[Option[User]] = {
    findByName(name).map(_.map(toUser))
  }

  override def findUserById(userId: UserID): TFuture[Option[User]] = {
    val id: UUID = UUID.fromString(userId.id)
    findById(id).map(_.map(toUser))
  }

  override def findCicsIds(userId: UserID): TFuture[Option[Set[String]]] = {
    val id: UUID = UUID.fromString(userId.id)
    findById(id).map(_.map(_.cicsIds))
  }

  override def findUserCredentials(userId: UserID): TFuture[Option[CredentialSet]] = {
    val id = UUID.fromString(userId.id)
    findById(id).map(_.map(_.credentials))
  }

  override def updateUser(userId: UserID, secret: UserSecret): TFuture[Unit] = {
    val id: UUID = UUID.fromString(userId.id)
    val currentTime: DateTime = DateTime.now
    // TODO Future.exception???
    val credentials = credentialsGenerator(secret).getOrElse(throw new RuntimeException("Failed to generate credential."))

    val modified = BSONDocument(
      "$set" -> BSONDocument(
        "lastSeen"    -> currentTime,
        "credentials" -> BSONDocument(
          "password"  -> credentials.password,
          "salt"      -> credentials.salt,
          "algorithm" -> credentials.algorithm
        )
      )
    )

    updateFieldsById(id, modified)
    .map(_ => {})
  }

  override def updateUserLastSeen(userId: UserID, timestamp: DateTime): TFuture[Unit] = {
    val id: UUID = UUID.fromString(userId.id)
    val modified = BSONDocument(
      "$set" -> BSONDocument(
        "lastSeen" -> timestamp
      )
    )

    updateFieldsById(id, modified)
    .map(_ => {})
  }

  override def updateUserCicsIds(userId: UserID, cicsIds: Set[String]): TFuture[Unit] = {
    val id: UUID = UUID.fromString(userId.id)
    val modified = BSONDocument(
      "$set" -> BSONDocument(
        "cicsIds" -> cicsIds
      )
    )

    updateFieldsById(id, modified)
    .map(_ => {})
  }

  override def removeUser(userId: UserID): TFuture[UserID] = {
    val id: UUID = UUID.fromString(userId.id)

    removeById(id)
    .map(_ => userId)
  }

  private def toUser(ue: UserDaoEntity): User = {
    User(UserID(ue.userId.toString), ue.name, ue.credentials, ue.role, ue.lastSeen, ue.cicsIds)
  }

}
