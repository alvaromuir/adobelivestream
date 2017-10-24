package com.verizon.bdcpe.adobelivestream.spark


import akka.actor.{ActorSystem, Props}
import ch.qos.logback.classic
import ch.qos.logback.classic.{Level, LoggerContext}
import org.slf4j.{Logger, LoggerFactory}
import com.typesafe.config.ConfigFactory
import com.verizon.bdcpe.adobelivestream.collector.Collector
import com.verizon.bdcpe.adobelivestream.collector.HitModel.Hit
import com.verizon.bdcpe.adobelivestream.spark.SparkService.FeederActor

import scala.concurrent.ExecutionContext.Implicits.global
import com.verizon.bdcpe.adobelivestream.collector.Parameters
import com.verizon.bdcpe.adobelivestream.spark.SparkService.Settings
import org.json4s.DefaultFormats
import org.rogach.scallop.exceptions._
import org.rogach.scallop.{ScallopConf, ScallopOption}

import scala.language.postfixOps
/*
  * Created by Alvaro Muir<alvaro.muir@verizon.com>
  * Verizon Big Data & Cloud Platform Engineering
  * 10/19/17.
  */
object AdobeLiveStreamSpark {
  val APP_NAME = "adobelivestream.Spark"
  val APP_VERSION = "1.0"

  val log: Logger = LoggerFactory.getLogger(getClass.getName)

  val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
  val sparkLogger: classic.Logger = loggerContext.getLogger("org")
  val akkaLogger: classic.Logger = loggerContext.getLogger("akka")
  val nettyLogger: classic.Logger = loggerContext.getLogger("io")
  sparkLogger.setLevel(Level.OFF)
  akkaLogger.setLevel(Level.OFF)
  nettyLogger.setLevel(Level.OFF)

  /**
    * Configuration is the overall program setup
    * @param arguments are command-line args to be parsed
    */
  case class Configuration(arguments: Seq[String]) extends ScallopConf(arguments) {
    mainOptions = Seq(appKey, appSecret, appId, connectionsMax,
      oauthTokenUrl, proxyHost, proxyPortNumber, proxyUsername, proxyPassword, eventLimit, required, excluded, filteredTo)

    version(s"\n$APP_NAME $APP_VERSION (c)2017 Verizon Big Data Cloud & Platform Engineering\n")
    banner("""Usage: adobelivestream-spark -k <appKey> -s <appSecret> -i <appId> -m <maxConnections> ... [OPTIONS]
             |adobelivestream.Spark sends real-time event data from Adobe Analytics to a Spark cluster.
             |Options:
             |""".stripMargin)
    footer("\nREADME - https://onestash.verizon.com/users/v603497/repos/adobelivestream/browse/spark")
    val appKey: ScallopOption[String] = opt[String](short = 'k', descr = "Adobe application key", required = true)
    val appSecret: ScallopOption[String] = opt[String](short = 's', descr = "Adobe application secret", required = true)
    val appId: ScallopOption[String] = opt[String](short = 'i', descr = "Adobe application ID", required = true)
    val connectionsMax: ScallopOption[Int] = opt[Int](short = 'm', validate = 9>, descr = "max concurrent connections", required = true)
    val oauthTokenUrl: ScallopOption[String] = opt[String](short = 'o', descr = "[Opt] Adobe OAuth Token Url")
    val sparkMaster: ScallopOption[String] = opt[String](short = 't', descr = "[Opt] Spark master, defaults to local[*]")
    val sparkAppName: ScallopOption[String] = opt[String](short = 'y', descr = "[Opt] Spark application name for history server")
    val sparkStreamHost: ScallopOption[String] = opt[String](short = 'w', descr = "[Opt] Spark stream listener host")
    val sparkStreamPort: ScallopOption[Int] = opt[Int](short = 'g', default = Some(9999), descr = "[Opt] Spark stream listener port")
    val kerberosEnabled: ScallopOption[Boolean] = opt[Boolean](short = 'e', descr = "[Opt] Kerberos SASL flag")
    val streamingInterval: ScallopOption[Int] = opt[Int](short = 'v', descr = "[Opt] Spark streaming sec intervals, defaults to 5")
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
    implicit val formats: DefaultFormats = DefaultFormats

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
    val params:Parameters = Parameters(conf.appKey.toOption, conf.appSecret.toOption, conf.appId.toOption,
      conf.connectionsMax.toOption, conf.oauthTokenUrl.toOption, conf.proxyHost.toOption, conf.proxyPortNumber.toOption,
      conf.proxyUsername.toOption, conf.proxyPassword.toOption, conf.eventLimit.toOption, conf.required.toOption,
      conf.excluded.toOption, conf.filteredTo.toOption
    )
    val sparkSettings = Settings(
      conf.sparkMaster.getOrElse("local[2]"),
      conf.sparkAppName.getOrElse("AdobeLiveStream"),
      conf.sparkStreamHost.getOrElse("localhost"),
      conf.sparkStreamPort.getOrElse(9999),
      conf.kerberosEnabled(),
      conf.streamingInterval.getOrElse(5)
    )
    val akkaConf = ConfigFactory.parseString(
      s"""akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.enabled-transports = ["akka.remote.netty.tcp"]
         |akka.remote.netty.tcp.hostname = "${sparkSettings.host}"
         |akka.remote.netty.tcp.port = ${sparkSettings.port}
         |""".stripMargin)
    val actorSystem = ActorSystem(sparkSettings.appName, akkaConf)
    val feeder = actorSystem.actorOf(Props[FeederActor], "HitFeeder")
    log.info(s"Feeder started as: $feeder")
    actorSystem.whenTerminated.foreach { _ => log.info(s"Actor system was shut down") }
    def sendToSpark(event: Any): Unit = {
      val hit = event.asInstanceOf[Hit]
      feeder ! hit
    }
    val collector = new Collector(params)
    collector.start(sendToSpark)

  }
}
