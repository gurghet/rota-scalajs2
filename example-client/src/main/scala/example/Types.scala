package example

import shared.{Day, Shift}

import scala.language.implicitConversions
import scala.scalajs.js
import scalajs.js.Dynamic.literal
import scalajs.js.JSConverters._

@js.native
trait JsDay extends js.Object {
  val ofTheMonth: Int = js.native
  var shifts: js.Array[JsShift] = js.native
}

object JsShift {
  def apply(order: Int,
            team: Seq[String],
            preferences: Map[String, Int],
            properties: Seq[String]): JsShift = {
    literal(
      order = order,
      team = team.toJSArray,
      preferences = preferences.toJSDictionary,
      properties = properties.toJSArray
    ).asInstanceOf[JsShift]
  }
  implicit def shift2jsshift(shift: Shift): JsShift = {
    literal(
      order = shift.order,
      team = shift.team.toJSArray,
      preferences = shift.preferences.toJSDictionary,
      properties = shift.properties.toJSArray
    ).asInstanceOf[JsShift]
  }
}

object JsDay {
  def apply(ofTheMonth: Int, shifts: Seq[_]): JsDay = {
    literal(
      ofTheMonth = ofTheMonth,
      shifts = shifts.map {
        case shift: Shift => JsShift.shift2jsshift(shift)
        case other => other
      }.toJSArray
    ).asInstanceOf[JsDay]
  }
  implicit def day2jsday(day: Day): JsDay = {
    literal(
      ofTheMonth = day.ofTheMonth,
      shifts = day.shifts.map(JsShift.shift2jsshift).toJSArray
    ).asInstanceOf[JsDay]
  }
  def jsday2day(jsDay: JsDay): Day = {
    Day(jsDay.ofTheMonth, jsDay.shifts.map{ jShift =>
      Shift(jShift.order, jShift.team, jShift.preferences.toMap, jShift.properties)
    })
  }
}

@js.native
trait JsShift extends js.Object {
  val order: Int = js.native
  var team: js.Array[String] = js.native
  var preferences: js.Dictionary[Int] = js.native
  var properties: js.Array[String] = js.native
}