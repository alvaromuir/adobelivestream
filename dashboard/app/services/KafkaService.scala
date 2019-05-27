package services

import java.util.Properties
import javax.inject.{Inject, Singleton}

import akka.NotUsed
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{AutoSubscription, ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import com.typesafe.config.ConfigFactory

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import play.api.{Configuration, Logger}
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future
import scala.collection.JavaConversions._

@Singleton
class KafkaService @Inject() (configuration: Configuration, appLifecycle: ApplicationLifecycle) {
  Logger.info(s"Starting Kafka service...")

  val config: Properties = ConfigFactory.load().getConfig("akka.kafka.consumer").entrySet().foldRight(new Properties) {
    (item, props) =>
      props.setProperty(item.getKey, item.getValue.unwrapped().toString)
      props
  }

  private val TOPIC = config.getProperty("topic")
  val subscription: AutoSubscription = Subscriptions.topics(TOPIC)

  implicit val system: ActorSystem = ActorSystem("AdobeLiveStreamConsumer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()


  val consumerSettings: ConsumerSettings[Array[Byte], String] = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers(config.getProperty("kafka-clients.bootstrap.servers"))
    .withGroupId(config.getProperty("kafka-clients.group.id"))
    .withClientId(config.getProperty("kafka-clients.client.id"))
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  appLifecycle.addStopHook { () =>
    materializer.shutdown()
    Logger.info(s"Shutting down Kafka service.")
    Future.successful(())
  }

  val source: Source[ConsumerRecord[Array[Byte], String], Consumer.Control] = Consumer.plainSource(consumerSettings, subscription)
  val flow: Flow[Any, String, NotUsed] = Flow.fromSinkAndSource(Sink.ignore, source.map(_.value))

}
