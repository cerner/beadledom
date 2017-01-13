package com.cerner.beadledom.client;

/**
 * Test json model.
 *
 * @author John Leacox
 */
public class JsonModel {
  private String fieldOne;
  private String fieldTwo;
  private String emptyField = null;

  public JsonModel() {
  }

  public void setFieldOne(String fieldOne) {
    this.fieldOne = fieldOne;
  }

  public void setFieldTwo(String fieldTwo) {
    this.fieldTwo = fieldTwo;
  }

  public void setEmptyField(String emptyField) {
    this.emptyField = emptyField;
  }

  public String getFieldOne() {
    return fieldOne;
  }

  public String getFieldTwo() {
    return fieldTwo;
  }

  public String getEmptyField() {
    return emptyField;
  }
}
