#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import ${package}.service.resource.HelloWorldResourceImpl;
import ${package}.api.HelloWorldResource;
import com.google.inject.PrivateModule;

public class ${name}Module extends PrivateModule {

  protected void configure() {
    bind(HelloWorldResource.class).to(HelloWorldResourceImpl.class);

    expose(HelloWorldResource.class);
  }
}
