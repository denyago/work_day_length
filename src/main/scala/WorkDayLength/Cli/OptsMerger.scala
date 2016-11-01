package WorkDayLength.Cli

import java.io.ByteArrayOutputStream

import WorkDayLength.Settings
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}

/**
  * Created by dyahofarov on 01/11/2016.
  */
class OptsMerger(opts: Array[String]) {

  private val parser = new scopt.OptionParser[Unit]("work_day_length") {
    head ("Work day length", "0.1")
    help("help")

    val dateFormatHint = "should be in YYYY-MM-DD format."

    opt[String]('s', "start-date").foreach( x => setVal("app.startDate", x) ).
      validate(dateValidator).
      text(s"start date of report. It $dateFormatHint")

    opt[String]('e', "end-date").foreach( x => setVal("app.endDate", x) ).
      validate(dateValidator).
      text(s"end date of report. It $dateFormatHint")

    opt[String]('m', "minimal-time").foreach( x => setVal("app.grouping.max_timeout", x) ).
      text("time may be skipped between activities")

    private def dateValidator(date: String): Either[String, Unit] = {
      val datePattern = """(\d\d\d\d)-(\d\d)-(\d\d)""".r

      datePattern.findFirstIn(date) match {
        case Some(_) => success
        case None => failure(s"Date $dateFormatHint")
      }
    }
  }

  private val defaultConfig = ConfigFactory.load()
  private var optsConfig = ConfigFactory.empty()

  var failed = false
  var errorMessage = ""

  def settings: Settings = {
    new Settings(optsConfig.withFallback(defaultConfig).resolve())
  }

  private def setVal(path: String, value: AnyRef): Unit = {
    this.optsConfig = optsConfig.
      withValue(path, ConfigValueFactory.fromAnyRef(value))
  }

  def parse: OptsMerger = {
    val bos = new ByteArrayOutputStream()
    Console.withErr(bos) {
      Console.withOut(bos) {
        this.failed = !parser.parse(opts)
      }
    }
    this.errorMessage = bos.toString("UTF-8")

    this
  }
}
