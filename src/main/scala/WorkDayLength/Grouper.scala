package WorkDayLength

import com.typesafe.config.Config
import java.time.Duration
import java.time.Duration.ofMinutes

/**
  * Created by dyahofarov on 18/07/2016.
  */
class Grouper(conf: Config) {

  def groupedEntries(objectResults: QueryResult) = {
    objectResults.entries.
    foldLeft(List(startingTimeEntry))(group).
    drop(1)
  }

  private val startingTimeEntry = new TimeEntry("2016-04-13T00:00:00", 0, 1, "", "", 0)
  private val maxTimeOut = ofMinutes(conf.getInt("app.max_timeout"))

  private def group(entries: List[TimeEntry], timeEntry: TimeEntry) = {
    val last = entries.last
    if (last.endsAt.plus(maxTimeOut).compareTo(timeEntry.startsAt) >= 0)  {
      entries.take(entries.size - 1) :::
        List(
          new TimeEntry(
            last.date,
            (Duration.between(last.startsAt, timeEntry.endsAt).toMillis / 1000).toInt,
            timeEntry.nPeople,
            last.activity ++ ", " ++ timeEntry.activity,
            "", // TODO: Fix me
            0   // TODO: Fix me
          )
        )
    } else {
      entries ::: List(timeEntry)
    }
  }

}