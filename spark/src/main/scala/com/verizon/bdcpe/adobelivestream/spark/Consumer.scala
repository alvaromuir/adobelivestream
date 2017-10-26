package com.verizon.bdcpe.adobelivestream.spark

import akka.actor.Props
import ch.qos.logback.classic
import ch.qos.logback.classic.{Level, LoggerContext}
import com.verizon.bdcpe.adobelivestream.spark.SparkService.{Settings, StreamActorReceiver, createStreamingContext}
import org.slf4j.{Logger, LoggerFactory}
import org.apache.spark.streaming.akka.AkkaUtils
/*
  * Created by Alvaro Muir<alvaro.muir@verizon.com>
  * Verizon Big Data & Cloud Platform Engineering
  * 10/19/17.
  */

/*
  * Consumer is a test class that demonstrates Spark Streaming in a console.
  * Since this is streaming JSON string by default, it simply counts number of hits
  * per batch that is processed by Spark
  */
object Consumer {
  val log: Logger = LoggerFactory.getLogger(getClass.getName)

  val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
  val sparkLogger: classic.Logger = loggerContext.getLogger("org")
  val akkaLogger: classic.Logger = loggerContext.getLogger("akka")
  val nettyLogger: classic.Logger = loggerContext.getLogger("io")
  sparkLogger.setLevel(Level.OFF)
  akkaLogger.setLevel(Level.OFF)
  nettyLogger.setLevel(Level.OFF)


  def main(args: Array[String]): Unit = {
    val sparkSettings = Settings("local[2]", "AdobeLiveStream", "localhost", 9999, kerberosEnabled = false, 2)
    val ssc = createStreamingContext(sparkSettings)
    val hits = AkkaUtils.createStream[String](
      ssc,
      Props(classOf[StreamActorReceiver[String]],
        s"akka.tcp://${sparkSettings.appName}@${sparkSettings.host}:${sparkSettings.port}/user/HitFeeder"),
      "StreamActorReceiver")
//    hits.flatMap(_.split("\\s+")).map(x => (x, 1)).reduceByKey(_ + _).print()
    hits.count().print()

    ssc.start()
    ssc.awaitTermination()

    sys.ShutdownHookThread {
      ssc.stop(stopSparkContext = true, stopGracefully = true)
    }
  }
}