## Synopsis
#### v 1.0

adobelivestream.dashboard is an alpha-phase dashboard enabled via the Play! framework for Adobe's
[Analytics Live Streaming](https://marketing.adobe.com/developer/documentation/analytics-live-stream/overview-1)
built on top of the [adobelivestream-collector API](https://onestash.verizon.com/users/v603497/repos/adobelivestream/browse/collector),
with the option to run as command-line application. The client-facing UI is developed with Angular 4.

**NOTE:** This project is in alpha release, and is developed in two parts for now: A server which ingests kafka messages
and a npm-based client app which consumes websockets from the server.

## Starting the dev backend server


change to the dashboard directory, run sbt


```
$ cd dashboard
$ sbt
$ run
```

Then navigate to http://<your server ip>:9000

## starting the dev fontend sever

```
$ cd dashboard/client
$ npm install
$ npm serve
```

Then navigate to http://<your server ip>:4200


## Installation

The project is built via Maven; Tests are included but have been disabled for compiling due to the nature of credential 
and endpoint settings. Packages can be built simply as:

```$sbt compile```


To work on the client, ensure you have ```$npm serve```

## Tests

Tests are run as normal via Maven. Be sure to add credentials and enable appropriate test as necessary.
Appropriate variables can be edited in Constants.java class, located in 
``` src/main/java/com.verizon.bdcpe.adobelivestream.core```

## License

We need some kinda Vz internal licensing scheme, but software is provided AS-IS.


Alvaro Muir <alvaro.muir@verizon.com>, Verizon Big Data & Cloud Platform Engineering