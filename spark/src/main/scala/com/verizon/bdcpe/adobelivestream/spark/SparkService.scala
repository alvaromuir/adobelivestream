package com.verizon.bdcpe.adobelivestream.spark

import akka.actor.{Actor, ActorRef}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.akka.ActorReceiver
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable
import scala.util.Random


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


  case class SubscribeReceiver(receiverActor: ActorRef)
  case class UnsubscribeReceiver(receiverActor: ActorRef)

  class FeederActor extends Actor {

    val rand = new Random()
    val receivers = new mutable.LinkedHashSet[ActorRef]()

    val strings: Array[String] = Array("words ", "may ", "count ")

    def makeMessage(): String = {
      val x = rand.nextInt(3)
      strings(x) + strings(2 - x)
    }

    /*
     * A thread to generate random messages
     */
    new Thread() {
      override def run() {
        while (true) {
          Thread.sleep(500)
          receivers.foreach(_ ! makeMessage)
        }
      }
    }.start()

    def receive: Receive = {
      case SubscribeReceiver(receiverActor: ActorRef) =>
        println(s"received subscribe from ${receiverActor.toString}")
        receivers += receiverActor

      case UnsubscribeReceiver(receiverActor: ActorRef) =>
        println(s"received unsubscribe from ${receiverActor.toString}")
        receivers -= receiverActor
    }
  }

  class HitActorReceiver[T](urlOfPublisher: String) extends ActorReceiver {

    lazy private val remotePublisher = context.actorSelection(urlOfPublisher)

    override def preStart(): Unit = remotePublisher ! SubscribeReceiver(context.self)

    def receive: PartialFunction[Any, Unit] = {
      case msg => store(msg.asInstanceOf[T])
    }

    override def postStop(): Unit = remotePublisher ! UnsubscribeReceiver(context.self)

  }
}
