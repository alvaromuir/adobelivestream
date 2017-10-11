package com.verizon.bdcpe.adobelivestream.kafka

import com.verizon.bdcpe.adobelivestream.collector.{Collector, Parameters}
import com.verizon.bdcpe.adobelivestream.kafka.KafkaService.{Settings, createProducer}
import org.apache.kafka.clients.producer.ProducerRecord
import org.rogach.scallop.exceptions._
import org.rogach.scallop.{ScallopConf, ScallopOption}

import scala.language.postfixOps

/*
  * Created by Alvaro Muir<alvaro.muir@verizon.com>
  * Verizon Big Data & Cloud Platform Engineering
  * 10/9/17.
  */

object AdobeLivestreamKafka {
  val APP_NAME = "adobelivestream.Kafka"
  val APP_VERSION = "1.0"

  /**
    * Configuration is the overall program setup
    * @param arguments are command-line args to be parsed
    */
  case class Configuration(arguments: Seq[String]) extends ScallopConf(arguments) {
    mainOptions = Seq(appKey, appSecret, appId, connectionsMax, kafkaBrokers, kafkaTopic, kafkaClientId, kerberosEnabled,
      oauthTokenUrl, proxyHost, proxyPortNumber, proxyUsername, proxyPassword, eventLimit, required, excluded, filteredTo)

    version(s"\n$APP_NAME $APP_VERSION (c)2017 Verizon Big Data Cloud & Platform Engineering\n")
    banner("""Usage: adobelivestream-kafka -k <appKey> -s <appSecret> -i <appId> -m <maxConnections> -b <kafkaBrokers> -t <kafkaTopic>  ... [OPTIONS]
             |adobelivestream.Kafka sends real-time event data from Adobe Analytics to a Kafka quorum.
             |Options:
             |""".stripMargin)
    footer("\nFor more information, see the README.")
    val appKey: ScallopOption[String] = opt[String](short = 'k', descr = "Adobe application key", required = true)
    val appSecret: ScallopOption[String] = opt[String](short = 's', descr = "Adobe application secret", required = true)
    val appId: ScallopOption[String] = opt[String](short = 'i', descr = "Adobe application ID", required = true)
    val connectionsMax: ScallopOption[Int] = opt[Int](short = 'm', validate = 9 >, descr = "max concurrent connections", required = true)
    val oauthTokenUrl: ScallopOption[String] = opt[String](short = 'o', descr = "[Opt] Adobe OAuth Token Url")
    val kafkaBrokers: ScallopOption[String] = opt[String](short = 'b', descr = "Kafka brokers list, comma separated", required = true)
    val kafkaTopic: ScallopOption[String] = opt[String](short = 't', descr = "Kafka topic", required = true)
    val kafkaClientId: ScallopOption[String] = opt[String](short = 'c', descr = "[Opt] Kafka clientId", required = true)
    val kerberosEnabled: ScallopOption[Boolean] = opt[Boolean](short = 'e', descr = "[Opt] Kerberos SASL flag")
    val proxyHost: ScallopOption[String] = opt[String](short = 'h', descr = "[Opt] Https proxy host")
    val proxyPortNumber: ScallopOption[Int] = opt[Int](short = 'n', default = Some(80), descr = "[Opt] Https proxy port")
    val proxyUsername: ScallopOption[String] = opt[String](short = 'u', descr = "[Opt] Https proxy username")
    val proxyPassword: ScallopOption[String] = opt[String](short = 'p', descr = "[Opt] Https proxy password")
    val eventLimit: ScallopOption[Int] = opt[Int](short = 'l', descr = "[Opt] Livestream retrieved events limit")
    val required: ScallopOption[String] = opt[String](short = 'r', descr = "[Opt] Required fields, comma separate")
    val excluded: ScallopOption[String] = opt[String](short = 'x', descr = "[Opt] Excluded fields, comma separated")
    val filteredTo:  ScallopOption[String] = opt[String](short = 'f', descr = "[Opt] Fields filtered to, comma separated")
    verify()
  }

  def main(args: Array[String]) {
    val conf = new Configuration(args) {
      override def onError(e: Throwable): Unit = e match {
        case Help("") =>
          printHelp()
          sys.exit(0)

        case Version =>
          println(s"$APP_NAME $APP_VERSION")
          sys.exit(0)

        case Exit() =>
          printHelp()
          sys.exit(0)

        case ScallopException(message) =>
          println(s"An ERROR has occurred: $message")
          printHelp()
          sys.exit(1)

        case RequiredOptionNotFound(optionName) =>
          println(s"ERROR: $optionName is required")
          printHelp()
          sys.exit(1)
      }
    }
    val params = Parameters(conf.appKey.toOption, conf.appSecret.toOption, conf.appId.toOption, conf.connectionsMax.toOption,
      conf.oauthTokenUrl.toOption, conf.proxyHost.toOption, conf.proxyPortNumber.toOption,
      conf.proxyUsername.toOption, conf.proxyPassword.toOption, conf.eventLimit.toOption,
      conf.required.toOption, conf.excluded.toOption, conf.filteredTo.toOption
    )
    val kafkaSettings = Settings(
      conf.kafkaBrokers(),
      conf.kafkaTopic(),
      conf.kafkaClientId(),
      conf.kerberosEnabled()
    )
    val producer = createProducer(kafkaSettings)
    def sendToKafka(event: Any): Unit = {
      producer.send(new ProducerRecord[Integer, String](kafkaSettings.kafkaTopic, event.toString))
    }
    val liveStream = Collector(params)
    liveStream.Collector.start(sendToKafka)
  }

}
