package com.cerner.beadledom.jooq

import javax.sql.DataSource
import org.jooq.{DSLContext, SQLDialect}
import org.scalatest.BeforeAndAfter
import scala.collection.mutable
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
 * Spec for DSLContextProviderImpl.
 *
 * @author John Leacox
 */
class DSLContextProviderImplSpec extends
    AnyFunSpec with MockitoSugar with BeforeAndAfter with Matchers {
  val transactionalHooks = new ThreadLocalJooqTransactionalHooks()

  before {
    transactionalHooks.clearTransaction()
  }

  describe("DSLContextProviderImpl") {
    describe("#isActive") {
      it("must return false on a new instance") {
        val dataSource = mock[DataSource]
        val provider = new DSLContextProviderImpl(dataSource, SQLDialect.MYSQL, transactionalHooks)

        provider.isActive mustBe false
      }
    }

    describe("#end") {
      it("must do nothing if it is not active") {
        val dataSource = mock[DataSource]
        val provider = new DSLContextProviderImpl(dataSource, SQLDialect.MYSQL, transactionalHooks)

        provider.end()
        provider.isActive mustBe false
      }
    }

    describe("#begin") {
      it("must be active after starting") {
        val dataSource = mock[DataSource]
        val provider = new DSLContextProviderImpl(dataSource, SQLDialect.MYSQL, transactionalHooks)

        provider.begin()
        provider.isActive mustBe true
      }

      it("must not be active after starting and stopping") {
        val dataSource = mock[DataSource]
        val provider = new DSLContextProviderImpl(dataSource, SQLDialect.MYSQL, transactionalHooks)

        provider.begin()
        provider.end()
        provider.isActive mustBe false
      }

      it("must throw an IllegalStateException if it is already active") {
        val dataSource = mock[DataSource]
        val provider = new DSLContextProviderImpl(dataSource, SQLDialect.MYSQL, transactionalHooks)

        provider.begin()
        intercept[IllegalStateException] {
          provider.begin()
        }
      }

      it("must allow restarting after stopping") {
        val dataSource = mock[DataSource]
        val provider = new DSLContextProviderImpl(dataSource, SQLDialect.MYSQL, transactionalHooks)

        provider.begin()
        provider.end()
        provider.begin()
        provider.isActive mustBe true
      }

      it("must be scoped by thread; allow starting from multiple threads") {
        val dataSource = mock[DataSource]
        val provider = new DSLContextProviderImpl(dataSource, SQLDialect.MYSQL, transactionalHooks)

        val runnable = new Runnable() {
          override def run(): Unit = {
            provider.isActive mustBe false
            provider.begin()
            provider.isActive mustBe true
          }
        }

        val threads = mutable.ListBuffer[Thread]()
        for (_ <- 1 to 5) {
          val thread = new Thread(runnable)
          threads += thread
          thread.start()
        }

        threads.foreach { t => t.join(5000) }
      }
    }

    describe("#get") {
      it("must return an a DSLContext if it is active") {
        val dataSource = mock[DataSource]
        val provider = new DSLContextProviderImpl(dataSource, SQLDialect.MYSQL, transactionalHooks)

        provider.begin()
        val dslContext = provider.get()
        dslContext mustBe a[DSLContext]
      }

      it("must throw an IllegalStateException if it is not active") {
        val dataSource = mock[DataSource]
        val provider = new DSLContextProviderImpl(dataSource, SQLDialect.MYSQL, transactionalHooks)

        intercept[IllegalStateException] {
          provider.get()
        }
      }

      it("must return the same DSLContext on the same thread") {
        val dataSource = mock[DataSource]
        val provider = new DSLContextProviderImpl(dataSource, SQLDialect.MYSQL, transactionalHooks)

        val runnable = new Runnable() {
          override def run(): Unit = {
            provider.begin()

            val dslContext1 = provider.get()
            val dslContext2 = provider.get()
            dslContext1 must be theSameInstanceAs dslContext2
          }
        }

        for (_ <- 1 to 5) {
          new Thread(runnable).start()
        }
      }
    }
  }
}
