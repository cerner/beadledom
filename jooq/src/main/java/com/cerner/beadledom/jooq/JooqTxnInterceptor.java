package com.cerner.beadledom.jooq;

import javax.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * A method interceptor for the {@link JooqTransactional} annotation.
 *
 * <p>This will execute methods annotated with {@link JooqTransactional} within a Jooq transaction.
 *
 * @author John Leacox
 * @since 2.7
 */
class JooqTxnInterceptor implements MethodInterceptor {
  private UnitOfWork unitOfWork;
  private DSLContextProvider dslContextProvider;

  @Inject
  void init(UnitOfWork unitOfWork, DSLContextProvider dslContextProvider) {
    this.unitOfWork = unitOfWork;
    this.dslContextProvider = dslContextProvider;
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    return invokeInTransactionAndUnitOfWork(invocation);
  }

  /**
   * Invokes the method wrapped within a unit of work and a transaction.
   *
   * @param methodInvocation the method to be invoked within a transaction
   * @return the result of the call to the invoked method
   * @throws Throwable if an exception occurs during the method invocation, transaction, or unit of
   *     work
   */
  private Object invokeInTransactionAndUnitOfWork(MethodInvocation methodInvocation)
      throws Throwable {
    boolean unitOfWorkAlreadyStarted = unitOfWork.isActive();
    if (!unitOfWorkAlreadyStarted) {
      unitOfWork.begin();
    }

    Throwable originalThrowable = null;
    try {
      return dslContextProvider.get().transactionResult(() -> {
        try {
          return methodInvocation.proceed();
        } catch (Throwable e) {
          if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
          }

          throw new RuntimeException(e);
        }
      });
    } catch (Throwable t) {
      originalThrowable = t;
      throw t;
    } finally {
      if (!unitOfWorkAlreadyStarted) {
        endUnitOfWork(originalThrowable);
      }
    }
  }

  /**
   * Ends the unit of work.
   *
   * <p>If originalThrowable is present, and an exception is thrown, then the originalThrowable
   * will be preferred and thrown instead; if originalThrowable is not present, and an exception is
   * thrown, then the unitOfWork exception will be thrown.
   *
   * @param originalThrowable the original exception that was thrown and should be propagated
   * @throws Throwable if an exception occurred while ending the unit of work
   */
  private void endUnitOfWork(Throwable originalThrowable) throws Throwable {
    try {
      unitOfWork.end();
    } catch (Throwable t) {
      if (originalThrowable != null) {
        throw originalThrowable;
      } else {
        throw t;
      }
    }
  }
}
