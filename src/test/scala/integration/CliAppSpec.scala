package integration

import unit.UnitSpec
import WorkDayLength.CliApp
import com.typesafe.config.ConfigFactory
import org.mockito.Mockito._
import com.typesafe.scalalogging._
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

    it("returns data from RescueTime via logger") {
      when(underlying.isInfoEnabled).thenReturn(true)
      CliApp.logger = Logger(underlying)
      CliApp.config = ConfigFactory.load("testApplication")

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

      CliApp.main(new Array(0))

      verify(underlying).info("2016-04-13T07:00:00 (PT1M29S): Google Chrome for Android")
    }
  }
}
