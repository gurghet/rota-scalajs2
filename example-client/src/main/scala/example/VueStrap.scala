package example

import com.felstar.scalajs.vue.Vue

import scala.scalajs.js
import scalatags.Text.all._

@js.native
trait CheckboxGroup extends Vue {
  var style: String = js.native
  var value: js.Array[String] = js.native
}

object CheckboxGroup {
  val checkboxGroup: Tag = "checkbox-group".tag
}

@js.native
trait CheckboxBtn extends Vue {
  override val $parent: CheckboxGroup = js.native
  var value: String = js.native
  var checked: Boolean = js.native
  def style: String = js.native
}

object CheckboxBtn {
  val checkboxBtn: Tag = "checkbox-btn".tag
}

@js.native
trait RadioGroup extends Vue {
  var style: String = js.native
  var value: Int = js.native
}

object RadioGroup {
  val radioGroup: Tag = "radio-group".tag
}

@js.native
trait RadioBtn extends Vue {
  override val $parent: RadioGroup = js.native
  var value: Int = js.native
  var checked: Boolean = js.native
  def style: String = js.native
}

object RadioBtn {
  val radioBtn: Tag = "radio-btn".tag
}