#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client;

import com.cerner.beadledom.client.BeadledomClient;
import com.cerner.beadledom.client.BeadledomClientConfiguration;
import com.cerner.beadledom.client.BeadledomClientModule;
import com.cerner.beadledom.client.BeadledomWebTarget;
import com.cerner.beadledom.client.jackson.ObjectMapperClientFeatureModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javax.inject.Singleton;

public class ${name}ClientModule extends AbstractModule {

  @Override
  public void configure() {
    requireBinding(${name}Config.class);

    install(BeadledomClientModule.with(${name}ClientFeature.class));
    install(ObjectMapperClientFeatureModule.with(${name}ClientFeature.class));
  }

  @Provides
  @Singleton
  @${name}ClientFeature
  public BeadledomClientConfiguration provideClientConfiguration() {
    return BeadledomClientConfiguration.builder().build();
  }

  @Provides
  @Singleton
  public HelloWorldResource provideHelloWorldResource(BeadledomWebTarget target) {
    return target.proxy(HelloWorldResource.class);
  }

  @Provides
  @Singleton
  public BeadledomWebTarget provideBeadledomWebTarget(
      @${name}ClientFeature BeadledomClient client, ${name}Config config) {
    return client.target(config.getBaseUrl());
  }
}
