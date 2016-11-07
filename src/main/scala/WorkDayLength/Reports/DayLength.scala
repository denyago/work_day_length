package WorkDayLength.Reports

import java.time.{Duration, ZoneOffset}

import WorkDayLength.TimeEntry

/**
  * Created by dyahofarov on 07/11/2016.
  */
object DayLength {
  def result(entries: List[TimeEntry]): String = {
    val workEntries = entries
      .sortBy(e => e.duration.toMillis)
      .reverse
      .take(2) // TODO: Before and after lunch. What if more?
      .sortBy(e => e.startsAt.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli)

    // TODO: Summerize properly
    val start = workEntries.head
    val end = workEntries.last
    val overallDuration = workEntries.foldLeft(Duration.ZERO)((b, a) => b plus a.duration)

    start.startsAt.toLocalDate + ": Worked from " +
      start.startsAt.toLocalTime + " till " + end.endsAt.toLocalTime + " for " +
      overallDuration.toHours + " hours " + (overallDuration.toMinutes - overallDuration.toHours * 60) + " minutes"
  }
}
