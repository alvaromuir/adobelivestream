package com.verizon.bdcpe.adobelivestream.collector

import com.verizon.bdcpe.adobelivestream.collector.AdobeLivestreamCollector.Configuration
import com.verizon.bdcpe.adobelivestream.collector.Stream.Config
import com.verizon.bdcpe.adobelivestream.core._
import org.scalatest.{FlatSpec, Ignore, Matchers}


/**
  * Created by Alvaro Muir<alvaro.muir@verizon.com>
  * Verizon Big Data & Cloud Platform Engineering
  * 7/23/17.
  */

@Ignore
class CollectorSpec extends FlatSpec with Matchers {
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

  val args: Array[String] = Array[String](
    "-k" + appKey, "-s" + appSecret, "-i" + appId, "-m" + connectionsMax,
    "-o" + oauthTokenUrl, "-h" + proxyHost, "-n" + proxyPortNumber, "-u" + proxyUsername, "-p" + proxyPassword,
    "-l" + eventLimit, "-r" + required, "-x" + excluded, "-f" + filteredTo)
  val conf = new Configuration(args)
  val credentials: Credentials = new Credentials.Builder(conf.appKey(), conf.appSecret())
    .tokenRequestUrl(conf.oauthTokenUrl())
    .proxyHost(conf.proxyHost())
    .proxyPortNumber(conf.proxyPortNumber())
    .proxyUserName(conf.proxyUsername())
    .proxyPassword(conf.proxyPassword())
    .build()

  "A Configuration" should "parse all provided flags" in {
    credentials.getClientId should be (appKey)
    credentials.getClientSecret should be (appSecret)
    credentials.getTokenRequestUrl should be (oauthTokenUrl)
    credentials.getProxyHost should be (proxyHost)
    credentials.getProxyPortNumber should be(proxyPortNumber)
    credentials.getProxyUserName should be (proxyUsername)
    credentials.getProxyPassword should be(proxyPassword)

    val endPoint = new Endpoint.Builder(conf.appId(), conf.connectionsMax()).build()

    endPoint.getApplicationId should be (appId)
    endPoint.getMaxConnections should be (connectionsMax)
  }

  "A Stream Config" should "hold an Int, and three optional Strings" in {
    val streamConfig = Config(conf.eventLimit.getOrElse(0), conf.required.toOption, conf.excluded.toOption, conf.filteredTo.toOption)

    streamConfig.limit should be (eventLimit)
    streamConfig.require should be (conf.required.toOption)
    streamConfig.exclude should be (conf.excluded.toOption)
    streamConfig.filters should be (conf.filteredTo.toOption)
  }
}
