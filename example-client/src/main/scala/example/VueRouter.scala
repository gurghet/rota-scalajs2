package example

import com.felstar.scalajs.vue.Vue

import scala.scalajs.js

/**
  * Created by gurghet on 01.05.16.
  */
@js.native
class VueRouter extends js.Object {

  /**
    * Register a map of top-level paths.
    *
    * @param {Object} map
    */
  def map(mapping: js.Object): VueRouter = js.native

  /**
    * Start the router.
    *
    * @param {VueConstructor} App
    * @param {String|Element} container
    * @param {Function} [cb]
    */
  def start(app: Vue, container: String, cb: js.Function = null) = js.native
}
