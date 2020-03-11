package com.cerner.beadledom.client.jackson;

import com.cerner.beadledom.guice.BindingAnnotations;
import com.cerner.beadledom.guice.dynamicbindings.DynamicBindingProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import java.lang.annotation.Annotation;
import javax.inject.Inject;
import javax.inject.Provider;

/**
 * A Guice provider for {@link JacksonJsonProvider}.
 *
 * @author John Leacox
 * @author Sundeep Paruvu
 * @author Nimesh Subramanian
 * @since 1.0
 * @deprecated As of 4.0, use Retrofit (https://github.com/square/retrofit) instead.
 */
@Deprecated
class JacksonJsonGuiceProvider implements Provider<JacksonJsonProvider> {
  private ObjectMapper objectMapper;
  private final Class<? extends Annotation> clientBindingAnnotation;

  JacksonJsonGuiceProvider(Class<? extends Annotation> clientBindingAnnotation) {
    if (clientBindingAnnotation == null) {
      throw new NullPointerException("clientBindingAnnotation:null");
    }

    BindingAnnotations.checkIsBindingAnnotation(clientBindingAnnotation);
    this.clientBindingAnnotation = clientBindingAnnotation;
  }

  @Inject
  void init(DynamicBindingProvider<ObjectMapper> objectMapperProvider) {
    this.objectMapper = objectMapperProvider.get(clientBindingAnnotation);
  }

  @Override
  public JacksonJsonProvider get() {
    return new JacksonJsonProvider(objectMapper);
  }
}
