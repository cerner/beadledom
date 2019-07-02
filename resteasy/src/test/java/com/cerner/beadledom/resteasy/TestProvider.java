package com.cerner.beadledom.resteasy;

import javax.inject.Provider;

/**
 * Provides a fully-constructed instance of {@link TestJaxRsProvider}.
 */
public class TestProvider implements Provider<TestJaxRsProvider> {

    /**
     * Constructor for TestProvider.
     */
    TestProvider() {
    }

    @Override
    public TestJaxRsProvider get() {
        return new TestJaxRsProvider();
    }
}