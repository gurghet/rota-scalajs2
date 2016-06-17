package example

import com.felstar.scalajs.vue.Vue
import org.scalajs.dom.ext.Ajax
import shared.{Shift, Day}
import upickle.Js

import scala.scalajs.js.ThisFunction
import scalatags.Text.all._
import scalajs.js.Dynamic.literal
import scalajs.js
import scalajs.js.JSConverters._

@js.native
trait EnterPreferences extends Vue {
  var mode: String = js.native
  var month: String = js.native
  var year: Int = js.native
  var days: js.Array[JsDay] = js.native
}

object EnterPreferences {
  import RadioGroup._
  import RadioBtn._
  import JsDay._
  import JsShift._
  import scala.concurrent.ExecutionContext.Implicits.global

  def apply(): EnterPreferences = {

    literal(
      template=template.toString(),
      data=data,
      methods=methods
    ).asInstanceOf[EnterPreferences]
  }

  val template = div(`class`:="enter-preferences",
    div(`class`:="name-gathering", "v-if".attr:="mode === 'init'",
      input(`type`:="text", placeholder:="Nome", "v-model".attr:="name"),
      button("v-on:click".attr:="enter", "Ok")
    ),
    div(`class`:="month-preferences", "v-if".attr:="mode === 'edit'",
      table(
        tr("v-for".attr:="day in days",
          td("{{ day.ofTheMonth }}"),
          td("v-for".attr:="shift in day.shifts",
            radioGroup("v-bind:value.sync".attr:="shift.preferences[name]",
              radioBtn("v-bind:value".attr:=(-2), "-2"),
              radioBtn("v-bind:value".attr:=(-1), "-1"),
              radioBtn("v-bind:value".attr:=1, "+1"),
              radioBtn("v-bind:value".attr:=2, "+2")
           )
          )
        )
      ),
      button("v-on:click".attr:="save", "Salva")
    )
  )

  val data = () => literal(
    mode="init",
    name="",
    month="",
    year=2016,
    days=js.Array(JsDay(1, Seq(JsShift(1, Seq("example"), Map("hello" -> 1), Seq("oh!")))))
  )

  val methods = literal(
    enter=((vm: EnterPreferences) => {
      Ajax.get(
        url = "/month/11",
        headers = Map("Content-Type" -> "application/json")
      ).map{ r => if (r.status == 200) {
        val days = upickle.default.read[Seq[Day]](r.responseText)
        val jsDays = days.map(JsDay.day2jsday).toJSArray
        vm.days = jsDays
      }}
      val month = vm.month
      val nShifts = 3
      val nMonth = month match {
        case "Giugno" => 6
        case "Luglio" => 7
        case _ => -1
      }
      val nDays = nMonth match {
        case 6 => 30
        case _ => 31
      }
      val retVal = for (d <- 1 to nDays) yield
        Day(d, collection.mutable.Seq.concat(
          (1 to nShifts).map{ shiftN => Shift(shiftN, Seq(), Map[String, Int](), Seq())}
        ))
      vm.days = retVal.map(JsDay.day2jsday).toJSArray
      vm.$set("mode", "edit")
    }): ThisFunction,
    save=((vm: EnterPreferences) => {
      Ajax.put(
        url = "/month/2016/06",
        data = upickle.json.write(upickle.default.writeJs(vm.days.map(jsday2day).toSeq)),
        headers = Map("content-type" -> "application/json")
      ).map{ r => if (r.status == 200) {
        org.scalajs.dom.console.info("should do something with router api")
      }}
    }): ThisFunction
  )
}
