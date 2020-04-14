package com.cerner.beadledom.correlation;

import static org.junit.jupiter.api.Assertions.*;

import com.cerner.beadledom.correlation.CorrelationIdContext.CorrelationIdCloseable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CorrelationIdContextImplTest {

  CorrelationIdContext correlationIdContext = new CorrelationIdContextImpl();
  @Nested
  @DisplayName("#get")
  class Get {
    @AfterEach
    void afterEach() {
      correlationIdContext.reset();
    }

    @DisplayName("with a correlation id set")
    @Test
    void getSet() {
      correlationIdContext.set("abc");
      assertEquals("abc", correlationIdContext.get());
    }

    @DisplayName("without a correlation id set")
    @Test
    void getNotSet() {
      assertNull(correlationIdContext.get());
    }
  }

  @Nested
  @DisplayName("#set")
  class Set {

    @AfterEach
    void afterEach() {
      correlationIdContext.reset();
    }

    @DisplayName("when a correlation id is passed in")
    @Test
    void withCorrelationId() {
      correlationIdContext.set("a");
      assertEquals("a", correlationIdContext.get());
    }

    @DisplayName("when a correlation id is set to null")
    @Test
    void withoutCorrelationId() {
      correlationIdContext.set(null);
      assertNotNull(correlationIdContext.get());
    }

  }

  @Nested
  @DisplayName("#reset")
  class Reset {

    @AfterEach
    void afterEach() {
      correlationIdContext.reset();
    }

    @DisplayName("resets the correlation id to null when correlation id is set")
    @Test
    void reset() {
      correlationIdContext.set("a");
      correlationIdContext.reset();
      assertNull(correlationIdContext.get());
    }

    @DisplayName("resets the correlation id to null when correlation id is not set")
    @Test
    void withoutCorrelationId() {
      correlationIdContext.set(null);
      correlationIdContext.reset();
      assertNull(correlationIdContext.get());
    }

  }

  @Nested
  @DisplayName("#setCloseable")
  class SetCloseable {

    @AfterEach
    void afterEach() {
      correlationIdContext.reset();
    }

    @DisplayName("uses correlation id when passed to setCloseable")
    @Test
    void withCorrelationId() {
      try (CorrelationIdCloseable correlationIdCloseable = correlationIdContext.setCloseable("abc")) {
        assertEquals("abc", correlationIdContext.get());
      }
    }

    @DisplayName("generates correlation id when not passed to setCloseable")
    @Test
    void withoutCorrelationId() {
      try (CorrelationIdCloseable correlationIdCloseable = correlationIdContext.setCloseable(null)) {
        assertNotNull(correlationIdContext.get());
      }
    }

    @DisplayName("clears correlation id after try block")
    @Test
    void clearsCorrelationIdAfterTryBlock() {
      try (CorrelationIdCloseable correlationIdCloseable = correlationIdContext.setCloseable("abc")) {
        assertEquals("abc", correlationIdContext.get());
      }
      assertNull(correlationIdContext.get());
    }
  }
}
