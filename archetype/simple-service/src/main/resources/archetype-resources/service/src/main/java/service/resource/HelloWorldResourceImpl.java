#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service.resource;

import ${package}.api.HelloWorldResource;
import ${package}.api.model.HelloWorldDto;
import com.cerner.beadledom.jaxrs.GenericResponse;
import com.cerner.beadledom.jaxrs.GenericResponses;

public class HelloWorldResourceImpl implements HelloWorldResource {

  public GenericResponse<HelloWorldDto> getHelloWorld() {
    return GenericResponses
        .ok(HelloWorldDto.create("Beadledom", "Hello World!"))
        .build();
  }
}
