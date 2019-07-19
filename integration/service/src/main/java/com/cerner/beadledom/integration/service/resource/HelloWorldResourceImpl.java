package com.cerner.beadledom.integration.service.resource;

import com.cerner.beadledom.integration.api.HelloWorldResource;
import com.cerner.beadledom.integration.api.model.HelloWorldDto;
import com.cerner.beadledom.jaxrs.GenericResponse;
import com.cerner.beadledom.jaxrs.GenericResponses;

public class HelloWorldResourceImpl implements HelloWorldResource {

  public GenericResponse<HelloWorldDto> getHelloWorld() {
    return GenericResponses
        .ok(HelloWorldDto.create("Beadledom", "Hello World!"))
        .build();
  }
}
