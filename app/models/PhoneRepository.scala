package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import services.Cache
import slick.jdbc.JdbcProfile
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future};

@Singleton
class PhoneRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  
  
  private val cacheByNumber = new Cache[String, Phone](10)
  
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class PhoneTable (tag: Tag) extends Table[Phone](tag, "phones") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def number = column[String]("number")

    def * = (id, title, number) <> ((Phone.apply _).tupled, Phone.unapply)
  }

  private val phones = TableQuery[PhoneTable]

  if (cacheByNumber.isEmpty){
    val list: Seq[Phone] = allPhones
    
    list.foreach(phone => {
      cacheByNumber.put(phone.number, phone)
    })
      
  }
  
  def create (phone: Phone): Future[Long] = {
    val userId = (phones returning phones.map(_.id)) += Phone(0, phone.title, phone.number)
    db.run(userId)
  }

  private def allPhones: Seq[Phone] ={
    val phonesFromDb: Future[Seq[Phone]] = db.run( phones.result)
    val list: Seq[Phone] = Await.result(phonesFromDb, Duration.Inf)
    list
  } 
  
  def list (): Seq[Phone] = {
    allPhones
  }

  def delete (id: Long): Unit = {
    val phone = this.findById(id)
    cacheByNumber.remove(phone.number)
    
    db.run(phones.filter(_.id === id).delete)
  }

  def searchByName (title: String): List[Phone] = {
    val phonesFromDb: Future[Seq[Phone]] =
      db.run(phones.filter(t => t.title.toLowerCase.like(s"%${title.toLowerCase}%")).result)
    Await.result(phonesFromDb, Duration.Inf).toList
  }

  def searchByNumber (number: String): List[Phone] = {
    val phonesFromDb: Future[Seq[Phone]] = db.run(phones.filter(t => t.number.like(s"%$number%")).result)
    Await.result(phonesFromDb, Duration.Inf).toList
  }

  def findById (id: Long): Phone = {
    val action = phones.filter(p => p.id === id).result
    val future: Future[Seq[Phone]] = db.run(action)
    val dbPhones = Await.result(future, Duration.Inf)
    if (dbPhones.nonEmpty) {
      return dbPhones.head
    }
    null
  }

  def getByNumber (number: String): Phone = {
    if (cacheByNumber.containsKey(number)) {
      return cacheByNumber.get(number)
    }
    val future: Future[Seq[Phone]] = db.run(phones.filter(p => p.number === number).result)
    val dbPhones = Await.result(future, Duration.Inf)
    if (dbPhones.nonEmpty) {
      val first = dbPhones.head
      cacheByNumber.put(first.number, first)
      return first
    }
    null
  }


  def edit (id: Long, title: String, number: String): Unit = {
    val titles = for {c <- phones if c.id === id} yield c.title
    val updateTitleAction = titles.update(title)

    val numbers = for {c <- phones if c.id === id} yield c.number
    val updateNumberAction = numbers.update(number)

    db.run(updateTitleAction)
    db.run(updateNumberAction)
  }
}
