//Author: zhanglao

package zlfinch.config

import com.twitter.finagle.stats.{LoadedStatsReceiver, StatsReceiver}
import zlfinch.util.config.ConfigUtils

trait MonitoringConfig extends ConfigUtils {
  //TODO remove lazy
  lazy val rollbarAccessKey = envVarOrFail("ROLLBAR_ACCESS_KEY")
}

trait SystemConfig extends ConfigUtils {
  lazy val statsReceiver: StatsReceiver = LoadedStatsReceiver

  val systemId = "zl-finch"
  val coreLoggerName = systemId

  def environment = envVarOrFail("ENV")

  def listenAddress = s":${envVarOrFail("PORT")}"

  val miscThreadPoolSize = 100
}

object Config extends SystemConfig with MonitoringConfig with ConfigUtils
{
  val AuthorisationHttpHeader = "Authorization"
}

