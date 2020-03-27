package com.cerner.beadledom.jaxrs

import com.google.common.base.Charsets
import java.io.{ByteArrayOutputStream, OutputStreamWriter}
import java.util.function.Consumer
import org.scalatest.{FunSpec, Matchers}
import org.scalatestplus.mockito.MockitoSugar

/**
  * @author John Leacox
  */
class StreamingWriterOutputSpec extends FunSpec with Matchers with MockitoSugar {

  implicit def toConsumer[A](function: A => Unit): Consumer[A] = new Consumer[A]() {
    override def accept(arg: A): Unit = function.apply(arg)
  }

  describe("StreamingWriterOutput") {
    describe("#with") {
      it("throws a NullPointerException if consumer is null") {
        intercept[NullPointerException] {
          StreamingWriterOutput.`with`(null)
        }
      }
    }

    describe("#write") {
      it("it should write to the output stream") {
        val streamingOutput = StreamingWriterOutput
            .`with`((writer: OutputStreamWriter) => writer.write("hello world"))

        val outputStream = new ByteArrayOutputStream()
        streamingOutput.write(outputStream)

        val output = new String(outputStream.toByteArray, Charsets.UTF_8)
        output shouldBe "hello world"
      }
    }
  }
}
