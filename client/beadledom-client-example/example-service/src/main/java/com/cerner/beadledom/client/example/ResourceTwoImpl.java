package com.cerner.beadledom.client.example;

import com.cerner.beadledom.client.example.model.JsonTwo;

/**
 * Service implementation of {@link ResourceTwo}.
 * 
 * @author John Leacox
 */
public class ResourceTwoImpl implements ResourceTwo {
  @Override
  public JsonTwo get() {
    return JsonTwo.create("Two", "Hello World");
  }

  @Override
  public JsonTwo echo(JsonTwo json) {
    return json;
  }

  @Override
  public JsonTwo patch(JsonTwo json) {
    return json.builder()
        .setHello("Hola2")
        .setTwo("New Json")
        .build();
  }
}
