package com.cerner.beadledom.testing

import com.cerner.beadledom.json.common.model.JsonError

import com.fasterxml.jackson.databind.ObjectMapper

import org.scalatest.matchers.{MatchResult, Matcher}

import javax.ws.rs.core.Response.Status._

/**
  * [[JsonError]] matchers.
  *
  * @author Brian van de Boogaard
  */
trait JsonErrorMatchers {
  val mapper = new ObjectMapper()

  class JsonErrorResponse(expectedStatus: Int, expectedMessage: String) extends Matcher[String] {
    def apply(left: String) = {
      val jsonError = mapper.readValue(left, classOf[JsonError])
      val code = jsonError.code()
      val message = jsonError.message()
      MatchResult(
        code == expectedStatus && message.equals(expectedMessage),
        s"""expected: [code: $expectedStatus, message: $expectedMessage] actual: [code: $code, message: $message]""",
        s"""expected: [code: $expectedStatus, message: $expectedMessage] matched: [code: $code, message: $message]"""
      )
    }
  }

  class BadRequestErrorMatcher(expectedMessage: String)
      extends JsonErrorResponse(BAD_REQUEST.getStatusCode, expectedMessage)
  class UnauthorizedErrorMatcher(expectedMessage: String)
      extends JsonErrorResponse(UNAUTHORIZED.getStatusCode, expectedMessage)
  class ForbiddenErrorMatcher(expectedMessage: String)
      extends JsonErrorResponse(FORBIDDEN.getStatusCode, expectedMessage)
  class NotFoundErrorMatcher(expectedMessage: String)
      extends JsonErrorResponse(NOT_FOUND.getStatusCode, expectedMessage)
  class InternalServerErrorMatcher(expectedMessage: String)
      extends JsonErrorResponse(INTERNAL_SERVER_ERROR.getStatusCode, expectedMessage)

  def beBadRequestError(expectedMessage: String = BAD_REQUEST.getReasonPhrase) =
    new BadRequestErrorMatcher(expectedMessage)
  def beUnauthorizedError(expectedMessage: String = UNAUTHORIZED.getReasonPhrase) =
    new UnauthorizedErrorMatcher(expectedMessage)
  def beForbiddenError(expectedMessage: String = FORBIDDEN.getReasonPhrase) =
    new ForbiddenErrorMatcher(expectedMessage)
  def beNotFoundError(expectedMessage: String = NOT_FOUND.getReasonPhrase) =
    new NotFoundErrorMatcher(expectedMessage)
  def beInternalServerError(expectedMessage: String = INTERNAL_SERVER_ERROR.getReasonPhrase) =
    new InternalServerErrorMatcher(expectedMessage)
  def beJsonError(expectedStatus: Int, expectedMessage: String) =
    new JsonErrorResponse(expectedStatus, expectedMessage)
}

object JsonErrorMatchers extends JsonErrorMatchers
