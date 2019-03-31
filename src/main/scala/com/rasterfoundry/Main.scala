package com.rasterfoundry.lambda.overviews

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler, LambdaLogger}
import io.circe.parser.{parse, decode}
import com.typesafe.scalalogging.LazyLogging
import geotrellis.contrib.vlm.{RasterSource, MosaicRasterSource}
import geotrellis.contrib.vlm.geotiff.GeoTiffRasterSource
import geotrellis.raster.RasterExtent
import geotrellis.raster.histogram._
import geotrellis.vector.Extent
import io.circe._

import java.util.UUID
import java.io.{InputStream, OutputStream}

case class Overview(
  id: UUID,
  uris: List[String],
  bandOverrides: List[(Int, Int, Int)],
  extent: (Double, Double, Double, Double)
)

object Overview {
  val decodeOverview: Decoder[Overview] = Decoder.forProduct4("id", "uris", "bandOverrides", "extent") {
    Overview.apply _
  }
}

class Main extends RequestStreamHandler {

  // this intends to write to s3 for now but could cache in memcached by recognizable keys instead
  def writeToS3(rs: RasterSource, hists: List[Histogram[Double]], x: Int, y: Int, r: Int, g: Int, b: Int): Unit = {
    // TODO get this from 4/x/y
    val ext: Extent = ???
    val png = rs.read(ext) map {
      _.tile.subsetBands(r, g, b).renderPng
    }
  }

  def overviewToS3(overview: Overview): Unit = {
    val sources = overview.uris map { GeoTiffRasterSource(_) }
    val hists: List[Histogram[Double]] = ???
    // TODO -- get actual cell size for rows / cols
    val rs = MosaicRasterSource.unsafeFromList(
      sources,
      _rasterExtent=Some(
        RasterExtent(Extent(overview.extent._1, overview.extent._2,overview.extent._3,overview.extent._4),
                     256, 256)))
    // TODO -- zoomed reader at level 4 with each x, y that can be read
    val keys: List[(Int, Int)] = ???
    for {
      (r, g, b) <- overview.bandOverrides
      (x, y) <- keys
    } yield {
      writeToS3(rs, hists, x, y, r, g, b)
    }
  }

  def handleRequest(input: InputStream, out: OutputStream, context: Context): Unit = {
    val logger = context.getLogger()
    val payload = parse(scala.io.Source.fromInputStream(input).mkString("")) match {
      case Right(js) => js
      case Left(e) => throw e
    }
    logger.log(s"Payload was: ${payload.noSpaces}")
  }
}
