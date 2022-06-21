# Click 
## A silly game to experiment with various technologies:
- Scala 3
- ScalaJS
- ZIO 2
- zio-http
- Tapir 1.x
- Laminar, Laminext

[**CHECK DEMO**](https://ilinandrii.github.io/click/)

## Projects
---
### Backend
---
#### **Compile**
Following commands are enough to build `backend` project:
```
> clean;compile
```

#### **Run**
To start `backend` use:
```
> backend/run
```
An http server will be started at [localhost:9000]()

#### **Package**
To build a local docker image for `backend` project use 
```
> Docker/publishLocal
```
Then run it using `docker run` command or `docker-compose`.
Default port `9000` will be exposed from docker container.
Port can be changed through config of [sbt-native-packager](https://sbt-native-packager.readthedocs.io/en/latest/) plugin in `build.sbt`

---
### Frontend
---
#### **Compile**
To compile `frontend` project
```
> fastLinkJS
```
or
```
> fullLinkJS
```
commands are used, to build JS with fast or full optimizations respectively.<br>

#### **Run**
[test.html](./frontend/src/main/resources/test.html) uses locally compiled fast optimized js client for development purposes. Build client with `fastLinkJS` and check it out in your preferred browser.

Currently client is using remote server hosted on **Heroku**.
Change [ClickAPI's](frontend/src/main/scala/io/github/ilinandrii/click/ClickAPI.scala) `baseUrl` for another host. 

---
### Site
---
#### **Publish**
Site is hosted on **GitHub Pages** and published from `gh-pages` branch.<br>
Built with [sbt-site](https://www.scala-sbt.org/sbt-site/) plugin which is set up to gather built client JS and site html page under `target/site` directory.<br>
To publish a newer version of a site (push a `target/site` content to `gh-pages` branch) an [sbt-ghpages](https://www.scala-sbt.org/sbt-site/publishing.html) plugin is used.

## TODO:
- add a username
- make a leaderboard page
- add persistence (`quill`, `postgresql`)
- warm up `zio-http` server after restart
- fully support mobile view
- serve swagger UI documentation
- use lighter weight base docker image for backend
- configurable server port 
- configurable client `baseUrl` for dev and prod environments through env or command line properties
- research functionality of **GitHub Actions** to automate build, site publishing process.

