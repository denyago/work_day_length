package integration

import unit.UnitSpec
import WorkDayLength.CliApp
import com.typesafe.config.ConfigFactory
import org.mockito.Mockito._
import com.typesafe.scalalogging.{Logger, _}
import org.mockserver.client.server.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.scalatest.mockito.MockitoSugar

import scala.io.Source

class CliAppSpec extends UnitSpec with MockitoSugar {

  def getFileContents(path: String): String = {
    Source.fromURL(getClass.getResource(path)).getLines.mkString
  }

  describe("CliApp") {
    val underlying = mock[org.slf4j.Logger]
    val cli_app = new CliApp(
      Logger(underlying),
      ConfigFactory.load("testApplication"),
      None
    )

    it("returns data from RescueTime via logger") {
      when(underlying.isInfoEnabled).thenReturn(true)
      startClientAndServer(1080)

      (new MockServerClient("localhost", 1080))
        .when(
          request()
            .withMethod("GET")
            .withPath("/anapi/data")
        )
        .respond(
          response()
            .withStatusCode(200)
            .withBody(getFileContents("/single_result_api.json"))
        )

      cli_app.run

      verify(underlying).info("2016-04-13: Worked from 09:45 till 17:22 for 6 hours 40 minutes")
    }
  }
}
