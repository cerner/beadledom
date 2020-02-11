package com.cerner.beadledom.health.resource;

import com.cerner.beadledom.health.api.DependenciesResource;
import com.cerner.beadledom.health.dto.HealthDependenciesDto;
import com.cerner.beadledom.health.dto.HealthDependencyDto;
import com.cerner.beadledom.health.internal.HealthChecker;
import com.cerner.beadledom.health.internal.HealthTemplateFactory;
import com.cerner.beadledom.health.internal.presenter.HealthDependenciesPresenter;
import com.cerner.beadledom.health.internal.presenter.HealthDependencyPresenter;
import com.cerner.beadledom.jaxrs.StreamingWriterOutput;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.inject.Inject;
import java.util.List;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

/**
 *  Dependencies health check resource.
 */
public class DependenciesResourceImpl implements DependenciesResource {
  private final HealthChecker checker;
  private final MustacheFactory mustacheFactory;

  @Inject
  DependenciesResourceImpl(
      HealthChecker checker, @HealthTemplateFactory MustacheFactory mustacheFactory) {
    this.checker = checker;
    this.mustacheFactory = mustacheFactory;
  }

  @Override
  public StreamingOutput getDependencyListingHtml() {
    HealthDependenciesDto dto = HealthDependenciesDto.builder()
        .setDependencies(checker.doDependencyListing())
        .build();
    Mustache mustache = mustacheFactory.compile("dependency_listing.mustache");
    return StreamingWriterOutput
        .with(writer -> mustache.execute(writer, new HealthDependenciesPresenter(dto)));
  }

  @Override
  public List<HealthDependencyDto> getDependencyListing() {
    return checker.doDependencyListing();
  }

  @Override
  public Response getDependencyAvailabilityCheckHtml(String name) {
    HealthDependencyDto dto = checker.doDependencyAvailabilityCheck(name);
    Mustache mustache = mustacheFactory.compile("dependency_availability.mustache");
    Integer status = returnStatus(dto);
    return Response.status(status)
        .entity(StreamingWriterOutput
            .with(writer -> mustache.execute(writer, new HealthDependencyPresenter(dto))))
        .build();
  }

  @Override
  public Response getDependencyAvailabilityCheck(String name) {
    HealthDependencyDto dto = checker.doDependencyAvailabilityCheck(name);
    int status = returnStatus(dto);
    return Response.status(status).entity(dto).build();
  }

  private int returnStatus(HealthDependencyDto dto) {
    return dto.isHealthy() ? 200 : 503;
  }
}
