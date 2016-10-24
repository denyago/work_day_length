package WorkDayLength

import java.time.ZoneOffset
import java.time.Duration

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger

class CliApp {
  // TODO: Get rid of varS
  var config = ConfigFactory.load()

  def logger = Logger(LoggerFactory.getLogger("CliApp"))

  def run(args: Array[String]): Unit = {
    val httpClient = new ApiClient(config.getConfig("app.http"), Logger(LoggerFactory.getLogger("HttpClient")))
    val grouper = new Grouper(config.getString("app.startDate"), config.getConfig("app.grouping"))

    // 2016-04-13: Worked from 09:45:00 till 17:00:00 for 6H18M

    val workEntries = grouper.
      groupedEntries(
        httpClient.fetchResults(
          config.getString("app.startDate"),
          config.getString("app.endDate")
        )
      )
      .sortBy(e => e.duration.toMillis)
      .reverse
      .take(2) // TODO: Before and after lunch. What if more?
      .sortBy(e => e.startsAt.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli)

    // TODO: Summerize properly
    val start = workEntries.head
    val end = workEntries.last
    val overallDuration = workEntries.foldLeft(Duration.ZERO)((b, a) => b plus a.duration)

    logger.info(
      start.startsAt.toLocalDate + ": Worked from " +
        start.startsAt.toLocalTime + " till " + end.endsAt.toLocalTime + " for " +
        overallDuration.toHours + " hours " + (overallDuration.toMinutes - overallDuration.toHours * 60) + " minutes"
    )
  }
}

object CliApp extends App {
  (new CliApp).run(args)
}

