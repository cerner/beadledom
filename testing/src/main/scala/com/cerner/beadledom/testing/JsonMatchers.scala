package com.cerner.beadledom.testing

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper, SerializationFeature}
import org.scalatest.matchers.{BeMatcher, MatchResult, Matcher}
import org.skyscreamer.jsonassert.{JSONCompare, JSONCompareMode}
import play.api.libs.json.{JsValue, Json}
import scala.util.Try

/**
 * Include this trait in the spec and to test JSON values as follows:
 *
 * {{{
 * "{\"foo\":\"bar\"} should equalJson("{\"foo\":\"baz\"}
 * }}}
 *
 * In addition to strings, either side can also be a play-json JsValue or Jackson JsonNode; these
 * can be used interchangeably.
 *
 * If the first string is not valid json, the test will fail and the string will be printed
 * verbatim in the failure message.
 *
 * If the two json values are not equivalent, both will be printed, along with a diff.
 *
 * Strings can be validated as follows
 *
 * {{{
 * "{\"foo\":\"bar\"}" should be (validJson)
 * }}}
 */
trait JsonMatchers {
  /**
   * A matcher that checks whether the given object is or can be parsed into a json value.
   */
  val validJson =
    BeMatcher { (left: Any) =>
      MatchResult(
        Try(parse(left)).isSuccess,
        s"Was not valid json:\n$left",
        s"Was valid json:\n$left")
    }
  private val objectMapper = new ObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT)
      .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)

  /**
   * A matcher that compares two json values.
   *
   * @param right the expected json, as a string, JsValue, or JsonNode
   */
  def equalJson(right: Any) =
    Matcher { (left: Any) =>
      // First check that we'll be able to parse it, and if not, return validJson's error message.
      val isValid = validJson(left)
      if (!isValid.matches) {
        isValid
      } else {
        val leftJson = parse(left)
        val rightJson = parse(right)
        MatchResult(
          leftJson.equals(rightJson),
          s"Json did not match.\n\n${comparison(leftJson, rightJson)}",
          s"Json unexpectedly matched.\n\n${comparison(leftJson, rightJson)}"
        )
      }
    }

  private def parse(obj: Any): JsonNode = obj match {
    case str: String => objectMapper.readTree(str)
    case json: JsValue => objectMapper.readTree(Json.stringify(json))
    case json: JsonNode => json
    case _ => throw new IllegalArgumentException(s"Cannot parse json from $obj")
  }

  private def comparison(left: JsonNode, right: JsonNode) =
    s"JSONassert diff: ${jsonAssertDiff(left, right)}\n\n" +
        s"Actual json:\n\n${pretty(left)}\n\n" +
        s"Expected json:\n\n${pretty(right)}"

  private def pretty(json: JsonNode) = objectMapper.writeValueAsString(json)

  private def jsonAssertDiff(left: JsonNode, right: JsonNode) =
    Try(JSONCompare.compareJSON(
      objectMapper.writeValueAsString(right),
      objectMapper.writeValueAsString(left),
      JSONCompareMode.STRICT).toString).getOrElse("<jsonassert diff unavailable>")
}

/**
 * This allows you to import the matchers without using the trait, if desired.
 */
object JsonMatchers extends JsonMatchers
