package com.cerner.beadledom.resteasy;

import javax.inject.Provider;
import java.lang.annotation.Annotation;

/**
 * Provides a fully-constructed instance of {@link TestJaxRsProvider} by taking in a binding annotation.
 *
 * @author Charan Panchagnula
 */
public class TestProviderWithBindingAnnotation implements Provider<TestJaxRsProvider> {

  TestProviderWithBindingAnnotation(Class<? extends Annotation> bindingAnnotation) {
    if (bindingAnnotation == null) {
      throw new NullPointerException("bindingAnnotation:null");
    }
  }

  @Override
  public TestJaxRsProvider get() {
    return new TestJaxRsProvider();
  }
}
