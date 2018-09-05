# Beadledom

Beadledom is a bundle of common components required for building JAX-RS web services. It is the starting point
for new services. The core of Beadledom provides health checks, monitoring via Stagemonitor,
API docs via Swagger, JSON serialization via Jackson, and integration of these components with
Guice. For more information on creating a service check out our [documentation](http://cerner.github.io/beadledom/).

Beadledom is made up of the following components:

* [avro](avro#beadledom-avro) - Additional Avro functionality required for services.
* [client](client#beadledom-client) - HTTP client for communicating with the JAX-RS web services.
* [common](common#beadledom-common) - Common utilities.
* [Configuration](configuration#beadledom-configuration) - Centralized API to access the aggregated configurations from different sources.
* [core](core#beadledom-core) - Core Guice module that pulls in all the other recommended Beadledom modules.
* [guice](guice#beadledom-guice) - Additional Guice extensions.
* [guice-dynamicbindings](guice-dynamicbindings#beadledom-guice-dynamicbindings) - To retrieve annotated bindings at runtime.
* [health](health#beadledom-health) - Health checks for services.
* [jackson](jackson#beadledom-jackson) - JSON serialization for services.
* [jaxrs](jaxrs#beadledom-jaxrs) - Restful API additions (filters and providers)
* [lifecycle](lifecycle#beadledom-lifecycle) - Defines and manages the lifecyle hooks for an application or a container.
* [lifecycle-governator](lifecycle-governator#beadledom-lifecycle-governator) - An implementation of the [beadledom-lifecycle](lifecycle#beadledom-lifecycle) using [Governator](https://github.com/Netflix/governator).
* [pagination](pagination#beadledom-pagination) - Parameters and hypermedia for paginated endpoints.
* [resteasy](resteasy#beadledom-resteasy) - RESTEasy implementation and integration of Beadledom components.
* [stagemonitor](stagemonitor#beadledom-stagemonitor) - Performance monitoring and troubleshooting.
* [swagger1](swagger1#beadledom-swagger1) - API documentation.
* [testing](testing#beadledom-testing) - Testing utilities for the services.

### Additional Documentation:
Below is the list of documentations that are helpful to get started but outside the scope of the Beadledom documentation.

* [Guice](https://github.com/google/guice/wiki/GettingStarted) for dependency injection.
* [Governator](https://github.com/Netflix/governator) for lifecycle management.
* [Resteasy](http://docs.jboss.org/resteasy/docs/3.0.13.Final/userguide/html/index.html) for JAX-RS implementation.
* [Swagger](http://swagger.io/getting-started/) for API documentation.
* [StageMonitor](https://github.com/stagemonitor/stagemonitor/wiki/Installation) for metering the API.
* [Jackson](http://wiki.fasterxml.com/JacksonDocumentation) for JSON serialization.
* [Avro](http://avro.apache.org/docs/1.7.7/) for avro serialization.
* [Configuration](https://commons.apache.org/proper/commons-configuration/userguide/quick_start.html) for Apache Commons Configuration2.

## Contribute

You are more than welcome to Contribute to Beadledom.

Read our [Contribution guidelines][contibuting_guidelines].

## Releasing

Depending on the need for the release, every month or earlier the beadledom committer team reviews contributions in master that have not been released. The committer team then takes a quick vote on if a release of master should be made. If 2/3 of the committers agree on the release a release is cut.

To release Beadledom please follow our [releasing documentation][releasing_guidelines].

[contibuting_guidelines]: CONTRIBUTING.md#contributing
[releasing_guidelines]: RELEASING.md#releasing-beadledom

## License

```
Copyright 2017 Cerner Innovation, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
