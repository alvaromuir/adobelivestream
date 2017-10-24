package com.verizon.bdcpe.adobelivestream.spark

import akka.actor.{Actor, ActorRef}
import com.verizon.bdcpe.adobelivestream.collector.HitModel.Hit
import org.apache.spark.SparkConf
import org.apache.spark.streaming.akka.ActorReceiver
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable
/*
  * Created by Alvaro Muir<alvaro.muir@verizon.com>
  * Verizon Big Data & Cloud Platform Engineering
  * 10/19/17.
  */
object SparkService {
  val log: Logger = LoggerFactory.getLogger(getClass.getName)

  /**
    * Returns settings object providing parameters for a kafka producer
    * @param master String spark master node
    * @param appName String spark job identifier
    * @param host String host Akka remote actor will listen on, defaults to '127.0.0.1'
    * @param port Int port of Akka remote actor host, defaults to 9999
    * @param kerberosEnabled Boolean [Opt] SASL flag, omit for 'false'
    * @param streamingInterval Int [Opt] spark streaming batch size
    */
  case class Settings(master: String, appName: String = "AdobeLiveStream", host: String = "127.0.0.1", port: Int = 9999,
                      kerberosEnabled: Boolean, streamingInterval: Int)

  /**
    * Returns a Spark StreamingContext with provided Settings object
    * @param settings Settings object
    * @return spark streaming context
    */
  def createStreamingContext(settings: Settings): StreamingContext = {
    val conf = new SparkConf()
      .setAppName(settings.appName)
      .setMaster(settings.master)
      .set("spark.streaming.stopGracefullyOnShutdown","true")

    log.info(s"streaming context starting with the following settings: {master: ${settings.master}, " +
      s"appName: ${settings.appName}, kerberosEnabled: ${settings.kerberosEnabled}, secondIntervals: ${settings.streamingInterval}")

    //  ToDo: pass in logging directive from main
    new StreamingContext(conf, Seconds(settings.streamingInterval))
  }

  /**
    * SubscribeReceiver returns a subscriber object of ActorRef for future subscriptions
    * @param receiverActor ActorRef object
    */
  case class SubscribeReceiver(receiverActor: ActorRef)

  /**
    * UnsubscribeReceiver returns a subscriber object of ActorRef for future removal
    * @param receiverActor ActorRef object
    */
  case class UnsubscribeReceiver(receiverActor: ActorRef)


  /**
    * Actor that manages both messages and subscriptions
    */
  class FeederActor extends Actor {
    val receivers = new mutable.LinkedHashSet[ActorRef]()
    override def receive: Receive = {
      case hit: Hit => receivers.foreach(_ ! hit.toString)
      case SubscribeReceiver(receiverActor: ActorRef) =>
        println(s"received subscribe from ${receiverActor.toString}")
        receivers += receiverActor

      case UnsubscribeReceiver(receiverActor: ActorRef) =>
        println(s"received unsubscribe from ${receiverActor.toString}")
        receivers -= receiverActor
    }
  }

  /**
    * Actor that manages both messages and subscriptions
    * @param urlOfPublisher is the source url the spark streamer listen's to
    */
  class StreamActorReceiver[T](urlOfPublisher: String) extends ActorReceiver {
    lazy private val remotePublisher = context.actorSelection(urlOfPublisher)
    override def preStart(): Unit = remotePublisher ! SubscribeReceiver(context.self)
    override def receive: PartialFunction[Any, Unit] = {
      case msg => store(msg.asInstanceOf[T])
    }
    override def postStop(): Unit = remotePublisher ! UnsubscribeReceiver(context.self)

  }
}
