package example

import com.felstar.scalajs.vue.Vue
import example.JsDay._
import org.scalajs.dom.ext.Ajax
import shared.RotaMeta

import scala.scalajs.js
import js.Dynamic.literal

import scalatags.Text.all._
import org.scalajs.dom
import upickle.default._
import scalajs.js.JSConverters._
import scala.concurrent.ExecutionContext.Implicits.global

@js.native
trait Transition extends js.Object {
  def next(): Unit = js.native
  def next(data: js.Object): Unit = js.native
}

object ExampleJS extends js.JSApp {
  val slot: Tag = "slot".tag

  def main(): Unit = {
    val summary = Vue.extend(literal(
      template=Summary.template.toString(),
      data=()=>literal(metas=js.Array()),
      route=literal(data=(transition: Transition)=>
        /*Ajax.get(
          url = "/month/all"
        ).map{ r => if (r.status == 200) {
          //val rotaMetas = read[Seq[RotaMeta]](r.responseText)
          //transition.next(literal(
          //  metas=rotaMetas.map(JsRotaMeta.rotameta2jsrotameta).toJSArray
          //))
        }}*/ // <== this code somehow sets a value$1 in the data! and calls next 2 times
        ////////////must look at the resulting javascript
      )
    )).asInstanceOf[Summary]

    val Bar = Vue.extend(literal(
      template="<p>This is bar!</p>"
    ))

    Vue.component("checkbox-btn", literal(
      template=label(
        `class`:="btn",
        ":class".attr:="""{
             'active':checked,
             'btn-success':style === 'success',
             'btn-warning':style === 'warning',
             'btn-info':style === 'info',
             'btn-danger':style === 'danger',
             'btn-default':style === 'default',
             'btn-primary':style === 'primary'
             }""",
        input(`type`:="checkbox",
          autocomplete:="false",
          ":checked".attr:="checked",
          "v-on:click".attr:="handleClick"),
        slot()
      ).render,
      props=literal(
        value=literal(
          "type" -> js.eval("String")
        ),
        checked=literal(
          "type" -> js.eval("Boolean"),
          "default" -> js.eval("false")
        )
      ),
      computed=literal(
        style=((th: CheckboxBtn) => th.$parent.style): js.ThisFunction
      ),
      methods=literal(
        handleClick=((vm: CheckboxBtn) => {
          val parent = vm.$parent
          val index = parent.value.indexOf(vm.value)
          if (index == -1) parent.value.push(vm.value) else parent.value.splice(index, 1)
          vm.checked = !vm.checked
        }): js.ThisFunction
      ),
      created=((vm: CheckboxBtn) => {
        if (vm.$parent.value.length > 0) {
          vm.checked = vm.$parent.value.indexOf(vm.value) > -1
        } else if (vm.checked) {
          vm.$parent.value.push(vm.value)
        }
      }): js.ThisFunction
    ))

    Vue.component("checkbox-group", literal(
      template=div(`class`:="btn-group", "data-toggle".attr:="buttons", "slot".tag).render,
      props=literal(
        "style" -> literal("default" -> "default"),
        "value" -> literal("default" -> (() => js.Array()))
      )
    ))

    val creationDialog = Vue.extend(CreationDialog())

    val App = Vue.extend(literal())

    val router = new VueRouter()

    router.map(literal(
      "/summary" -> literal(component=summary),
      "/bar" -> literal(component=Bar),
      "/rota/:id" -> literal(
        name="rota",
        component=Bar
      ),
      "/rota/new" -> literal(
        name="create",
        component=creationDialog
      )
    ))

    router.start(App, "#app")
  }

}
