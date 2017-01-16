#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client;

public class ${name}Config {
  private final String baseUrl;

  // If OAuth is being used this is a natural place to encapsulate the values
  public ${name}Config(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getBaseUrl() {
    return baseUrl;
  }
}
