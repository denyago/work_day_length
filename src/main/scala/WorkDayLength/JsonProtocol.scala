package WorkDayLength

import spray.json.DefaultJsonProtocol

/**
  * JSON output is of the form:
  *
  * {{{
  *   {
    "notes": "data is an array of arrays (rows), column names for rows in row_headers",
    "row_headers": [
        "Date",
        "Time Spent (seconds)",
        "Number of People",
        "Activity",
        "Category",
        "Productivity"
    ],
    "rows": [
        [
          "2016-04-12T08:05:00",
          300,
          1,
          "m.habrahabr.ru",
          "Professional Networking",
          -2
        ],
  ]
    }
  * }}}
  *
  * The type of the `rows` field is a bit weird, but this is a consequence of the array returned not having proper
  * labels.
  *
  * TODO - There's probably a cleaner way to represent this...
  *
  * @param notes useless field
  * @param row_headers headers for each segment for each `row` entry
  * @param rows time entries
  */
case class ApiResult(notes: String, row_headers: List[String], rows: List[List[Either[Int, String]]])

/**
  * Creating a first class type for each entry so its more clear to the user
  * @param date date of activity
  * @param nSeconds seconds activity lasted
  * @param nPeople number of people who participated
  * @param activity string representing activity
  * @param category category of said activity
  * @param productivity level of productivity
  */
case class TimeEntry(date: String, nSeconds: Int, nPeople: Int, activity: String, category: String, productivity: Int) {
  override def toString: String =  s"$activity : $date ($nSeconds sec.)"
}
case class QueryResult(notes: String, rowHeaders: List[String], entries: List[TimeEntry])
case class DataSet(startDate: String, endDate: String, results: List[QueryResult])

/**
  * RescueTime API JSON protocol
  *
  * CONVERT ALL THE THINGS!!
  */
object JsonProtocol extends DefaultJsonProtocol {

  /**
    * Implicit JsonWriter to translate between JSON <=> ApiResult
    */
  implicit val rescueTimeApiResultFormat  = jsonFormat3(ApiResult)
  implicit val timeEntryFormat = jsonFormat6(TimeEntry)
  implicit val queryFormat = jsonFormat3(QueryResult)
  implicit val dataSetFormat = jsonFormat3(DataSet)

  implicit def toTimeEntry(row: List[Either[Int, String]]): TimeEntry = {
    row match {
      case List(Right(date), Left(nSeconds), Left(nPeople), Right(activity), Right(category), Left(productivity)) =>
        TimeEntry(date, nSeconds, nPeople, activity, category, productivity)
    }
  }

  implicit def toEitherList(e: TimeEntry): List[Either[Int, String]] = {
    List(Right(e.date), Left(e.nSeconds), Left(e.nPeople), Right(e.activity), Right(e.category), Left(e.productivity))
  }

  implicit def toQueryResult(apiResult: ApiResult):QueryResult = {
    val entries = apiResult.rows.map(toTimeEntry)
    QueryResult(apiResult.notes, apiResult.row_headers, entries)
  }

  implicit def toApiResult(result: QueryResult): ApiResult = {
    val rows = result.entries.map(toEitherList)
    ApiResult(result.notes, result.rowHeaders, rows)
  }
}
