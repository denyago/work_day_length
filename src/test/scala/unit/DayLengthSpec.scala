package unit

import java.time.LocalTime

import WorkDayLength.Reports.DayLength
import WorkDayLength.TimeEntry

class DayLengthSpec extends UnitSpec{
  it("returns all activities within 'workday relative time'") {
    val dayStartsAt = LocalTime.parse("09:45")
    val dayEndsAt = LocalTime.parse("18:40")
    // Delta is 5 minutes
    val entries = List(
      TimeEntry("2016-04-13T09:30:00", 60, 1, "early activity", "", 0), // out
      TimeEntry("2016-04-13T09:41:00", 60, 1, "first activity", "", 0), // in for 1 minutes
      TimeEntry("2016-04-13T12:00:00", 60, 1, "second activity", "", 0), // in for 1 minute
      TimeEntry("2016-04-13T12:01:00", 60, 1, "after second activity", "", 0), // in for 1 minute
      TimeEntry("2016-04-13T18:43:00", 60, 1, "third activity", "", 0), // in for 1 minute
      TimeEntry("2016-04-13T18:50:00", 60, 1, "late activity", "", 0) // out for 1 minute
    )

    DayLength.result(entries, dayStartsAt, dayEndsAt) shouldBe "2016-04-13: Worked from 09:41 till 18:44 for 4 minutes"
  }
}
