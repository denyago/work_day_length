package WorkDayLength.Cli

import java.time.{Duration, ZoneOffset}

import WorkDayLength.Cli.Arguments.OptsToSettings
import WorkDayLength.{ApiClient, Grouper, Settings}
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

case class CliApp(logger: Logger, args: Array[String]) {
  def run: Int = {
    (new OptsToSettings(args).settings) match {
      case Left(s) =>
        getData(s)
        0 // Exit code for success
      case Right(er) =>
        logger.error(er.message)
        er.exitCode
    }
  }

  private def getData(settings: Settings): Unit = {

    val httpClient = new ApiClient(settings.httpClient, Logger(LoggerFactory.getLogger("HttpClient")))
    val grouper = new Grouper(settings.app.startDate, settings.app.minimalTime)

    // 2016-04-13: Worked from 09:45:00 till 17:00:00 for 6H18M

    val workEntries = grouper.
      groupedEntries(
        httpClient.fetchResults(
          settings.app.startDate,
          settings.app.endDate
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

    val message = {
      start.startsAt.toLocalDate + ": Worked from " +
        start.startsAt.toLocalTime + " till " + end.endsAt.toLocalTime + " for " +
        overallDuration.toHours + " hours " + (overallDuration.toMinutes - overallDuration.toHours * 60) + " minutes"
    }

    logger.info(message)
  }
}

object CliApp extends App {
  val exitCode = new CliApp(Logger(LoggerFactory.getLogger("CliApp")), args).run
  if (exitCode > 0) { System.exit(exitCode) }
}

