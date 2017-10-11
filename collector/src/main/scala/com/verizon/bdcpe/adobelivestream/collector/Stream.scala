package com.verizon.bdcpe.adobelivestream.collector

import java.util.concurrent.LinkedBlockingQueue

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.json4s.{DefaultFormats, _}
import org.json4s.jackson.JsonMethods.{compact, parse, render}
import org.json4s.jackson.Serialization.write
import org.json4s.JsonDSL._
import org.slf4j.{Logger, LoggerFactory}
import com.verizon.bdcpe.adobelivestream.core.Connection
import com.verizon.bdcpe.adobelivestream.collector.HitModel.Hit
import com.verizon.bdcpe.adobelivestream.collector.Processor.FilteredHit

/*
  * Created by Alvaro Muir<alvaro.muir@verizon.com>
  * Verizon Big Data & Cloud Platform Engineering
  * 7/23/17.
  */

object Stream {
  implicit val system: ActorSystem = ActorSystem("AdobeLivestreamSystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val formats: DefaultFormats = DefaultFormats

  /**
    * StreamConfig provided parameters that define returned results from a processed hit
    * @param limit is an integer that describes the maximum amount of results
    * @param require is a string of items that each result must contain
    * @param exclude is a string of items that each result must NOT contain
    * @param filters is a string of items to restrict the final json response to
    */
  case class Config(limit: Int, require: Option[String], exclude: Option[String], filters: Option[String])

  /**
    * Flow starts, stops and filters messages as required
    * @param connection is an adobeLivestream-core Connection object
    * @param queue is a LinkedBlockingQueue of Strings
    * @param config is a StreamConfig
    */
  class Flow(connection: Connection, queue: LinkedBlockingQueue[String], config: Config) {
    val log: Logger = LoggerFactory.getLogger(getClass.getName)
    val streamLimit: Int = config.limit
    val requiredKeys: Option[String] = config.require
    val excludedKeys: Option[String] = config.exclude
    val filteredTo: Option[String] = config.filters
    val collector: Thread = new Thread(connection)

    /**
      * Filter returns a filtered string representation of a hit
      * @param hit is a Hit object
      */
    def filter(hit: Hit): String = {
      val filters = config.filters.get.split(",").map(_.trim).toList
      var json: JObject = ("sessionId" -> (hit.hitIdHigh + hit.hitIdLow)) ~ ("visitorId" -> (hit.visIdHigh + hit.visIdLow)) ~ ("timeGMT" -> hit.timeGMT)

      filters.foreach {
        case x if !x.toLowerCase.startsWith("prop") && !x.toLowerCase.startsWith("evar") && !x.toLowerCase.startsWith("event") =>
          val root = Extraction.decompose(hit)(DefaultFormats).values.asInstanceOf[Map[String, Any]]
          json = json ~ (x ->  Extraction.decompose(root.getOrElse(x, null)))

        case x if x.toLowerCase.startsWith("prop") =>
          val props = Extraction.decompose(hit.props)(DefaultFormats).values.asInstanceOf[Map[String, Any]]
          if(props.nonEmpty) json = json ~ (x -> Extraction.decompose(props.getOrElse(x, null)))

        case x if x.toLowerCase.startsWith("evar") =>
          val evars = Extraction.decompose(hit.evars)(DefaultFormats).values.asInstanceOf[Map[String, Any]]
          if(evars.nonEmpty) {
            val eVars = Extraction.decompose(evars.get("evars"))(DefaultFormats).values.asInstanceOf[Map[String, Any]] // see HitModel for explanation
            if(eVars.nonEmpty) json = json ~ (x -> Extraction.decompose(eVars.getOrElse(x, null)))
          }

        case x if x.toLowerCase.startsWith("event") =>
          val events = Extraction.decompose(hit.events)(DefaultFormats).values.asInstanceOf[Map[String, Any]]
          if(events.nonEmpty) json = json ~ (x -> Extraction.decompose(events.getOrElse(x, null)))

        case _ =>
      }
      compact(render(json))
    }

    /**
      * preProcess returns a Hit object prepared for consumption.
      * This takes into account any filtering as required in config.
      * @param json is a Hit object
      */
    def preProcess(json: String): Hit = {
      var rslts: String = json

      if (requiredKeys.isDefined) {
        val required = requiredKeys.get
        if (required.contains(",")) {
          for (k <- requiredKeys.get.split(",").toList) {
            if (!json.contains("\""+k.trim+"\"")) { rslts = null }
          }
        } else{
          if (!json.contains("\""+required.trim+"\"")) { rslts = null}
        }
      }
      if (excludedKeys.isDefined) {
        val excluded = excludedKeys.get
        if (excluded.contains(",")) {
          for (k <- excludedKeys.get.split(",").toList) {
            if (json.contains("\""+k.trim+"\"")) { rslts = null }
          }
        } else if (json.contains("\""+excluded.trim+"\"")) { rslts = null }
      }
      if (rslts != null) parse(rslts).extract[Hit]
      else null
    }

    /**
      * process is a partial function required to send output to a receiver
      * @param f is the operation desired
      * @param x is the text input, almost aways a string representation of a hit
      */
    def process(f: (Any) => Unit, x: Any): Unit = {
      f(x)
    }

    /**
      * Start commences the collector and processing operation
      * @param fn is desired preProcess method.
      */
    def start(fn: (Any) => Unit): Unit = {
      log.info("starting collector . . .")
      collector.start()

      // check stream config
      if (requiredKeys.isDefined) {
        val required = requiredKeys.get
        log.info(s"Required key(s): $required.")
        if (excludedKeys.isDefined) {
          val excluded = excludedKeys.get
          log.info(s"Excluded key(s): $excluded")
          if(excludedKeys.exists(requiredKeys.contains)) {
            log.error(s"ERROR: required and excluded keys are not exclusive.")
            System.exit(1)
          }
        }
      }

      // Limit results
      // ToDo: Refactor this entire block to case object
      if (streamLimit > 0) {
        var limit = streamLimit
        log.info(s"limiting results to $streamLimit hit(s)")
        while(limit > 0) try {
          val event = parse(queue.take).extract[Hit]
          if(event != null) {
            if (filteredTo.isDefined) process(fn, FilteredHit(filter(event)))
            else process(fn, write(event))
            limit -= 1
          }
        }
        catch { case e: Exception => log.error(e.getMessage) }
        stop()
        System.exit(0)
      }
      else {
        while(true) try {
          val event: Hit = parse(queue.take).extract[Hit]
          if(event != null) {
            if (filteredTo.isDefined) process(fn, FilteredHit(filter(event)))
            else process(fn, write(event))

          }
        }
        catch { case e: Exception => log.error(e.getMessage) }
      }

    }

    /** Stop ends the collector, the processing and closes the stream
      *
      */
    def stop(): Unit = {
      log.info("shutting down collector . . .")
      collector.interrupt()
    }
  }
}