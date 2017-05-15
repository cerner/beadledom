package com.cerner.beadledom.client.example;

import com.cerner.beadledom.client.example.model.JsonOne;

/**
 * Service implementation of {@link ResourceOne}.
 *
 * @author John Leacox
 */
public class ResourceOneImpl implements ResourceOne {
  @Override
  public JsonOne get() {
    return JsonOne.create("One", "Hello World");
  }

  @Override
  public JsonOne echo(JsonOne json) {
    return json;
  }

  @Override
  public JsonOne patch(JsonOne json) {
    return json.builder()
        .setHello("Hola")
        .setOne("New Json")
        .build();
  }
}
