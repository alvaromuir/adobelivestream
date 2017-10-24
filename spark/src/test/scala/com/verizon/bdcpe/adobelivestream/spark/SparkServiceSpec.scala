package com.verizon.bdcpe.adobelivestream.spark

import org.scalatest.{FlatSpec, Ignore, Matchers}

@Ignore
class SparkServiceSpec extends FlatSpec with Matchers {
  // be sure to change to adjusts your Constants.scala file:
  val kafkaBrokers: String = Constants.KAFKA_BROKERS
  val kafkaClientId: String = Constants.KAFKA_CLIENTID
  val kerberosEnabled: Boolean = true

}