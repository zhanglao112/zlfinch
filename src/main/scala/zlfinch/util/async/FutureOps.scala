// Author: zhanglao

package zlfinch.util.async

import com.twitter.util.{Future => TFuture, Promise => TPromise, Return, Throw}
import scala.concurrent.{Future => SFuture, Promise => SPromise, ExecutionContext}
import scala.util.{Success, Failure}

object RichFuture {
  implicit class RichTFuture[A](val f: TFuture[A]) extends AnyVal {
    def asScala(implicit e: ExecutionContext): SFuture[A] = {
      val p: SPromise[A] = SPromise()
      f.respond {
        case Return(value) => p.success(value)
        case Throw(exception) => p.failure(exception)
      }

      p.future
    }
  }

  implicit class RichSFuture[A](val f: SFuture[A]) extends AnyVal {
    def asTwitter(implicit e: ExecutionContext): TFuture[A] = {
      val p: TPromise[A] = new TPromise[A]
      f.onComplete {
        case Success(value) => p.setValue(value)
        case Failure(exception) => p.setException(exception)
      }

      p
    }
  }
}

case class FutureO[+A](future: TFuture[Option[A]]) extends AnyVal {
  def flatMap[B](f: A => FutureO[B]): FutureO[B] = {
    val newFuture = future.flatMap {
      case Some(a) => f(a).future
      case None => TFuture.value(None)
    }
    FutureO(newFuture)
  }

  def map[B](f: A => B): FutureO[B] = {
    FutureO(future.map(option => option map f))
  }
}
