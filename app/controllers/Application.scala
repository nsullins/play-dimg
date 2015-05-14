package controllers

import play.api.mvc._
import play.api._
import models.{StatusDao, ImageMagick, DimgDao}
import models.Contexts.dbExecutionContext
import dimg.global.Global._
import scala.Predef._
import scala.Some

object Application extends Controller {

  val maxHeight, maxWidth = 6600

  def info = Action{
    Async{
      StatusDao.checkStatus map{
        case None =>  ServiceUnavailable
        case Some(s) => Ok(views.html.info("OK", config))
      }
    }
  }

  def dimg(dim: String, id: String, name: String) = Action {implicit request =>
    parseDimensionsHandleRequest(dim, id, name, request.getQueryString("wm"))
  }

  def dimgCrop(dim: String, id: String, name: String) = Action {implicit request =>
    parseDimensionsHandleRequest(dim, id, name, request.getQueryString("wm"), crop = true)
  }

  def parseDimensionsHandleRequest(dim: String, id: String, name: String, watermark: Option[String], crop: Boolean = false): Result = {
    val addWaterMark = watermark match{
      case Some(x) if(x.toLowerCase == "true") => true
      case _ => false
    }
    val dimensions = dim.split("x")
    handleImageReq(dimensions(0).toInt, dimensions(1).toInt, id, name, crop, addWaterMark)
  }

  def handleImageReq(width: Int, height: Int, id: String, name: String, crop: Boolean, addWaterMark: Boolean): Result = (width, height) match {
      case (w, h) if(w > maxWidth) => handleImageReq(maxWidth, h, id, name, crop, addWaterMark)
      case (w, h) if(h > maxHeight) => handleImageReq(w, maxHeight, id, name, crop, addWaterMark)
      case _ => {
        Logger.info(s"Creating media with dimensions width: $width and height: $height")
        streamResult(width, height, id, name, crop, addWaterMark)
    }
  }

  def streamResult(width: Int, height: Int, id: String, name: String, crop: Boolean, addWaterMark: Boolean): Result = {
    Async{
      DimgDao.queryForImage(id) map {
        case None => Ok.stream(ImageMagick.photoNotFound(width, height, crop)).withHeaders(CONTENT_TYPE -> "image/png")
        case Some(s) => Ok.stream(ImageMagick.resize(width, height, id, name, s.imageType.toLowerCase, s.path, crop, addWaterMark)).withHeaders(CONTENT_TYPE -> "image/".concat(s.imageType))
      }
    }
  }
}