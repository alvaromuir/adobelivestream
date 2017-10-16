package com.verizon.bdcpe.adobelivestream.collector

import com.verizon.bdcpe.adobelivestream.collector.Processor.writeToConsole
import org.rogach.scallop.exceptions._
import org.rogach.scallop.{ScallopConf, ScallopOption}

import scala.language.postfixOps

/*
  * Created by Alvaro Muir<alvaro.muir@verizon.com>
  * Verizon Big Data & Cloud Platform Engineering
  * 7/23/17.
  */

object AdobeLivestreamCollector {
  val APP_NAME = "adobelivestream.Collector"
  val APP_VERSION = "1.2"

  /**
    * Configuration is the overall program setup
    * @param arguments are command-line args to be parsed
    */
  class Configuration(arguments: Seq[String]) extends ScallopConf(arguments) {
    mainOptions = Seq(appKey, appSecret, appId, connectionsMax, oauthTokenUrl, proxyHost, proxyPortNumber, proxyUsername,
                      proxyPassword, eventLimit, required, excluded, filteredTo)

    version(s"\n$APP_NAME $APP_VERSION (c)2017 Verizon Big Data Cloud & Platform Engineering")
    banner("""Usage: adobelivestream.collector -k <appKey> -s <appSecret> -i <appId> -m <maxConnections> ... [OPTIONS]
             |adobelivestream.Collector retrieves real-time event data from Adobe Analytics.
             |Options:
             |""".stripMargin)
    footer("\nREADME - https://onestash.verizon.com/users/v603497/repos/adobelivestream/browse/collector")

    val appKey: ScallopOption[String] = opt[String](short = 'k', descr = "Adobe application key", required = true)
    val appSecret: ScallopOption[String] = opt[String](short = 's', descr = "Adobe application secret", required = true)
    val appId: ScallopOption[String] = opt[String](short = 'i', descr = "Adobe application ID", required = true)
    val connectionsMax: ScallopOption[Int] = opt[Int](short = 'm', validate = 9 >, descr = "max concurrent connections", required = true)
    val oauthTokenUrl: ScallopOption[String] = opt[String](short = 'o', descr = "[Opt] Adobe OAuth Token Url")
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
    val params:Parameters = Parameters(conf.appKey.toOption, conf.appSecret.toOption, conf.appId.toOption,
      conf.connectionsMax.toOption, conf.oauthTokenUrl.toOption, conf.proxyHost.toOption, conf.proxyPortNumber.toOption,
      conf.proxyUsername.toOption, conf.proxyPassword.toOption, conf.eventLimit.toOption, conf.required.toOption,
      conf.excluded.toOption, conf.filteredTo.toOption
    )
    val collector = new Collector(params)
    collector.start(writeToConsole)
  }
}