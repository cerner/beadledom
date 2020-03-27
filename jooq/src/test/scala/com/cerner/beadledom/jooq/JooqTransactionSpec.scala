package com.cerner.beadledom.jooq

import org.scalatest.BeforeAndAfter
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
 * Spec for JooqTransaction.
 *
 * @author John Leacox
 */
class JooqTransactionSpec extends AnyFunSpec with MockitoSugar with BeforeAndAfter with Matchers {
  describe("JooqTransaction") {
    describe("#addCommitHooks") {
      it("adds the actions to the transactions commit hooks") {
        val transaction = new JooqTransaction()

        val actionOne = new Runnable {
          override def run(): Unit = ???
        }
        transaction.addCommitHook(actionOne)

        val actionTwo = new Runnable {
          override def run(): Unit = ???
        }
        transaction.addCommitHook(actionTwo)

        val hooks = transaction.getCommitHooks

        hooks must have size 2
        hooks must contain(actionOne)
        hooks must contain(actionTwo)
      }
    }

    describe("#getCommitHooks") {
      describe("when no actions have been added") {
        it("returns an empty list") {
          val transaction = new JooqTransaction()

          transaction.getCommitHooks must have size 0
        }
      }
    }

    describe("#addNestedTransaction") {
      it("adds the transaction to the nested transactions") {
        val transaction = new JooqTransaction()

        val nestedOne = new JooqTransaction()
        transaction.addNestedTransaction(nestedOne)

        val nestedTwo = new JooqTransaction()
        transaction.addNestedTransaction(nestedTwo)

        val nested = transaction.getNestedTransactions

        nested must have size 2
        nested must contain(nestedOne)
        nested must contain(nestedTwo)
      }
    }

    describe("#getNestedTransaction") {
      describe("when no nested transactions have been added") {
        it("returns an empty list") {
          val transaction = new JooqTransaction()

          transaction.getNestedTransactions must have size 0
        }
      }
    }

    describe("#clearCommitHooks") {
      it("removes all commit hooks") {
        val transaction = new JooqTransaction()

        val actionOne = new Runnable {
          override def run(): Unit = ???
        }
        transaction.addCommitHook(actionOne)

        val actionTwo = new Runnable {
          override def run(): Unit = ???
        }
        transaction.addCommitHook(actionTwo)

        transaction.clearCommitHooks()

        transaction.getCommitHooks must have size 0
      }
    }

    describe("#clearNestedTransaction") {
      it("removes all nested transactions") {
        val transaction = new JooqTransaction()

        val nestedOne = new JooqTransaction()
        transaction.addNestedTransaction(nestedOne)

        val nestedTwo = new JooqTransaction()
        transaction.addNestedTransaction(nestedTwo)

        transaction.clearNestedTransaction()

        transaction.getNestedTransactions must have size 0
      }
    }
  }
}
