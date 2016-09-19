package WorkDayLength

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger

object CliApp extends App {
  // TODO: Get rid of varS
  var logger = Logger(LoggerFactory.getLogger("CliApp"))
  var config = ConfigFactory.load()

  override def main(args: Array[String]): Unit = {
    val httpClient = new ApiClient(config.getConfig("app.http"), Logger(LoggerFactory.getLogger("HttpClient")))
    val grouper = new Grouper(config.getString("app.startDate"), config.getConfig("app.grouping"))

    grouper.
      groupedEntries(
        httpClient.fetchResults(
          config.getString("app.startDate"),
          config.getString("app.endDate")
        )
      ).
      foreach(e => logger.info(e.toString))
  }
}
