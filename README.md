# Git Scrapper

Proxy between to github REST API 

https://developer.github.com/v3


## Build and run

    mvn clean install

During the build test are run automatically, if you want to skip use:

    mvn clean install -D skipTest

and someone will hunt you for the rest of your life or until you run:

    mvn test

To run integration tests and create coverage report

     mvn clean package -Dgroups="integration"

or to run pure unit tests

     mvn clean package -Dgroups="unit"

Reports with coverage are provided at ***target/site/jacoco/index.html***

To run server locally, you can configure with **application.properties**
For application to be able to access github REST API, it has to be provided with
OAuth token, you have to provide your own token, the one that is present is expired and
is just an example.

    github.token=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

How to obtain token is described here https://docs.github.com/en/rest/guides/getting-started-with-the-rest-api#authentication


and then run it with:

    mvn spring-boot:run

Service can be accessed by default at:

http://localhost:9191/repo/{github-user}

e.g. http://localhost:9191/repo/muros

To test the interface you can also use Swagger interface at:

http://localhost:9191/swagger-doc/swagger-ui.html

or create client using Swagger YAML or XML file:

XML:
http://localhost:9191/swagger-doc/v3/api-docs

YAML:
http://localhost:9191/swagger-doc/v3/api-docs.yaml

YAML is also provided localy as **api-docs.yaml**

## Implementation

Implementation is based on Spring boot with Webflux reactive framework.

Where REST API interface is exposed over embedded Netty server.

Implementation that uses github provided REST API - https://developer.github.com/v3 to access
repos of specified user. For each repo, branches are retrieved and lash commit SHA saved.

All collected data is then returned in a single response JSON formatted array.

Implementation is fully reactive and uses Webflux functional approach to implementing API.

## Todo

- Stress testing with parallel load - probably with JMetter.
- Unit tests for classes - with emphasis on functional returns.
- More integration tests - now there are three basic ones, as described in an assignment.
- Deeper understanding of Webflux.
- Fix that WebConfig mess of error handling.
- Build docker container.
- AWS deployment with Gateway API.


