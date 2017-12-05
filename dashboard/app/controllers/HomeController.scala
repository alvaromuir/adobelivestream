package controllers

import javax.inject._

import play.api.mvc._

import akka.actor.ActorSystem
import akka.kafka.{AutoSubscription, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import org.apache.kafka.clients.consumer.ConsumerRecord
import services.KafkaService
import services.DbService

import scala.concurrent.Future


@Singleton
class HomeController @Inject()(cc: ControllerComponents, kafkaService: KafkaService) (implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc)  {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  val subscription: AutoSubscription = Subscriptions.topics("da_dev_rts_digital_adobe")
  val kafkaSource: Source[ConsumerRecord[Array[Byte], String],Consumer.Control] = Consumer.plainSource(kafkaService.consumerSettings, subscription)

  def socket = WebSocket.acceptOrResult[Any, String] { _ =>
    val flow = Flow.fromSinkAndSource(Sink.ignore, kafkaSource.map(_.value))
    Future.successful(Right(flow))
  }

}
