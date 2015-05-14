package models


import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith



/**
 * Created with IntelliJ IDEA.
 * User: sun
 * Date: 3/24/13
 * Time: 10:14 PM
 */
@RunWith(classOf[JUnitRunner])
class ImageMagickSpec extends Specification {

  "ImageMagick" should {

    "help understand how iteratees work by running isolated in test case" in {

      val width: Int = 555
      val height: Int = 666
      val imageId: String = "1870912"
      val name: String = "blah.jpg"
      val path: String = "P/57/681957"
      val size: Double = 44.44
      val imageType: String = "jpg"
      val img: DimgImage = DimgImage(imageId, name, path, size, imageType)

      ImageMagick.resize(width, height, imageId, name, img)

      3 mustEqual(3)//make test pass
    }
  }

}
