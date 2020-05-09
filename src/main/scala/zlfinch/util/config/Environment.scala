// Author: zhanglao

package zlfinch.util.config

//TODO
//import zlfinch.util.error.ErrorReporter

trait Environment {
  val name: String
  val isDevelopment: Boolean
  val isTest: Boolean
  val isProduction: Boolean
}
