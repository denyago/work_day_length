package WorkDayLength.Reports

import java.time.format.DateTimeFormatter
import java.time.{Duration, LocalTime}

import WorkDayLength.{TimeEntry, TimeEntryHelper}

/**
  * Created by dyahofarov on 07/11/2016.
  */
object DayLength {
  def result(entries: List[TimeEntry], dayStartsAt: LocalTime, dayEndsAt: LocalTime): String = {

    def closestIndex(entries: List[TimeEntry], time: LocalTime): Int = {
      entries.
      zipWithIndex.
      map(pair => {
        val distance = Duration.between(time, pair._1.startsAt.toLocalTime).getSeconds
        val index = pair._2
        (Math.abs(distance), index)
      }).
      minBy(_._1).
      _2
    }

    val closestIndexToStart = closestIndex(entries, dayStartsAt)
    val closestIndexToEnd = closestIndex(entries, dayEndsAt)

    val workEntries = entries.slice(closestIndexToStart, closestIndexToEnd + 1)

    // TODO: Summerize properly
    val startAt = TimeEntryHelper.startsEarliest(workEntries).startsAt
    val endAt = TimeEntryHelper.endsLatest(workEntries).endsAt
    val overallDuration = TimeEntryHelper.addDurations(workEntries)

    startAt.toLocalDate + ": Worked from " +
      startAt.toLocalTime + " till " + endAt.toLocalTime + " for " +
      TimeEntryHelper.DurationParts(overallDuration).toString
  }
}
