// Author: zhanglao

package zlfinch.core.entities

import org.joda.time.DateTime

// case class Reactor(reactorId: ReactorID, reactorName: String, cicsId: CicsID, cicsName: String, rType: Int, status: Int, createTime: DateTime, totalTime: DateTime, info: String)
case class Reactor(reactorId: ReactorID, reactorName: String, cicsId: CicsID, rType: Int, status: Int, createTime: DateTime, totalTime: DateTime, info: String)

case class ReactorID(id: String)
