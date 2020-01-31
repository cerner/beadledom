package com.cerner.beadledom.avro

/**
 * Spec tests for {@link SwaggerAvroModelConverter}.
 */
//class SwaggerAvroModelConverterSpec
//    extends FunSpec with Matchers with JsonMatchers with BeforeAndAfterAll {
//  val converter = new SwaggerAvroModelConverter
//
//  override def beforeAll = {
//    ModelConverters.getInstance().addConverter(converter)
//  }
//
//  override def afterAll = {
//    ModelConverters.getInstance().removeConverter(converter)
//  }
//
//  describe("SwaggerAvroModelConverter") {
//    it("reads models correctly") {
//      val expected = Resources.toString(
//        getClass.getClassLoader.getResource("expected-happy-model.json"), Charsets.UTF_8)
//
//      JsonSerializer[HappyModel]
//      JsonSerializer.asJson(ModelConverters.readAll(classOf[HappyModel])) should equalJson(expected)
//    }
//
//    it("allows other converters to handle non-Avro models") {
//      val expected =
//        """
//          |[ {
//          |  "id" : "NonAvroModel",
//          |  "description" : "Sooo boring.",
//          |  "properties" : {
//          |    "foo" : {
//          |      "type" : "string"
//          |    }
//          |  }
//          |} ]
//        """.stripMargin
//      JsonSerializer.asJson(ModelConverters.readAll(classOf[NonAvroModel])) should
//          equalJson(expected)
//    }
//  }
//}
//
//@ApiModel(description = "Sooo boring.")
//class NonAvroModel {
//  val foo = "bar"
//}
