// Author: zhanglao

package zlfinch.core.entities

import org.joda.time.DateTime

case class Insurance(insuranceId: InsuranceID, cnrtDate: DateTime, psnName: String, bbrName: String, prem: Double)

case class InsuranceID(id: String)
