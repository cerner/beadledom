package com.cerner.beadledom.jaxrs


import java.net.URI
import java.util
import java.util.{Collections, Date, Locale}
import javax.ws.rs.core._

import com.cerner.beadledom.testing.UnitSpec
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar

class ResponseToStringWrapperSpec extends UnitSpec with MockitoSugar {
  describe("ResponseToStringWrapper") {

    describe("#toString") {
      it("includes all Response values in String") {
        val rawResponse = mock[Response]
        val date = new Date(0L)

        when(rawResponse.getStatus).thenReturn(200)
        when(rawResponse.getMediaType).thenReturn(MediaType.APPLICATION_JSON_TYPE)
        when(rawResponse.getDate).thenReturn(date)
        when(rawResponse.getLength).thenReturn(123)
        when(rawResponse.getLastModified).thenReturn(date)
        when(rawResponse.getEntityTag).thenReturn(new EntityTag("tag-value"))
        when(rawResponse.getLanguage).thenReturn(Locale.ENGLISH)
        when(rawResponse.getLocation).thenReturn(new URI("http://localhost"))
        val headers  = new MultivaluedHashMap[String, Object]()
        headers.put("header-key", util.Arrays.asList("header-value"))
        when(rawResponse.getHeaders).thenReturn(headers)
        when(rawResponse.getCookies).thenReturn(Collections.singletonMap("my-cookie", new NewCookie("key", "value")))
        when(rawResponse.getLinks).thenReturn(new util.HashSet[Link]())

        new ResponseToStringWrapper(rawResponse).toString mustBe
        s"""Response{status=200, mediaType=application/json, date=$date, length=123,
          |lastModified=$date, entityTag="tag-value", language=en, location=http://localhost,
          |headers={header-key=[header-value]}, cookies={my-cookie=key=value;Version=1}, links=[] }""".stripMargin.replaceAll("\n", " ")
      }
    }

  }
}
