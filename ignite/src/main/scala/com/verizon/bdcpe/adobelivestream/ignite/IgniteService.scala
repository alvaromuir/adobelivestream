package com.verizon.bdcpe.adobelivestream.ignite



import javax.cache.configuration.Factory
import javax.cache.event.{CacheEntryEvent, CacheEntryEventFilter, CacheEntryUpdatedListener}

import org.apache.ignite.cache.query.{ContinuousQuery, ScanQuery}
import org.apache.ignite.lang.IgniteBiPredicate
import org.apache.ignite.{Ignite, IgniteCache, Ignition}
import org.slf4j
import org.slf4j.LoggerFactory

/*
  * Created by Alvaro Muir<alvaro.muir@verizon.com>
  * Verizon Big Data & Cloud Platform Engineering
  * 10/17/17.
  */

object IgniteService {
  val log: slf4j.Logger = LoggerFactory.getLogger(getClass.getName)

  /**
    * Returns settings object providing parameters for a Ignite cache cluster
    *
    * @param cacheName  String identifier for Ignite cache
    * @param configPath String path to Ignite configuration file
    */
  case class Settings(evictionTime: Int, cacheName: String, configPath: String)

  /**
    * Returns a plain IgniteCache with provided Settings object
    *
    * @param settings Settings object
    * @return ignite cache with double keys and double values
    */
  def createCache(settings: Settings): IgniteCache[Double, Double] = {
    val ignite: Ignite = Ignition.start(settings.configPath)
    ignite.getOrCreateCache(settings.cacheName)
  }
}
  
