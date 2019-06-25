## Synopsis
#### v 1.0

adobelivestream.kafka is kafka-based pubsub middleware for Adobe's
[Analytics Live Streaming](https://marketing.adobe.com/developer/documentation/analytics-live-stream/overview-1)
built on top of the [adobelivestream-collector API](https://github.com/alvaromuir//adobelivestream/browse/collector),
with the option to run as command-line application. As with the collector, required parameters are passed in via parameters
with additional functionality added to filter the returned json as well as limit the number of results.

## Quick start Example

The standalone jar is built by default. Running with the --help flag provides instructions on parameters:

```
$java -jar adobelivestream-kafka-<VERSION>.jar --help

Usage: adobelivestream-kafka -k <appKey> -s <appSecret> -i <appId> -m <maxConnections> -b <kafkaBrokers> -t <kafkaTopic>  ... [OPTIONS]
adobelivestream.Kafka sends real-time event data from Adobe Analytics to a Kafka quorum.
Options:

  -k, --app-key  <arg>             Adobe application key
  -s, --app-secret  <arg>          Adobe application secret
  -i, --app-id  <arg>              Adobe application ID
  -m, --connections-max  <arg>     max concurrent connections
  -b, --kafka-brokers  <arg>       Kafka brokers list, comma separated
  -t, --kafka-topic  <arg>         Kafka topic
  -c, --kafka-client-id  <arg>     [Opt] Kafka clientId
  -e, --kerberos-enabled           [Opt] Kerberos SASL flag
  -o, --oauth-token-url  <arg>     [Opt] Adobe OAuth Token Url
  -h, --proxy-host  <arg>          [Opt] Https proxy host
  -n, --proxy-port-number  <arg>   [Opt] Https proxy port
  -u, --proxy-username  <arg>      [Opt] Https proxy username
  -p, --proxy-password  <arg>      [Opt] Https proxy password
  -l, --event-limit  <arg>         [Opt] Livestream retrieved events limit
  -r, --required  <arg>            [Opt] Required fields, comma separate
  -x, --excluded  <arg>            [Opt] Excluded fields, comma separated
  -f, --filtered-to  <arg>         [Opt] Fields filtered to, comma separated

      --help                       Show help message
      --version                    Show version of this program
      
README - https://github.com/alvaromuir//adobelivestream/browse/kafka
```
The essential flags are the application key (-k), application secret(-s), application ID (-a), maximum connections (-m), the Kafka brokers list (-b) and topic (-t).

Please see adobelivestream.core's README for details on required, excluded and filtered flags.


## Installation

The project is built via Maven; Tests are included but have been disabled for compiling due to the nature of credential 
and endpoint settings. Packages can be built simply as:
                                            
```$ mvn clean package -Dmaven.test.skip=true```

## Tests

Tests are run as normal via Maven. Be sure to add credentials and enable appropriate test as necessary.

## License

Software is provided AS-IS, no guarantees included.


Alvaro Muir <alvaro.muir@verizon.com>, Verizon Big Data & Cloud Platform Engineering