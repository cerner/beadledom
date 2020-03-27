package com.cerner.beadledom.metadata

import java.util.Properties
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
 * Spec tests for {@link BuildInfo}.
 */
class BuildInfoSpec extends AnyFunSpec with Matchers {
  val properties = new Properties()
  properties.setProperty("git.commit.id", "abcdef")
  properties.setProperty("project.artifactId", "blub-factory")
  properties.setProperty("project.groupId", "com.boilerplate")
  properties.setProperty("project.version", "105.6.21-RC13")
  properties.setProperty("project.build.date", "2016-07-29T06:12:33-05:00")

  describe("BuildInfo") {
    describe(".create") {
      it("creates an instance correctly with build date time") {
        properties.setProperty("project.build.date", "2016-07-29T06:12:33-05:00")
        val info = BuildInfo.create(properties)
        info.getArtifactId should be("blub-factory")
        info.getGroupId should be("com.boilerplate")
        info.getRawProperties should be(properties)
        info.getScmRevision should be("abcdef")
        info.getVersion should be("105.6.21-RC13")
        info.getBuildDateTime.get() should be("2016-07-29T06:12:33-05:00")
      }

      it("creates an instance correctly with out build date time") {
        val info = BuildInfo.create(properties)
        info.getArtifactId should be("blub-factory")
        info.getGroupId should be("com.boilerplate")
        info.getRawProperties should be(properties)
        info.getScmRevision should be("abcdef")
        info.getVersion should be("105.6.21-RC13")
      }

      it("requires git commit ID") {
        val modified = properties.clone().asInstanceOf[Properties]
        modified.remove("git.commit.id")
        an[NullPointerException] should be thrownBy BuildInfo.create(modified)
      }

      it("requires artifact ID") {
        val modified = properties.clone().asInstanceOf[Properties]
        modified.remove("project.artifactId")
        an[NullPointerException] should be thrownBy BuildInfo.create(modified)
      }

      it("requires group ID") {
        val modified = properties.clone().asInstanceOf[Properties]
        modified.remove("project.groupId")
        an[NullPointerException] should be thrownBy BuildInfo.create(modified)
      }

      it("requires version") {
        val modified = properties.clone().asInstanceOf[Properties]
        modified.remove("project.version")
        an[NullPointerException] should be thrownBy BuildInfo.create(modified)
      }
    }
  }
}
