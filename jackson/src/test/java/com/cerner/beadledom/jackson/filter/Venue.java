package com.cerner.beadledom.jackson.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

/**
 * Creation of a complex model which shares nested inner classes.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
    "id",
    "name",
    "tenors",
    "guitarists",
    "failures",
    "vocalists"
})
public class Venue {
  @JsonProperty("name")
  public String name;
  @JsonProperty("location")
  public String location;
  @JsonProperty("tenors")
  public List<Tenor> tenors;
  @JsonProperty("vocalists")
  public List<Vocalist> vocalists;
  @JsonProperty("guitarists")
  public List<Musician> guitarists;
  @JsonProperty("failures")
  public List<Failure> failures;
  @JsonProperty("id")
  private String id;

  public Venue(
      String id, String name, String location, List<Tenor> tenors, List<Vocalist> vocalists,
      List<Musician> guitarists, List<Failure> failures) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.tenors = tenors;
    this.vocalists = vocalists;
    this.guitarists = guitarists;
    this.failures = failures;
  }

  /**
   * Base inner class used to test inheritance of both properties and methods for json serialization.
   */
  @JsonPropertyOrder({"id", "name"})
  public static class Musician {
    @JsonProperty("name")
    public String name;
    @JsonProperty("id")
    private String id;

    public Musician(String id, String name) {
      this.id = id;
      this.name = name;
    }

    @JsonProperty("name")
    public String getName() {
      return name;
    }
  }

  /**
   * Your most basic musician.  Often with bad hair.
   */
  @JsonPropertyOrder({"albums", "hair_style", "id", "name"})
  public static class Vocalist extends Musician {
    @JsonProperty("albums")
    private List<Album> albums;
    @JsonProperty("hair_style")
    private String hairStyle;

    public Vocalist(String id, String name, List<Album> albums, String hairStyle) {
      super(id, name);
      this.albums = albums;
      this.hairStyle = hairStyle;
    }

    public List<Album> getAlbums() {
      return albums;
    }

    @JsonProperty("hair_style")
    public String getHairStyle() {
      return hairStyle;
    }
  }

  /**
   * A specific vocalist.
   */
  @JsonPropertyOrder({"pitch_pipe", "id", "name", "albums", "hair_style"})
  public static class Tenor extends Vocalist {
    @JsonProperty("pitch_pipe")
    private String pitchPipe;

    public Tenor(String id, String name, List<Album> albums, String hairStyle, String pitchPipe) {
      super(id, name, albums, hairStyle);
      this.pitchPipe = pitchPipe;
    }

    @JsonProperty("pitch_pipe")
    public String getPitchPipe() {
      return pitchPipe;
    }
  }

  /**
   * A specific musician.
   */
  @JsonPropertyOrder({"albums", "attitude", "id", "name"})
  public static class Guitarist extends Musician {
    @JsonProperty("albums")
    private List<Album> albums;
    @JsonProperty("attitude")
    private String attitude;

    public Guitarist(String id, String name, List<Album> albums, String attitude) {
      super(id, name);
      this.albums = albums;
      this.attitude = "HAPPY";
    }
  }

  /**
   * Failure of a musician.
   */
  @JsonPropertyOrder({"id", "name", "excuses"})
  public static class Failure extends Musician {
    @JsonProperty("excuses")
    private final List<String> excuses;

    public Failure(String id, String name, List<String> excuses) {
      super(id, name);
      this.excuses = excuses;
    }
  }

  /**
   * More cases.
   */
  @JsonPropertyOrder({"id", "name"})
  public static class Album {
    @JsonProperty("id")
    private String id;
    private String name;

    public Album(String id, String name) {
      this.id = id;
      this.name = name;
    }

    @JsonProperty("name")
    public String getName() {
      return name;
    }
  }
}
