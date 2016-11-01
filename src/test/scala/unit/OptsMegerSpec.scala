package unit

import WorkDayLength.Cli.OptsMerger

class OptsMegerSpec extends UnitSpec {
  lazy val badOpts = List("--start-date", "--end-date").toArray
  lazy val emptyOptions = new Array[String](0)

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
  }

  describe("#errorMessage") {
    it("returns empty string if options parsed") {
      new OptsMerger(emptyOptions).
        parse.errorMessage shouldEqual ""
    }

    it("returns error message of options not parsed") {
      new OptsMerger(badOpts).
        parse.errorMessage shouldEqual "Error: Date should be in YYYY-MM-DD format.\nTry --help for more information.\n"
    }
  }
}