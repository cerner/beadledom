package com.cerner.beadledom.client.resteasy

import com.cerner.beadledom.client.TestResource
import java.util.concurrent.atomic.AtomicLong
import javax.ws.rs.NotFoundException
import javax.ws.rs.client.{Client, Entity}
import org.scalatest.{BeforeAndAfter, DoNotDiscover}
import org.slf4j.{Logger, LoggerFactory}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

/**
 * @author John Leacox
 */
@DoNotDiscover
class DefaultClientHttpEngineSpec(contextRoot: String, servicePort: Int)
    extends AnyFunSpec with Matchers with BeforeAndAfter {
  val logger: Logger = LoggerFactory.getLogger(classOf[DefaultClientHttpEngineSpec])

  val threadCount = 7
  val requestCount = 1

  describe("Connection cleanup with ApacheHttpClient43engine") {
    it("cleans up with GC") {
      val counter = new AtomicLong()
      counter.set(0)
      val client = BeadledomResteasyClientBuilder.newBuilder().setConnectionPoolSize(5).build()

      val threads = new Array[Thread](threadCount)

      for (i <- 0 until threadCount) {
        threads(i) = new Thread() {
          override def run() {
            for (i <- 0 until requestCount) {
              getIt(client, counter, false)
              System.gc()
            }
          }
        }
      }

      for (i <- 0 until threadCount) {
        threads(i).start()
      }
      for (i <- 0 until threadCount) {
        threads(i).join()
      }

      counter.get should be(threadCount * requestCount)
    }

    it("cleans up with manual connection closing") {
      val counter = new AtomicLong()
      counter.set(0)
      val client = BeadledomResteasyClientBuilder.newBuilder().setConnectionPoolSize(5).build()

      val threads = new Array[Thread](threadCount)

      for (i <- 0 until threadCount) {
        threads(i) = new Thread() {
          override def run() {
            for (i <- 0 until requestCount) {
              getIt(client, counter, true)
            }
          }
        }
      }

      for (i <- 0 until threadCount) {
        threads(i).start()
      }
      for (i <- 0 until threadCount) {
        threads(i).join()
      }

      counter.get should be(threadCount * requestCount)
    }

    it("cleans up with proxy") {
      val counter = new AtomicLong()
      counter.set(0)
      val client = BeadledomResteasyClientBuilder.newClient()
      val proxy = client.target(s"http://localhost:$servicePort/$contextRoot")
          .proxy(classOf[TestResource])

      val threads = new Array[Thread](threadCount)

      for (i <- 0 until threadCount) {
        threads(i) = new Thread() {
          override def run() {
            for (i <- 0 until requestCount) {
              logger.debug("calling proxy")
              val str = proxy.get()
              logger.debug(s"returned: $str")
              str should be("hello world")
              counter.incrementAndGet()
            }
          }
        }
      }

      for (i <- 0 until threadCount) {
        threads(i).start()
      }
      for (i <- 0 until threadCount) {
        threads(i).join()
      }

      counter.get should be(threadCount * requestCount)
    }

    it("cleans up after error with GC") {
      val counter = new AtomicLong()
      counter.set(0)
      val client = BeadledomResteasyClientBuilder.newBuilder().setConnectionPoolSize(5).build()
      val proxy = client.target(s"http://localhost:$servicePort/$contextRoot")
          .proxy(classOf[TestResource])

      val threads = new Array[Thread](threadCount)

      for (i <- 0 until threadCount) {
        threads(i) = new Thread() {
          override def run() {
            for (i <- 0 until requestCount) {
              logger.debug("calling proxy")
              callProxy(proxy, counter, false)
              System.gc()
              logger.debug("returned")
            }
          }
        }
      }

      for (i <- 0 until threadCount) {
        threads(i).start()
      }
      for (i <- 0 until threadCount) {
        threads(i).join()
      }

      counter.get should be(threadCount * requestCount)
    }

    it("cleans up after error with with manual connection closing") {
      val counter = new AtomicLong()
      counter.set(0)
      val client = BeadledomResteasyClientBuilder.newBuilder().setConnectionPoolSize(5).build()
      val proxy = client.target(s"http://localhost:$servicePort/$contextRoot")
          .proxy(classOf[TestResource])

      val threads = new Array[Thread](threadCount)

      for (i <- 0 until threadCount) {
        threads(i) = new Thread() {
          override def run() {
            for (i <- 0 until requestCount) {
              logger.debug("calling proxy")
              callProxy(proxy, counter, true)
              System.gc()
              logger.debug("returned")
            }
          }
        }
      }

      for (i <- 0 until threadCount) {
        threads(i).start()
      }
      for (i <- 0 until threadCount) {
        threads(i).join()
      }

      counter.get should be(threadCount * requestCount)
    }

    it("cleans up after error with proxy") {
      val counter = new AtomicLong()
      counter.set(0)
      val client = BeadledomResteasyClientBuilder.newBuilder().setConnectionPoolSize(5).build()
      val proxy = client.target(s"http://localhost:$servicePort/$contextRoot")
          .proxy(classOf[TestResource])

      val threads = new Array[Thread](threadCount)

      for (i <- 0 until threadCount) {
        threads(i) = new Thread() {
          override def run() {
            for (i <- 0 until requestCount) {
              logger.debug("calling proxy")
              try {
                proxy.error()
              } catch {
                case e: NotFoundException =>
                  e.getResponse.getStatus should be(404)
                  counter.incrementAndGet()
              }
            }
          }
        }
      }

      for (i <- 0 until threadCount) {
        threads(i).start()
      }
      for (i <- 0 until threadCount) {
        threads(i).join()
      }

      counter.get should be(threadCount * requestCount)
    }

    it("cleans up after post with GC") {
      val counter = new AtomicLong()
      counter.set(0)
      val client = BeadledomResteasyClientBuilder.newBuilder().setConnectionPoolSize(5).build()

      val threads = new Array[Thread](threadCount)

      for (i <- 0 until threadCount) {
        threads(i) = new Thread() {
          override def run() {
            for (i <- 0 until requestCount) {
              postIt(client, counter, false)
              System.gc()
            }
          }
        }
      }

      for (i <- 0 until threadCount) {
        threads(i).start()
      }
      for (i <- 0 until threadCount) {
        threads(i).join()
      }

      counter.get should be(threadCount * requestCount)
    }

    it("cleans up after post with manual connection closing") {
      val counter = new AtomicLong()
      counter.set(0)
      val client = BeadledomResteasyClientBuilder.newBuilder().setConnectionPoolSize(5).build()

      val threads = new Array[Thread](threadCount)

      for (i <- 0 until threadCount) {
        threads(i) = new Thread() {
          override def run() {
            for (i <- 0 until requestCount) {
              postIt(client, counter, true)
            }
          }
        }
      }

      for (i <- 0 until threadCount) {
        threads(i).start()
      }
      for (i <- 0 until threadCount) {
        threads(i).join()
      }

      counter.get should be(threadCount * requestCount)
    }

    it("cleans up after post with proxy") {
      val counter = new AtomicLong()
      counter.set(0)
      val client = BeadledomResteasyClientBuilder.newBuilder().setConnectionPoolSize(5).build()
      val proxy = client.target(s"http://localhost:$servicePort/$contextRoot")
          .proxy(classOf[TestResource])

      val threads = new Array[Thread](threadCount)

      for (i <- 0 until threadCount) {
        threads(i) = new Thread() {
          override def run() {
            for (i <- 0 until requestCount) {
              logger.debug("calling proxy")
              val str = proxy.getData("hello world")
              logger.debug(s"returned: $str")
              str should be("Here is your string:hello world")
              counter.incrementAndGet()
            }
          }
        }
      }

      for (i <- 0 until threadCount) {
        threads(i).start()
      }
      for (i <- 0 until threadCount) {
        threads(i).join()
      }

      counter.get should be(threadCount * requestCount)
    }
  }

  private def callProxy(proxy: TestResource, counter: AtomicLong, cleanup: Boolean) {
    try {
      val str = proxy.error()
    } catch {
      case e: NotFoundException =>
        e.getResponse.getStatus should be(404)
        counter.incrementAndGet()
        if (cleanup) {
          e.getResponse.close()
        }
    }
  }

  private def getIt(client: Client, counter: AtomicLong, cleanup: Boolean): Unit = {
    val target = client.target(s"http://localhost:$servicePort/$contextRoot/test")
    try {
      logger.debug("get")
      val response = target.request().get()
      response.getStatus should be(200)
      response.readEntity(classOf[String]) should be("hello world")
      logger.debug("ok")
      if (cleanup) {
        response.close()
      }
    } catch {
      case e: Throwable => throw new RuntimeException(e);
    }
    counter.incrementAndGet()
  }

  private def postIt(client: Client, counter: AtomicLong, cleanup: Boolean): Unit = {
    val target = client.target(s"http://localhost:$servicePort/$contextRoot/test/data")

    logger.debug("post")
    val stringEntity: Entity[String] = Entity.text("hello world")
    val response = target.request.post(stringEntity)
    response.getStatus should be(200)
    response.readEntity(classOf[String]) should be("Here is your string:hello world")
    logger.debug("ok")
    if (cleanup) {
      response.close()
    }

    counter.incrementAndGet()
  }
}
