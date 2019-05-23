# adobelivestream
## v1.0

Built with the following components:

 * core
 * collector, an API built on top of core
 * kafka, an API built on top of collector
 * spark, an API built on top of collector
 * ignite, an API built on top of collector

See individual module README's for details.

## Installation

The project is built via Maven; Tests are included but have been disabled for compiling due to the nature of credential 
and endpoint settings. The entire project can be built from the root or within individual modules as simply as:
                      
```$ mvn clean install -Dmaven.test.skip=true```

## Overall Setup

Since this is dependent on Adobe's API, you must first obtain a client key and secret.

This is accomplished by logging into [Adobe's Developer Connection](https://marketing.adobe.com/developer), and creating a application via Developer > Applications. 

Note, this requires a log in - so use your Adobe Analytics credentials.

Specific data on Adobe Live Stream API can be [found here](https://marketing.adobe.com/developer/documentation/analytics-live-stream/overview-1).

**IMPORTANT:** EVERY USER (OR SERVER) MUST HAVE THEIR OWN CREDENTIALS, AS *THE API WILL ONLY SERVE ONE STREAM PER TOKEN AT A TIME*

## Tests

Tests are run as normal via Maven. Be sure to add credentials and enable appropriate test as necessary.

## License

Software is provided AS-IS, no guarantees included.


Alvaro Muir <alvaro.muir@verizon.com>, Verizon Big Data & Cloud Platform Engineering
