#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "HelloWorldDto")
public class HelloWorldDto {

  @ApiModelProperty(required = true, value = "name of message author")
  private String name;
  @ApiModelProperty(required = true, value = "message content")
  private String helloWorldMessage;

  private HelloWorldDto(String name, String message) {
    this.name = Preconditions.checkNotNull(name);
    this.helloWorldMessage = Preconditions.checkNotNull(message);
  }

  /**
   * Creates a new {@link HelloWorldDto} instance.
   *
   * @param name author to be associated with the message
   * @param message the message to be returned
   * @return {@link HelloWorldDto} encapsulating a name and message
   */
  @JsonCreator
  public static HelloWorldDto create(@JsonProperty("name") String name, @JsonProperty("hello_world_message") String message) {
    return new HelloWorldDto(name, message);
  }

  /** Returns name. */
  public String getName() {
    return name;
  }

  /** Returns hello world message. */
  public String getHelloWorldMessage() {
    return helloWorldMessage;
  }
}
