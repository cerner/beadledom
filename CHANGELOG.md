# Beadledom Changelog

## 4.1 - 28th July 2020

### Changed
* Upgrade Avro to 1.10.0.

## 4.0 - 26th June 2020

[Migration guide](http://engineering.cerner.com/beadledom/4.0/docs/releases/Beadledom40.html) to Beadledom 4.0 from Beadledom 3.x.

### Breaking Changes
* Removed beadledom-swagger1 and beadledom-stagemonitor modules.
* Changed AnnotatedObjectMapperProvider and ObjectMapperProvider to use [PropertyNamingStrategy.LOWER_CAMEL_CASE](https://javadoc.io/static/com.fasterxml.jackson.core/jackson-databind/2.10.4/com/fasterxml/jackson/databind/PropertyNamingStrategy.html#LOWER_CAMEL_CASE) from [PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES](https://javadoc.io/static/com.fasterxml.jackson.core/jackson-databind/2.10.4/com/fasterxml/jackson/databind/PropertyNamingStrategy.html#CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES).

### Changed
* Upgraded RESTEasy to 4.5.5.Final.
* Upgraded to Scala 2.12 and Scalatest 3.1

## 3.7 - 27th May 2020

### Added
* `HttpHealthDependency` class for dependencies that are HTTP services.
* Add server relative quality factor to health resources.

## 3.6 - 30th April 2020

### Added
* The `beadledom-correlation` module.

### Changed
* Updated resteasy to 3.11. Following changes are affected if your service is also uplifting to use 4.x version of Resteasy.
* Make sure to install the [Beadledom JAX-RS CorrelationIdFilter](jaxrs/src/main/java/com/cerner/beadledom/jaxrs/provider/CorrelationIdFilter.java) on the server side so that correlationIds are forwarded properly when consuming a beadledom client.
  This is automatically installed if your service installs the core `BeadledomModule` guice module.
* With resteasy update, if your service is using [ParamConverter](https://docs.jboss.org/resteasy/docs/4.0.0.Final/userguide/html/StringConverter.html#d4e1553) for validating query parameters, it will now throw a Jax-RS NotFoundException.
  Use [validation](https://docs.jboss.org/resteasy/docs/4.0.0.Final/userguide/html/Validation.html) section of Resteasy user guide to understand how to do param validation.
*  [`MockHttpResponse`](https://github.com/resteasy/Resteasy/blob/3.11/resteasy-jaxrs/src/main/java/org/jboss/resteasy/mock/MockHttpResponse.java) class from Resteasy now has the `Content-Type` header as a `JAX-RS` MediaType class instead of a String.

### Deprecated
* All beadledom-client modules and classes have been deprecated. Use [Retrofit](https://github.com/square/retrofit) instead.

## 3.5 - 21 February 2020

### Changed
* Retryable IOExceptions in HTTP clients will now use a simple exponential backoff delay instead of retrying immediately.
* Update Jackson to 2.10.2 and Tomcat to 7.0.99

## 3.4 - 8 November 2019

### Changed
* Nested transactions via `@JooqTransactional` no longer use SavePoints. Technically this a non-passive behavioral change, but seems like the safer option and likely what is more in line with what is expected by users of `@JooqTransactional`.

### Deprecated
* com.cerner.beadledom.jaxrs.PATCH has been deprecated. Use PATCH annotation from JAX-RS 2.1, https://jax-rs.github.io/apidocs/2.1/javax/ws/rs/PATCH.html instead.

### Fixed
* Update `ThreadLocalCommitHookExecutingTransactionListener` to override `TransactionListener#beginStart` instead of `TransactionListener#beginEnd`

## 3.3 - 8 August 2019

### Added

* Jooq transaction hooks for executing actions after a successful commit.
* New `totalResults` convenience method on `OffsetPaginatedList.Builder` for when only a total results value needs to be set.
* `ForwardedHeaderFilter` to determine the correct HTTP protocol of the origin request and apply it to the internal request object.
* The `beadledom-integration` module to test features of Beadledom using a service behind a reverse-proxy.

### Changed
* Update archetype dependencies to match beadledom library
* Switch to Keep a Changelog style for Changelog moving forward - https://keepachangelog.com/en/1.0.0/
* Update maven archetype for project generation with new client recommendations.
* Update client documentation to recommend using an abstracted resource pattern.

### Deprecated
* The convenience overloads of the `metadata` method on `OffsetPaginatedList.Builder` have been deprecated.

### Fixed
* JAX-RS providers with binding annotations will no longer be registered in the resteasy provider factory for the server.
* Guice-based Beadledom clients will now use the correct time unit for time-outs.

## 3.2.5 - 25 March 2019

### Defects Corrected
* Add back `jersey-media-multipart` with an explicit version (turns out `swagger-jersey2-jaxrs` requires it).

## 3.2.4 - 22 March 2019

### Defects Corrected
* Fixed exclusion of `jersey-media-multipart` from `swagger-jersey2-jaxrs`.
* Jackson version uplift from 2.9.6 -> 2.9.8 because of vulnerabilities:
  - CVE-2018-19360
  - CVE-2018-19362
  - CVE-2018-14720
  - CVE-2018-19361
  - CVE-2018-14719
  - CVE-2018-14718
  - CVE-2018-14721

## 3.2.3 - 4 December 2018

### Defects Corrected
* [internal] Use the `-q` option on `mvn site` to enable quiet running of the command (only printing errors) in an effort to reduce log length in Travis.

## 3.2.2 - 4 December 2018

### Defects Corrected
* [internal] Use the `-B` option on our `mvn site` command to reduce log length by eliminating progress dialogs from downloads. [More Info](https://stackoverflow.com/questions/21638697/disable-maven-download-progress-indication)

## 3.2.1 - 3 December 2018

### Defects Corrected
* [internal] Upgrade to spotbug 3.1.8 to eliminate incorrect identification of bugs that was causing Travis to fail. [More Info](https://github.com/spotbugs/spotbugs/pull/688)

## 3.2 - 30 November 2018

### Additions
* Add OffsetPaginatedListDto interface to the pagination module.
* Add configuration to allow 0 as a pagination limit value (disabled by default - limit=1 as minimum value).
* Add Swagger 2 module.

### Defects Corrected
* Fix isHealthy field being included in the health dependency list JSON.
* Health resource (/health) now excludes non-primary health dependencies. The non-primary dependencies are still included in the diagnostic resource (/health/diagnostic). The default for health dependencies is also changed to be primary.

## 3.1 - 12 September 2018

### Additions
* Add offset based pagination with beadledom-pagination.

### Defects Corrected
* Fixed beadledom client deserialization to GenericResponse when the JAX-RS response has no entity.

## 3.0 - 3 August 2018

### Breaking Changes
* Remove StagemonitorModule, SwaggerModule, AvroJacksonGuiceModule, AvroSwaggerGuiceModule, and
HealthModule modules from being installed by BeadledomModule. If the removed functionality is
desired, install the removed modules in the consuming guice module.
* Bump minimum Java version to 1.8 for all modules.
* Upgrade to JAX-RS 2.1 with Resteasy 3.6.x
* Change the HttpClient `ServiceUnavailableRetryStrategy` to only retry on 503 response codes.
* The beadledom-swagger module was renamed to beadledom-swagger1 in preparation for support of
swagger 2 and OpenAPI 3.

### Enhancements
* Support building with JDK 9/10+
* Add [migration guide](http://engineering.cerner.com/beadledom/3.0/docs/releases/Beadledom30.html) to documentation site.

### Defects Corrected
* Fixed health DTOs Jackson deserialization.

## 2.8 - 16 May 2018

### Additions
* Add [New Relic](https://newrelic.com/) module.

## 2.7.2 - 11 April 2018

### Additions
* Add updated GPG signing key with a longer duration until it expires (20 years).

### Enhancements
* Update GenericResponse to extend the Closeable interface.

## 2.7.1 - 9 March 2018

### Additions
* Add beadledom-json-common to beadledom-bom.

## 2.7 - 13 November 2017

### Additions
* Add environment variables configuration source.
* Add [jOOQ](https://www.jooq.org/) module for database interaction.

## 2.6.1 - 22 September 2017

### Enhancements
* Add `JsonMappingExceptionMapper` and `JsonParseExceptionMapper` to map Jackson exceptions to the JSON error response structure.

## 2.6 - 15 September 2017

### Enhancements
* Add `JsonError` and `ErrorDetail` models to standardize JSON error response structure. Addresses [Issue #24](https://github.com/cerner/beadledom/issues/24).
* Update the `WebApplicationExceptionMapper` to follow JSON error response structure.
* Add two new exception mappers, `ThrowableExceptionMapper` and `FailureExceptionMapper`, for other uncaught error cases that also follow the JSON error response structure. Addresses [Issue #43](https://github.com/cerner/beadledom/issues/43).

## 2.5.1 - 19 August 2017

### Enhancements
* Add exception to request trace error logging when serializing the trace to JSON fails.

## 2.5 - 23 May 2017

### Additions
* Add Bill of Material(BOM) pom for dependency management.

### Enhancements
* Disable the stagemonitor web widget in the service generated by the archetype, as the default configuration.
* Added toString implementation to DelegatingGenericResponse and BuiltGenericResponse.
* Added a PATCH annotation because JAX-RS does not have one.

### Defects Corrected
* Cleaned up dependencies and fixed few minor issues with generated code in archetype.

## 2.4 - 16 Feb 2017

### Additions
* Documentation and archetype changes for best practices for structuring service guice modules.

### Defects Corrected
* Removed unnecessary loop in `ResteasyContextListener` which would cause infinite loop if run.
* Fixed issue with redirects in the bootstrap script ([issue-15](https://github.com/cerner/beadledom/issues/15)).
* WebApplicationException mapper is called when exceptions are thrown by the checkParam method in JaxRsParamConditions class ([#16](https://github.com/cerner/beadledom/pull/16)).
* Fixed `BeadledomClientBuilderProvider` using root Guice injector instead of nearest child injector.

## 2.3 - 24 Jan 2017

### Enhancements
* Fixed resteasy [compatibility issues](https://github.com/cerner/beadledom/issues/9).
* Open sourced.
* Add JavaDoc to doc site.
* Add user manual to doc site.

-------
**Note**: The below versions were released internally and not available via Maven Central. These entries are kept to track down the history of beadledom.


## 2.2 - 13 Nov 2016

### Enhancements
* Client specific ObjectMapper.

### Defects Corrected
* Fixed the dependency's health check urls.

## 2.1.1 - 21 Nov 2016

### Enhancements
* Removed the usage of Optional with boolean.

### Defects Corrected
* Corrected the defect which always gave a faulty true value for healthy.

## 2.1 - 8 Nov 2016

### Additions
* Added priories to the Beadledom Configuration.
* An archetype module for bootstrapping Beadledom projects.
* Getting started guide for Beadledom.
* Troubleshooting section to gh-pages.
* Healthcheck issue added to Troubleshooting docs.

## 1.7 - 13 Oct 2016

### Additions
* A configuration module for loading a common configuration from multiple sources.

## 1.6 - 16 Sept 2016

### Enhancements
* Configure LogJsonRequestTraceReporter utilizing Stagemonitor configuration.

## 1.5 - 1 Sept 2016

### Defects corrected
* Health check pages load without exceptions when there is no `BasicAvailabilityUrl` defined for a health dependency.

## 1.4 - 18 Aug 2016

### Enhancements
* Upgraded governator version from 1.12.10 to 1.13.5
* Health check end points have been updated to use new JSON structure.

## 1.3 - 21 Jul 2016

### Additions

* JAX-RS GenericResponse API module for type safe JAX-RS responses.
* Resteasy implementation of JAX-RS GenericResponse API.

## 1.2 - 27 Apr 2016

### Additions

* A lifecycle module for managing a container's lifecycle.
* Guice core extensions module with common guice extensions.
* Guice dynamic bindings extension module for working with bindings with dynamic annotations.
* Added detailed documentation.

### Enhancements

* Jackson ObjectMapper features can be enabled/disabled via Guice multibindings.
* Supports Guice 4 annotations for adding objects to a Multibinder.

### Defects corrected

* Beadledom provides a default empty multi-binding for HealthDependency.
* Stagemonitor JSON trace logging now correctly logs the condensed JSON request trace.
