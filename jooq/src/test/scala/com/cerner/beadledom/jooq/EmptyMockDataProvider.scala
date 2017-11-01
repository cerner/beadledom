package com.cerner.beadledom.jooq

import org.jooq.tools.jdbc.{MockDataProvider, MockExecuteContext, MockResult}

/**
 * A [MockDataProvider] that returns an empty array of results.
 *
 * @author John Leacox
 */
class EmptyMockDataProvider extends MockDataProvider {
  override def execute(ctx: MockExecuteContext): Array[MockResult] = Array.empty
}
