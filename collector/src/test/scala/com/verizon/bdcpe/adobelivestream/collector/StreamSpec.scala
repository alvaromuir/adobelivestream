package com.verizon.bdcpe.adobelivestream.collector

import org.scalatest.{FlatSpec, Ignore, Matchers}

/**
  * Created by Alvaro Muir<alvaro.muir@verizon.com>
  * Verizon Big Data & Cloud Platform Engineering
  * 7/23/17.
  */

@Ignore
class StreamSpec extends FlatSpec with Matchers {
  // be sure to change to adjusts your Constants.scala file:
  val appKey: String = Constants.APP_KEY
  val appSecret: String = Constants.APP_SECRET
  val appId: String = Constants.APP_ID
  val connectionsMax: Int = Constants.CONNECTIONS_MAX
  val oauthTokenUrl: String = Constants.TOKEN_GET_URL
  val proxyHost: String = Constants.PROXY_HOST
  val proxyPortNumber: Int = Constants.PROXY_PORTNUMBER
  val proxyUsername: String = Constants.PROXY_USERNAME
  val proxyPassword: String = Constants.PROXY_PASSWORD
  val eventLimit: Int = Constants.EVENT_LIMIT
  val required: String = "prop1, eVar1"
  val excluded: String = "prop2, eVar2"
  val filteredTo: String = "prop10"

//  ToDo un-stub these ...
  "A Processor" should "pre-process json as required" in {}
  "A required flag" should "return only objects with required fields" in {}
  "An excluded flag" should "return only objects with excluded fields" in {}
  "A filter flag" should "return only objects with filter fields" in {}
}
