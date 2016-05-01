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
trait CreationDialog extends Vue {
  var mode: String = js.native
  var month: String = js.native
  var year: Int = js.native
  var days: js.Array[JsDay] = js.native
}

object CreationDialog {
  import CheckboxBtn._
  import CheckboxGroup._
  import JsDay._
  import scala.concurrent.ExecutionContext.Implicits.global

  def apply(): CreationDialog = {

    literal(
      template=template.toString(),
      data=data,
      methods=methods
    ).asInstanceOf[CreationDialog]
  }

  val template = div(`class`:="creation-dialog",
    div(`class`:="information-gathering", "v-if".attr:="mode === 'init'",
      input(`type`:="text", placeholder:="Mese", "v-model".attr:="month"),
      button("v-on:click".attr:="create", "Ok")
    ),
    div(`class`:="month-creation", "v-if".attr:="mode === 'create'",
      table(
        tr("v-for".attr:="day in days",
          td("{{ day.ofTheMonth }}"),
          td("v-for".attr:="shift in day.shifts",
            checkboxGroup("v-bind:value".attr:="shift.properties",
              checkboxBtn(value:="gettone", "Gett"),
              checkboxBtn(value:="altroRep", "A/R")
           )
          )
        )
      ),
      button("v-on:click".attr:="save", "Salva")
    )
  )

  val data = () => literal(
    mode="init",
    month="",
    year=2016,
    days=js.Array(JsDay(1, Seq(JsShift(1, Seq("example"), Map("hello" -> 1), Seq("oh!")))))
  )

  val methods = literal(
    create=((vm: CreationDialog) => {
      val month = vm.month
      val nShifts = 3
      val nMonth = month match {
        case "Giugno" => 6
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
      vm.$set("mode", "create")
    }): ThisFunction,
    save=((vm: CreationDialog) => {
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
