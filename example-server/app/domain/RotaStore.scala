package domain

import scala.concurrent.Future

trait RotaStore {
  def get(id: Int): Future[(Int, Int, String)]
  def fetch(): Future[Seq[(Int, Int, Int)]]
  def create(year: Int, month: Int, rotaWithoutId: String): Future[Int]
  def update(rota: String, id: Int): Future[Boolean]
  def delete(id: Int*): Future[Boolean]
}