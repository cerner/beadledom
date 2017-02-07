# Beadledom Changelog

## 2.4 - In Development
* Added SCM Revision to health check.

### Defects Corrected
* Removed unnecessary loop in `ResteasyContextListener` which would cause infinite loop if run.
* Fixed issue with redirects in the bootstrap script ([issue-15](https://github.com/cerner/beadledom/issues/15)).
* WebApplicationException mapper is called when exceptions are thrown by the checkParam method in JaxRsParamConditions class ([#16](https://github.com/cerner/beadledom/pull/16)).

## 2.3 - 24 01 2017

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
