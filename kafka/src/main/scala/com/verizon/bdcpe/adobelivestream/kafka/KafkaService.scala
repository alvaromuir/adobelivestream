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
  case class Settings(kafkaBrokers: String, kafkaTopic: String, kafkaClientId: String, kerberosEnabled: Boolean)

  def createProducer(settings: Settings): KafkaProducer[Integer, String] = {
    log.info(s"connecting to kafka with the following settings: {brokers: ${settings.kafkaBrokers}, " +
      s"topic: ${settings.kafkaTopic}, clientId: ${settings.kafkaClientId}, kerberosEnabled: ${settings.kerberosEnabled}}")
    new KafkaProducer[Integer,String](generateProps(settings.kafkaBrokers, settings.kafkaClientId, settings.kerberosEnabled))
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