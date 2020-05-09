// Author: zhanglao

package zlfinch.core.db.life

import zlfinch.core.entities.{Insurance, InsuranceID}
import org.joda.time.DateTime
import com.twitter.util.Future

trait InsuranceDao {
  def createInsurance(psnName: String, bbrName: String, prem: Double): Future[Insurance]
  def findInsurances():Future[Seq[Insurance]]
}
