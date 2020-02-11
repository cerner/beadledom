package com.cerner.beadledom.health.resource;

import com.cerner.beadledom.health.api.DiagnosticResource;
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
 *  Diagnostic health check resource.
 */
public class DiagnosticResourceImpl implements DiagnosticResource {
  private final HealthChecker checker;
  private final MustacheFactory mustacheFactory;

  @Inject
  DiagnosticResourceImpl(
      HealthChecker checker, @HealthTemplateFactory MustacheFactory mustacheFactory) {
    this.checker = checker;
    this.mustacheFactory = mustacheFactory;
  }

  @Override
  public Response getDiagnosticHealthCheckHtml() {
    HealthDto dto = checker.doDiagnosticHealthCheck();
    Mustache mustache = mustacheFactory.compile("diagnostic_health.mustache");
    return Response.status(dto.getStatus())
        .entity(StreamingWriterOutput
            .with(writer -> mustache.execute(writer, new HealthPresenter(dto))))
        .build();
  }

  @Override
  public Response getDiagnosticHealthCheck() {
    HealthDto dto = checker.doDiagnosticHealthCheck();
    return Response.status(dto.getStatus()).entity(dto).build();
  }
}
