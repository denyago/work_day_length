package unit

import WorkDayLength.Cli.OptsMerger

class OptsMegerSpec extends UnitSpec {
  lazy val badOpts = List("--start-date", "--end-date").toArray
  lazy val emptyOptions = new Array[String](0)
  lazy val helpOpts = List("--help").toArray

  describe("#settings") {
    it("returns settings based on default config if no options given") {
      val settings = new OptsMerger(emptyOptions).parse.settings

      settings.toString shouldEqual "App: startDate: 1986-05-05, endDate: 1986-05-06, minimalTime: 5; HttpClient: protocol: http, hostname: 127.0.0.1, port: 1080, key: ''"
    }

    it("returns settings merged with options and default config") {
      val opts = List("--start-date", "2016-06-01", "--end-date", "2016-06-02", "--minimal-time", "10").toArray
      val settings = new OptsMerger(opts).parse.settings

      settings.toString shouldEqual "App: startDate: 2016-06-01, endDate: 2016-06-02, minimalTime: 10; HttpClient: protocol: http, hostname: 127.0.0.1, port: 1080, key: ''"
    }
  }

  describe("#failed") {
    it("returns false if options parsed") {
      new OptsMerger(emptyOptions).
        parse.failed shouldEqual false
    }

    it("returns true if options not parsed") {
      new OptsMerger(badOpts).
        parse.failed shouldEqual true
    }

    it("returns true if --help was passed") {
      new OptsMerger(helpOpts).
        parse.failed shouldEqual true
    }
  }

  describe("#errorMessage") {
    it("returns empty string if options parsed") {
      new OptsMerger(emptyOptions).
        parse.errorMessage shouldEqual ""
    }

    it("returns error message if options not parsed") {
      new OptsMerger(badOpts).
        parse.errorMessage shouldEqual "Error: Date should be in YYYY-MM-DD format.\nTry --help for more information.\n"
    }

    it("returns help message if --help key passed") {
      new OptsMerger(helpOpts).
        parse.errorMessage.
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