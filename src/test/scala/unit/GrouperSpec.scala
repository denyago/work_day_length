package unit

import WorkDayLength.{Grouper, QueryResult, Settings, TimeEntry}
import com.typesafe.config.ConfigFactory

class GrouperSpec extends UnitSpec {
  describe("#groupedEntries") {
    lazy val settings = new Settings(ConfigFactory.load())
    lazy val grouper = new Grouper(settings.app.startDate, settings.app.minimalTime)

    def buildInput(list: List[TimeEntry]): QueryResult = {
      new QueryResult("", List(), list)
    }

    it("returns empty List when input is empty") {
      val input = buildInput(List())

      grouper.groupedEntries(input) shouldBe List()
    }

    it("returns List with one entity when input has one element") {
      val timeEntry = new TimeEntry("2016-04-13T01:00:00", 1, 1, "", "", 0 )
      val input = buildInput(List(timeEntry))

      grouper.groupedEntries(input) shouldBe List(timeEntry)
    }

    it("returns List with entities when input has entities it can't group") {
      val firstEntity = new TimeEntry("2016-04-13T01:00:00", 1, 1, "first activity", "", 0)
      val secondEntity = new TimeEntry("2016-04-13T02:00:00", 1, 1, "second activity", "", 0)
      val input = buildInput(List(firstEntity, secondEntity))

      grouper.groupedEntries(input) shouldBe List(firstEntity, secondEntity)
    }

    it("returns List with single entity when input has entities it can group") {
      val firstEntity = new TimeEntry("2016-04-13T01:00:00", 1, 1, "first activity", "", 0)
      val secondEntity = new TimeEntry("2016-04-13T01:00:05", 1, 1, "second activity", "", 0)
      val input = buildInput(List(firstEntity, secondEntity))

      // TODO: Why 6 sec? 0 + 1 + 5 + 1 == 7 sec.
      val groupedEntity = new TimeEntry("2016-04-13T01:00:00", 6, 1, "first activity, second activity", "", 0)
      grouper.groupedEntries(input) shouldBe List(groupedEntity)
    }

    it("returns List with single entity when input has fully intersecting entries") {
      val firstEntity = new TimeEntry("2016-04-13T01:00:00", 60, 1, "first activity (PC)", "", 0)
      val secondEntity = new TimeEntry("2016-04-13T01:00:05", 50, 1, "second activity (Mobile)", "", 0)
      val input = buildInput(List(firstEntity, secondEntity))

      val groupedEntity = new TimeEntry("2016-04-13T01:00:00", 60, 1, "first activity (PC), second activity (Mobile)", "", 0)
      grouper.groupedEntries(input) shouldBe List(groupedEntity)
    }

    it("returns List with single entity when input has partly intersecting entries") {
      val firstEntity = new TimeEntry("2016-04-13T01:00:00", 60, 1, "first activity (PC)", "", 0)
      val secondEntity = new TimeEntry("2016-04-13T01:00:45", 60, 1, "second activity (Mobile)", "", 0)
      val input = buildInput(List(firstEntity, secondEntity))

      // 00:00 + 60 = 01:00
      // 00:45 + 60 = 01:45
      // Together:    01:45 = 105 sec
      val groupedEntity = new TimeEntry("2016-04-13T01:00:00", 105, 1, "first activity (PC), second activity (Mobile)", "", 0)
      grouper.groupedEntries(input) shouldBe List(groupedEntity)
    }
  }
}
