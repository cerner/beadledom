package com.cerner.beadledom.jooq

import org.jooq.TransactionContext
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FunSpec, MustMatchers}

/**
 * Spec for ThreadLocalCommitHookExecutingTransactionListener.
 *
 * @author John Leacox
 */
class ThreadLocalCommitHookExecutingTransactionListenerSpec
    extends FunSpec with MockitoSugar with BeforeAndAfter with MustMatchers {
  //  val transactionalHooks = new ThreadLocalJooqTransactionalHooks()
  val transactionalHooks = mock[JooqTransactionalHooks]
  val context = mock[TransactionContext]

  before {
    reset(transactionalHooks)
    reset(context)
    //    transactionalHooks.clearTransaction()
  }

  describe("ThreadLocalCommitHookExecutingTransactionListener") {
    describe("#beginEnd") {
      describe("when no transaction is active") {
        it("sets a new transaction on JooqTransactionalHooks") {
          val listener = new ThreadLocalCommitHookExecutingTransactionListener(transactionalHooks)

          listener.beginEnd(context)

          verify(transactionalHooks).setTransaction(any())
        }
// TODO: Is there anything else to test here?
//        it("does stuff") {
//          fail()
//        }
      }

      describe("when a transaction is already active") {
        it("does other stuff") {
          val listener = new ThreadLocalCommitHookExecutingTransactionListener(transactionalHooks)
          listener.beginEnd(context)

          listener.beginEnd(context)
          fail()
        }
      }
    }

    describe("#commitEnd") {
      describe("when it is the root transaction") {
        it("does stuff") {
          fail()
        }
      }

      describe("when it is a nested transaction") {
        it("doesn't do some stuff") {
          fail()
        }
      }
    }

    describe("#rollbackEnd") {
      describe("when it is the root transaction") {
        it("does stuff") {
          fail()
        }
      }

      describe("when it is a nested transaction") {
        it("doesn't do some stuff") {
          fail()
        }
      }
    }
  }
}
