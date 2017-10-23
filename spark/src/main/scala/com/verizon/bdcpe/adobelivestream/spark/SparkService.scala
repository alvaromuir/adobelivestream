package com.verizon.bdcpe.adobelivestream.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.akka.ActorReceiver
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.{Logger, LoggerFactory}


/*
  * Created by Alvaro Muir<alvaro.muir@verizon.com>
  * Verizon Big Data & Cloud Platform Engineering
  * 10/19/17.
  */

object SparkService {
  val log: Logger = LoggerFactory.getLogger(getClass.getName)


  /**
    * Returns settings object providing parameters for a kafka producer
    * @param master spark master node
    * @param appName spark job identifier
    * @param kerberosEnabled [Opt] SASL flag, omit for 'false'
    */
  case class Settings(master: String, appName: String, kerberosEnabled: Boolean, streamingInterval: Int)



  /**
    * Returns a Spark StreamingContext with provided Settings object
    * @param settings Settings object
    * @return spark streaming context
    */
  def createStreamingContext(settings: Settings): StreamingContext = {
    val conf = new SparkConf().setAppName(settings.appName).setMaster(settings.master)
    log.info(s"streaming context starting with the following settings: {master: ${settings.master}, " +
      s"appName: ${settings.appName}, kerberosEnabled: ${settings.kerberosEnabled}, secondIntervals: ${settings.streamingInterval}")

    //  ToDo: pass in logging directive from main
    new StreamingContext(conf, Seconds(settings.streamingInterval))
  }


  class StreamHitActor extends ActorReceiver {
    override def receive: Receive = {
      case data: String => store(data)
    }
  }

}
