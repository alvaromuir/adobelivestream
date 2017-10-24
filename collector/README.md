## Synopsis
#### v 1.2

adobelivestream.collector is a standalone collector for Adobe's
[Analytics Live Streaming](https://marketing.adobe.com/developer/documentation/analytics-live-stream/overview-1)
built on top of the [adobelivestream-core API](https://onestash.verizon.com/users/v603497/repos/adobelivestream/browse/core), with the option to run as command-line application.
As with the core, required parameters are passed in via parameters with additional functionality added to filter the returned json as well as limit the number of results.

###### whats new, 10.6.17
v 1.2 is processor output agnostic -- meaning while the collector will still grab code, the actual output format/destination/methodology is left as an abstract function.

## Quick start Example

The standalone jar is built by default. Running with the --help flag provides instructions on parameters:

```
$java -jar adobelivestream-collector-<VERSION>.jar --help

Usage: adobelivestream.collector -k <appKey> -s <appSecret> -i <appId> -m <maxConnections> ... [OPTIONS]
adobelivestream.Collector retrieves real-time event data from Adobe Analytics.
Options:

  -k, --app-key  <arg>             Adobe application key
  -s, --app-secret  <arg>          Adobe application secret
  -i, --app-id  <arg>              Adobe application ID
  -m, --connections-max  <arg>     max concurrent connections
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

README - https://onestash.verizon.com/users/v603497/repos/adobelivestream/browse/collector
```
The essential flags are the application key (-k), application secret(-s), application ID (-a), and maximum connections (-m).

For example to add a criteria that requires "prop1" and "prop2" but excludes "prop3" you would run a command with the -r and -x flags,
respectively:

```
$ java -jar adobelivestream-collector-1.1.jar -k $appKey -s $appSecret -a $appId -m 1 -r prop1,prop2 -x prop3
```

To return a custom JSON with only a subset of desired keys, the filtered-to flag (-f) is needed:

```
$ java -jar adobelivestream-collector-1.1.jar -k $appKey -s $appSecret -a $appId -m 1 -f prop1,eVar1
```

Note, although the returned json is subset, the desired fields could still be null if empty. Additionally, all "filtered"
responses have the following keys:
* "sessionId": hitIdHigh + hitIdLow
* "visitorId": visIdHigh + visIdLow
* "timeGMT"  : timeGMT


## Installation

The project is built via Maven; Tests are included but have been disabled for compiling due to the nature of credential 
and endpoint settings. Packages can be built simply as:
                      
```$ mvn clean package -Dmaven.test.skip=true```

## Tests

Tests are run as normal via Maven. Be sure to add credentials and enable appropriate test as necessary.

## License

Software is provided AS-IS, no guarantees included.


Alvaro Muir <alvaro.muir@verizon.com>, Verizon Big Data & Cloud Platform Engineering