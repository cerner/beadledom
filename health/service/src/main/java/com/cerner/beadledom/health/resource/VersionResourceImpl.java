package com.cerner.beadledom.health.resource;

import com.cerner.beadledom.health.api.VersionResource;
import com.cerner.beadledom.health.dto.BuildDto;
import com.cerner.beadledom.health.internal.HealthTemplateFactory;
import com.cerner.beadledom.health.internal.presenter.BuildPresenter;
import com.cerner.beadledom.jaxrs.StreamingWriterOutput;
import com.cerner.beadledom.metadata.BuildInfo;
import com.cerner.beadledom.metadata.ServiceMetadata;

import com.github.mustachejava.MustacheFactory;
import com.google.inject.Inject;

import javax.ws.rs.core.StreamingOutput;

/**
 *  Version information Resource.
 *
 *  @since 1.4
 */
public class VersionResourceImpl implements VersionResource {
  private final StreamingOutput healthHtmlOutput;
  private final BuildDto buildDto;

  @Inject
  VersionResourceImpl(
      ServiceMetadata serviceMetadata,
      @HealthTemplateFactory MustacheFactory mustacheFactory) {

    BuildInfo buildInfo = serviceMetadata.getBuildInfo();

    BuildDto.Builder buildInfoBuilder = BuildDto
        .builder()
        .setArtifactName(buildInfo.getArtifactId())
        .setVersion(buildInfo.getVersion());

    buildInfo.getBuildDateTime().ifPresent(date -> buildInfoBuilder.setBuildDateTime(date));

    buildDto = buildInfoBuilder.build();

    healthHtmlOutput = StreamingWriterOutput
        .with(writer -> mustacheFactory
            .compile("version.mustache")
            .execute(writer, new BuildPresenter(buildDto)));
  }

  @Override
  public StreamingOutput getVersionInfoHtml() {
    return healthHtmlOutput;
  }

  @Override
  public BuildDto getVersionInfo() {
    return buildDto;
  }
}
