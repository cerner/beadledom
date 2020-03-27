package com.cerner.beadledom.resteasy.exceptionmapping

import com.cerner.beadledom.json.common.model.JsonError
import com.cerner.beadledom.resteasy.fauxservice.dao.HelloDao
import com.cerner.beadledom.resteasy.fauxservice.resource.HelloResource
import com.cerner.beadledom.testing.JsonErrorMatchers._

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import javax.ws.rs.core.HttpHeaders._
import javax.ws.rs.core.Response.Status._
import javax.ws.rs.core.{MediaType, Response}
import org.jboss.resteasy.mock.{MockDispatcherFactory, MockHttpRequest, MockHttpResponse}
import org.jboss.resteasy.spi.{Failure, LoggableFailure, ReaderException, WriterException}
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Unit tests for [[FailureExceptionMapper]].
  *
  * @author Cal Fisher
  */
class FailureExceptionMapperSpec
    extends AnyFunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll with MockitoSugar {

  val failureExceptionMapper = new FailureExceptionMapper
  val helloDao = mock[HelloDao]
  val helloResource = new HelloResource(helloDao)
  val url = "/hello"
  val request = MockHttpRequest.get(url)
  var response: MockHttpResponse = _
  val dispatcher = MockDispatcherFactory.createDispatcher()

  override def beforeAll(): Unit = {
    dispatcher.getRegistry.addSingletonResource(helloResource)
    dispatcher.getProviderFactory.registerProvider(classOf[JacksonJsonProvider])
    dispatcher.getProviderFactory.registerProvider(classOf[FailureExceptionMapper])
  }

  before {
    Mockito.reset(helloDao)
    response = new MockHttpResponse
  }

  describe("FailureExceptionMapper") {
    describe("when an exception is thrown from a resource") {
      describe("when the service throws a ReaderException") {
        describe("when the ReaderException is constructed with just a message") {
          it("returns a Json response with internal server error and status 500") {
            val exception = new ReaderException("Exception Message")

            when(helloDao.getHello()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beInternalServerError()
          }
        }

        describe("when the ReaderException is constructed with a message and response") {
          it("returns a Json response with the response's status and message") {
            val exceptionResponse = Response
                .status(BAD_REQUEST)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception = new ReaderException("Exception Message", exceptionResponse)

            when(helloDao.getHello()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe BAD_REQUEST.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beBadRequestError()
          }
        }

        describe("when the ReaderException is constructed with a message and error code") {
          it("returns a Json response with the error code and message") {
            val exception = new ReaderException("Exception Message", BAD_REQUEST.getStatusCode)

            when(helloDao.getHello()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe BAD_REQUEST.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beBadRequestError()
          }
        }
      }

      describe("when the service throws a WriterException") {
        describe("when the WriterException is constructed with just a message") {
          it("returns a Json response with internal server error and status 500") {
            val exception = new WriterException("Exception Message")

            when(helloDao.getHello()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beInternalServerError()
          }
        }

        describe("when the WriterException is constructed with a message and response") {
          it("returns a Json response with the response's status and message") {
            val exceptionResponse = Response
                .status(INTERNAL_SERVER_ERROR)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception = new WriterException("Exception Message", exceptionResponse)

            when(helloDao.getHello()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beInternalServerError()
          }
        }

        describe("when the WriterException is constructed with a message and error code") {
          it("returns a Json response with the error code and message") {
            val exception =
              new WriterException("Exception Message", INTERNAL_SERVER_ERROR.getStatusCode)

            when(helloDao.getHello()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beInternalServerError()
          }
        }
      }

      describe("when the service throws a LoggableFailure exception") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new LoggableFailure("Exception Message")

          when(helloDao.getHello()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beInternalServerError()
        }
      }

      describe("when the service throws a Failure exception") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new Failure("Exception Message")

          when(helloDao.getHello()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beInternalServerError()
        }
      }

      describe("when the service throws a custom Failure exception") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new CustomFailureException("Exception Message")

          when(helloDao.getHello()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beInternalServerError()
        }
      }

      describe("when an exception code is not recognized") {
        describe("when the exception code is 100-199") {
          it("returns a Json response with the code and the informational family message") {
            val exceptionResponse = Response
                .status(199)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception = new Failure("Exception Message", exceptionResponse)

            when(helloDao.getHello()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe 199
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beJsonError(199, "Informational")
          }
        }

        describe("when the exception code is 200-299") {
          it("returns a Json response with the code and the successful family message") {
            val exceptionResponse = Response
                .status(299)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception = new Failure("Exception Message", exceptionResponse)

            when(helloDao.getHello()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe 299
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beJsonError(299, "Successful")
          }
        }

        describe("when the exception code is 300-399") {
          it("returns a Json response with the code and the redirection family message") {
            val exceptionResponse = Response
                .status(399)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception = new Failure("Exception Message", exceptionResponse)

            when(helloDao.getHello()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe 399
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beJsonError(399, "Redirection")
          }
        }

        describe("when the exception code is 400-499") {
          it("returns a Json response with the code and the client error family message") {
            val exceptionResponse = Response
                .status(499)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception = new Failure("Exception Message", exceptionResponse)

            when(helloDao.getHello()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe 499
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beJsonError(499, "Client Error")
          }
        }

        describe("when the exception code is 500-599") {
          it("returns a Json response with the code and the server error family message") {
            val exceptionResponse = Response
                .status(599)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception = new Failure("Exception Message", exceptionResponse)

            when(helloDao.getHello()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe 599
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beJsonError(599, "Server Error")
          }
        }
      }

      describe("when an exception response is null") {
        it("returns a Json response with the error code and message") {
          val exception = mock[Failure]
          when(exception.getResponse).thenReturn(null)
          when(exception.getErrorCode).thenReturn(NOT_FOUND.getStatusCode)

          when(helloDao.getHello()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe NOT_FOUND.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beNotFoundError()
        }
      }

      describe("when an exception response is null and error code is not in the valid range") {
        describe("when the error code is below 100") {
          it("returns a Json response with internal server error and status 500") {
            val exception = mock[Failure]
            when(exception.getResponse).thenReturn(null)
            when(exception.getErrorCode).thenReturn(99)

            when(helloDao.getHello()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beInternalServerError()
          }
        }

        describe("when the error code is above 599") {
          it("returns a Json response with internal server error and status 500") {
            val exception = mock[Failure]
            when(exception.getResponse).thenReturn(null)
            when(exception.getErrorCode).thenReturn(600)

            when(helloDao.getHello()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beInternalServerError()
          }
        }
      }
    }

    describe("unit testing the FailureExceptionMapper class") {
      describe("when a ReaderException is thrown") {
        describe("when the ReaderException is constructed with just a message") {
          it("is mapped to a Json response with internal server error and status 500") {
            val exception = new ReaderException("Exception Message")

            val response = failureExceptionMapper.toResponse(exception)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
            jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
          }
        }

        describe("when the ReaderException is constructed with a message and response") {
          it("returns a Json response with the response's status and message") {
            val exceptionResponse = Response
                .status(BAD_REQUEST)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception = new ReaderException("Exception Message", exceptionResponse)

            val response = failureExceptionMapper.toResponse(exception)

            response.getStatus mustBe BAD_REQUEST.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe BAD_REQUEST.getStatusCode
            jsonError.message mustBe BAD_REQUEST.getReasonPhrase
          }
        }

        describe("when the ReaderException is constructed with a message and error code") {
          it("returns a Json response with the error code and message") {
            val exception = new ReaderException("Exception Message", BAD_REQUEST.getStatusCode)

            val response = failureExceptionMapper.toResponse(exception)

            response.getStatus mustBe BAD_REQUEST.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe BAD_REQUEST.getStatusCode
            jsonError.message mustBe BAD_REQUEST.getReasonPhrase
          }
        }
      }

      describe("when a WriterException is thrown") {
        describe("when the WriterException is constructed with just a message") {
          it("is mapped to a Json response with internal server error and status 500") {
            val exception = new WriterException("Exception Message")

            val response = failureExceptionMapper.toResponse(exception)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
            jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
          }
        }

        describe("when the WriterException is constructed with a message and response") {
          it("returns a Json response with the response's status and message") {
            val exceptionResponse = Response
                .status(INTERNAL_SERVER_ERROR)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception = new WriterException("Exception Message", exceptionResponse)

            val response = failureExceptionMapper.toResponse(exception)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
            jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
          }
        }

        describe("when the WriterException is constructed with a message and error code") {
          it("returns a Json response with the error code and message") {
            val exception =
              new WriterException("Exception Message", INTERNAL_SERVER_ERROR.getStatusCode)

            val response = failureExceptionMapper.toResponse(exception)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
            jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
          }
        }
      }

      describe("when a LoggableFailure exception is thrown") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exception = new LoggableFailure("Exception Message")

          val response = failureExceptionMapper.toResponse(exception)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
        }
      }

      describe("when a Failure exception is thrown") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exception = new Failure("Exception Message")

          val response = failureExceptionMapper.toResponse(exception)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
        }
      }

      describe("when a custom Failure exception is thrown") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exception = new CustomFailureException("Exception Message")

          val response = failureExceptionMapper.toResponse(exception)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
        }
      }

      describe("when an exception code is not recognized") {
        describe("when the exception code is 100-199") {
          it("is mapped to a Json response with the code and the informational family message") {
            val exceptionResponse = Response
                .status(199)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception = new Failure("Exception Message", exceptionResponse)

            val response = failureExceptionMapper.toResponse(exception)

            response.getStatus mustBe 199
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe 199
            jsonError.message mustBe "Informational"
          }
        }

        describe("when the exception code is 200-299") {
          it("is mapped to a Json response with the code and the successful family message") {
            val exceptionResponse = Response
                .status(299)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception = new Failure("Exception Message", exceptionResponse)

            val response = failureExceptionMapper.toResponse(exception)

            response.getStatus mustBe 299
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe 299
            jsonError.message mustBe "Successful"
          }
        }

        describe("when the exception code is 300-399") {
          it("is mapped to a Json response with the code and the redirection family message") {
            val exceptionResponse = Response
                .status(399)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception = new Failure("Exception Message", exceptionResponse)

            val response = failureExceptionMapper.toResponse(exception)

            response.getStatus mustBe 399
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe 399
            jsonError.message mustBe "Redirection"
          }
        }

        describe("when the exception code is 400-499") {
          it("is mapped to a Json response with the code and the client error family message") {
            val exceptionResponse = Response
                .status(499)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception = new Failure("Exception Message", exceptionResponse)

            val response = failureExceptionMapper.toResponse(exception)

            response.getStatus mustBe 499
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe 499
            jsonError.message mustBe "Client Error"
          }
        }

        describe("when the exception code is 500-599") {
          it("is mapped to a Json response with the code and the server error family message") {
            val exceptionResponse = Response
                .status(599)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception = new Failure("Exception Message", exceptionResponse)

            val response = failureExceptionMapper.toResponse(exception)

            response.getStatus mustBe 599
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe 599
            jsonError.message mustBe "Server Error"
          }
        }

        describe("when the exception code is anything else") {
          it("is mapped to a Json response with the code and an unrecognized status code message") {
            val exceptionResponse = Response
                .status(999)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception = new Failure("Exception Message", exceptionResponse)

            val response = failureExceptionMapper.toResponse(exception)

            response.getStatus mustBe 999
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe 999
            jsonError.message mustBe "Unrecognized Status Code"
          }
        }
      }

      describe("when an exception response is null") {
        describe("when the error code is 404") {
          it("is mapped to a Json response with not found error and status 404") {
            val exception = mock[Failure]
            when(exception.getResponse).thenReturn(null)
            when(exception.getErrorCode).thenReturn(NOT_FOUND.getStatusCode)

            val response = failureExceptionMapper.toResponse(exception)

            response.getStatus mustBe NOT_FOUND.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe NOT_FOUND.getStatusCode
            jsonError.message mustBe NOT_FOUND.getReasonPhrase
          }
        }
      }

      describe("when an exception response is null and error code is not in the valid range") {
        describe("when the error code is below 100") {
          it("returns a Json response with internal server error and status 500") {
            val exception = mock[Failure]
            when(exception.getResponse).thenReturn(null)
            when(exception.getErrorCode).thenReturn(99)

            val response = failureExceptionMapper.toResponse(exception)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
            jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
          }
        }

        describe("when the error code is above 599") {
          it("returns a Json response with internal server error and status 500") {
            val exception = mock[Failure]
            when(exception.getResponse).thenReturn(null)
            when(exception.getErrorCode).thenReturn(600)

            val response = failureExceptionMapper.toResponse(exception)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
            jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
          }
        }
      }
    }
  }
}

class CustomFailureException(val message: String) extends Failure(message)
