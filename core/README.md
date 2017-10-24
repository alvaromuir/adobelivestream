## Synopsis
#### v 1.3

adobelivestream.core provides both a low-level and quick-start API for collecting Streaming data from Adobe's
        [Analytics Live Streaming](https://marketing.adobe.com/developer/documentation/analytics-live-stream/overview-1)
        program. Live Stream is OAuth 2 authenticated. This client utilizes 'client credentials' grant tokens which
        expire every hour. All exceptions are thrown, so an extended library has the opportunity to implement
        routing, reconnecting and micro-batching strategies as necessary.
## Quick start Example

The all-inclusive 'standalone' library has a test application built in, simply run a jar with appropriate flags:

```
$java -jar adobelivestream-core-<VERSION>-standalone.jar

usage: adobelivestream
 -k, --Key <arg>                  application key (or clientId)
 -s, --Secret <arg>               application secret (or clientSecret)
 -i, --ApplicationId <arg>        Adobe analytics live stream endpoint
 -m, --connectionsMax <arg>       Signature of open socket limit
 -o, --OauthTokenUrl <arg>        [opt] Adobe OAuth 2.0 token request url
 -h, --proxyHost <arg>            [opt] proxy host, if required
 -n, --proxyPortnumber <arg>      [opt] proxy port number, if required
 -u, --proxyUsername <arg>        [opt] proxy username, if required
 -l, --eventLimit <arg>           [opt] limit of retrieved events
 -p, --proxyPassword <arg>        [opt] proxy password, if required


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