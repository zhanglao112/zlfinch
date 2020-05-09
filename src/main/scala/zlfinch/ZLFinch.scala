//Author: zhanglao

package zlfinch

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}

import zlfinch.core.authentication.tokenGenerators.{JwtBearerTokenGenerator, SecuredTokenGenerator}
import zlfinch.core.db.DatabaseContext
import zlfinch.core.db.clients.ConnectedClientsStore
import zlfinch.core.authentication.{UserAuthenticator, UserSecretUtils, AuthenticationService}
import zlfinch.core.farms.{CicsService, ReactorService}

import zlfinch.core.life.InsuranceService

import zlfinch.core.filter.{AuthenticationFilter, RequestLoggingFilter, ExceptionFilter}
import zlfinch.util.time.TaggedTypesFunctions._
import zlfinch.util.time.Time
import org.eclipse.paho.client.mqttv3.MqttClient
import zlfinch.mqtt.PahoMqttClient

import zlfinch.api.v1.Errors._
import zlfinch.api.v1.hello.HelloApi._
import zlfinch.api.v1.http.authentication.AuthenticationApi._
//import zlfinch.api.v1.http.dispatcher.DispatcherApi._
import zlfinch.api.v1.http.cics.CicsApi._
import zlfinch.api.v1.http.cics.CicsModel._
import zlfinch.api.v1.http.reactor.ReactorApi._
import zlfinch.api.v1.http.reactor.ReactorModel._

import zlfinch.api.v1.http.insurance.InsuranceApi._
import zlfinch.api.v1.http.insurance.InsuranceModel._

import zlfinch.api.v1.http.ErrorHandler.apiErrorHandler
import zlfinch.api.v1.http.ResponseEncoders
import zlfinch.api.v1.hello.HelloApi._
import zlfinch.api.v1.hello.Hello._
import io.finch.circe._

object UnhandledExceptionsFilter extends ExceptionFilter[Request]

object ZLFinchApi extends ResponseEncoders{

  val database = new DatabaseContext()
  val bearerTokenGenerator = new JwtBearerTokenGenerator(SecuredTokenGenerator.generate, Time(Millis(100000)))
  val userAuthenticator = new UserAuthenticator(UserSecretUtils.validate, bearerTokenGenerator, database.userDao)
  val connectedClients = new ConnectedClientsStore()
  val authService = new AuthenticationService(userAuthenticator, database.userDao, connectedClients)
  val authFilter = new AuthenticationFilter(authService)

  // val clientMqtt = FutureMqttClient.createConnectionWithUserPassword("tcp://127.0.0.1:1883", "switch", "user", "123")
  val mqttClient: MqttClient = PahoMqttClient.createMqttClient("tcp://localhost:1883")
  //mqttClient.connect()

  val cicsService = new CicsService(database.cicsDao)
  val reactorService = new ReactorService(database.userDao, database.reactorDao, mqttClient)

  val insuranceService = new InsuranceService(database.insuranceDao)


  // TODO
  //private def accountApi = authApi(authService)
  private def api = helloApi() :+: authApi(authService) :+: cicsApi(cicsService) :+: reactorApi(reactorService) :+: insuranceApi(insuranceService)

  def apiService: Service[Request, Response] = 
    RequestLoggingFilter andThen
    UnhandledExceptionsFilter andThen
//todo
//    accountApi.handle(apiErrorHandler).toService andThen
    // errorFilter andThen
    authFilter andThen
    //api.toService
    api.handle(apiErrorHandler).toService
}
