package com.cerner.beadledom.resteasy;

import javax.inject.Provider;

/**
 * Provides a fully-constructed instance of {@link TestJaxRsProvider}.
 *
 * @author Charan Panchagnula
 */
public class TestProvider implements Provider<TestJaxRsProvider> {

    TestProvider() {
    }

    @Override
    public TestJaxRsProvider get() {
        return new TestJaxRsProvider();
    }
}
