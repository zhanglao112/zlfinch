//Author: zhanglao

package zlfinch.api.v1.http.entities

/**
  * Authenticated client information
  * @param version client version
  * @param ipAddress client ip address for messaging
  */
case class ClientInformation(version: String, ipAddress: String)
