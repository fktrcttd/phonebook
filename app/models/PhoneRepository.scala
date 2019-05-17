package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class PhoneRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext){
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._
  private class PhoneTable(tag: Tag ) extends Table[Phone](tag, "phones"){

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def number = column[String]("number")
    def * = (id, title, number) <> ((Phone.apply _).tupled, Phone.unapply)
  }

  private val phones = TableQuery[PhoneTable]

  def create(phone: Phone) = {
    val userId = (phones returning phones.map(_.id)) += Phone(0, phone.title, phone.number)
    db.run(userId)
  }
  
  def list():Seq[Phone] = {
    val where = phones.result
    val phonesFromDb: Future[Seq[Phone]] = db.run(where)
    Await.result(phonesFromDb, Duration.Inf)
  }

  def delete(id: Long) ={
    val q = phones.filter(_.id === id)
    val action = q.delete
    val affectedRowsCount: Future[Int] = db.run(action)
    val sql = action.statements.head
    println("Удалено строк" + affectedRowsCount.toString)
    println("SQL код" + sql)
  }
  
  def searchByName (title: String) = {
    val where = phones.result
    val phonesFromDb: Future[Seq[Phone]] = db.run(where)
    val list = Await.result(phonesFromDb, Duration.Inf).toList
    list.filter(p => p.title.toLowerCase().contains(title))
  }
  
  def searchByNumber (number: String) = {
    val where = phones.result
    val phonesFromDb: Future[Seq[Phone]] = db.run(where)
    val list = Await.result(phonesFromDb, Duration.Inf).toList
    list.filter(_.number.toLowerCase().contains(number))
  }

  def readById (id:Long)= {
    val where = phones.filter(p =>p.id ===id)
    val action = where.result
    val phonesFromDb: Future[Seq[Phone]] = db.run(action)
    val sql = action.statements.head
    val r = Await.result(phonesFromDb, Duration.Inf)
    r.head
  }

  def edit(id:Long, title:String, number: String) = {
    val titles = for { c <- phones if c.id === id } yield c.title
    val updateTitleAction = titles.update(title)
    val updTitleSql = titles.updateStatement

    val numbers = for { c <- phones if c.id === id } yield c.number
    val updateNumberAction = numbers.update(number)
    val numbersSql = numbers.updateStatement

   val q = db.run(updateTitleAction)
   val q1 = db.run(updateNumberAction)
  }
}
