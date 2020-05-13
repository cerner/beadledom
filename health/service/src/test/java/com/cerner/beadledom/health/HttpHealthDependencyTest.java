package com.cerner.beadledom.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpHealthDependencyTest {

  private static MockWebServer mockWebServer = new MockWebServer();

  @BeforeAll
  static void setup() throws IOException {
    mockWebServer.start();
  }

  @AfterAll
  static void shutDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Nested
  @DisplayName("#checkAvailability")
  class CheckAvailability {

    @Nested
    class when_service_is_healthy {
      @Test
      void returns_success() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        HttpHealthDependency httpHealthDependency =
            new HttpHealthDependency(
                mockWebServer.url("/meta/availability").toString(), "testDependency", true);
        HealthStatus healthStatus = httpHealthDependency.checkAvailability();

        assertEquals(200, healthStatus.getStatus());
        assertEquals("testDependency is available.", healthStatus.getMessage());
      }
    }

    @Nested
    class when_service_is_unhealthy {
      @Test
      void returns_failure() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        HttpHealthDependency httpHealthDependency =
            new HttpHealthDependency(
                mockWebServer.url("/meta/availability").toString(), "testDependency", true);
        HealthStatus healthStatus = httpHealthDependency.checkAvailability();

        assertEquals(503, healthStatus.getStatus());
        assertEquals("testDependency is not available.", healthStatus.getMessage());
      }
    }
  }

  @Nested
  @DisplayName("#constructor")
  class ConstructorTest {

    @Nested
    class when_url_is_null {
      @Test
      void throws_NullPointerException() {
        assertThrows(
            NullPointerException.class,
            () -> new HttpHealthDependency(null, "testDependency", true));
      }
    }

    @Nested
    class when_name_is_null {
      @Test
      void throws_NullPointerException() {
        assertThrows(
            NullPointerException.class,
            () -> new HttpHealthDependency("url", null, true));
      }
    }

    @Nested
    class when_parameters_are_valid {
      @Test
      void initializes_successfully() {
        HttpHealthDependency healthDependency =
            new HttpHealthDependency("url", "testDependency", true);
        assertEquals("testDependency", healthDependency.getName());
      }
    }
  }

  @Nested
  @DisplayName("#getName")
  class GetName {
    @Test
    void returns_valid_name() {
      assertEquals(
          "testDependency", new HttpHealthDependency("url", "testDependency", true).getName());
    }
  }
}
