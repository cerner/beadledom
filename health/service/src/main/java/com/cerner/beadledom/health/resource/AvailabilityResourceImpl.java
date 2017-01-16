package com.cerner.beadledom.health.resource;

import com.cerner.beadledom.health.api.AvailabilityResource;
import com.cerner.beadledom.health.dto.HealthDto;
import com.cerner.beadledom.health.internal.HealthTemplateFactory;
import com.cerner.beadledom.jaxrs.StreamingWriterOutput;
import com.cerner.beadledom.metadata.ServiceMetadata;
import com.github.mustachejava.MustacheFactory;

import javax.inject.Inject;
import javax.ws.rs.core.StreamingOutput;

/**
 *  Availability health check resource.
 */
public class AvailabilityResourceImpl implements AvailabilityResource {
  private final HealthDto healthDto;
  private final StreamingOutput healthHtmlOutput;

  @Inject
  AvailabilityResourceImpl(
      ServiceMetadata serviceMetadata,
      @HealthTemplateFactory MustacheFactory mustacheFactory) {
    healthDto = HealthDto.builder()
        .setMessage(serviceMetadata.getBuildInfo().getArtifactId() + " is available")
        .setStatus(200)
        .build();

    healthHtmlOutput = StreamingWriterOutput
        .with(writer -> mustacheFactory.compile("basic_availability.mustache")
            .execute(writer, healthDto));
  }

  @Override
  public StreamingOutput getBasicAvailabilityCheckHtml() {
    return healthHtmlOutput;
  }

  @Override
  public HealthDto getBasicAvailabilityCheck() {
    return healthDto;
  }
}
