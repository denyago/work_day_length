package WorkDayLength.Cli.Arguments

import java.io.ByteArrayOutputStream

import WorkDayLength.Settings
import com.typesafe.config.{Config, ConfigFactory, ConfigValueFactory}

/**
  * Created by dyahofarov on 01/11/2016.
  */
class OptsToSettings(opts: Array[String]) {

  private val parser = new scopt.OptionParser[Config]("work_day_length") {
    head ("Work day length", "0.1")
    help("help")

    private val dateFormatHint = "should be in YYYY-MM-DD format."

    opt[String]('s', "start-date").action( (x, c) => setVal(c, "app.startDate", x) ).
      validate(dateValidator).
      text(s"start date of report. It $dateFormatHint")

    opt[String]('e', "end-date").action( (x, c) => setVal(c, "app.endDate", x) ).
      validate(dateValidator).
      text(s"end date of report. It $dateFormatHint")

    opt[String]('m', "minimal-time").action( (x, c) => setVal(c, "app.grouping.max_timeout", x) ).
      text("time may be skipped between activities")

    private def dateValidator(date: String): Either[String, Unit] = {
      val datePattern = """(\d\d\d\d)-(\d\d)-(\d\d)""".r

      datePattern.findFirstIn(date) match {
        case Some(_) => success
        case None => failure(s"Date $dateFormatHint")
      }
    }

    override def terminate(exitState: Either[String, Unit]): Unit = ()
  }

  def settings: Either[Settings, ExitResult] = {
    val bos = new ByteArrayOutputStream()

    Console.withErr(bos) {
      Console.withOut(bos) {
        parser.parse(opts, ConfigFactory.empty()) match {
          case Some(optsConfig) =>
            if (opts.toList.contains("--help")) {
             Right(ExitResult(bos.toString("UTF-8"), 0))
            } else {
              val defaultConfig = ConfigFactory.load()
              val s = new Settings(optsConfig.withFallback(defaultConfig).resolve())
              Left(s)
            }

          case None =>
            Right(ExitResult(bos.toString("UTF-8"), 1))
        }
      }
    }
  }

  private def setVal(c: Config, path: String, value: AnyRef): Config = {
    c.withValue(path, ConfigValueFactory.fromAnyRef(value))
  }
}
