package com.cerner.beadledom.health;

import com.cerner.beadledom.health.api.AvailabilityResource;
import com.cerner.beadledom.health.api.DependenciesResource;
import com.cerner.beadledom.health.api.DiagnosticResource;
import com.cerner.beadledom.health.api.HealthResource;
import com.cerner.beadledom.health.api.VersionResource;
import com.cerner.beadledom.health.internal.HealthChecker;
import com.cerner.beadledom.health.internal.HealthTemplateFactory;
import com.cerner.beadledom.health.resource.AvailabilityResourceImpl;
import com.cerner.beadledom.health.resource.DependenciesResourceImpl;
import com.cerner.beadledom.health.resource.DiagnosticResourceImpl;
import com.cerner.beadledom.health.resource.HealthResourceImpl;
import com.cerner.beadledom.health.resource.VersionResourceImpl;
import com.cerner.beadledom.metadata.ServiceMetadata;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.MultibindingsScanner;
import java.util.Map;
import java.util.Set;
import javax.inject.Singleton;
import javax.ws.rs.core.UriInfo;

/**
 * A Guice module that provides JAX-RS resources to implement the Health Check.
 *
 * <p>With the module installed, you can add new 'dependencies' to your service's health check by
 * adding bindings of {@link com.cerner.beadledom.health.HealthDependency} using the Multibinder.
 *
 * <p>Example:
 *
 * <p><pre><code>
 *     class MyModule extends AbstractModule {
 *      {@literal @}Override
 *       protected void configure() {
 *         Multibinder&lt;HealthDependency&gt; healthDependencyBinder = Multibinder
 *             .newSetBinder(binder(), HealthDependency.class);
 *         healthDependencyBinder.addBinding().to(FoobarHealthDependency.class);
 *       }
 *     }
 *
 *     class FoobarHealthDependency extends HealthDependency {
 *      {@literal @}Override
 *       public HealthStatus checkAvailability() {
 *         if (...) { // check health somehow
 *           return HealthStatus.create(200, "foobar is available");
 *         } else {
 *           return HealthStatus.create(503, "foobar is gone. just gone.");
 *         }
 *       }
 *
 *      {@literal @}Override
 *       public String getName() {
 *         return "foobar";
 *       }
 *     }
 * </code></pre>
 *
 * <p>Provides:
 * <ul>
 *     <li>{@link AvailabilityResource}</li>
 *     <li>{@link HealthResource}</li>
 *     <li>{@link VersionResource}</li>
 *     <li>{@link DiagnosticResource}</li>
 *     <li>{@link DependenciesResource}</li>
 *     <li>The following {@link com.fasterxml.jackson.databind.Module Jackson module} multibindings:
 *       <ul>
 *         <li>{@link com.fasterxml.jackson.datatype.jdk8.Jdk8Module}</li>
 *         <li>{@link com.fasterxml.jackson.datatype.jsr310.JavaTimeModule}</li>
 *       </ul>
 *     </li>
 *     <li>{@link com.cerner.beadledom.health.internal.HealthChecker} (for internal use only)</li>
 *     <li>{@link Map}&lt;{@link String}, {@link com.cerner.beadledom.health.HealthDependency}&gt;
 *     (for internal use only)</li>
 *     <li>{@link com.github.mustachejava.MustacheFactory} with binding annotation
 *     HealthTemplateFactory (for internal use only)</li>
 * </ul>
 *
 * <p>Requires:
 * <ul>
 *     <li>{@link com.cerner.beadledom.metadata.ServiceMetadata}</li>
 *     <li>{@link javax.ws.rs.core.UriInfo} (request-scoped)</li>
 * </ul>
 *
 * <p>Installs:
 * <ul>
 *   <li> {@link MultibindingsScanner} </li>
 * </ul>
 */
public class HealthModule extends AbstractModule {
  @Override
  protected void configure() {
    requireBinding(ServiceMetadata.class);
    requireBinding(UriInfo.class);

    bind(AvailabilityResource.class).to(AvailabilityResourceImpl.class);
    bind(HealthResource.class).to(HealthResourceImpl.class);
    bind(DependenciesResource.class).to(DependenciesResourceImpl.class);
    bind(DiagnosticResource.class).to(DiagnosticResourceImpl.class);
    bind(VersionResource.class).to(VersionResourceImpl.class);
    bind(HealthChecker.class);

    //This is to provide a default binding for HealthDependency,
    // so that services with no HealthDependency bindings can start
    Multibinder<HealthDependency> healthDependencyModuleBinder = Multibinder.newSetBinder(binder(),
        HealthDependency.class);

    Multibinder<Module> jacksonModuleBinder = Multibinder.newSetBinder(binder(), Module.class);
    jacksonModuleBinder.addBinding().to(Jdk8Module.class);
    jacksonModuleBinder.addBinding().to(JavaTimeModule.class);
  }

  /**
   * Convert a Set of HealthDependency instances into a Map of them keyed by dependency name.
   *
   * <p>Fails fast if there are duplicate names.
   */
  @Provides
  @Singleton
  Map<String, HealthDependency> provideHealthDependencyMap(
      Set<HealthDependency> healthDependencySet) {
    Map<String, HealthDependency> result = Maps.newHashMap();
    for (HealthDependency dependency : healthDependencySet) {
      if (result.containsKey(dependency.getName())) {
        throw new IllegalStateException(
            "Dependency name " + dependency.getName() + " is used by both "
                + dependency.getName() + " and " + result.get(dependency.getName()));
      }
      result.put(dependency.getName(), dependency);
    }
    return ImmutableMap.copyOf(result);
  }

  @Provides
  @Singleton
  @HealthTemplateFactory
  MustacheFactory provideMustacheFactory() {
    return new DefaultMustacheFactory(
        "com/cerner/beadledom/health");
  }
}
