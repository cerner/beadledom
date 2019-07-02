package com.cerner.beadledom.resteasy;

import com.cerner.beadledom.guice.BindingAnnotations;

import javax.inject.Provider;
import java.lang.annotation.Annotation;

/**
 * Provides a fully-constructed instance of {@link TestJaxRsProvider} by taking in a binding annotation.
 */
public class TestProviderWithBindingAnnotation implements Provider<TestJaxRsProvider> {

  /**
   * Constructor for TestProviderWithBindingAnnotation.
   * @param bindingAnnotation The binding annotation.
   */
  TestProviderWithBindingAnnotation(Class<? extends Annotation> bindingAnnotation) {
    if (bindingAnnotation == null) {
      throw new NullPointerException("bindingAnnotation:null");
    } else {
      BindingAnnotations.checkIsBindingAnnotation(bindingAnnotation);
    }
  }

  @Override
  public TestJaxRsProvider get() {
    return new TestJaxRsProvider();
  }
}
