package example

import com.felstar.scalajs.vue.Vue
import org.scalajs.dom.ext.Ajax
import shared.RotaMeta

import scala.scalajs.js
import js.Dynamic.literal

import scalatags.Text.all._
import upickle.default._
import js.JSConverters._
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
        Ajax.get(url = "/month/all").map{ case xhr =>
          val metas: Seq[RotaMeta] = read[Seq[RotaMeta]](xhr.responseText)
          val jsMetas: js.Array[JsRotaMeta] = metas.map(JsRotaMeta.rotameta2jsrotameta).toJSArray
          literal(metas=jsMetas)
        }.toJSPromise
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

    Vue.component("radio-btn", literal(
      template=label(
        `class`:="btn",
        ":class".attr:="""{
             'active':active,
             'btn-success':style === 'success',
             'btn-warning':style === 'warning',
             'btn-info':style === 'info',
             'btn-danger':style === 'danger',
             'btn-default':style === 'default',
             'btn-primary':style === 'primary'
             }""",
        input(`type`:="radio",
          ":checked".attr:="checked",
          "v-on:click".attr:="handleClick"),
        slot()
      ).render,
      props=literal(
        value=literal(
          "type" -> js.eval("Number"),
          "default" -> 0
        ),
        checked=literal(
          "type" -> js.eval("Boolean"),
          "default" -> js.eval("false")
        )
      ),
      computed=literal(
        style=((th: RadioBtn) => th.$parent.style): js.ThisFunction,
        active=((th: RadioBtn) => th.$parent.value == th.value): js.ThisFunction
      ),
      methods=literal(
        handleClick=((vm: RadioBtn) => {
          val parent = vm.$parent
          parent.value = vm.value
        }): js.ThisFunction
      ),
      created=((vm: RadioBtn) => {
        if (vm.$parent.value == vm.value) {
          vm.checked = true
        } else if (vm.$parent.value != 0 && vm.checked) {
          vm.$parent.value = vm.value
        }
      }): js.ThisFunction
    ))

    Vue.component("radio-group", literal(
      template=div(`class`:="btn-group", "data-toggle".attr:="buttons", "slot".tag).render,
      props=literal(
        "value" -> literal("default" -> 0, "twoWay" -> true),
        "style" -> literal("default" -> "default")
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
        component=Vue.extend(EnterPreferences())
      ),
      "/rota/new" -> literal(
        name="create",
        component=creationDialog
      )
    ))

    router.start(App, "#app")
  }

}
