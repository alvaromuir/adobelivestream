package com.verizon.bdcpe.adobelivestream.kafka

object Constants {

  /**
    * Created by Alvaro Muir<alvaro.muir@verizon.com>
    * Verizon Big Data & Cloud Platform Engineering
    * 7/24/17.
    */

    val CURRENT_API_VERSION = "1.0"

    /**
      * For testing
      */
    val APP_KEY: String = "<your app key>"
    val APP_SECRET: String = "<your app secret>"
    val APP_ID: String = "<your app id>"
    val MAX_CONNECTIONS: Int = 8
    val TOKEN_URL: String = "https://api.omniture.com/token"
    val PROXY_HOST: String = "<your proxy host>"
    val PROXY_PORTNUMBER: Int = 80
    val PROXY_USERNAME: String = "<your proxy username>"
    val PROXY_PASSWORD: String = "<your proxy password>"
    val KAFKA_BROKERS: String = "tbldakf02adv-hdp.tdc.vzwcorp.com:6667,tbldakf01adv-hdp.tdc.vzwcorp.com:6667"
    val KAFKA_CLIENTID: String = "ADOBE RTS DEV"
    val KERBEROS_ENABLED: Boolean = false
    val EVENT_LIMIT: Int = 1 // keep it small for testing
}
