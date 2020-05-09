// Author: zhanglao

package zlfinch.core.life

import zlfinch.core.db.life.InsuranceDao
import zlfinch.core.entities.{UserID, InsuranceID, Insurance, AuthTokenContext}
import com.twitter.util.{Future => TFuture}
import org.joda.time.DateTime

class InsuranceService(insuranceDao: InsuranceDao) {
  val ADMIN = "admin"

  def create(authContext: AuthTokenContext, psnName: String, bbrName: String, prem: Double): TFuture[Option[Insurance]] = {
    insuranceDao.createInsurance(psnName, bbrName, prem).map(Some(_))
  }

  def get(authContext: AuthTokenContext): TFuture[Seq[Insurance]] = {
    // if (authContext.role == ADMIN) {

    // }
    insuranceDao.findInsurances();
  }
}
