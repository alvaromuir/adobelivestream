package com.verizon.bdcpe.adobelivestream.ignite

import scala.collection.JavaConversions._
import java.lang.{Iterable => JavaIterable}
import java.text.SimpleDateFormat
import javax.cache.configuration.Factory
import java.time.Instant
import java.util.{Date, TimeZone}
import javax.cache.event.{CacheEntryEvent, CacheEntryEventFilter, CacheEntryUpdatedListener}

import com.verizon.bdcpe.adobelivestream.collector.HitModel.Hit
import com.verizon.bdcpe.adobelivestream.collector.{Collector, Parameters}
import com.verizon.bdcpe.adobelivestream.ignite.CacheModel.CacheEntry
import com.verizon.bdcpe.adobelivestream.ignite.IgniteService.{Settings, createCache}
import org.apache.ignite.cache.query.{ContinuousQuery, ScanQuery}
import org.apache.ignite.lang.IgniteBiPredicate
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.rogach.scallop.exceptions._
import org.rogach.scallop.{ScallopConf, ScallopOption}
import org.slf4j.{Logger, LoggerFactory}

import scala.language.postfixOps

/*
  * Created by Alvaro Muir<alvaro.muir@verizon.com>
  * Verizon Big Data & Cloud Platform Engineering
  * 10/17/17.
  */

object AdobeLiveStreamIgnite {
  val log: Logger = LoggerFactory.getLogger(getClass.getName)
  val APP_NAME = "adobelivestream.Ignite"
  val APP_VERSION = "1.0"

  /**
    * Configuration is the overall program setup
    * @param arguments are command-line args to be parsed
    */
  case class Configuration(arguments: Seq[String]) extends ScallopConf(arguments) {
    mainOptions = Seq(appKey, appSecret, appId, connectionsMax,
      igniteCacheName, igniteConfigPath,
      oauthTokenUrl, proxyHost, proxyPortNumber, proxyUsername, proxyPassword, eventLimit, required, excluded, filteredTo)

    version(s"\n$APP_NAME $APP_VERSION (c)2017 Verizon Big Data Cloud & Platform Engineering\n")
    banner("""Usage: adobelivestream-ignite -k <appKey> -s <appSecret> -i <appId> -m <maxConnections>... [OPTIONS]
             |adobelivestream.Ignite sends real-time event data from Adobe Analytics to an Ignite cache cluster.
             |Options:
             |""".stripMargin)
    footer("\nREADME - https://onestash.verizon.com/users/v603497/repos/adobelivestream/browse/ignite")
    val appKey: ScallopOption[String] = opt[String](short = 'k', descr = "Adobe application key", required = true)
    val appSecret: ScallopOption[String] = opt[String](short = 's', descr = "Adobe application secret", required = true)
    val appId: ScallopOption[String] = opt[String](short = 'i', descr = "Adobe application ID", required = true)
    val connectionsMax: ScallopOption[Int] = opt[Int](short = 'm', validate = 9>, descr = "max concurrent connections", required = true)
    val oauthTokenUrl: ScallopOption[String] = opt[String](short = 'o', descr = "[Opt] Adobe OAuth Token Url")
    val igniteEvictionTime: ScallopOption[Int] = opt[Int](short = 'v', validate = 9<, descr = "cache entry TTL, in seconds", required = true)
    val igniteCacheName: ScallopOption[String] = opt[String](short = 't', descr = "[Opt] Ignite cache name")
    val igniteConfigPath: ScallopOption[String] = opt[String](short = 'g', descr = "[Opt] Ignite configuration xml file path")
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
    val igniteSettings = Settings(
      conf.igniteEvictionTime(),
      conf.igniteCacheName.getOrElse(getClass.getSimpleName.split("\\$").last),
      conf.igniteConfigPath.getOrElse(null)
    )
    val cache = createCache(igniteSettings)
    def sendToIgnite(event: Any): Unit = {
      implicit val formats: DefaultFormats = DefaultFormats
      val hit = event.asInstanceOf[Hit]
      val cacheEntry:CacheEntry = CacheEntry(hit.hitIdHigh.get + hit.hitIdLow.get, hit.timeGMT.get)
      cache.put(cacheEntry.sessionId, cacheEntry.timeGMT)

      val qry:ContinuousQuery[Double, Double] = new ContinuousQuery[Double, Double]()

      qry.setInitialQuery(new ScanQuery[Double, Double](new IgniteBiPredicate[Double, Double] {
        override def apply(key: Double, value: Double): Boolean = {
          value < Instant.now.minusSeconds(igniteSettings.evictionTime).toEpochMilli.toDouble
        }
      }))


      qry.setLocalListener(new CacheEntryUpdatedListener[Double, Double]{
        override def onUpdated(events: JavaIterable[CacheEntryEvent[_ <: Double, _ <: Double]]) {
          for (e <- events) {
//            println("Expired entry [key=" + e.getKey + ", val=" + e.getValue + ']')
            cache.remove(e.getKey)
          }
        }
      })


      qry.setRemoteFilterFactory(new Factory[CacheEntryEventFilter[Double, Double]] {
        override def create(): CacheEntryEventFilter[Double, Double] = {
          new CacheEntryEventFilter[Double, Double] {
            override def evaluate(event: CacheEntryEvent[_ <: Double, _ <: Double]): Boolean = {
              event.getValue < Instant.now.minusSeconds(igniteSettings.evictionTime).toEpochMilli.toDouble
            }
          }
        }
      })

      // Execute query
      val now = new Date
      val simpleDateFormat = new SimpleDateFormat("hh:mm:ss")
      val localDateFormat = new SimpleDateFormat("hh:mm:ss")
      localDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
      val cur = cache.query(qry)
      try {
        for (e <- cur) {
          println(s"Expired entry [key=${e.getKey}, val=${simpleDateFormat.format(e.getValue.toLong)}, now = ${localDateFormat.format(now)}]")
//          cache.remove(e.getKey)
        }
        Thread.sleep(2000)
      }
      finally {
        if (cur != null) cur.close()
      }
    }
    val collector = new Collector(params)
    collector.start(sendToIgnite)
    sys.exit(0)
  }
}