package com.cerner.beadledom.metadata;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.auto.value.AutoValue;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates metadata about a built artifact.
 *
 * <p>Generally, you would create this by loading a properties file created by Maven resource
 * filtering. If you create the following file in
 * src/main/resources/com/cerner/mypackage/build-info.properties
 *
 * <p>Required properties:
 * <pre>
 * git.commit.id=${git.commit.id}
 * project.artifactId=${project.artifactId}
 * project.groupId=${project.groupId}
 * project.version=${project.version}
 * </pre>
 *
 * <p>Optional properties:
 * <pre>
 * project.build.date=${mvn.build.timestamp}
 * </pre>
 *
 * <p>If using project.build.date property in the build-info.properties file create the timestamp
 * property in the pom.xml.
 *
 * <pre>{@code
 * <properties>
 *   <mvn.build.timestamp>${maven.build.timestamp}</mvn.build.timestamp>
 * </properties>
 * }</pre>
 *
 * <p>Maven does not let you pass the maven.build.timestamp property that's why this work around
 * is needed. If curious about the reason Ref: https://issues.apache.org/jira/browse/MRESOURCES-99.
 *
 * <p>And ensure resource filtering and the git-commit-id-plugin are enabled in your pom:
 *
 * <pre>
 *   &lt;build&gt;
 *       &lt;resources&gt;
 *           &lt;resource&gt;
 *               &lt;directory&gt;src/main/resources&lt;/directory&gt;
 *               &lt;filtering&gt;true&lt;/filtering&gt;
 *           &lt;/resource&gt;
 *       &lt;/resources&gt;
 *   &lt;/build&gt;
 *
 *   &lt;plugins&gt;
 *       &lt;plugin&gt;
 *           &lt;groupId&gt;pl.project13.maven&lt;/groupId&gt;
 *           &lt;artifactId&gt;git-commit-id-plugin&lt;/artifactId&gt;
 *       &lt;/plugin&gt;
 *   &lt;/plugins&gt;
 * </pre>
 *
 * <p>You could then create an instance by using the following code within
 * src/main/java/com/cerner/mypackage/SomeClass.java
 *
 * <pre>
 *     {@code
 *     BuildInfo.load(getClass().getResourceAsStream("build-info.properties"));
 *     }
 * </pre>
 */
@AutoValue
public abstract class BuildInfo {
  private static final Logger LOGGER = LoggerFactory.getLogger(BuildInfo.class);

  public static Builder builder() {
    return new AutoValue_BuildInfo.Builder().setBuildDateTime(Optional.empty());
  }

  public static Builder builder(BuildInfo buildInfo) {
    return buildInfo.toBuilder();
  }

  /**
   * Create an instance using the given properties.
   *
   * <p>Currently, at least the following properties are required:
   * <ul>
   *     <li>git.commit.id</li>
   *     <li>project.artifactId</li>
   *     <li>project.groupId</li>
   *     <li>project.version</li>
   * </ul>
   *
   * <p>Optional properties:
   * <ul>
   *     <li>project.build.date</li>
   * </ul>
   */
  public static BuildInfo create(Properties properties) {
    checkNotNull(properties, "properties: null");
    return builder()
        .setArtifactId(
            checkNotNull(properties.getProperty("project.artifactId"), "project.artifactId: null"))
        .setGroupId(
            checkNotNull(properties.getProperty("project.groupId"), "project.groupId: null"))
        .setRawProperties(properties)
        .setScmRevision(
            checkNotNull(properties.getProperty("git.commit.id"), "git.commit.id: null"))
        .setVersion(
            checkNotNull(properties.getProperty("project.version"), "project.version: null"))
        .setBuildDateTime(Optional.ofNullable(properties.getProperty("project.build.date")))
        .build();
  }

  /**
   * Creates an instance using properties from the given stream (which must be in property file
   * format).
   *
   * <p>See {@link #create(Properties)} for a list of required properties.
   *
   * <p>For convenience, this method throws a runtime exception if an IOException occurs. If you
   * plan to do anything other than crash in the event of an error, you should use
   * {@link #create(Properties)} instead.
   */
  public static BuildInfo load(InputStream propertiesStream) {
    Properties properties = new Properties();
    try {
      properties.load(propertiesStream);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to load build info properties", e);
    } finally {
      try {
        propertiesStream.close();
      } catch (IOException e) {
        LOGGER.warn("Unable to close properties stream", e);
      }
    }

    return create(properties);
  }

  /**
   * Returns the artifact Id.
   */
  public abstract String getArtifactId();

  /**
   * Returns the group Id for the artifact.
   */
  public abstract String getGroupId();

  /**
   * Returns all build properties that were passed in when this object was created.
   */
  public abstract Properties getRawProperties();

  /**
   * Returns the SCM commit revision at which the artifact was built.
   */
  public abstract String getScmRevision();

  /**
   * Returns the version of the artifact.
   */
  public abstract String getVersion();

  /**
   * Returns the the build date/time.
   */
  public abstract Optional<String> getBuildDateTime();

  /**
   * Returns a builder with same property values as this; allowing modification of some values.
   */
  public abstract Builder toBuilder();

  @AutoValue.Builder
  public abstract static class Builder {
    /**
     * Sets the artifact Id.
     */
    public abstract Builder setArtifactId(String artifactId);

    /**
     * Sets the group Id for the artifact.
     */
    public abstract Builder setGroupId(String groupId);

    /**
     * Sets all build properties that were passed in when this object was created.
     */
    public abstract Builder setRawProperties(Properties properties);

    /**
     * Sets the SCM commit revision at which the artifact was built.
     */
    public abstract Builder setScmRevision(String scmRevision);

    /**
     * Sets the version of the artifact.
     */
    public abstract Builder setVersion(String version);

    /**
     * Sets the build time of the artifact.
     */
    public abstract Builder setBuildDateTime(Optional<String> buildDateTime);

    public Builder setBuildDateTime(String buildDateTime) {
      return setBuildDateTime(Optional.ofNullable(buildDateTime));
    }

    public abstract BuildInfo build();
  }
}
