## Synopsis
#### v 1.0

adobelivestream.spark is spark-based stream processing middleware for Adobe's
[Analytics Live Streaming](https://marketing.adobe.com/developer/documentation/analytics-live-stream/overview-1)
built on top of the [adobelivestream-collector API](https://onestash.verizon.com/users/v603497/repos/adobelivestream/browse/collector),
with the option to run as command-line application. As with the collector, required parameters are passed in via parameters
with additional functionality added to filter the returned json as well as limit the number of results.

## Quick start Example

The standalone jar is built by default. Running with the --help flag provides instructions on parameters:

```
$java -jar adobelivestream-spark-<VERSION>.jar --help

Usage: adobelivestream-spark -k <appKey> -s <appSecret> -i <appId> -m <maxConnections> ... [OPTIONS]
adobelivestream.Spark sends real-time event data from Adobe Analytics to a Spark cluster.
Options:

  -k, --app-key  <arg>              Adobe application key
  -s, --app-secret  <arg>           Adobe application secret
  -i, --app-id  <arg>               Adobe application ID
  -m, --connections-max  <arg>      max concurrent connections
  -o, --oauth-token-url  <arg>      [Opt] Adobe OAuth Token Url
  -h, --proxy-host  <arg>           [Opt] Https proxy host
  -n, --proxy-port-number  <arg>    [Opt] Https proxy port
  -u, --proxy-username  <arg>       [Opt] Https proxy username
  -p, --proxy-password  <arg>       [Opt] Https proxy password
  -l, --event-limit  <arg>          [Opt] Livestream retrieved events limit
  -r, --required  <arg>             [Opt] Required fields, comma separate
  -x, --excluded  <arg>             [Opt] Excluded fields, comma separated
  -f, --filtered-to  <arg>          [Opt] Fields filtered to, comma separated

  -e, --kerberos-enabled            [Opt] Kerberos SASL flag
  -y, --spark-app-name  <arg>       [Opt] Spark application name for history
                                    server
  -t, --spark-master  <arg>         [Opt] Spark master, defaults to local[*]
  -w, --spark-stream-host  <arg>    [Opt] Spark stream listener host
  -g, --spark-stream-port  <arg>    [Opt] Spark stream listener port
  -v, --streaming-interval  <arg>   [Opt] Spark streaming sec intervals,
                                    defaults to 5
      --help                        Show help message
      --version                     Show version of this program

README - https://onestash.verizon.com/users/v603497/repos/adobelivestream/browse/spark
```
The essential flags are the application key (-k), application secret(-s), application ID (-a) and maximum connections (-m).

Please see adobelivestream.core's README for details on required, excluded and filtered flags.
##

## test stream consumer ##

Additionally, this library contains a toy "listener example located in the Consumer.scala source"
```
$java -cp adobelivestream-spark.jar com.verizon.bdcpe.adobelivestream.spark.Consumer
```

Note, this will spin up a spark streaming instance with the following parameters:
* locally with two cores
* application name of 'AdobeLiveStream'
* listening bound to spark://localhost:9999
* kerberos disabled
* 2 second batch windows

warning: These tests are intended to run as detached jobs. Be sure to check your java processes ($ jps) and ensure you've killed them.

## Installation

The project is built via Maven; Tests are included but have been disabled for compiling due to the nature of credential 
and endpoint settings. Packages can be built simply as:
                                            
```$ mvn clean package -Dmaven.test.skip=true```

## Tests

Tests are run as normal via Maven. Be sure to add credentials and enable appropriate test as necessary.

## License

Software is provided AS-IS, no guarantees included.


Alvaro Muir <alvaro.muir@verizon.com>, Verizon Big Data & Cloud Platform Engineering