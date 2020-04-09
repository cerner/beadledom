package com.cerner.beadledom.jaxrs.exceptionmapping

import com.cerner.beadledom.json.common.model.JsonError
import com.cerner.beadledom.jaxrs.provider.{FakeRepository, FakeResource}
import com.cerner.beadledom.testing.JsonErrorMatchers._

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider

import org.jboss.resteasy.mock.{MockDispatcherFactory, MockHttpRequest, MockHttpResponse}
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}

import javax.ws.rs._
import javax.ws.rs.core.HttpHeaders._
import javax.ws.rs.core.Response.Status._
import javax.ws.rs.core.{MediaType, Response}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers

/**
  * Unit tests for [[WebApplicationExceptionMapper]].
  *
  * @author Cal Fisher
  */
class WebApplicationExceptionMapperSpec
    extends AnyFunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll with MockitoSugar {

  val webApplicationExceptionMapper = new WebApplicationExceptionMapper
  val fakeRepository = mock[FakeRepository]
  val fakeResource = new FakeResource(fakeRepository)
  val url = "/fakeResource/ExceptionEndpoint"
  val request = MockHttpRequest.get(url)
  var response: MockHttpResponse = _
  val dispatcher = MockDispatcherFactory.createDispatcher()

  override def beforeAll(): Unit = {
    dispatcher.getRegistry.addSingletonResource(fakeResource)
    dispatcher.getProviderFactory.registerProvider(classOf[JacksonJsonProvider])
    dispatcher.getProviderFactory.registerProvider(classOf[WebApplicationExceptionMapper])
  }

  before {
    Mockito.reset(fakeRepository)
    response = new MockHttpResponse
  }

  describe("WebApplicationExceptionMapper") {
    describe("when an exception is thrown from a resource") {
      describe("when the service throws a WebApplicationException") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new WebApplicationException("Exception Message")

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beInternalServerError()
        }
      }

      describe("when the service throws a WebApplicationException with status 400") {
        it("returns a Json response with internal server error and status 400") {
          val exceptionResponse = Response
              .status(BAD_REQUEST)
              .`type`(MediaType.APPLICATION_JSON)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe BAD_REQUEST.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beBadRequestError()
        }
      }

      describe("when the service throws a WebApplicationException with status 401") {
        it("returns a Json response with internal server error and status 401") {
          val exceptionResponse = Response
              .status(UNAUTHORIZED)
              .`type`(MediaType.APPLICATION_JSON)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe UNAUTHORIZED.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beUnauthorizedError()
        }
      }

      describe("when the service throws a WebApplicationException with status 403") {
        it("returns a Json response with internal server error and status 403") {
          val exceptionResponse = Response
              .status(FORBIDDEN)
              .`type`(MediaType.APPLICATION_JSON)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe FORBIDDEN.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beForbiddenError()
        }
      }

      describe("when the service throws a WebApplicationException with status 404") {
        it("returns a Json response with internal server error and status 404") {
          val exceptionResponse = Response
              .status(NOT_FOUND)
              .`type`(MediaType.APPLICATION_JSON)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe NOT_FOUND.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beNotFoundError()
        }
      }

      describe("when the service throws a WebApplicationException with status 500") {
        it("returns a Json response with internal server error and status 500") {
          val exceptionResponse = Response
              .status(INTERNAL_SERVER_ERROR)
              .`type`(MediaType.APPLICATION_JSON)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beInternalServerError()
        }
      }

      describe("when the service throws a BadRequestException") {
        it("returns a Json response with internal server error and status 400") {
          val exception = new BadRequestException("Exception Message")

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe BAD_REQUEST.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beBadRequestError()
        }
      }

      describe("when the service throws a NotAuthorizedException") {
        it("returns a Json response with internal server error and status 401") {
          val exception = new NotAuthorizedException("Exception Message")

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe UNAUTHORIZED.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beUnauthorizedError()
        }
      }

      describe("when the service throws a ForbiddenException") {
        it("returns a Json response with internal server error and status 403") {
          val exception = new ForbiddenException("Exception Message")

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe FORBIDDEN.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beForbiddenError()
        }
      }

      describe("when the service throws a NotFoundException") {
        it("returns a Json response with internal server error and status 404") {
          val exception = new NotFoundException("Exception Message")

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe NOT_FOUND.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beNotFoundError()
        }
      }

      describe("when the service throws an InternalServerErrorException") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new InternalServerErrorException("Exception Message")

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beInternalServerError()
        }
      }

      describe("when the service throws a custom WebApplicationException") {
        describe("when the service throws a WebApplicationException") {
          it("returns a Json response with internal server error and status 500") {
            val exception = new CustomWebApplicationException("Exception Message")

            when(fakeRepository.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beInternalServerError()
          }
        }

        describe("when the service throws a custom WebApplicationException with status 400") {
          it("returns a Json response with internal server error and status 400") {
            val exceptionResponse = Response
                .status(BAD_REQUEST)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception =
              new CustomWebApplicationExceptionWithResponse("Exception Message", exceptionResponse)

            when(fakeRepository.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe BAD_REQUEST.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beBadRequestError()
          }
        }

        describe("when the service throws a custom WebApplicationException with status 401") {
          it("returns a Json response with internal server error and status 401") {
            val exceptionResponse = Response
                .status(UNAUTHORIZED)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception =
              new CustomWebApplicationExceptionWithResponse("Exception Message", exceptionResponse)

            when(fakeRepository.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe UNAUTHORIZED.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beUnauthorizedError()
          }
        }

        describe("when the service throws a custom WebApplicationException with status 403") {
          it("returns a Json response with internal server error and status 403") {
            val exceptionResponse = Response
                .status(FORBIDDEN)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception =
              new CustomWebApplicationExceptionWithResponse("Exception Message", exceptionResponse)

            when(fakeRepository.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe FORBIDDEN.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beForbiddenError()
          }
        }

        describe("when the service throws a custom WebApplicationException with status 404") {
          it("returns a Json response with internal server error and status 404") {
            val exceptionResponse = Response
                .status(NOT_FOUND)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception =
              new CustomWebApplicationExceptionWithResponse("Exception Message", exceptionResponse)

            when(fakeRepository.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe NOT_FOUND.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beNotFoundError()
          }
        }

        describe("when the service throws a custom WebApplicationException with status 500") {
          it("returns a Json response with internal server error and status 500") {
            val exceptionResponse = Response
                .status(INTERNAL_SERVER_ERROR)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception =
              new CustomWebApplicationExceptionWithResponse("Exception Message", exceptionResponse)

            when(fakeRepository.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beInternalServerError()
          }
        }

        describe("when the service throws a custom BadRequestException") {
          it("returns a Json response with internal server error and status 400") {
            val exception = new CustomBadRequestException("Exception Message")

            when(fakeRepository.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe BAD_REQUEST.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beBadRequestError()
          }
        }

        describe("when the service throws a custom NotAuthorizedException") {
          it("returns a Json response with internal server error and status 401") {
            val exception = new CustomNotAuthorizedException("Exception Message")

            when(fakeRepository.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe UNAUTHORIZED.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beUnauthorizedError()
          }
        }

        describe("when the service throws a custom ForbiddenException") {
          it("returns a Json response with internal server error and status 403") {
            val exception = new CustomForbiddenException("Exception Message")

            when(fakeRepository.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe FORBIDDEN.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beForbiddenError()
          }
        }

        describe("when the service throws a custom NotFoundException") {
          it("returns a Json response with internal server error and status 404") {
            val exception = new CustomNotFoundException("Exception Message")

            when(fakeRepository.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe NOT_FOUND.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beNotFoundError()
          }
        }

        describe("when the service throws a custom InternalServerErrorException") {
          it("returns a Json response with internal server error and status 500") {
            val exception = new CustomInternalServerErrorException("Exception Message")

            when(fakeRepository.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beInternalServerError()
          }
        }
      }

      describe("when the service throws a WebApplicationException with `text/plain` mediatype") {
        it("returns a Json response with the same status and `application/json` mediatype") {
          val exceptionResponse = Response
              .status(INTERNAL_SERVER_ERROR)
              .`type`(MediaType.TEXT_PLAIN)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          when(fakeRepository.fakeMethod()).thenThrow(exception)

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
            val exception = new WebApplicationException("Exception Message", exceptionResponse)

            when(fakeRepository.fakeMethod()).thenThrow(exception)

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
            val exception = new WebApplicationException("Exception Message", exceptionResponse)

            when(fakeRepository.fakeMethod()).thenThrow(exception)

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
            val exception = new WebApplicationException("Exception Message", exceptionResponse)

            when(fakeRepository.fakeMethod()).thenThrow(exception)

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
            val exception = new WebApplicationException("Exception Message", exceptionResponse)

            when(fakeRepository.fakeMethod()).thenThrow(exception)

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
            val exception = new WebApplicationException("Exception Message", exceptionResponse)

            when(fakeRepository.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus mustBe 599
            response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
            response.getContentAsString must beJsonError(599, "Server Error")
          }
        }
      }

      describe("when an exception response is null") {
        it("returns a Json response with internal server error and status 500") {
          val exception = mock[WebApplicationException]
          when(exception.getResponse).thenReturn(null)

          when(fakeRepository.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) mustBe MediaType.APPLICATION_JSON
          response.getContentAsString must beInternalServerError()
        }
      }
    }

    describe("unit testing the WebApplicationExceptionMapper class") {
      describe("when a WebApplicationException is thrown") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exception = new WebApplicationException("Exception Message")

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
        }
      }

      describe("when a WebApplicationException with status 400 is thrown") {
        it("is mapped to a Json response with bad request error and status 400") {
          val exceptionResponse = Response
              .status(BAD_REQUEST)
              .`type`(MediaType.APPLICATION_JSON)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus mustBe BAD_REQUEST.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe BAD_REQUEST.getStatusCode
          jsonError.message mustBe BAD_REQUEST.getReasonPhrase
        }
      }

      describe("when a WebApplicationException with status 401 is thrown") {
        it("is mapped to a Json response with unauthorized error and status 401") {
          val exceptionResponse = Response
              .status(UNAUTHORIZED)
              .`type`(MediaType.APPLICATION_JSON)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus mustBe UNAUTHORIZED.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe UNAUTHORIZED.getStatusCode
          jsonError.message mustBe UNAUTHORIZED.getReasonPhrase
        }
      }

      describe("when a WebApplicationException with status 403 is thrown") {
        it("is mapped to a Json response with forbidden error and status 403") {
          val exceptionResponse = Response
              .status(FORBIDDEN)
              .`type`(MediaType.APPLICATION_JSON)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus mustBe FORBIDDEN.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe FORBIDDEN.getStatusCode
          jsonError.message mustBe FORBIDDEN.getReasonPhrase
        }
      }

      describe("when a WebApplicationException with status 404 is thrown") {
        it("is mapped to a Json response with not found error and status 404") {
          val exceptionResponse = Response
              .status(NOT_FOUND)
              .`type`(MediaType.APPLICATION_JSON)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus mustBe NOT_FOUND.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe NOT_FOUND.getStatusCode
          jsonError.message mustBe NOT_FOUND.getReasonPhrase
        }
      }

      describe("when a WebApplicationException with status 500 is thrown") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exceptionResponse = Response
              .status(INTERNAL_SERVER_ERROR)
              .`type`(MediaType.APPLICATION_JSON)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
        }
      }

      describe("when a BadRequestException is thrown") {
        it("is mapped to a Json response with bad request error and status 400") {
          val exception = new BadRequestException("Exception Message")

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus mustBe BAD_REQUEST.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe BAD_REQUEST.getStatusCode
          jsonError.message mustBe BAD_REQUEST.getReasonPhrase
        }
      }

      describe("when a NotAuthorizedException is thrown") {
        it("is mapped to a Json response with unauthorized error and status 401") {
          val exception = new NotAuthorizedException("Exception Message")

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus mustBe UNAUTHORIZED.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe UNAUTHORIZED.getStatusCode
          jsonError.message mustBe UNAUTHORIZED.getReasonPhrase
        }
      }

      describe("when a ForbiddenException is thrown") {
        it("is mapped to a Json response with forbidden error and status 403") {
          val exception = new ForbiddenException("Exception Message")

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus mustBe FORBIDDEN.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe FORBIDDEN.getStatusCode
          jsonError.message mustBe FORBIDDEN.getReasonPhrase
        }
      }

      describe("when a NotFoundException is thrown") {
        it("is mapped to a Json response with not found error and status 404") {
          val exception = new NotFoundException("Exception Message")

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus mustBe NOT_FOUND.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe NOT_FOUND.getStatusCode
          jsonError.message mustBe NOT_FOUND.getReasonPhrase
        }
      }

      describe("when an InternalServerErrorException is thrown") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exception = new InternalServerErrorException("Exception Message")

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
        }
      }

      describe("when a custom WebApplicationException is thrown") {
        describe("when a custom WebApplicationException is thrown") {
          it("is mapped to a Json response with internal server error and status 500") {
            val exception = new CustomWebApplicationException("Exception Message")

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
            jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
          }
        }

        describe("when a custom WebApplicationException with status 400 is thrown") {
          it("is mapped to a Json response with bad request error and status 400") {
            val exceptionResponse = Response
                .status(BAD_REQUEST)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception =
              new CustomWebApplicationExceptionWithResponse("Exception Message", exceptionResponse)

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus mustBe BAD_REQUEST.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe BAD_REQUEST.getStatusCode
            jsonError.message mustBe BAD_REQUEST.getReasonPhrase
          }
        }

        describe("when a custom WebApplicationException with status 401 is thrown") {
          it("is mapped to a Json response with unauthorized error and status 401") {
            val exceptionResponse = Response
                .status(UNAUTHORIZED)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception =
              new CustomWebApplicationExceptionWithResponse("Exception Message", exceptionResponse)

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus mustBe UNAUTHORIZED.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe UNAUTHORIZED.getStatusCode
            jsonError.message mustBe UNAUTHORIZED.getReasonPhrase
          }
        }

        describe("when a custom WebApplicationException with status 403 is thrown") {
          it("is mapped to a Json response with forbidden error and status 403") {
            val exceptionResponse = Response
                .status(FORBIDDEN)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception =
              new CustomWebApplicationExceptionWithResponse("Exception Message", exceptionResponse)

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus mustBe FORBIDDEN.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe FORBIDDEN.getStatusCode
            jsonError.message mustBe FORBIDDEN.getReasonPhrase
          }
        }

        describe("when a custom WebApplicationException with status 404 is thrown") {
          it("is mapped to a Json response with not found error and status 404") {
            val exceptionResponse = Response
                .status(NOT_FOUND)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception =
              new CustomWebApplicationExceptionWithResponse("Exception Message", exceptionResponse)

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus mustBe NOT_FOUND.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe NOT_FOUND.getStatusCode
            jsonError.message mustBe NOT_FOUND.getReasonPhrase
          }
        }

        describe("when a custom WebApplicationException with status 500 is thrown") {
          it("is mapped to a Json response with internal server error and status 500") {
            val exceptionResponse = Response
                .status(INTERNAL_SERVER_ERROR)
                .`type`(MediaType.APPLICATION_JSON)
                .build
            val exception =
              new CustomWebApplicationExceptionWithResponse("Exception Message", exceptionResponse)

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
            jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
          }
        }

        describe("when a custom BadRequestException is thrown") {
          it("is mapped to a Json response with bad request error and status 400") {
            val exception = new CustomBadRequestException("Exception Message")

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus mustBe BAD_REQUEST.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe BAD_REQUEST.getStatusCode
            jsonError.message mustBe BAD_REQUEST.getReasonPhrase
          }
        }

        describe("when a custom NotAuthorizedException is thrown") {
          it("is mapped to a Json response with unauthorized error and status 401") {
            val exception = new CustomNotAuthorizedException("Exception Message")

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus mustBe UNAUTHORIZED.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe UNAUTHORIZED.getStatusCode
            jsonError.message mustBe UNAUTHORIZED.getReasonPhrase
          }
        }

        describe("when a custom ForbiddenException is thrown") {
          it("is mapped to a Json response with forbidden error and status 403") {
            val exception = new CustomForbiddenException("Exception Message")

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus mustBe FORBIDDEN.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe FORBIDDEN.getStatusCode
            jsonError.message mustBe FORBIDDEN.getReasonPhrase
          }
        }

        describe("when a custom NotFoundException is thrown") {
          it("is mapped to a Json response with not found error and status 404") {
            val exception = new CustomNotFoundException("Exception Message")

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus mustBe NOT_FOUND.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe NOT_FOUND.getStatusCode
            jsonError.message mustBe NOT_FOUND.getReasonPhrase
          }
        }

        describe("when a custom InternalServerErrorException is thrown") {
          it("is mapped to a Json response with internal server error and status 500") {
            val exception = new CustomInternalServerErrorException("Exception Message")

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus mustBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe INTERNAL_SERVER_ERROR.getStatusCode
            jsonError.message mustBe INTERNAL_SERVER_ERROR.getReasonPhrase
          }
        }
      }

      describe("when WebApplicationException is thrown with `text/plain` mediatype") {
        it("is mapped to a Json response with the same status and `application/json` mediatype") {
          val exceptionResponse = Response
              .status(INTERNAL_SERVER_ERROR)
              .`type`(MediaType.TEXT_PLAIN)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          val response = webApplicationExceptionMapper.toResponse(exception)

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
            val exception = new WebApplicationException("Exception Message", exceptionResponse)

            val response = webApplicationExceptionMapper.toResponse(exception)

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
            val exception = new WebApplicationException("Exception Message", exceptionResponse)

            val response = webApplicationExceptionMapper.toResponse(exception)

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
            val exception = new WebApplicationException("Exception Message", exceptionResponse)

            val response = webApplicationExceptionMapper.toResponse(exception)

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
            val exception = new WebApplicationException("Exception Message", exceptionResponse)

            val response = webApplicationExceptionMapper.toResponse(exception)

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
            val exception = new WebApplicationException("Exception Message", exceptionResponse)

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus mustBe 599
            response.getMediaType.toString mustBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code mustBe 599
            jsonError.message mustBe "Server Error"
          }
        }
      }

      describe("when an exception response is null") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exception = mock[WebApplicationException]
          when(exception.getResponse).thenReturn(null)

          val response = webApplicationExceptionMapper.toResponse(exception)

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

class CustomWebApplicationExceptionWithResponse(message: String, response: Response)
    extends WebApplicationException(message, response)

class CustomWebApplicationException(message: String) extends WebApplicationException(message)

class CustomBadRequestException(message: String) extends BadRequestException(message)

class CustomNotAuthorizedException(message: String) extends NotAuthorizedException(message)

class CustomForbiddenException(message: String) extends ForbiddenException(message)

class CustomNotFoundException(message: String) extends NotFoundException(message)

class CustomInternalServerErrorException(message: String)
    extends InternalServerErrorException(message)
