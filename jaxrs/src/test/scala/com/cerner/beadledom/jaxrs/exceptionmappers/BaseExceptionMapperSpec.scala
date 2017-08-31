package com.cerner.beadledom.jaxrs.exceptionmappers

import com.cerner.beadledom.jaxrs.models.JsonError

import com.google.gson.Gson

import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, FunSpec, ShouldMatchers}

import play.api.libs.json.{JsValue, Json}

import javax.ws.rs.core.Response.Status

/**
  * Base spec to be used by ExceptionMapper spec classes.
  *
  * @author Cal Fisher
  */
class BaseExceptionMapperSpec
    extends FunSpec with ShouldMatchers with BeforeAndAfterEach with MockitoSugar {

  val gson = new Gson

  def createJsonError(status: Status): JsValue = {
    Json.parse(gson.toJson(
      JsonError.builder
          .code(status.getStatusCode)
          .message(status.getReasonPhrase)
          .build))
  }
}
