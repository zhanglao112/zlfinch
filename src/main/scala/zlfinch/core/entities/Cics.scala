// Author: zhanglao

package zlfinch.core.entities

import org.joda.time.DateTime

case class Cics(cicsId: CicsID, name: String, location:String, onLine:Boolean, createTime: DateTime, info: String)

case class CicsID(id: String)
