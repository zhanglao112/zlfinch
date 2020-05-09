// Author: zhanglao

package zlfinch.util.async

import java.util.concurrent.Executors.newFixedThreadPool
import java.util.concurrent.TimeUnit._
import com.twitter.util.{Future, FuturePool}
import scala.concurrent.ExecutionContext
import zlfinch.util.log.Logger.log
import zlfinch.config.Config

trait AsyncOps {
  lazy val executorService = newFixedThreadPool(Config.miscThreadPoolSize)
  lazy val futurePool = FuturePool.interruptible(executorService)
  lazy val globalAsyncExecutionContext: ExecutionContext = scala.concurrent.ExecutionContext.fromExecutor(executorService)

  sys.addShutdownHook(shutdownExecutorService())

  def runAsync[T](f: => T): Future[T] = futurePool.apply(f)

  def shutdownExecutorService(): Unit = {
    log.info("shutting down executor service...")
    executorService.shutdown()
    try {
      executorService.awaitTermination(10L, SECONDS)
    } catch {
      case e: InterruptedException => {
        log.warn("forcibly shutting down...")
        executorService.shutdownNow()
      }
    }
  }
}

object AsyncOps extends AsyncOps

