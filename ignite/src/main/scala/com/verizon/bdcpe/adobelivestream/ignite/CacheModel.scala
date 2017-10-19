package com.verizon.bdcpe.adobelivestream.ignite

/*
  * Created by Alvaro Muir<alvaro.muir@verizon.com>
  * Verizon Big Data & Cloud Platform Engineering
  * 10/17/17.
  */

object CacheModel {
  case class CacheEntry(
  sessionId: Double,
  timeGMT:Double
  )
}
