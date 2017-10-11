package com.verizon.bdcpe.adobelivestream.collector

import java.util.concurrent.LinkedBlockingQueue

import com.verizon.bdcpe.adobelivestream.collector.Stream.{Config, Flow}
import com.verizon.bdcpe.adobelivestream.core.{Connection, Credentials, Endpoint, TokenRequest}


case class Collector(params: Parameters) {
  object Collector {

    val maxConns: Int = params.connectionsMax match {
      case Some(x: Int) => x
      case _ => 0
    }

    val credentials: Credentials.Builder = new Credentials.Builder(params.appKey.get, params.appSecret.get)

    if (params.tokenGetUrl.isDefined) credentials.tokenRequestUrl(params.tokenGetUrl.get)
    if (params.proxyHost.isDefined) credentials.proxyHost(params.proxyHost.get)
    if (params.proxyPortNumber.isDefined) credentials.proxyPortNumber(params.proxyPortNumber.get)
    if (params.proxyUsername.isDefined) credentials.proxyUserName(params.proxyUsername.get)
    if (params.proxyPassword.isDefined) credentials.proxyUserName(params.proxyPassword.get)

    val creds: Credentials = credentials.build()
    val events = new LinkedBlockingQueue[String]()
    val connection: Connection = new Connection.Builder(
      creds,
      new Endpoint.Builder(params.appId.get, maxConns).build(),
      new TokenRequest(creds).newToken()
    ).eventQueue(events).build()

    val streamConfig = Config(params.eventLimit.getOrElse(0),
      params.required,
      params.excluded,
      params.filteredTo)

    val flow = new Flow(connection, events, streamConfig)

    def start(fn: (Any) => Unit): Unit = { flow.start(fn: (Any) => Unit) }
  }
}

case class Parameters(appKey: Option[String] = None, appSecret: Option[String] = None, appId: Option[String] = None, connectionsMax: Option[Int] = None,
                      tokenGetUrl: Option[String] = None, proxyHost: Option[String],
                      proxyPortNumber: Option[Int] = None, proxyUsername: Option[String] = None,
                      proxyPassword: Option[String] = None, eventLimit: Option[Int] = None,
                      required: Option[String] = None, excluded: Option[String] = None,
                      filteredTo: Option[String] = None
                )