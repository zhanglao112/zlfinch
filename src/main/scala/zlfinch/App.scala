//Author: zhanglao

package zlfinch

import com.twitter.finagle.{Http, ListeningServer}
import com.twitter.util.Await


object App {
  def main(args: Array[String]): Unit = {
    Await.ready(Http.serve(":8080", ZLFinchApi.apiService))
  }
}
