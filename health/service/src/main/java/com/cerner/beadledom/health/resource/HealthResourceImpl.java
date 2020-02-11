package com.cerner.beadledom.health.resource;

import com.cerner.beadledom.health.api.HealthResource;
import com.cerner.beadledom.health.dto.HealthDto;
import com.cerner.beadledom.health.internal.HealthChecker;
import com.cerner.beadledom.health.internal.HealthTemplateFactory;
import com.cerner.beadledom.health.internal.presenter.HealthPresenter;
import com.cerner.beadledom.jaxrs.StreamingWriterOutput;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

/**
 *  Primary health check resource.
 */
public class HealthResourceImpl implements HealthResource {
  private final HealthChecker checker;
  private final MustacheFactory mustacheFactory;

  @Inject
  HealthResourceImpl(
      HealthChecker checker, @HealthTemplateFactory MustacheFactory mustacheFactory) {
    this.checker = checker;
    this.mustacheFactory = mustacheFactory;
  }

  @Override
  public Response getPrimaryHealthCheckHtml() {
    HealthDto dto = checker.doPrimaryHealthCheck();
    Mustache mustache = mustacheFactory.compile("primary_health.mustache");
    return Response.status(dto.getStatus())
        .entity(
            StreamingWriterOutput.with(writer -> mustache.execute(writer, new HealthPresenter(dto))))
        .build();
  }

  @Override
  public Response getPrimaryHealthCheck() {
    HealthDto dto = checker.doPrimaryHealthCheck();
    return Response.status(dto.getStatus()).entity(dto).build();
  }
}
