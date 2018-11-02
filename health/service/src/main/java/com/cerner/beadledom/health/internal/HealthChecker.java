package com.cerner.beadledom.health.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import com.cerner.beadledom.health.HealthDependency;
import com.cerner.beadledom.health.HealthStatus;
import com.cerner.beadledom.health.api.DependenciesResource;
import com.cerner.beadledom.health.dto.HealthDependencyDto;
import com.cerner.beadledom.health.dto.HealthDto;
import com.cerner.beadledom.health.dto.LinksDto;
import com.cerner.beadledom.metadata.ServiceMetadata;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the logic for the primary health check, diagnostic health check, dependency listing,
 * and dependency availability checks.
 *
 * <p>Health checks are performed by invoking the checkAvailability method of the injected
 * HealthDependency instances.
 */
public class HealthChecker {
  private static final Logger LOGGER = LoggerFactory.getLogger(HealthChecker.class);
  private static final Range<Integer> HEALTHY_STATUS_RANGE = Range.closed(200, 299);

  private final UriInfo uriInfo;
  private final ServiceMetadata serviceMetadata;
  private final Map<String, HealthDependency> healthDependencies;

  @Inject
  HealthChecker(
      UriInfo uriInfo,
      ServiceMetadata serviceMetadata,
      Map<String, HealthDependency> healthDependencies) {
    this.uriInfo = uriInfo;
    this.serviceMetadata = serviceMetadata;
    this.healthDependencies = ImmutableSortedMap.copyOf(healthDependencies);
  }

  /**
   * Performs the Primary Health Check.
   *
   * <p>The resulting DTO will contain information about the health of all the main dependencies
   * (currently, all dependencies) as well as an overall status of either 200 (if all dependencies
   * are healthy) or 503 (if one or more dependencies are unhealthy) and corresponding message.
   * Other metadata fields on the HealthDto will also be populated.
   */
  public HealthDto doPrimaryHealthCheck() {
    List<HealthDependency> primaryHealthDependencies = healthDependencies.values().stream()
        .filter(HealthDependency::isPrimary)
        .collect(Collectors.toList());
    return checkHealth(primaryHealthDependencies);
  }

  /**
   * Performs the Diagnostic Health Check.
   *
   * <p>Currently the same as the Primary Health Check.
   */
  public HealthDto doDiagnosticHealthCheck() {
    return checkHealth(healthDependencies.values());
  }

  /**
   * Returns a list of all health dependencies, but does not check their health.
   */
  public List<HealthDependencyDto> doDependencyListing() {
    List<HealthDependencyDto> listing = Lists.newArrayList();
    for (HealthDependency dependency : healthDependencies.values()) {
      listing.add(dependencyDtoBuilder(dependency).build());
    }
    return listing;
  }

  /**
   * Returns information about a dependency, including the result of checking its health.
   */
  public HealthDependencyDto doDependencyAvailabilityCheck(String name) {
    HealthDependency dependency = healthDependencies.get(checkNotNull(name));
    if (dependency == null) {
      throw new WebApplicationException(Response.status(404).build());
    }
    return checkDependencyHealth(dependency);
  }

  private HealthDto checkHealth(Iterable<HealthDependency> dependencies) {
    int status = 200;
    HealthDto.Builder builder = HealthDto.builder(serviceMetadata);
    List<HealthDependencyDto> dependencyDtos = Lists.newArrayList();

    // TODO - this might be worth parallelizing in the future
    for (HealthDependency dependency : dependencies) {
      HealthDependencyDto dependencyDto = checkDependencyHealth(dependency);

      if (!dependencyDto.isHealthy()) {
        status = 503;
      }

      dependencyDtos.add(dependencyDto);
    }

    String message = serviceMetadata.getBuildInfo().getArtifactId() + " is available";

    if (status != 200) {
      message = serviceMetadata.getBuildInfo().getArtifactId() + " is unavailable";
    }

    builder.setDependencies(dependencyDtos)
        .setMessage(message)
        .setStatus(status);

    return builder.build();
  }

  private HealthDependencyDto checkDependencyHealth(HealthDependency dependency) {
    HealthDependencyDto.Builder builder = dependencyDtoBuilder(dependency);

    try {
      HealthStatus status = dependency.checkAvailability();

      status.getException().ifPresent(
          e -> LOGGER.error("Health dependency {} returned an exception", dependency.getName(), e));

      builder.setMessage(messageFromStatus(status));
      builder.setHealthy(isHealthy(status));
    } catch (Exception e) {
      LOGGER.error("Health dependency {} threw  an exception", dependency.getName(), e);
      builder.setHealthy(false)
          .setMessage(Throwables.getStackTraceAsString(e));
    }

    return builder.build();
  }

  private String messageFromStatus(HealthStatus status) {
    String message = status.getMessage();

    return status.getException()
        .map(e -> message + ' ' + Throwables.getStackTraceAsString(e))
        .orElse(message);
  }

  private boolean isHealthy(HealthStatus status) {
    return HEALTHY_STATUS_RANGE.contains(status.getStatus());
  }

  private HealthDependencyDto.Builder dependencyDtoBuilder(HealthDependency dependency) {
    HealthDependencyDto.Builder dto = HealthDependencyDto.builder()
        .setPrimary(dependency.isPrimary())
        .setId(dependency.getName())
        .setLinks(LinksDto.builder()
            .setSelf(
                uriInfo.getBaseUriBuilder()
                    .path(DependenciesResource.class)
                    .path(dependency.getName())
                    .build()
                    .toString())
            .build());

    dependency.getDescription().ifPresent(dto::setName);
    return dto;
  }
}
