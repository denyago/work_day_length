package unit

import WorkDayLength.Reports.DayLength
import WorkDayLength.TimeEntry

class DayLengthSpec extends UnitSpec{
  it("returns 2 longest running activities") {
    // TODO: Should not be so
    val entries = List(
      TimeEntry("2016-04-13T03:00:00", 120, 1, "third activity", "", 0),
      TimeEntry("2016-04-13T02:00:00", 60, 1, "second activity", "", 0),
      TimeEntry("2016-04-13T01:00:00", 120, 1, "first activity", "", 0)
    )

    DayLength.result(entries) shouldBe "2016-04-13: Worked from 01:00 till 03:02 for 0 hours 4 minutes"
  }
}
