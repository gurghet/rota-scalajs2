package controllers

import domain.RotaSlickStore
import play.api._
import play.api.http.MimeTypes
import play.api.mvc._
import shared.{RotaMeta, Day}
import upickle.Js
import scalatags.Text.TypedTag
import scalatags.Text.all._
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(indexView().toString()).withHeaders(CONTENT_TYPE -> MimeTypes.HTML)
  }

  def indexView() = {
    val routerView: Tag = "router-view".tag
    val vLink = "v-link".attr
    val body = Seq(
      div(id:="app",
        h2("This title is outside of the router"),
        p(
          a(vLink:="{ path: '/summary' }", "Go to Summary"),
          a(vLink:="{ path: '/bar' }", "Go to Bar")
        ),
        routerView
      )
    )
    mainView(body)
  }

  def mainView(content: Seq[Modifier]) = {
    html(
      head(
        link(rel:="stylesheet",
          href:="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
      ),
      body(
        content,
        scripts()
        )
    )
  }

  def scripts() = Seq(jsdeps(), selectScript(), launcher())

  def jsdeps() = {
    script(src := "/assets/exampleclient-jsdeps.js")
  }

  def selectScript(): TypedTag[String] = {
    if (Play.isProd(Play.current)) {
      script(src := "/assets/exampleclient-opt.js")
    } else {
      script(src := "/assets/exampleclient-fastopt.js")
    }
  }

  def launcher(): TypedTag[String] = {
    script(src := "/assets/exampleclient-launcher.js")
  }

  def createMonth(year: Int, month: Int) = Action.async(parse.tolerantText) { implicit request =>
    import upickle.default._

    val days = read[Seq[Day]](request.body)
    val id = year * 10 + month

    val idd = RotaSlickStore.create(year, month, request.body)

    idd.map{ i =>
      Ok(upickle.json.write(Js.Num(i)))
    }
  }



  def allMonths() = Action.async { implicit request =>
    val rotas = RotaSlickStore.fetch()

    rotas.map{ t =>
      Ok(upickle.json.write(upickle.default.writeJs(t.map{ m => RotaMeta(m._1, m._2, m._3)}))).withHeaders("content-type" -> "application/json")
    }
  }

  def getMonth(id: Int) = Action.async {
    val rota = RotaSlickStore.get(id)

    rota.map{ t =>
      Ok(upickle.json.write(upickle.default.writeJs[Seq[Day]](upickle.default.read[Seq[Day]](t._3)))).as("application/json")
    }
  }
}
