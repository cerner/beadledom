package com.cerner.beadledom.jackson.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Class to test edge cases in processing.
 */
public class DeepModel {

  @JsonProperty
  private final String id;
  @JsonProperty("stupid_name")
  private final List<EmbeddedDeep> embeddedDeepList;
  @JsonIgnore
  private String unlovedField;

  public DeepModel(String id, List<EmbeddedDeep> embeddedDeepList) {
    this.id = id;
    this.embeddedDeepList = embeddedDeepList;
  }

  public static class EmbeddedDeep {
    @JsonProperty("id")
    private final String id;
    @JsonProperty("no_escape")
    private final List<EmbeddedDeeper> embeddedDeeperList;

    public EmbeddedDeep(String id, List<EmbeddedDeeper> embeddedDeeperList) {
      this.id = id;
      this.embeddedDeeperList = embeddedDeeperList;
    }
  }

  public static class EmbeddedDeeper {
    @JsonProperty("id")
    private final String id;
    @JsonProperty("so_far_in")
    private final List<InChinaNow> outTheOtherSide;

    public EmbeddedDeeper(String id, List<InChinaNow> outTheOtherSide) {
      this.id = id;
      this.outTheOtherSide = outTheOtherSide;
    }
  }

  public static class InChinaNow {
    @JsonProperty
    private final String id;
    @JsonProperty
    private final int miles;

    public InChinaNow(String id, int miles) {
      this.id = id;
      this.miles = miles;
    }
  }
}
