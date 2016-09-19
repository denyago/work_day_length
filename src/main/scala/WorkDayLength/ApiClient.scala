package WorkDayLength

import com.typesafe.config.Config

import org.http4s.Uri.{Authority, RegName}
import org.http4s.util.CaseInsensitiveString
import org.http4s._
import org.http4s.headers.Accept
import org.http4s.client.blaze.PooledHttp1Client
import JsonProtocol._
import spray.json._

import com.typesafe.scalalogging.Logger

class ApiClient(config: Config, logger: Logger) {

  private def httpCall(apiUri: Uri): String = {
    val headers = Headers(Accept(MediaType.`application/json`))
    val request = Request(method = Method.GET,
                         uri     = apiUri,
                         headers = headers)

    val httpClient  = PooledHttp1Client()
    val apiCallTask = httpClient.fetchAs[String](request)

    apiCallTask.run
  }

  private def buildApiUri(key: String, startDate: String, endDate: String): Uri = {
    Uri(
      scheme    = Some(CaseInsensitiveString(config.getString("protocol"))),
      authority = Some(
        Authority(
          host = RegName(config.getString("hostname")),
          port = Option(config.getInt("port"))
        )
      ),
      path      = s"/anapi/data?key=$key&format=json&restrict_begin=$startDate&restrict_end=$endDate&perspective=interval&resolution_time=minute"
    )
  }

  def fetchResults(startDate: String, endDate: String): QueryResult = {
    httpCall(
      buildApiUri(
        config.getString("key"),
        startDate,
        endDate
      )
    ).parseJson.
      convertTo[ApiResult]
  }
}

