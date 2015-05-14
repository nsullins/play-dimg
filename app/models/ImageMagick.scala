package models

import dimg.global.Global._
import play.api.libs.iteratee.{Enumerator, Enumeratee}
import java.io.File
import playcli.CLI

/**
 * Created with IntelliJ IDEA.
 * User: sun
 * Date: 3/23/13
 * Time: 3:53 PM
 */
object ImageMagick {

  def convertSize(width: Int, height: Int, crop: Boolean, imageType: String): Enumeratee[Array[Byte], Array[Byte]] = {
    if(crop)
      CLI.pipe(s"convert - -resize $width" + "x" + s"$height" + s"^ -gravity Center -extent $width" + "x" + s"$height $imageType:-")
    else
      CLI.pipe(s"convert - -resize $width" + "x" + s"$height $imageType:-")
  }

  def minDimension(width: Int, height: Int): Int = (width::height::Nil).min

  def watermark(width: Int, height: Int, imageType: String): Enumeratee[Array[Byte], Array[Byte]] = {
    minDimension(width, height) match{
      case x if x < 400 => waterMarkWithSize(50, imageType)
      case x if x > 400 && x < 750 => waterMarkWithSize(100, imageType)
      case _ => waterMarkWithSize(150, imageType)
    }
  }

  def waterMarkWithSize(sideLength: Int, imageType: String): Enumeratee[Array[Byte], Array[Byte]] = {
    CLI.pipe(s"composite -watermark 50% -gravity southeast public/images/$sideLength" + "x" + s"$sideLength" + s"_rpi_watermark.png - $imageType:-")
  }

  def resize(width: Int, height: Int, id: String, name: String, imageType:String, path: String, crop: Boolean, addWaterMark: Boolean): Enumerator[Array[Byte]] = {
    val convert = Enumerator.fromFile(new File(config.getString("dynamic.media.file.path") + s"/$path" + s"/$id" + s".$imageType"))&>
      convertSize(width, height, crop, imageType)
    if(addWaterMark)
      convert &> watermark(width, height, imageType)
    else
      convert
  }

  def photoNotFound(width: Int, height: Int, crop: Boolean): Enumerator[Array[Byte]] = {
    Enumerator.fromFile(new File("public/images/photo-not-found.png")) &> convertSize(width, height, crop, "PNG")
  }
}
