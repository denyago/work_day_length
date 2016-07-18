package WorkDayLength

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger

object CliApp extends App {
  val logger = Logger(LoggerFactory.getLogger("Client"))
  val conf = ConfigFactory.load()
  val httpClient = new HttpClient(conf, logger)
  val grouper = new Grouper(conf)

  grouper.
    groupedEntries(httpClient.fetchResults()).
    foreach(e => logger.info(e.toString))

  //logger.info("Done")
}
