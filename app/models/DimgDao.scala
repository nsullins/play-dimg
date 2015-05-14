package models

import dimg.global.Global._
import scala.slick.session.{Database}
import play.api._
import concurrent.{Future, ExecutionContext}
import play.libs.Akka

/**
 * Created with IntelliJ IDEA.
 * User: Nathan
 * Date: 3/13/13
 * Time: 9:59 AM
 */
case class DimgImage(id: String, fileName: String, path: String, size: Double, imageType: String)

object DimgDao extends DatabaseAccess{

  // Import the query language features from the driver
  import scala.slick.driver.PostgresDriver.simple._

  object DimgImages extends Table[DimgImage]("media") {
    def id = column[String]("media_id", O.PrimaryKey)
    def fileName = column[String]("file_name")
    def path = column[String]("relative_path")
    def size = column[Double]("size_in_bytes")
    def imageType = column[String]("image_type")
    def * = id ~ fileName ~ path ~ size ~ imageType <> (DimgImage, DimgImage.unapply _)
  }

  def get(id: String)(implicit session: Session): Option[DimgImage] =
    (for(img <- DimgImages if img.id === id) yield img).firstOption

  def queryForImage(id: String)(implicit ec: ExecutionContext = Contexts.dbExecutionContext): Future[Option[DimgImage]] = {
    Future{
      DBPool withSession {implicit session: Session =>
        try {
          get(id)
        }catch{
          case t => {
            Logger.debug(s"Error retrieving image data from DB for id: $id", t)
            None
          }
        }
      }
    }
  }
}

object Contexts{
  implicit val dbExecutionContext: ExecutionContext = Akka.system.dispatchers.lookup("akka.actor.db-context")
}

import com.mchange.v2.c3p0.ComboPooledDataSource

trait DatabaseAccess {
  val DBPool = {                      //TODO Determine correct config settings.
    val ds = new ComboPooledDataSource
    ds.setDriverClass(config.getString("db.default.driver"))
    ds.setJdbcUrl(config.getString("db.default.url"))
    ds.setUser(config.getString("db.default.user"))
    ds.setPassword(config.getString("db.default.password"))
    ds.setMinPoolSize(5)
    ds.setAcquireIncrement(5)
    ds.setMaxPoolSize(10)
    Database.forDataSource(ds)
  }
}