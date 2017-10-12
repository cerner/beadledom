package com.cerner.beadledom.jooq;

import javax.inject.Provider;
import org.jooq.DSLContext;

/**
 * A javax.inject provider for a {@link DSLContext}.
 *
 * @author John Leacox
 * @since 2.7
 */
public interface DSLContextProvider extends Provider<DSLContext> {
}
