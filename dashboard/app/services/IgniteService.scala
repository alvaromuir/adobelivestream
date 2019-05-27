package services

import java.time.Instant
import java.lang.{Iterable => JavaIterable}
import java.util._
import javax.cache.Cache
import javax.cache.configuration.Factory
import javax.cache.event.{CacheEntryEvent, CacheEntryEventFilter, CacheEntryUpdatedListener}
import javax.inject.Inject

import com.typesafe.config.ConfigFactory
import com.verizon.bdcpe.adobelivestream.collector.HitModel.Hit
import org.apache.ignite.cache.CacheMode
import org.apache.ignite.cache.query.{ContinuousQuery, QueryCursor, ScanQuery}
import org.apache.ignite.configuration.CacheConfiguration
import org.apache.ignite.{Ignite, IgniteCache, Ignition}
import org.apache.ignite.events.Event
import org.apache.ignite.events.EventType._
import org.apache.ignite.internal.util.scala.impl
import org.apache.ignite.lang.{IgniteBiPredicate, IgnitePredicate}
import org.apache.ignite.scalar.scalar._
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder
import play.api.{Configuration, Logger}
import play.api.inject.ApplicationLifecycle

import scala.collection.JavaConversions._

case class CachedHit(visitorId: Double, timeGMT: Double, details: Hit)

class IgniteService @Inject() (configuration: Configuration, appLifecycle: ApplicationLifecycle) {
  Logger.info(s"Starting Ignite service...")

  val config: Properties = ConfigFactory.load().getConfig("ignite.client").entrySet().foldRight(new Properties) {
    (item, props) =>
      props.setProperty(item.getKey, item.getValue.unwrapped().toString)
      props
  }

  private val CONFIG = config.getProperty("config-path")
  private val NAME = config.getProperty("cache-name")
  private val spi = new TcpDiscoverySpi
  private val ignite: Ignite = Ignition.start(CONFIG)
  private val cacheConfig = new CacheConfiguration[Double, CachedHit]()

  spi.setIpFinder(new TcpDiscoveryMulticastIpFinder()
                        .setAddresses(java.util.Arrays.asList("localhost"))
  )
  cacheConfig.setName(NAME)
  cacheConfig.setCacheMode(CacheMode.PARTITIONED)
  cacheConfig.setIndexedTypes(classOf[Double], classOf[CachedHit])

  Ignition.setClientMode(true)
  val cache = ignite.getOrCreateCache(cacheConfig)

  val qry = new ContinuousQuery[Double, CachedHit]()


  qry.setInitialQuery(new ScanQuery[Double, CachedHit](new IgniteBiPredicate[Double, CachedHit]() {
    @impl def apply(sid: Double, hit: CachedHit): Boolean = {
      hit.timeGMT.toLong > Instant.now.minusSeconds(1800).getEpochSecond
    }
  }))

  qry.setLocalListener(new CacheEntryUpdatedListener[Double, CachedHit] {
    @impl def onUpdated(events: JavaIterable[CacheEntryEvent[_ <: Double, _ <: CachedHit]]): Unit = {
      for (e <- events) {
        println(s"Active Session: sesId=${e.getKey}, visID=${e.getValue.visitorId}")
      }
    }
  })

//  qry.setRemoteFilterFactory(new Factory[CacheEntryEventFilter[Double, CachedHit]] {
//    @impl def create(): CacheEntryEventFilter[Double, CachedHit] = {
//      new CacheEntryEventFilter[Double, CachedHit]() {
//        @impl def evaluate(event: CacheEntryEvent[_ <: Double, _ <: CachedHit]): Boolean = {
//          event.getValue.timeGMT > Instant.now.minusSeconds(1800).getEpochSecond
//        }
//      }
//    }
//  })

//  val qryCsr: QueryCursor[Cache.Entry[Double, CachedHit]] = cache.query(qry)
//
//    try {
//      val qryCsr: QueryCursor[Cache.Entry[Double, CachedHit]] = cache.query(qry)
//
//      for (e <- qryCsr) { println(s"Active Session: sesId=${e.getKey}, visID=${e.getValue.visitorId}") }
//
//      Thread.sleep(2000)
//    } catch {
//      case err: Throwable => println(s"Error with Continuous Execution: $err")
//    }
//  } catch {
//    case err: Throwable => println(s"Error with Continuous Setup: $err")
//  }

  /**
    * This method will register listener for cache events on all nodes,
    * so we can actually see what happens underneath locally and remotely.
    * from ignite examples
    */
  def registerListener() {
    val g = ignite$

    g *< (() => {
      val listener = new IgnitePredicate[Event] {
        override def apply(e: Event): Boolean = {
          println(e.shortDisplay)
          true
        }
      }

      if (g.cluster().nodeLocalMap[String, AnyRef].putIfAbsent("listener", listener) == null) {
        g.events().localListen(listener,
          EVT_CACHE_OBJECT_PUT,
          EVT_CACHE_OBJECT_READ,
          EVT_CACHE_OBJECT_REMOVED)

        println("Listener is registered.")
      }
    }, null)
  }
}
