package example

import com.felstar.scalajs.vue.Vue
import shared.RotaMeta

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.annotation.ScalaJSDefined
import scalatags.Text.all._

@js.native
trait Summary extends Vue {
  var metas: js.Array[JsRotaMeta] = js.native
}

@js.native
trait JsRotaMeta extends js.Object {
  val id: Int
  val year: Int
  val month: Int
}

object JsRotaMeta {
  def apply(id: Int, year: Int, month: Int): JsRotaMeta =
    js.Dynamic.literal(id=id,year=year,month=month).asInstanceOf[JsRotaMeta]
  def rotameta2jsrotameta(rotaMeta: RotaMeta): JsRotaMeta =
    literal(id=rotaMeta.id, year=rotaMeta.year, month=rotaMeta.month).asInstanceOf[JsRotaMeta]
}

object Summary {
  val template = div(`class`:="summary",
    button("v-link".attr:="{ name: 'create' }", "Create a new rota"),
    ol(
      li("v-for".attr:="meta in metas",
        a("v-link".attr:="{ name: 'rota', params: { id: meta.id }}",
          "{{meta.id}}: {{meta.year}}-{{meta.month}}"),
        button("Delete"),
        button("Compute")
      )
    )
  )
  val methods = literal()
}