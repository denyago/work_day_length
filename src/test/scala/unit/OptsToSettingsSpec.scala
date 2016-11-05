package unit

import WorkDayLength.Cli.Arguments.{ExitResult, OptsToSettings}
import WorkDayLength.Settings
import com.typesafe.config.ConfigFactory

class OptsToSettingsSpec extends UnitSpec {
  lazy val badOpts = List("--start-date", "--end-date").toArray
  lazy val emptyOptions = new Array[String](0)
  lazy val helpOpts = List("--help").toArray

  describe("#result") {
    def getSettings(result: Either[Settings, ExitResult]): Settings = {
      result match {
        case Left(s) => s
        case Right(_) => new Settings(ConfigFactory.empty())
      }
    }

    def getExitResult(result: Either[Settings, ExitResult]): ExitResult = {
      result match {
        case Left(_) => ExitResult("", -1)
        case Right(er) => er
      }
    }

    it("returns settings based on default config if no options given") {
      val settings = getSettings(new OptsToSettings(emptyOptions).settings)

      settings.toString shouldEqual "App: startDate: 1986-05-05, endDate: 1986-05-06, minimalTime: 5; HttpClient: protocol: http, hostname: 127.0.0.1, port: 1080, key: ''"
    }

    it("returns settings merged with options and default config") {
      val opts = List("--start-date", "2016-06-01", "--end-date", "2016-06-02", "--minimal-time", "10").toArray
      val settings = getSettings(new OptsToSettings(opts).settings)

      settings.toString shouldEqual "App: startDate: 2016-06-01, endDate: 2016-06-02, minimalTime: 10; HttpClient: protocol: http, hostname: 127.0.0.1, port: 1080, key: ''"
    }

    it("returns bad ExitResult if options not parsed") {
      val exitResult = getExitResult(new OptsToSettings(badOpts).settings)

      exitResult.exitCode shouldEqual 1
      exitResult.message shouldEqual "Error: Date should be in YYYY-MM-DD format.\nTry --help for more information.\n"
    }

    it("returns good ExitStatus if --help was passed") {
      val exitResult = getExitResult(new OptsToSettings(helpOpts).settings)

      exitResult.exitCode shouldEqual 0
      exitResult.message.
        replaceAll("""(?m)\s+$""", "") shouldEqual """Work day length 0.1
Usage: work_day_length [options]
  --help
  -s, --start-date <value>
                           start date of report. It should be in YYYY-MM-DD format.
  -e, --end-date <value>   end date of report. It should be in YYYY-MM-DD format.
  -m, --minimal-time <value>
                           time may be skipped between activities"""
    }
  }
}