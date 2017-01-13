package com.cerner.beadledom.avro;

import static com.google.common.base.Preconditions.checkState;

import com.cerner.beadledom.metadata.BuildInfo;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.apache.avro.specific.SpecificRecordBase;

/**
 * A Jackson {@link Module} that provides serialization and deserialization of Avro objects
 * extending from {@link SpecificRecordBase}.
 *
 * <p>To use this module, register it with the {@link com.fasterxml.jackson.databind.ObjectMapper}.
 * An instance of {@link BuildInfo} is required to instantiate a {@code AvroJacksonModule}.
 */
public class AvroJacksonModule extends Module {
  private final Version version;

  @Inject
  AvroJacksonModule(BuildInfo buildInfo) {
    Matcher versionMatcher = Pattern.compile("\\A(\\d+)\\.(\\d+)(\\.(\\d+))?.*").matcher(buildInfo.getVersion());
    checkState(versionMatcher.matches(), "artifact version could not be parsed: " + buildInfo.getVersion());
    int majorVersion = Integer.parseInt(versionMatcher.group(1));
    int minorVersion = Integer.parseInt(versionMatcher.group(2));
    int fixVersion = versionMatcher.group(4) == null ? 0 : Integer.parseInt(versionMatcher.group(4));

    String[] versionSplit = buildInfo.getVersion().split("-", 2);
    String snapshotInfo = versionSplit.length > 1 ? versionSplit[1] : null;

    this.version = new Version(majorVersion, minorVersion, fixVersion, snapshotInfo, buildInfo.getGroupId(), buildInfo.getArtifactId());
  }

  @Override
  public Version version() {
    return version;
  }

  @Override
  public String getModuleName() {
    return getClass().getName();
  }

  @Override
  public void setupModule(SetupContext context) {
    // Serialization is easy - this mixin disables serialization of the Schema field,
    // and everything else Just Works.
    context.setMixInAnnotations(SpecificRecordBase.class, AvroMappingMixin.class);
    // Deserialization is harder. Registering a custom Deserializers instance allows us to manually
    // construct a JsonDeserializer each time the ObjectMapper encounters a new type, so we can
    // detect SpecificRecordBase subclasses and handle them specially.
    context.addDeserializers(new AvroJacksonDeserializers());
  }
}
