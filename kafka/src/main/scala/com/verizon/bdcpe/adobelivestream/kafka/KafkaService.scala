package com.verizon.bdcpe.adobelivestream.kafka

import org.apache.kafka.clients.producer.KafkaProducer
import org.slf4j.{Logger, LoggerFactory}
/*
  * Created by Alvaro Muir<alvaro.muir@verizon.com>
  * Verizon Big Data & Cloud Platform Engineering
  * 10/9/17.
  */

object KafkaService {
  val log: Logger = LoggerFactory.getLogger(getClass.getName)

  /**
    * Returns settings object providing parameters for a kafka producer
    * @param brokers list of kafka brokers - comma separated
    * @param topic desired kafka topic
    * @param clientId [Opt] kafka client ID for JMX
    * @param kerberosEnabled [Opt] SASL flag, omit for 'false'
    */
  case class Settings(brokers: String, topic: String, clientId: Option[String] = None, kerberosEnabled: Boolean)

  /**
    * Returns a plain Kafka producer with provided Settings object
    * @param settings Settings object
    * @return kafka producer with integer keys and string values
    */
  def createProducer(settings: Settings): KafkaProducer[Integer, String] = {
    log.info(s"connecting to kafka with the following settings: {brokers: ${settings.brokers}, " +
      s"topic: ${settings.topic}, clientId: ${settings.clientId}, kerberosEnabled: ${settings.kerberosEnabled}}")
    //  ToDo: pass in logging directive from main
    new KafkaProducer[Integer,String](generateProps(settings.brokers, settings.clientId.getOrElse(""), settings.kerberosEnabled))
  }

  def generateProps(servers: String, clientID: String, secureTransport: Boolean = false): java.util.Properties = {
    val props = new java.util.Properties()

    props.put("bootstrap.servers", servers)
    props.put("client.id", clientID)
    props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("acks", "1")
    props.put("buffer.memory", "33554432")
    props.put("retries", "1")
    props.put("batch.size", "16384")
    props.put("linger.ms", "0")
    props.put("request.timeout.ms", "50") //don't hang.
    if(secureTransport) {
      props.put("security.protocol", "SASL_PLAINTEXT")
      props.put("sasl.kerberos.service.name", "kafka")
    }
    props
  }
  //  ToDo: write methods to check if kafka broker is live. Probably need to do this via Zookeeper?
}