package com.verizon.bdcpe.adobelivestream.collector

import com.verizon.bdcpe.adobelivestream.collector.Collector.system
import akka.actor.{Actor, ActorRef, Props}
import org.slf4j.{Logger, LoggerFactory}

object Processor {
  val log: Logger = LoggerFactory.getLogger(getClass.getName)
  val hitActor: ActorRef = system.actorOf(Props[HitActor])

  /**
    * RawHit is a basic structure used for Akka message encapsulation
    * @param event describes a AdobeLivestream message passed to the system
    */
  case class RawHit(event: Any)

  /**
    * FilteredHit is a filter structure used for Akka message encapsulation
    * @param event describes a filtered AdobeLivestream message passed to the system
    */
  case class FilteredHit(event: Any)

  /**
    * HitActor is the Akka messaging processing system
    */
  class HitActor extends Actor {
    override def receive: PartialFunction[Any, Unit] = {
      case RawHit(event) => println(event.toString)
      // future use for alternative file formats
      case FilteredHit(event) => println(event.toString)
      case _ =>
    }
  }


  def writeToConsole(event: Any) {
    hitActor ! RawHit(event)
  }
}
