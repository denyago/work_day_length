package WorkDayLength

import java.time.Duration

import org.http4s.Uri.{Authority, RegName}
import org.http4s.dsl.Path
import org.http4s.util.CaseInsensitiveString
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger
import org.http4s._
import org.http4s.headers.Accept
import org.http4s.client.blaze.PooledHttp1Client
import JsonProtocol._
import spray.json._
import java.time.Duration.ofMinutes

object Client extends App {

  def httpCall(apiUri: Uri): String = {
    val headers = Headers(Accept(MediaType.`application/json`))
    val request = Request(method = Method.GET,
                         uri     = apiUri,
                         headers = headers)

    val httpClient  = PooledHttp1Client()
    val apiCallTask = httpClient.fetchAs[String](request)

    apiCallTask.run
  }

  def buildApiUri(key: String, startDate: String, endDate: String): Uri = {
    Uri(
      scheme    = Some(CaseInsensitiveString("https")),
      authority = Some(Authority(host = RegName("www.rescuetime.com"))),
      path      = s"/anapi/data?key=$key&format=json&restrict_begin=$startDate&restrict_end=$endDate&perspective=interval&resolution_time=minute"
    )
  }

  val logger = Logger(LoggerFactory.getLogger("Client"))
  val key = "B63qmSUh2hbN_ZzMjwAHuS21AFvKn_CUCoVH_nM0"
  val startDate = "2016-04-13"
  val endDate = "2016-04-14"
  val apiUri = buildApiUri(key, startDate, endDate)
  val maxTimeOut = ofMinutes(5)

  logger.info(s"Getting data for $startDate - $endDate")


  val result = httpCall(apiUri)
  logger.info(s"API response: $result")

  val objectResults = result.parseJson.convertTo[ApiResult]

  def group(entries: List[TimeEntry], timeEntry: TimeEntry) = {
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

  //objectResults.entries.foreach(e => logger.info(e.toString))

  val startingTimeEntry = new TimeEntry("2016-04-13T00:00:00", 0, 1, "", "", 0)
  val grouppedEntries = objectResults.entries.
    foldLeft(List(startingTimeEntry))(group).
    drop(1)

  grouppedEntries.foreach(e => logger.info(e.toString))

   /**
   * testArray.foldLeft(List(("", -1000)))(group).drop(1)
   */

  logger.info("Done")
}

