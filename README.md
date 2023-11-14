[![Java CI with Gradle](https://github.com/egorklimov/test-assignment/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/egorklimov/test-assignment/actions/workflows/ci.yml)
# `test-assignment`


## 👋 About
Welcome to the test task of the Utilizing Runtime Information to Improve Development Process PhD Position!

This task has two parts:
* In the main part, you need to analyze traces and other runtime information of a test project. 
  In the repository you will find a docker-compose configuration containing a service called **`cat-api`** and an OpenTelemetry infrastructure to collect its execution traces. 
  The tool should reveal bugs and performance issues in the **`cat-api`** service by analyzing execution traces or other runtime information. 
* In the bonus part of the task, we ask you to improve this tool using machine learning models.

**Requirements**:
* Detect performance issues, anomalies, bugs, code or architectural smells using runtime information. As a first step, you should detect the N+1 query problem in the **`cat-api`** service.
* Issues found must be linked to source code and shown to the user. For example, the problem is in method X.

**Desirable**:
* Effectiveness: we will pay attention to the performance of your solution
* Scalability: the solution can be used with different projects, runtimes, and languages.
* Extensibility: detection of new types of issues can be easily added.
* Code quality and tests: the tool is designed with attention to code quality.
* User (developer) experience: it can be used by developers on a daily basis to assist in the software development process.
* Use not only traces, but also logs and other data (even git information).
* Take into account that runtime information may be obtained from different environments (different service installations).

**You are welcome to**:
* Choose any implementation you want — it can be a CLI tool, standalone tool or IntelliJ plugin, any other options are also valid.
* Modify the test project to add new cases to analyze or change the runtime information processing.

## Quick start

To explore the data format, you can analyze the trace data [added](https://github.com/egorklimov/test-assignment/blob/docs/trace_exploration) to the repository.

To work with real data, you can setup the environment:

0. Install [docker](https://docs.docker.com/engine/install/) & [docker compose](https://docs.docker.com/compose/install/)
1. Clone repository or download [docker-compose](https://github.com/egorklimov/test-assignment/blob/docs/docker-compose.yml) configuration
2. Start docker-compose:
    ```bash
    docker compose up
    ```
3. Go to the **`cat-api`** HTTP API [documentation](http://localhost:8080/swagger-ui/index.html) and call the endpoints. To call endpoint, please press "Try it out" button near the endpoint documentation.
4. You will then be able to find traces in [Jaeger UI](http://localhost:16686/search?operation=GET%20%2Fapi%2Fcats&service=cat-api) related to your API calls.
<p align="center">
  <img src="https://github.com/egorklimov/test-assignment/blob/docs/docs/quickstart.gif?raw=true" alt="Quickstart" width="738">
  <br/>
  <em>Recorded quickstart: docs/quickstart.gif</em>
</p>

<p align="center">
  <img src="https://github.com/egorklimov/test-assignment/blob/docs/docs/schema.png?raw=true" alt="C4 container diagram" width="738">
  <br/>
  <em>Fig.1. C4 container diagram for the test task</em>
</p>

> **Note**
> it doesn't mean that your solution must be dockerized, in the C4 model, a container represents an application or a data store

## Development
### cat-api
Service to get information about cats.

To build docker image locally, you can run **`jibDockerBuild`** command:
```bash
./gradlew jibDockerBuild
```

[ktlint](https://github.com/pinterest/ktlint) is used to check code style, to auto-format source code you can run **`ktlintFormat`** command: 
```bash
./gradlew ktlintFormat
```

To run tests in [the test folder,](https://github.com/egorklimov/test-assignment/blob/docs/src/test) you can run **`test`** command: 
```bash
./gradlew test
```

#### HTTP API
OpenAPI spec is described in the [openapi.yaml](https://github.com/egorklimov/test-assignment/blob/docs/src/main/resources/static/openapi.yaml) file. 

App follows API-first approach, if you want to add new endpoints, please modify openapi spec and then generate server side using **`openApiGenerate`** command:
```bash
./gradlew openApiGenerate
```

#### Database
Database migrations are available in the [db.migration](https://github.com/egorklimov/test-assignment/blob/docs/src/main/resources/db/migration) directory.
Flyway is used to migrate database schema. 
If you want to update database schema, please create a new migration (e.g., V3__my_changes.sql). 
Migration will be applied on the application startup.

**`cat-api`** generates cat records on startup.

You can configure connection to the database using your favorite tool, e.g., psql (*password: postgres*):
```bash
 psql -h localhost -p 5432 -d cats -U postgres
```

<p align="center">
  <img src="https://github.com/egorklimov/test-assignment/blob/docs/docs/db_schema.png?raw=true" alt="Database schema" width="369">
  <br/>
  <em>Fig. 3. Database schema</em>
</p>

### Distributed tracing
Distributed tracing is a method of observing requests as they propagate through distributed cloud environments.

Dataflow is similar to the [Jaeger’s SPM demo environment](https://github.com/jaegertracing/jaeger/tree/main/docker-compose/monitor).
1. **`cat-api`** is instrumented by OpenTelemetry **`javaagent`**.
2. **`javaagent`** sends metrics to the **`otel-collector`**
3. **`otel-collector`** sends data to the **`Jaeger`** and **`Prometheus`**

<p align="center">
  <img src="https://github.com/egorklimov/test-assignment/blob/docs/docs/tracing.png?raw=true" alt="Tracing schema" width="738">
  <br/>
  <em>Fig. 4. Distributed tracing schema</em>
</p>
