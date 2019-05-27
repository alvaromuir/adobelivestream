package controllers

import java.time.{Instant, LocalDateTime, ZoneId}
import javax.inject._

import play.api.mvc._
import akka.actor.ActorSystem
import akka.stream.Materializer
import services.{CachedHit, IgniteService, KafkaService}
import com.verizon.bdcpe.adobelivestream.collector.HitModel.Hit
import org.apache.ignite.IgniteCache
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods
import org.json4s.jackson.Serialization.write

import scala.concurrent.Future



@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               kafkaService: KafkaService,
                               igniteService: IgniteService)
                              (implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc)  {


  val cache: IgniteCache[Double, CachedHit] = igniteService.cache

  implicit val formats: DefaultFormats.type = DefaultFormats

  kafkaService.source.runForeach(x => {
    val hit = JsonMethods.parse(x.value()).extract[Hit]
    val sessionId = hit.hitIdHigh.get + hit.hitIdLow.get
    val cachedHit = CachedHit(hit.visIdHigh.get + hit.visIdLow.get, hit.timeGMT.get, hit)

    if(! cache.replaceAsync(sessionId, cachedHit).get) cache.put(sessionId, cachedHit)
    val zone = ZoneId.systemDefault()
    val timeStamp = Instant.now.minusSeconds(1800)
    val timeStampLocal = LocalDateTime.ofInstant(timeStamp, zone)
    val timeGMT = cachedHit.details.timeGMT.get
    val timeGMTLocal = Instant.ofEpochSecond(timeGMT.toLong).atZone(ZoneId.systemDefault()).toLocalDateTime()

    println(s"%21s %20s %20s %5s".format(sessionId, timeGMTLocal, timeStampLocal, timeGMT.toLong > timeStamp.getEpochSecond))

//    println(f"$sessionId $timeGMTLocal%20s $timeStampLocal%20s ${timeGMT.toLong > timeStamp.getEpochSecond}")
  })

  def socket: WebSocket = WebSocket.acceptOrResult[Any, String] { _ =>
    Future.successful(Right(kafkaService.flow))
  }

  def index: Action[AnyContent] = Action {
    Ok(views.html.index(s"cache is running, with a size of ${cache.sizeLong()}"))
  }

}
