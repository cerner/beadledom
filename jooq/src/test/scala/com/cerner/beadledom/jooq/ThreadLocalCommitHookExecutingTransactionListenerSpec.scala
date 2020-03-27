package com.cerner.beadledom.jooq

import org.jooq.TransactionContext
import org.mockito.Mockito.reset
import org.scalatest.BeforeAndAfter
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
 * Spec for ThreadLocalCommitHookExecutingTransactionListener.
 *
 * @author John Leacox
 */
class ThreadLocalCommitHookExecutingTransactionListenerSpec
    extends AnyFunSpec with MockitoSugar with BeforeAndAfter with Matchers {
  val context = mock[TransactionContext]

  val transactionalHooks = new ThreadLocalJooqTransactionalHooks

  var actionExecutionOrderIndex = 0

  var rootActionOneExecutionIndex = -1
  val rootActionOne = new Runnable {
    override def run(): Unit = {
      rootActionOneExecutionIndex = actionExecutionOrderIndex
      actionExecutionOrderIndex += 1
    }
  }

  var nestedLevelOneActionOneExecutionIndex = -1
  val nestedLevelOneActionOne = new Runnable {
    override def run(): Unit = {
      nestedLevelOneActionOneExecutionIndex = actionExecutionOrderIndex
      actionExecutionOrderIndex += 1
    }
  }

  var nestedLevelOneActionTwoExecutionIndex = -1
  val nestedLevelOneActionTwo = new Runnable {
    override def run(): Unit = {
      nestedLevelOneActionTwoExecutionIndex = actionExecutionOrderIndex
      actionExecutionOrderIndex += 1
    }
  }

  var nestedLevelTwoActionOneExecutionIndex = -1
  val nestedLevelTwoActionOne = new Runnable {
    override def run(): Unit = {
      nestedLevelTwoActionOneExecutionIndex = actionExecutionOrderIndex
      actionExecutionOrderIndex += 1
    }
  }

  before {
    reset(context)

    transactionalHooks.clearTransaction()

    actionExecutionOrderIndex = 0
    rootActionOneExecutionIndex = -1
    nestedLevelOneActionOneExecutionIndex = -1
    nestedLevelOneActionTwoExecutionIndex = -1
    nestedLevelTwoActionOneExecutionIndex = -1
  }

  describe("ThreadLocalCommitHookExecutingTransactionListener") {
    describe("when a transaction is committed") {
      describe("when it is the root transaction") {
        it("executes commit hooks in the order registered") {
          val listener = new ThreadLocalCommitHookExecutingTransactionListener(transactionalHooks)

          listener.beginStart(context)
          transactionalHooks.whenCommitted(rootActionOne)
          listener.beginStart(context)
          transactionalHooks.whenCommitted(nestedLevelOneActionOne)

          listener.beginStart(context)
          transactionalHooks.whenCommitted(nestedLevelTwoActionOne)
          listener.commitEnd(context)

          transactionalHooks.whenCommitted(nestedLevelOneActionTwo)

          listener.commitEnd(context)

          rootActionOneExecutionIndex mustBe -1
          nestedLevelOneActionOneExecutionIndex mustBe -1
          nestedLevelOneActionTwoExecutionIndex mustBe -1
          nestedLevelTwoActionOneExecutionIndex mustBe -1

          listener.commitEnd(context)

          rootActionOneExecutionIndex mustBe 0
          nestedLevelOneActionOneExecutionIndex mustBe 1
          nestedLevelOneActionTwoExecutionIndex mustBe 3
          nestedLevelTwoActionOneExecutionIndex mustBe 2
        }

        describe("after a nested connection is rolled back") {
          it("only executes commit hooks for the non-rolled back connections") {
            val listener = new ThreadLocalCommitHookExecutingTransactionListener(transactionalHooks)

            listener.beginStart(context)
            transactionalHooks.whenCommitted(rootActionOne)
            listener.beginStart(context)
            transactionalHooks.whenCommitted(nestedLevelOneActionOne)
            transactionalHooks.whenCommitted(nestedLevelOneActionTwo)
            listener.beginStart(context)
            transactionalHooks.whenCommitted(nestedLevelTwoActionOne)

            listener.rollbackEnd(context)
            listener.commitEnd(context)

            rootActionOneExecutionIndex mustBe -1
            nestedLevelOneActionOneExecutionIndex mustBe -1
            nestedLevelOneActionTwoExecutionIndex mustBe -1
            nestedLevelTwoActionOneExecutionIndex mustBe -1

            listener.commitEnd(context)

            rootActionOneExecutionIndex mustBe 0
            nestedLevelOneActionOneExecutionIndex mustBe 1
            nestedLevelOneActionTwoExecutionIndex mustBe 2
            nestedLevelTwoActionOneExecutionIndex mustBe -1
          }
        }
      }

      describe("when it is a nested transaction") {
        it("does not execute commit hooks") {
          val listener = new ThreadLocalCommitHookExecutingTransactionListener(transactionalHooks)

          listener.beginStart(context)
          transactionalHooks.whenCommitted(rootActionOne)
          listener.beginStart(context)
          transactionalHooks.whenCommitted(nestedLevelOneActionOne)
          transactionalHooks.whenCommitted(nestedLevelOneActionTwo)
          listener.beginStart(context)
          transactionalHooks.whenCommitted(nestedLevelTwoActionOne)

          listener.commitEnd(context)
          listener.commitEnd(context)

          rootActionOneExecutionIndex mustBe -1
          nestedLevelOneActionOneExecutionIndex mustBe -1
          nestedLevelOneActionTwoExecutionIndex mustBe -1
          nestedLevelTwoActionOneExecutionIndex mustBe -1
        }
      }
    }

    describe("when a transaction is rolled back") {
      describe("when it is the root transaction") {
        it("does not execute commit hooks") {
          val listener = new ThreadLocalCommitHookExecutingTransactionListener(transactionalHooks)

          listener.beginStart(context)
          transactionalHooks.whenCommitted(rootActionOne)
          listener.beginStart(context)
          transactionalHooks.whenCommitted(nestedLevelOneActionOne)
          transactionalHooks.whenCommitted(nestedLevelOneActionTwo)
          listener.beginStart(context)
          transactionalHooks.whenCommitted(nestedLevelTwoActionOne)

          listener.rollbackEnd(context)
          listener.rollbackEnd(context)
          listener.rollbackEnd(context)

          rootActionOneExecutionIndex mustBe -1
          nestedLevelOneActionOneExecutionIndex mustBe -1
          nestedLevelOneActionTwoExecutionIndex mustBe -1
          nestedLevelTwoActionOneExecutionIndex mustBe -1
        }
      }
    }
  }
}
