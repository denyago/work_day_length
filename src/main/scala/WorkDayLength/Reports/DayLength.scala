package WorkDayLength.Reports

import WorkDayLength.{TimeEntry, TimeEntryHelper}

/**
  * Created by dyahofarov on 07/11/2016.
  */
object DayLength {
  def result(entries: List[TimeEntry]): String = {
    // TODO: Before and after lunch. What if more?

    val workEntries = entries
      .sortBy( - _.duration.toMillis)
      .take(2)

    // TODO: Summerize properly
    val startAt = TimeEntryHelper.startsEarliest(workEntries).startsAt
    val endTime = TimeEntryHelper.endsLatest(workEntries).endsAt.toLocalTime
    val overallDuration = TimeEntryHelper.addDurations(workEntries)

    startAt.toLocalDate + ": Worked from " +
      startAt.toLocalTime + " till " + endTime + " for " +
      TimeEntryHelper.DurationParts(overallDuration).toString
  }
}
