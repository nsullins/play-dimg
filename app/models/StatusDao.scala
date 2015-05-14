package models

import scala.concurrent.{ExecutionContext, Future}
import scala.slick.session.{Session}
import scala.slick.jdbc.StaticQuery.interpolation
import play.api.Logger


/**
 * Created with IntelliJ IDEA.
 * User: Nathan
 * Date: 4/23/13
 * Time: 3:32 PM
 */
object StatusDao extends DatabaseAccess{

  def checkStatus(implicit ec: ExecutionContext = Contexts.dbExecutionContext): Future[Option[Int]] = {
    Future{
      DBPool withSession {implicit session: Session =>
        try{
          sql"SELECT count(*) AS COUNT FROM list_element".as[Int].firstOption
        }catch{
          case t => {
            Logger.warn("Error connecting to DB for info.jsp", t)
            None
          }

        }
      }
    }
  }
}
