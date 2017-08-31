package com.cerner.beadledom.jaxrs.exceptionmappers

import com.cerner.beadledom.jaxrs.models.JsonError
import com.cerner.beadledom.jaxrs.provider.{FakeDao, FakeResource}

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider

import org.jboss.resteasy.mock.{MockDispatcherFactory, MockHttpRequest, MockHttpResponse}
import org.mockito.Mockito
import org.mockito.Mockito._

import play.api.libs.json.Json

import javax.ws.rs._
import javax.ws.rs.core.HttpHeaders._
import javax.ws.rs.core.Response.Status._
import javax.ws.rs.core.{MediaType, Response}

/**
  * Unit tests for {@link WebApplicationExceptionMapper}.
  *
  * @author Cal Fisher
  */
class WebApplicationExceptionMapperSpec extends BaseExceptionMapperSpec {

  val webApplicationExceptionMapper = new WebApplicationExceptionMapper
  val fakeDao = mock[FakeDao]
  val fakeResource = new FakeResource(fakeDao)
  val url = "/fakeResource/ExceptionEndpoint"
  val request = MockHttpRequest.get(url)
  var response: MockHttpResponse = _

  val badRequestJsonError = createJsonError(BAD_REQUEST)
  val unauthorizedJsonError = createJsonError(UNAUTHORIZED)
  val forbiddenJsonError = createJsonError(FORBIDDEN)
  val notFoundJsonError = createJsonError(NOT_FOUND)
  val internalServerErrorJsonError = createJsonError(INTERNAL_SERVER_ERROR)

  override def beforeEach {
    Mockito.reset(fakeDao)
    response = new MockHttpResponse
  }

  describe("WebApplicationExceptionMapper") {
    describe("unit testing the behavior of ThrowableExceptionMapper class when an exception is " +
        "thrown from a resource") {
      val dispatcher = MockDispatcherFactory.createDispatcher()
      dispatcher.getRegistry.addSingletonResource(fakeResource)
      dispatcher.getProviderFactory.registerProvider(classOf[JacksonJsonProvider])
      dispatcher.getProviderFactory.registerProvider(classOf[WebApplicationExceptionMapper])

      describe("when the service throws a WebApplicationException") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new WebApplicationException("Exception Message")

          when(fakeDao.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) shouldBe internalServerErrorJsonError
        }
      }

      describe("when the service throws a WebApplicationException with status 400") {
        it("returns a Json response with internal server error and status 400") {
          val exceptionResponse = Response
              .status(BAD_REQUEST)
              .`type`(MediaType.APPLICATION_JSON)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          when(fakeDao.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus shouldBe BAD_REQUEST.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) shouldBe badRequestJsonError
        }
      }

      describe("when the service throws a WebApplicationException with status 401") {
        it("returns a Json response with internal server error and status 401") {
          val exceptionResponse = Response
              .status(UNAUTHORIZED)
              .`type`(MediaType.APPLICATION_JSON)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          when(fakeDao.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus shouldBe UNAUTHORIZED.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) shouldBe unauthorizedJsonError
        }
      }

      describe("when the service throws a WebApplicationException with status 403") {
        it("returns a Json response with internal server error and status 403") {
          val exceptionResponse = Response
              .status(FORBIDDEN)
              .`type`(MediaType.APPLICATION_JSON)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          when(fakeDao.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus shouldBe FORBIDDEN.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) shouldBe forbiddenJsonError
        }
      }

      describe("when the service throws a WebApplicationException with status 404") {
        it("returns a Json response with internal server error and status 404") {
          val exceptionResponse = Response
              .status(NOT_FOUND)
              .`type`(MediaType.APPLICATION_JSON)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          when(fakeDao.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus shouldBe NOT_FOUND.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) shouldBe notFoundJsonError
        }
      }

      describe("when the service throws a WebApplicationException with status 500") {
        it("returns a Json response with internal server error and status 500") {
          val exceptionResponse = Response
              .status(INTERNAL_SERVER_ERROR)
              .`type`(MediaType.APPLICATION_JSON)
              .build
          val exception = new WebApplicationException("Exception Message", exceptionResponse)

          when(fakeDao.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) shouldBe internalServerErrorJsonError
        }
      }

      describe("when the service throws a BadRequestException") {
        it("returns a Json response with internal server error and status 400") {
          val exception = new BadRequestException("Exception Message")

          when(fakeDao.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus shouldBe BAD_REQUEST.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) shouldBe badRequestJsonError
        }
      }

      describe("when the service throws a NotAuthorizedException") {
        it("returns a Json response with internal server error and status 401") {
          val exception = new NotAuthorizedException("Exception Message")

          when(fakeDao.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus shouldBe UNAUTHORIZED.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) shouldBe unauthorizedJsonError
        }
      }

      describe("when the service throws a ForbiddenException") {
        it("returns a Json response with internal server error and status 403") {
          val exception = new ForbiddenException("Exception Message")

          when(fakeDao.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus shouldBe FORBIDDEN.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) shouldBe forbiddenJsonError
        }
      }

      describe("when the service throws a NotFoundException") {
        it("returns a Json response with internal server error and status 404") {
          val exception = new NotFoundException("Exception Message")

          when(fakeDao.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus shouldBe NOT_FOUND.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) shouldBe notFoundJsonError
        }
      }

      describe("when the service throws an InternalServerErrorException") {
        it("returns a Json response with internal server error and status 500") {
          val exception = new InternalServerErrorException("Exception Message")

          when(fakeDao.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) shouldBe internalServerErrorJsonError
        }
      }

      describe("when the service throws a custom WebApplicationException") {
        describe("when the service throws a WebApplicationException") {
          it("returns a Json response with internal server error and status 500") {
            val exception = new CustomWebApplicationException("Exception Message")

            when(fakeDao.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
            Json.parse(response.getContentAsString) shouldBe internalServerErrorJsonError
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

            when(fakeDao.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus shouldBe BAD_REQUEST.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
            Json.parse(response.getContentAsString) shouldBe badRequestJsonError
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

            when(fakeDao.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus shouldBe UNAUTHORIZED.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
            Json.parse(response.getContentAsString) shouldBe unauthorizedJsonError
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

            when(fakeDao.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus shouldBe FORBIDDEN.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
            Json.parse(response.getContentAsString) shouldBe forbiddenJsonError
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

            when(fakeDao.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus shouldBe NOT_FOUND.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
            Json.parse(response.getContentAsString) shouldBe notFoundJsonError
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

            when(fakeDao.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
            Json.parse(response.getContentAsString) shouldBe internalServerErrorJsonError
          }
        }

        describe("when the service throws a custom BadRequestException") {
          it("returns a Json response with internal server error and status 400") {
            val exception = new CustomBadRequestException("Exception Message")

            when(fakeDao.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus shouldBe BAD_REQUEST.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
            Json.parse(response.getContentAsString) shouldBe badRequestJsonError
          }
        }

        describe("when the service throws a custom NotAuthorizedException") {
          it("returns a Json response with internal server error and status 401") {
            val exception = new CustomNotAuthorizedException("Exception Message")

            when(fakeDao.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus shouldBe UNAUTHORIZED.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
            Json.parse(response.getContentAsString) shouldBe unauthorizedJsonError
          }
        }

        describe("when the service throws a custom ForbiddenException") {
          it("returns a Json response with internal server error and status 403") {
            val exception = new CustomForbiddenException("Exception Message")

            when(fakeDao.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus shouldBe FORBIDDEN.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
            Json.parse(response.getContentAsString) shouldBe forbiddenJsonError
          }
        }

        describe("when the service throws a custom NotFoundException") {
          it("returns a Json response with internal server error and status 404") {
            val exception = new CustomNotFoundException("Exception Message")

            when(fakeDao.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus shouldBe NOT_FOUND.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
            Json.parse(response.getContentAsString) shouldBe notFoundJsonError
          }
        }

        describe("when the service throws a custom InternalServerErrorException") {
          it("returns a Json response with internal server error and status 500") {
            val exception = new CustomInternalServerErrorException("Exception Message")

            when(fakeDao.fakeMethod()).thenThrow(exception)

            dispatcher.invoke(request, response)

            response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
            Json.parse(response.getContentAsString) shouldBe internalServerErrorJsonError
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

          when(fakeDao.fakeMethod()).thenThrow(exception)

          dispatcher.invoke(request, response)

          response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getOutputHeaders.getFirst(CONTENT_TYPE) shouldBe MediaType.APPLICATION_JSON
          Json.parse(response.getContentAsString) shouldBe internalServerErrorJsonError
        }
      }
    }

    describe("unit testing the WebApplicationExceptionMapper class") {
      describe("when a WebApplicationException is thrown") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exception = new WebApplicationException("Exception Message")

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
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

          response.getStatus shouldBe BAD_REQUEST.getStatusCode
          response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code shouldBe BAD_REQUEST.getStatusCode
          jsonError.message shouldBe BAD_REQUEST.getReasonPhrase
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

          response.getStatus shouldBe UNAUTHORIZED.getStatusCode
          response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code shouldBe UNAUTHORIZED.getStatusCode
          jsonError.message shouldBe UNAUTHORIZED.getReasonPhrase
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

          response.getStatus shouldBe FORBIDDEN.getStatusCode
          response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code shouldBe FORBIDDEN.getStatusCode
          jsonError.message shouldBe FORBIDDEN.getReasonPhrase
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

          response.getStatus shouldBe NOT_FOUND.getStatusCode
          response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code shouldBe NOT_FOUND.getStatusCode
          jsonError.message shouldBe NOT_FOUND.getReasonPhrase
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

          response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
        }
      }

      describe("when a BadRequestException is thrown") {
        it("is mapped to a Json response with bad request error and status 400") {
          val exception = new BadRequestException("Exception Message")

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus shouldBe BAD_REQUEST.getStatusCode
          response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code shouldBe BAD_REQUEST.getStatusCode
          jsonError.message shouldBe BAD_REQUEST.getReasonPhrase
        }
      }

      describe("when a NotAuthorizedException is thrown") {
        it("is mapped to a Json response with unauthorized error and status 401") {
          val exception = new NotAuthorizedException("Exception Message")

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus shouldBe UNAUTHORIZED.getStatusCode
          response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code shouldBe UNAUTHORIZED.getStatusCode
          jsonError.message shouldBe UNAUTHORIZED.getReasonPhrase
        }
      }

      describe("when a ForbiddenException is thrown") {
        it("is mapped to a Json response with forbidden error and status 403") {
          val exception = new ForbiddenException("Exception Message")

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus shouldBe FORBIDDEN.getStatusCode
          response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code shouldBe FORBIDDEN.getStatusCode
          jsonError.message shouldBe FORBIDDEN.getReasonPhrase
        }
      }

      describe("when a NotFoundException is thrown") {
        it("is mapped to a Json response with not found error and status 404") {
          val exception = new NotFoundException("Exception Message")

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus shouldBe NOT_FOUND.getStatusCode
          response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code shouldBe NOT_FOUND.getStatusCode
          jsonError.message shouldBe NOT_FOUND.getReasonPhrase
        }
      }

      describe("when an InternalServerErrorException is thrown") {
        it("is mapped to a Json response with internal server error and status 500") {
          val exception = new InternalServerErrorException("Exception Message")

          val response = webApplicationExceptionMapper.toResponse(exception)

          response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
        }
      }

      describe("when a custom WebApplicationException is thrown") {
        describe("when a custom WebApplicationException is thrown") {
          it("is mapped to a Json response with internal server error and status 500") {
            val exception = new CustomWebApplicationException("Exception Message")

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
            jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
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

            response.getStatus shouldBe BAD_REQUEST.getStatusCode
            response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code shouldBe BAD_REQUEST.getStatusCode
            jsonError.message shouldBe BAD_REQUEST.getReasonPhrase
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

            response.getStatus shouldBe UNAUTHORIZED.getStatusCode
            response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code shouldBe UNAUTHORIZED.getStatusCode
            jsonError.message shouldBe UNAUTHORIZED.getReasonPhrase
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

            response.getStatus shouldBe FORBIDDEN.getStatusCode
            response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code shouldBe FORBIDDEN.getStatusCode
            jsonError.message shouldBe FORBIDDEN.getReasonPhrase
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

            response.getStatus shouldBe NOT_FOUND.getStatusCode
            response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code shouldBe NOT_FOUND.getStatusCode
            jsonError.message shouldBe NOT_FOUND.getReasonPhrase
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

            response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
            jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
          }
        }

        describe("when a custom BadRequestException is thrown") {
          it("is mapped to a Json response with bad request error and status 400") {
            val exception = new CustomBadRequestException("Exception Message")

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus shouldBe BAD_REQUEST.getStatusCode
            response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code shouldBe BAD_REQUEST.getStatusCode
            jsonError.message shouldBe BAD_REQUEST.getReasonPhrase
          }
        }

        describe("when a custom NotAuthorizedException is thrown") {
          it("is mapped to a Json response with unauthorized error and status 401") {
            val exception = new CustomNotAuthorizedException("Exception Message")

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus shouldBe UNAUTHORIZED.getStatusCode
            response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code shouldBe UNAUTHORIZED.getStatusCode
            jsonError.message shouldBe UNAUTHORIZED.getReasonPhrase
          }
        }

        describe("when a custom ForbiddenException is thrown") {
          it("is mapped to a Json response with forbidden error and status 403") {
            val exception = new CustomForbiddenException("Exception Message")

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus shouldBe FORBIDDEN.getStatusCode
            response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code shouldBe FORBIDDEN.getStatusCode
            jsonError.message shouldBe FORBIDDEN.getReasonPhrase
          }
        }

        describe("when a custom NotFoundException is thrown") {
          it("is mapped to a Json response with not found error and status 404") {
            val exception = new CustomNotFoundException("Exception Message")

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus shouldBe NOT_FOUND.getStatusCode
            response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code shouldBe NOT_FOUND.getStatusCode
            jsonError.message shouldBe NOT_FOUND.getReasonPhrase
          }
        }

        describe("when a custom InternalServerErrorException is thrown") {
          it("is mapped to a Json response with internal server error and status 500") {
            val exception = new CustomInternalServerErrorException("Exception Message")

            val response = webApplicationExceptionMapper.toResponse(exception)

            response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
            response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

            val jsonError = response.getEntity.asInstanceOf[JsonError]
            jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
            jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
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

          response.getStatus shouldBe INTERNAL_SERVER_ERROR.getStatusCode
          response.getMediaType.toString shouldBe MediaType.APPLICATION_JSON

          val jsonError = response.getEntity.asInstanceOf[JsonError]
          jsonError.code shouldBe INTERNAL_SERVER_ERROR.getStatusCode
          jsonError.message shouldBe INTERNAL_SERVER_ERROR.getReasonPhrase
        }
      }
    }
  }
}

class CustomWebApplicationExceptionWithResponse(val message: String, val response: Response)
    extends WebApplicationException(message, response)

class CustomWebApplicationException(val message: String) extends WebApplicationException(message)

class CustomBadRequestException(val message: String) extends BadRequestException(message)

class CustomNotAuthorizedException(val message: String) extends NotAuthorizedException(message)

class CustomForbiddenException(val message: String) extends ForbiddenException(message)

class CustomNotFoundException(val message: String) extends NotFoundException(message)

class CustomInternalServerErrorException(val message: String)
    extends InternalServerErrorException(message)
