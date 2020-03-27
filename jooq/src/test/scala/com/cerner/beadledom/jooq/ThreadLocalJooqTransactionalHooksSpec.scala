package com.cerner.beadledom.jooq

import org.scalatest.BeforeAndAfter
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
 * Spec for ThreadLocalJooqTransactionalHooks.
 *
 * @author John Leacox
 */
class ThreadLocalJooqTransactionalHooksSpec
    extends AnyFunSpec with MockitoSugar with BeforeAndAfter with Matchers {
  describe("ThreadLocalJooqTransactionalHooks") {
    describe("#whenCommitted") {
      it("throws a NullPointerException if action is null") {
        val hooks = new ThreadLocalJooqTransactionalHooks()

        intercept[NullPointerException] {
          hooks.whenCommitted(null)
        }
      }

      describe("when a transaction is not active") {
        it("executes the action immediately") {
          val hooks = new ThreadLocalJooqTransactionalHooks()

          var wasActionExecuted = false
          hooks.whenCommitted(new Runnable {
            override def run(): Unit = {
              wasActionExecuted = true
            }
          })

          wasActionExecuted mustBe true
        }
      }

      describe("when a transaction is active") {
        it("adds the action to the current transaction to be executed later") {
          val hooks = new ThreadLocalJooqTransactionalHooks()

          val transaction = new JooqTransaction()
          hooks.setTransaction(transaction)

          var wasActionExecuted = false
          hooks.whenCommitted(new Runnable {
            override def run(): Unit = {
              wasActionExecuted = true
            }
          })

          wasActionExecuted mustBe false

          transaction.getCommitHooks.get(0).run()
          wasActionExecuted mustBe true
        }
      }

      describe("when multiple transactions are active on different threads") {
        it("adds actions to their thread local transaction only") {
          val hooks = new ThreadLocalJooqTransactionalHooks()

          val transactionOne = new JooqTransaction()
          val transactionTwo = new JooqTransaction()

          var wasActionOneExecuted = false
          val actionOne = new Runnable {
            override def run(): Unit = {
              wasActionOneExecuted = true
            }
          }

          var wasActionTwoExecuted = false
          val actionTwo = new Runnable {
            override def run(): Unit = {
              wasActionTwoExecuted = true
            }
          }

          val threadOne = new Thread {
            override def run(): Unit = {
              hooks.setTransaction(transactionOne)
              hooks.whenCommitted(actionOne)
            }
          }

          val threadTwo = new Thread {
            override def run(): Unit = {
              hooks.setTransaction(transactionTwo)
              hooks.whenCommitted(actionTwo)
            }
          }

          wasActionOneExecuted mustBe false
          wasActionTwoExecuted mustBe false

          threadOne.start()
          threadOne.join(5000)

          transactionOne.getCommitHooks must have size 1
          transactionOne.getCommitHooks.get(0).run()
          wasActionOneExecuted mustBe true
          wasActionTwoExecuted mustBe false

          threadTwo.start()
          threadTwo.join(5000)

          transactionTwo.getCommitHooks must have size 1
          transactionTwo.getCommitHooks.get(0).run()
          wasActionTwoExecuted mustBe true
        }
      }
    }

    describe("#clearTransaction") {
      it("removes any currently set transaction") {
        val hooks = new ThreadLocalJooqTransactionalHooks()

        val transaction = new JooqTransaction()
        hooks.setTransaction(transaction)
        hooks.clearTransaction()

        var wasActionExecuted = false
        hooks.whenCommitted(new Runnable {
          override def run(): Unit = {
            wasActionExecuted = true
          }
        })

        transaction.getCommitHooks must have size 0
        wasActionExecuted mustBe true
      }
    }
  }
}
