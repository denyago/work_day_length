package WorkDayLength

import java.time.LocalTime
import java.time.format.DateTimeFormatter

import com.typesafe.config.Config

class Settings(config: Config) {

  val app = new AppSettings(config.getConfig("app"))
  val httpClient = new HttpSettings(config.getConfig("app.http"))

  override def toString: String = s"App: $app; HttpClient: $httpClient"
}

class AppSettings(config: Config) {
  val startDate = config.getString("startDate")
  val endDate = config.getString("endDate")
  val minimalTime = config.getInt("grouping.max_timeout")
  val dayStartsAt = LocalTime.parse(config.getString("dayStartsAt"), DateTimeFormatter.ISO_LOCAL_TIME)
  val dayEndsAt = LocalTime.parse(config.getString("dayEndsAt"), DateTimeFormatter.ISO_LOCAL_TIME)

  override def toString: String = s"startDate: $startDate, endDate: $endDate, minimalTime: $minimalTime"
}

class HttpSettings(config: Config) {
  val key = config.getString("key")
  val protocol = config.getString("protocol")
  val hostname = config.getString("hostname")
  val port = config.getInt("port")

  override def toString: String = s"protocol: $protocol, hostname: $hostname, port: $port, key: $key"
}
