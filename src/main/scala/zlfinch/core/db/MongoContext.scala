// Author: zhanglao

package zlfinch.core.db

import com.typesafe.config.Config
import reactivemongo.api._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object MongoContext {
  private lazy val driver = new MongoDriver

  def connect(config: Config)(implicit ec: ExecutionContext): Future[DefaultDB] = {

    val uri = config.getString("uri")

    MongoConnection.parseURI(uri).toOption.fold(
      throw new RuntimeException(s"Couldn't parse mongodb connection uri")
    ) { parsedUri =>
      val failoverStrategy = FailoverStrategy(retries = 10, initialDelay = 1.second)
      val connection = driver.connection(parsedUri)
      connection.database(parsedUri.db.get, failoverStrategy)
    }
  }
}
