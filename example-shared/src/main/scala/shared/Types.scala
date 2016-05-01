package shared

case class Shift(order: Int,
                 team: Seq[String],
                 preferences: Map[String, Int],
                 properties: Seq[String])

case class Day(ofTheMonth: Int, shifts: Seq[Shift])

case class RotaMeta(id: Int, year: Int, month: Int)