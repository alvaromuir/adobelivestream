package com.verizon.bdcpe.adobelivestream.collector

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
    val CONNECTIONS_MAX: Int = 8
    val TOKEN_GET_URL: String = "https://api.omniture.com/token"
    val PROXY_HOST: String = "<your proxy host>"
    val PROXY_PORTNUMBER: Int = 80
    val PROXY_USERNAME: String = "<your proxy username>"
    val PROXY_PASSWORD: String = "<your proxy password>"
    val EVENT_LIMIT: Int = 1 // keep it small for testing
}
