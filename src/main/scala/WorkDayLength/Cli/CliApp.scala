package WorkDayLength.Cli

import java.time.{Duration, ZoneOffset}

import WorkDayLength.{ApiClient, Grouper}
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

case class CliApp(logger: Logger, args: Array[String]) {
  def run: Unit = {
    val opts = new OptsMerger(args).parse
    if (opts.failed) {
      logger.error(opts.errorMessage)
      return
    }

    val httpClient = new ApiClient(opts.settings.httpClient, Logger(LoggerFactory.getLogger("HttpClient")))
    val grouper = new Grouper(opts.settings.app.startDate, opts.settings.app.minimalTime)

    // 2016-04-13: Worked from 09:45:00 till 17:00:00 for 6H18M

    val workEntries = grouper.
      groupedEntries(
        httpClient.fetchResults(
          opts.settings.app.startDate,
          opts.settings.app.endDate
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
  new CliApp(Logger(LoggerFactory.getLogger("CliApp")), args).run
}

