package com.verizon.bdcpe.adobelivestream.kafka

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by Alvaro Muir<alvaro.muir@verizon.com>
  * Verizon Big Data & Cloud Platform Engineering
  * 10/6/17.
  */

class KafkaServiceSpec extends FlatSpec with Matchers {
  // be sure to change to adjusts your Constants.scala file:
  val kafkaBrokers: String = Constants.KAFKA_BROKERS
  val kafkaClientId: String = Constants.KAFKA_CLIENTID
  val kerberosEnabled: Boolean = true

  "The producer prop generator" should "return appropriate producer properties" in {
    val producerPops = KafkaService.generateProps(kafkaBrokers, kafkaClientId, kerberosEnabled)
    producerPops.getProperty("bootstrap.servers") should be (Constants.KAFKA_BROKERS)
    producerPops.getProperty("client.id") should be (Constants.KAFKA_CLIENTID)
    producerPops.getProperty("security.protocol") should be ("SASL_PLAINTEXT")
  }

}
