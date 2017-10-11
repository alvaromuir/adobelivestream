# adobelivestream
## v1.0

adobelivestream Modular refactor of [adobe-live-streamer](https://onestash.verizon.com/users/v603497/repos/adobe-live-streamer/browse)

Built with the following components:

 * core
 * collector, an API built on top of core
 * kafka, an API built on top of collector

See individual module README's for details.

##### Installation

The project is built via Maven; Tests are included but have been disabled for compiling due to the nature of credential 
and endpoint settings. The entire project can be built from the root or within individual modules as simply as:
                      
```$ mvn clean package -Dmaven.test.skip=true```

##### Overall Setup

Since this is dependent on Adobe's API, you must first obtain a client key and secret.

This is accomplished by logging into [Adobe's Developer Connection](https://marketing.adobe.com/developer), and creating a application via Developer > Applications. 

Note, this requires a log in - so use your Adobe Analytics credentials.

Specific data on Adobe Live Stream API can be [found here](https://marketing.adobe.com/developer/documentation/analytics-live-stream/overview-1).

**IMPORTANT:** EVERY USER (OR SERVER) MUST HAVE THEIR OWN CREDENTIALS, AS *THE API WILL ONLY SERVE ONE STREAM PER TOKEN AT A TIME*