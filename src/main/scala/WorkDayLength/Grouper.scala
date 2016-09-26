package WorkDayLength

import com.typesafe.config.Config
import java.time.Duration.ofMinutes

/**
  * Created by dyahofarov on 18/07/2016.
  */
class Grouper(startAt: String, conf: Config) {

  def groupedEntries(objectResults: QueryResult) = {
    objectResults.entries.
    foldLeft(List(startingTimeEntry))(group).
    drop(1) // TODO: Bug when single entry groupped into startingTimeEntry
  }

  private lazy val startAtTime = "2016-04-13T00:00:00" // FIXME: Hard-coded
  private lazy val startingTimeEntry = new TimeEntry(startAtTime, 0, 1, "", "", 0)
  private val maxTimeOut = ofMinutes(conf.getInt("max_timeout"))

  private def group(entries: List[TimeEntry], timeEntry: TimeEntry) = {
    val last = entries.last
    if (last.endsAt.plus(maxTimeOut).compareTo(timeEntry.startsAt) >= 0)  {
      entries.take(entries.size - 1) :::
        List(
          last + timeEntry
        )
    } else {
      entries ::: List(timeEntry)
    }
  }

}
