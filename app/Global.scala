import com.typesafe.config.ConfigFactory
import play.api.GlobalSettings

/**
 * Created with IntelliJ IDEA.
 * User: Nathan
 * Date: 3/13/13
 * Time: 10:15 AM
 */
package dimg{
  package global{
    object Global extends GlobalSettings{
      lazy val config = ConfigFactory.load
    }
  }
}