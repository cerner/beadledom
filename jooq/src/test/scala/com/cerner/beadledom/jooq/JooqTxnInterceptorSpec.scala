package com.cerner.beadledom.jooq

import org.aopalliance.intercept.MethodInvocation
import org.jooq.impl.{DSL, DefaultConfiguration, ThreadLocalTransactionProvider}
import org.jooq.tools.jdbc._
import org.jooq.{ConnectionProvider, ContextTransactionalCallable, DSLContext}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{spy, times, verify, when}
import org.scalatest.BeforeAndAfter
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
 * Spec for JooqTxnInterceptor.
 *
 * @author John Leacox
 */
class JooqTxnInterceptorSpec
    extends AnyFunSpec with MockitoSugar with BeforeAndAfter with Matchers {
  describe("JooqTxnInterceptor") {
    describe("#invoke") {
      it("does not end the unit of work if it did not start it") {
        val unitOfWork = mock[UnitOfWork]
        when(unitOfWork.isActive).thenReturn(true)

        val dslContext = mock[DSLContext]
        val provider = mock[DSLContextProvider]
        when(provider.get()).thenReturn(dslContext)

        val interceptor = new JooqTxnInterceptor
        interceptor.init(unitOfWork, provider)

        val method = mock[MethodInvocation]

        interceptor.invoke(method)

        verify(unitOfWork, times(0)).end()
      }

      it("ends the unit of work if it starts it") {
        val unitOfWork = mock[UnitOfWork]
        when(unitOfWork.isActive).thenReturn(false)

        val dslContext = mock[DSLContext]
        val provider = mock[DSLContextProvider]
        when(provider.get()).thenReturn(dslContext)

        val interceptor = new JooqTxnInterceptor
        interceptor.init(unitOfWork, provider)

        val method = mock[MethodInvocation]

        interceptor.invoke(method)

        verify(unitOfWork, times(1)).begin()
        verify(unitOfWork, times(1)).end()
      }

      it("ends the unit of work if it starts it when the transaction throws an exception") {
        val unitOfWork = mock[UnitOfWork]
        when(unitOfWork.isActive).thenReturn(false)

        val dslContext = DSL.using(new MockConnection(new MockDataProvider {
          override def execute(ctx: MockExecuteContext): Array[MockResult] = Array.empty
        }))
        val provider = mock[DSLContextProvider]
        when(provider.get()).thenReturn(dslContext)

        val interceptor = new JooqTxnInterceptor
        interceptor.init(unitOfWork, provider)

        val method = mock[MethodInvocation]
        when(method.proceed()).thenThrow(classOf[RuntimeException])

        intercept[RuntimeException] {
          interceptor.invoke(method)
        }

        verify(unitOfWork, times(1)).end()
      }

      it("throws the original exception if transaction and unit of work both throw exceptions") {
        val unitOfWork = mock[UnitOfWork]
        when(unitOfWork.isActive).thenReturn(false)
        when(unitOfWork.end()).thenThrow(classOf[IllegalStateException])

        val dslContext = DSL.using(new MockConnection(new EmptyMockDataProvider))
        val provider = mock[DSLContextProvider]
        when(provider.get()).thenReturn(dslContext)

        val interceptor = new JooqTxnInterceptor
        interceptor.init(unitOfWork, provider)

        val method = mock[MethodInvocation]
        when(method.proceed()).thenThrow(classOf[RuntimeException])

        intercept[RuntimeException] {
          interceptor.invoke(method)
        }

        verify(unitOfWork, times(1)).end()
      }

      it("throws the unit of work exception if only unit of work throws an exception") {
        val unitOfWork = mock[UnitOfWork]
        when(unitOfWork.isActive).thenReturn(false)
        when(unitOfWork.end()).thenThrow(classOf[IllegalStateException])

        val dslContext = mock[DSLContext]
        val provider = mock[DSLContextProvider]
        when(provider.get()).thenReturn(dslContext)

        val interceptor = new JooqTxnInterceptor
        interceptor.init(unitOfWork, provider)

        val method = mock[MethodInvocation]

        intercept[IllegalStateException] {
          interceptor.invoke(method)
        }

        verify(unitOfWork, times(1)).end()
      }

      it("wraps the method invocation in a transaction") {
        val unitOfWork = mock[UnitOfWork]
        when(unitOfWork.isActive).thenReturn(false)

        val configuration = new DefaultConfiguration()

        val connectionProvider = mock[ConnectionProvider]
        val connection = new MockConnection(new MockDataProvider {
          override def execute(ctx: MockExecuteContext): Array[MockResult] = Array.empty
        })
        when(connectionProvider.acquire()).thenReturn(connection)

        val transactionProvider = new ThreadLocalTransactionProvider(connectionProvider)
        configuration.setTransactionProvider(transactionProvider)

        val dslContext = DSL.using(configuration)

        val dslContextSpy = spy(dslContext)
        val provider = mock[DSLContextProvider]
        when(provider.get()).thenReturn(dslContextSpy)

        val interceptor = new JooqTxnInterceptor
        interceptor.init(unitOfWork, provider)

        val method = mock[MethodInvocation]

        interceptor.invoke(method)

        verify(dslContextSpy, times(1)).transactionResult(any[ContextTransactionalCallable[Any]])
        verify(method, times(1)).proceed()
      }
    }
  }
}
