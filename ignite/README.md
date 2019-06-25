## Synopsis
#### v 1.0

adobelivestream.ignite is ignite-based in-memory key-value store middleware for Adobe's
[Analytics Live Streaming](https://marketing.adobe.com/developer/documentation/analytics-live-stream/overview-1)
built on top of the [adobelivestream-collector API](https://github.com/alvaromuir//adobelivestream/browse/collector),
with the option to run as command-line application. As with the collector, required parameters are passed in via parameters 
with additional functionality added to filter the returned json as well as limit the number of results.

## Quick start Example

The all-inclusive 'standalone' library has a test application built in, simply run a jar with appropriate flags:

```
$java -jar adobelivestream-ignite-<VERSION>.jar

Usage: adobelivestream-ignite -k <appKey> -s <appSecret> -i <appId> -m <maxConnections>... [OPTIONS]
adobelivestream.Ignite sends real-time event data from Adobe Analytics to an Ignite cache cluster.
Options:

  -k, --app-key  <arg>                Adobe application key
  -s, --app-secret  <arg>             Adobe application secret
  -i, --app-id  <arg>                 Adobe application ID
  -m, --connections-max  <arg>        max concurrent connections
  -t, --ignite-cache-name  <arg>      [Opt] Ignite cache name
  -g, --ignite-config-path  <arg>     [Opt] Ignite configuration xml file path
  -o, --oauth-token-url  <arg>        [Opt] Adobe OAuth Token Url
  -h, --proxy-host  <arg>             [Opt] Https proxy host
  -n, --proxy-port-number  <arg>      [Opt] Https proxy port
  -u, --proxy-username  <arg>         [Opt] Https proxy username
  -p, --proxy-password  <arg>         [Opt] Https proxy password
  -l, --event-limit  <arg>            [Opt] Livestream retrieved events limit
  -r, --required  <arg>               [Opt] Required fields, comma separate
  -x, --excluded  <arg>               [Opt] Excluded fields, comma separated
  -f, --filtered-to  <arg>            [Opt] Fields filtered to, comma separated

  -v, --ignite-eviction-time  <arg>   cache entry TTL, in seconds
      --help                          Show help message
      --version                       Show version of this program

README - https://github.com/alvaromuir//adobelivestream/browse/ignite
```
At the very least, the clientKey (-k), clientSecret(-s), applicationId (-i) and connectionsMax (-m) are required.
For brevity, a slim jar is also built by default (e.g. adobelivestream-core-\<VERSION\>.jar)

## API Details

When including in your own library, the API allows for closable socket connections along with individual token request 
as well as endpoint calls. This enables local authentication strategies and connection pooling administration.

## Installation

The project is built via Maven; Tests are included but have been disabled for compiling due to the nature of credential 
and endpoint settings. Packages can be built simply as:

```$ mvn clean package -Dmaven.test.skip=true```


## Tests

Tests are run as normal via Maven. Be sure to add credentials and enable appropriate test as necessary.
Appropriate variables can be edited in Constants.java class, located in 
``` src/main/java/com.verizon.bdcpe.adobelivestream.core```

## License

We need some kinda Vz internal licensing scheme, but software is provided AS-IS.


Alvaro Muir <alvaro.muir@verizon.com>, Verizon Big Data & Cloud Platform Engineering