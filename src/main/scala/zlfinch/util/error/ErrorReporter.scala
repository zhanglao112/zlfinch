// Author: zhanglao

package zlfinch.util.error

import com.rollbar.Rollbar
import zlfinch.config.Config._
import zlfinch.config.Environment
import zlfinch.util.async.AsyncOps.runAsync
import zlfinch.util.config.Environment

trait ErrorReporter {
  def registerForUnhandledExceptions(): Unit

  def info(t: Throwable): Unit

  def warning(t: Throwable): Unit

  def error(t: Throwable): Unit
}

final class RollbarErrorReporter(accessToken: String, environment: Environment) extends ErrorReporter {
  private lazy val rollbar = new Rollbar(accessToken, environment.name)

  override def registerForUnhandledExceptions() = rollbar.handleUncaughtErrors()
  override def info(t: Throwable) = runAsync(rollbar.info(t))

  override def warning(t: Throwable) = runAsync(rollbar.warning(t))

  override def error(t: Throwable) = runAsync(rollbar.error(t))
}

object ErrorReporter {
  val errorReporter: ErrorReporter = new RollbarErrorReporter(rollbarAccessKey, Environment.env)
}
