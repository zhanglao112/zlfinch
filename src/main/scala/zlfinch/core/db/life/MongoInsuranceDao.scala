// Author: zhanglao

package zlfinch.core.db.life

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
import zlfinch.core.entities.{Insurance, InsuranceID, UserID}

case class InsuranceDaoEntity(insuranceId: UUID, cnrtDate: DateTime, psnName: String, bbrName: String, prem: Double)

class MongoInsuranceDao(db: SFuture[DefaultDB], userDao: UserDao)(implicit ec: ExecutionContext) extends MongoDao[InsuranceDaoEntity, UUID](db, collectionName = "insurance") with InsuranceDao {
  override protected implicit def reader: BSONDocumentReader[InsuranceDaoEntity] = new BSONDocumentReader[InsuranceDaoEntity] {
    override def read(bson: BSONDocument): InsuranceDaoEntity = {
      (for {
        insuranceId <- bson.getAs[UUID]("id")
        cnrtDate    <- bson.getAs[DateTime]("cnrtDate")
        psnName     <- bson.getAs[String]("psnName")
        bbrName     <- bson.getAs[String]("bbrName")
        prem        <- bson.getAs[Double]("prem")
      } yield InsuranceDaoEntity(insuranceId, cnrtDate, psnName, bbrName, prem)).get
    }
  }

  override protected implicit def writer: BSONDocumentWriter[InsuranceDaoEntity] = new BSONDocumentWriter[InsuranceDaoEntity] {
    override def write(insurance: InsuranceDaoEntity): BSONDocument = BSONDocument(
      "id"         -> insurance.insuranceId,
      "cnrtDate"   -> insurance.cnrtDate,
      "psnName"   ->  insurance.psnName,
      "bbrName"   ->  insurance.bbrName,
      "prem"      ->  insurance.prem
      )
  }

    override def createInsurance(psnName: String, bbrName: String, prem: Double): TFuture[Insurance] = {
      val id = UUID.randomUUID()
      val currentTime: DateTime = DateTime.now
      val insurance = InsuranceDaoEntity(id, currentTime, psnName, bbrName, prem)

      insert(insurance)
      .map(_ => Insurance(InsuranceID(id.toString), currentTime, psnName, bbrName, prem))
    }

    override def findInsurances(): TFuture[Seq[Insurance]] = {
      findAll().map(_.map(toInsurance))
    }

    private def toInsurance(ie: InsuranceDaoEntity): Insurance = {
      Insurance(InsuranceID(ie.insuranceId.toString), ie.cnrtDate, ie.psnName, ie.bbrName, ie.prem)
    }
}
