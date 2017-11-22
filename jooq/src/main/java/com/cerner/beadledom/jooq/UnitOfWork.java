package com.cerner.beadledom.jooq;

import org.jooq.DSLContext;

/**
 * This interface represents a unit of work using a Jooq {@link DSLContext}.
 *
 * <p>The scope of a unit of work (begin/end) corresponds to the scope of the Jooq
 * {@link DSLContext}. The scope is also contained to a single thread. Be sure to always call
 * end() in a finally block to avoid leaking contexts across threads.
 *
 * @author John Leacox
 * @since 2.7
 */
public interface UnitOfWork {
  /**
   * Begins a unit of work. This initializes a new Jooq {@link DSLContext}.
   */
  void begin();

  /**
   * Ends the unit of work. This closes the Jooq {@link DSLContext}.
   */
  void end();

  /**
   * Returns true if the unit of work is active; false otherwise.
   */
  boolean isActive();
}
