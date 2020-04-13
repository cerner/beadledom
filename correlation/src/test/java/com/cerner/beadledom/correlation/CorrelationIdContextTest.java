package com.cerner.beadledom.correlation;

import static org.junit.jupiter.api.Assertions.*;

import com.cerner.beadledom.correlation.CorrelationIdContext.CorrelationIdCloseable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CorrelationIdContextTest {

  @Nested
  @DisplayName("#get")
  class Get {
    @AfterEach
    void afterEach() {
      CorrelationIdContext.reset();
    }

    @DisplayName("with a correlation id set")
    @Test
    void getSet() {
      CorrelationIdContext.set("abc");
      assertEquals("abc", CorrelationIdContext.get());
    }

    @DisplayName("without a correlation id set")
    @Test
    void getNotSet() {
      assertNull(CorrelationIdContext.get());
    }
  }

  @Nested
  @DisplayName("#set")
  class Set {

    @AfterEach
    void afterEach() {
      CorrelationIdContext.reset();
    }

    @DisplayName("when a correlation id is passed in")
    @Test
    void withCorrelationId() {
      CorrelationIdContext.set("a");
      assertEquals("a", CorrelationIdContext.get());
    }

    @DisplayName("when a correlation id is set to null")
    @Test
    void withoutCorrelationId() {
      CorrelationIdContext.set(null);
      assertNotNull(CorrelationIdContext.get());
    }

  }

  @Nested
  @DisplayName("#reset")
  class Reset {

    @AfterEach
    void afterEach() {
      CorrelationIdContext.reset();
    }

    @DisplayName("resets the correlation id to null when correlation id is set")
    @Test
    void reset() {
      CorrelationIdContext.set("a");
      CorrelationIdContext.reset();
      assertNull(CorrelationIdContext.get());
    }

    @DisplayName("resets the correlation id to null when correlation id is not set")
    @Test
    void withoutCorrelationId() {
      CorrelationIdContext.set(null);
      CorrelationIdContext.reset();
      assertNull(CorrelationIdContext.get());
    }

  }

  @Nested
  @DisplayName("#setCloseable")
  class SetCloseable {

    @AfterEach
    void afterEach() {
      CorrelationIdContext.reset();
    }

    @DisplayName("uses correlation id when passed to setCloseable")
    @Test
    void withCorrelationId() {
      try (CorrelationIdCloseable correlationIdCloseable = CorrelationIdContext.setCloseable("abc")) {
        assertEquals("abc", CorrelationIdContext.get());
      }
    }

    @DisplayName("generates correlation id when not passed to setCloseable")
    @Test
    void withoutCorrelationId() {
      try (CorrelationIdCloseable correlationIdCloseable = CorrelationIdContext.setCloseable(null)) {
        assertNotNull(CorrelationIdContext.get());
      }
    }

    @DisplayName("clears correlation id after try block")
    @Test
    void clearsCorrelationIdAfterTryBlock() {
      try (CorrelationIdCloseable correlationIdCloseable = CorrelationIdContext.setCloseable("abc")) {
        assertEquals("abc", CorrelationIdContext.get());
      }
      assertNull(CorrelationIdContext.get());
    }

  }

  @Test
  void setCloseable() {
  }

  @Test
  void resetCorrelationId() {
  }
}