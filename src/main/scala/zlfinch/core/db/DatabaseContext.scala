// Author: zhanglao

package zlfinch.core.db

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import zlfinch.core.authentication.UserSecretUtils
import zlfinch.core.authentication.tokenGenerators.SecuredTokenGenerator
import zlfinch.core.db.users._
import zlfinch.core.db.farms._
import zlfinch.core.db.life._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}

class DatabaseContext {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val config = ConfigFactory.load()
  val db = MongoContext.connect(config.getConfig("mongodb"))

  // val credentialsDao: UserCredentialsDao = new MongoUserCredentialsDao(db, UserSecretUtils.encrypt(_, SecuredTokenGenerator.generate))

  // val usersDao: UsersDao = new MongoUsersDao(db, credentialsDao.createUserCredentials, credentialsDao.updateUserCredentials)

  val userDao: UserDao = new MongoUserDao(db, UserSecretUtils.encrypt(_, SecuredTokenGenerator.generate))
  val cicsDao: CicsDao = new MongoCicsDao(db, userDao);
  val reactorDao: ReactorDao = new MongoReactorDao(db);

  val insuranceDao: InsuranceDao = new MongoInsuranceDao(db, userDao)
}

