#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import ${package}.service.resource.HelloWorldResource;
import com.google.inject.PrivateModule;

public class ${name}Module extends PrivateModule {

  protected void configure() {
    bind(HelloWorldResource.class);

    // Note: All clients that will be used should be installed in this module to avoid registering
    // client JAX-RS resources with the server. The Correlation ID header name for clients could
    // also be configured/overridden here.

    expose(HelloWorldResource.class);
  }
}
