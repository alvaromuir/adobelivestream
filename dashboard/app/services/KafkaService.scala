package services

import java.util.Properties
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer

import com.typesafe.config.ConfigFactory

import org.apache.kafka.clients.consumer.ConsumerConfig
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
  val subscription = Subscriptions.topics("da_dev_rts_digital_adobe")

  implicit val system: ActorSystem = ActorSystem("AdobeLiveStreamConsumer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()


  val consumerSettings: ConsumerSettings[Array[Byte], String] = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers(config.getProperty("kafka-clients.bootstrap.servers"))
    .withGroupId(config.getProperty("kafka-clients.group.id"))
    .withClientId(config.getProperty("kafka-clients.client.id"))
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  appLifecycle.addStopHook { () =>
    materializer.shutdown()
    Logger.info(s"Shutting down Kafka service.")
    Future.successful(())
  }
}
