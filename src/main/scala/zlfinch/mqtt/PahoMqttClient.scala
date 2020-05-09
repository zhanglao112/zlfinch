// Author: zhanglao

package zlfinch.mqtt

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence

object PahoMqttClient {
  val brokerUrl = "tcp://localhost:1883"
  
  val persistence = new MqttDefaultFilePersistence("/tmp")

  def createMqttClient(url: String): MqttClient = {
    val client  = new MqttClient(url, MqttClient.generateClientId, persistence)
    client.connect()
    client
//    client.connect()
  }
}
