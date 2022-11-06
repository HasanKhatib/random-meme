## Meme Generator & OpenTracing

<div align="center">

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/hasankhatib/random-meme/CI%20-%20Build%20&%20Dockerize)
[![versionspringboot](https://img.shields.io/badge/dynamic/xml?color=brightgreen&url=https://raw.githubusercontent.com/bhuism/badge/master/pom.xml&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27parent%27%5D%2F%2A%5Blocal-name%28%29%3D%27version%27%5D&label=springboot)](https://github.com/spring-projects/spring-boot)
[![java: &gt;= 17](https://oss.aoapps.com/ao-badges/java-17.svg)](https://openjdk.org/projects/jdk/17/)
![Twitter Follow](https://img.shields.io/twitter/follow/hasankhatib?style=social)
</div>

Spring reactive-web application that utilizes two external APIs to generate random memes :grin:

### Development
- JDK17
- gradle 7.5
- Jaeger tracing 1.38 _(utilize the docker image below to run it)_

```shell
docker run -d --name jaeger \                                                                              
  -e COLLECTOR_ZIPKIN_HOST_PORT=:9411 \
  -e COLLECTOR_OTLP_ENABLED=true \
  -p 6831:6831/udp \
  -p 6832:6832/udp \
  -p 5778:5778 \
  -p 16686:16686 \
  -p 4317:4317 \
  -p 4318:4318 \
  -p 14250:14250 \
  -p 14268:14268 \
  -p 14269:14269 \
  -p 9411:9411 \
  jaegertracing/all-in-one:1.38
```

### Build & Run

PS. You will need a _RAPID API key_ in order to generate memes with this service. And subscribe to these two APIs:
- [Dad jokes API](https://rapidapi.com/KegenGuyll/api/dad-jokes)
- [Meme generator](https://rapidapi.com/meme-generator-api-meme-generator-api-default/api/meme-generator)

**Docker**
```shell
docker pull hasankhatib/random-meme
docker run -d -p 8080:8080 -e RAPIDAPI_KEY=$YOUR_RAPIDAPI_KEY hasankhatib/random-meme:latest
```

**Run with gradle**
```shell
gradle clean build
gradle bootRun
```
