// Author: zhanglao

package zlfinch.core.farms

import zlfinch.core.db.farms.ReactorDao
import zlfinch.core.db.users.UserDao
import zlfinch.core.entities.{CicsID, UserID, Reactor, ReactorID, AuthTokenContext}
import com.twitter.util.{Future => TFuture}
import org.eclipse.paho.client.mqttv3.{MqttClient, MqttMessage}
import org.joda.time.DateTime

class ReactorService(userDao: UserDao, reactorDao: ReactorDao, mqttClient: MqttClient) {

  val ADMIN = "admin"

  def create(authContext: AuthTokenContext, reactorName: String, cicsId: CicsID, rType: Int, status: Int, info: String): TFuture[Option[Reactor]] = {
    if (authContext.role == ADMIN) {
      reactorDao.findReactor(cicsId, reactorName) flatMap {
        case Some(_) => TFuture.value(None)
        case None => reactorDao.createReactor(reactorName, cicsId, rType, status, info).map(Some(_))
      }
    } else {
      val cicsIds = userDao.findCicsIds(authContext.userId)
      cicsIds flatMap {
        case Some(ids) => if (ids.contains(cicsId.id)) {
          reactorDao.findReactor(cicsId, reactorName) flatMap {
            case Some(_) => TFuture.value(None)
            case None => reactorDao.createReactor(reactorName, cicsId, rType, status, info).map(Some(_))
          }
        } else {
          TFuture.value(None)
        }
        case None => TFuture.value(None)
      }
    }

    // reactorDao.findReactor(cicsId, reactorName) flatMap {
    //   case Some(_) => TFuture.value(None)
    //   case None => reactorDao.createReactor(reactorName, cicsId, rType, status, info).map(Some(_))
    // }
  }

  def get(authContext: AuthTokenContext): TFuture[Seq[Reactor]] = {

    if (authContext.role == ADMIN) {
      reactorDao.findReactors()
    } else {
      val cicsIds = userDao.findCicsIds(authContext.userId)
      cicsIds flatMap {
        case Some(ids) => reactorDao.findReactors(ids)
        case None      => TFuture.value(List[Reactor]())
      }
    }
    //reactorDao.findReactors()
    // if (userId.id == "123") { /* TODO */
    //   reactorDao.findReactors()
    // } else {
    //   val cicsIds = userDao.findCicsIds(userId)
    //   cicsIds flatMap {
    //     case Some(ids) => reactorDao.findReactors(ids)
    //     case None      => TFuture.value(List[Reactor]())
    //   }
    // }
  }

  def update(authContext: AuthTokenContext, reactorId: ReactorID, reactorName: String, rType: Int, totalTime: DateTime, info: String): TFuture[Option[Unit]] = {
    if (authContext.role == ADMIN) {
      reactorDao.updateReactor(reactorId, reactorName, rType, totalTime, info).map(Some(_))
    } else {
      val cicsIds = userDao.findCicsIds(authContext.userId)
      cicsIds flatMap {
        case Some(ids) => reactorDao.findReactor(reactorId, ids) flatMap {
          case Some(_) => reactorDao.updateReactor(reactorId, reactorName, rType, totalTime, info).map(Some(_))
          case None => TFuture.value(None)
        }
        case None => TFuture.value(None)
      }
    }
  }

  def mqttSending(reactor: Reactor, value: Int) = {
    val cicsId: String = reactor.cicsId.id
    val topic = s"/${cicsId}/${reactor.reactorId.id}"
    val msg = s"""{"values":{"value":{value}}}"""
    val msgTopic = mqttClient.getTopic(topic)
    val message = new MqttMessage(msg.getBytes("utf-8"))
    msgTopic.publish(message)
  }

  def updateStatus(authContext: AuthTokenContext, reactorId: ReactorID, status: Int): TFuture[Option[Unit]] = {

    if (authContext.role == ADMIN) {
      reactorDao.findReactorById(reactorId) flatMap {
        case Some(reactor) => {
          val dbStatus = 1
          reactorDao.updateReactorStatus(reactorId, dbStatus).map {
            mqttSending(reactor, status)
            Some(_)
          }
        }
        case None => TFuture.value(None)
      }
      // reactorDao.updateReactorStatus(reactorId, status).map(Some(_))
    } else {
      val cicsIds = userDao.findCicsIds(authContext.userId)
      cicsIds flatMap {
        case Some(ids) => reactorDao.findReactor(reactorId, ids) flatMap {
          case Some(reactor) => {
            // val cicsId: String = reactor.cicsId.id
            // val topic = s"/${cicsId}/${reactorId.id}"
            // val msg = s"""${"status":status}"""
            // val msgTopic = mqttClient.getTopic(topic)
            // val message = new MqttMessage(msg.getBytes("utf-8"))
            // msgTopic.publish(message)

            // val dbStatus = if (status == 0) 2 else 3
            // reactorDao.updateReactorStatus(reactorId, dbStatus).map(Some(_))
            val dbStatus = 1
            reactorDao.updateReactorStatus(reactorId, dbStatus).map {
              mqttSending(reactor, status)
              Some(_)
            }
          }
          case None => TFuture.value(None)
        }
        case None => TFuture.value(None)
      }
    }
  }
  // reactorDao.updateReactorStatus(reactorId, status)

  def remove(authContext: AuthTokenContext, reactorId: ReactorID): TFuture[Option[ReactorID]] = {
    if (authContext.role == ADMIN) {
      reactorDao.removeReactor(reactorId).map(Some(_))
    } else {
      val cicsIds = userDao.findCicsIds(authContext.userId)
      cicsIds flatMap {
        case Some(ids) => reactorDao.findReactor(reactorId, ids) flatMap {
          case Some(_) => reactorDao.removeReactor(reactorId).map(Some(_))
          case None => TFuture.value(None)
        }
        case None => TFuture.value(None)
      }
    }
  }

}

