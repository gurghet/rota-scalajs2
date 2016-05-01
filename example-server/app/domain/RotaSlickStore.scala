package domain
import scala.concurrent.Future

/**
  * Created by gurghet on 01.05.16.
  */
object RotaSlickStore extends RotaStore {
  import play.api.db.DB
  import slick.driver.H2Driver.api._
  import scala.concurrent.ExecutionContext.Implicits.global

  class Rotas(tag: Tag) extends Table[(Option[Int], Int, Int, String)](tag, "ROTAS"){
    def id  = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)
    def year = column[Int]("YEAR")
    def month = column[Int]("MONTH")
    def rota = column[String]("OBJ")
    def * = (id, year, month, rota) <> (ee, Tuple4.unapply[Option[Int], Int, Int, String])
  }

  lazy val ee: ((Option[Int], Int, Int, String)) => (Option[Int], Int, Int, String) = { case ((a, b, c, d)) => (a, b, c, d) }

  import play.api.Play.current
  private def db: Database = Database.forDataSource(DB.getDataSource())

  val rotas = TableQuery[Rotas]

  override def get(id: Int): Future[(Int, Int, String)] = {
    db.run(rotas.filter(_.id === id).map{r => (r.year, r.month, r.rota)}.result.head)
  }

  override def create(year: Int, month: Int, rotaWithoutId: String): Future[Int] = {
    val eventualResult = db.run((rotas returning rotas.map(_.id)) += (None, year, month, rotaWithoutId))
    eventualResult.map(singleInsertResult => singleInsertResult.get)
  }

  override def update(rota: String, id: Int): Future[Boolean] = {
    db.run{
      val q = for {t <- rotas if t.id === id } yield t.rota
      q.update(rota)
    }.map(_ == 1)
  }

  override def delete(id: Int*): Future[Boolean] = ???

  override def fetch(): Future[Seq[(Int, Int, Int)]] = {
    db.run(rotas.map{r => (r.id.getOrElse(-1), r.year, r.month)}.result)
  }
}
