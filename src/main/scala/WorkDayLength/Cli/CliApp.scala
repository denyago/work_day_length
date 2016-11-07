package WorkDayLength.Cli

import WorkDayLength.Cli.Arguments.OptsToSettings
import WorkDayLength.{ApiClient, Grouper, Settings, Reports}
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

    logger.info(
      Reports.DayLength.result(workEntries)
    )
  }
}

object CliApp extends App {
  val exitCode = new CliApp(Logger(LoggerFactory.getLogger("CliApp")), args).run
  if (exitCode > 0) { System.exit(exitCode) }
}

