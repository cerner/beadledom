#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client;

import com.cerner.beadledom.client.BeadledomClient;
import com.cerner.beadledom.client.BeadledomWebTarget;

public class ${name}Client {
  private final HelloWorldResource helloWorldResource;

    ${name}Client(BeadledomClient client, ${name}Config config) {
    BeadledomWebTarget target = client.target(config.getBaseUrl());

    helloWorldResource = target.proxy(HelloWorldResource.class);
  }

  public HelloWorldResource helloWorldResource() {
    return helloWorldResource;
  }
}
