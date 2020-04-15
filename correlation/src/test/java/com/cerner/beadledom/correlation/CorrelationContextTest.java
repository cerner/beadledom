package com.cerner.beadledom.correlation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CorrelationContextTest {

  CorrelationContext correlationContext = CorrelationContext.create();

  @AfterEach
  void afterEach() {
    correlationContext.clearId();
  }

  @Nested
  @DisplayName("#getId")
  class Get {

    @DisplayName("with a correlation id set")
    @Test
    void getSet() {
      correlationContext.setId("abc");
      assertEquals("abc", correlationContext.getId());
    }

    @DisplayName("without a correlation id set")
    @Test
    void getNotSet() {
      assertNull(correlationContext.getId());
    }
  }

  @Nested
  @DisplayName("#setId")
  class Set {

    @DisplayName("when a correlation id is passed in")
    @Test
    void withCorrelationId() {
      correlationContext.setId("a");
      assertEquals("a", correlationContext.getId());
    }

    @DisplayName("when a correlation id is set to null")
    @Test
    void withoutCorrelationId() {
      correlationContext.setId(null);
      assertNotNull(correlationContext.getId());
    }

  }

  @Nested
  @DisplayName("#clearId")
  class Clear {

    @DisplayName("sets the correlation id to null when correlation id is set")
    @Test
    void reset() {
      correlationContext.setId("a");
      correlationContext.clearId();
      assertNull(correlationContext.getId());
    }

    @DisplayName("sets the correlation id to null when correlation id is not set")
    @Test
    void withoutCorrelationId() {
      correlationContext.setId(null);
      correlationContext.clearId();
      assertNull(correlationContext.getId());
    }

  }

  @Nested
  @DisplayName("#withId")
  class WithId {

    @DisplayName("uses correlation id when passed to withId")
    @Test
    void withCorrelationId() throws Exception {
      correlationContext.withId("abc", () -> {
        assertEquals("abc", correlationContext.getId());
        return null;
      });
    }

    @DisplayName("generates correlation id when not passed to withId")
    @Test
    void withoutCorrelationId() throws Exception {
      correlationContext.withId(null, () -> {
        assertNotNull(correlationContext.getId());
        return null;
      });
    }

    @DisplayName("clears correlation id after the block")
    @Test
    void clearsCorrelationIdAfterTryBlock() throws Exception {
      correlationContext.withId("abc", () -> {
        assertEquals("abc", correlationContext.getId());
        return null;
      });
      assertNull(correlationContext.getId());
    }
  }
}
