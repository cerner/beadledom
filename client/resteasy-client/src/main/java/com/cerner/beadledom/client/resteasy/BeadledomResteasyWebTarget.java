package com.cerner.beadledom.client.resteasy;

import com.cerner.beadledom.client.BeadledomWebTarget;
import com.cerner.beadledom.client.proxy.GenericResponseResourceProxyFactory;
import java.net.URI;
import java.util.Map;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

/**
 * Resteasy wrapper for the BeadledomWebTarget.
 *
 * @author Sundeep Paruvu
 * @since 2.0
 */
class BeadledomResteasyWebTarget extends BeadledomWebTarget {
  private final ResteasyWebTarget webTarget;

  private BeadledomResteasyWebTarget(ResteasyWebTarget webTarget) {
    this.webTarget = webTarget;
  }

  public static BeadledomResteasyWebTarget create(ResteasyWebTarget webTarget) {
    if (webTarget == null) {
      throw new NullPointerException("webTarget:null");
    }
    return new BeadledomResteasyWebTarget(webTarget);
  }

  @Override
  public <T> T proxy(Class<T> proxyInterface) {
    return new GenericResponseResourceProxyFactory(new ResteasyResourceProxyFactory(webTarget))
        .proxy(proxyInterface);
  }

  @Override
  public URI getUri() {
    return webTarget.getUri();
  }

  @Override
  public UriBuilder getUriBuilder() {
    return webTarget.getUriBuilder();
  }

  @Override
  public BeadledomResteasyWebTarget path(String path) {
    webTarget.path(path);
    return this;
  }

  @Override
  public BeadledomWebTarget resolveTemplate(String name, Object value) {
    webTarget.resolveTemplate(name, value);
    return this;
  }

  @Override
  public BeadledomWebTarget resolveTemplate(String name, Object value, boolean encodeSlashInPath) {
    webTarget.resolveTemplate(name, value, encodeSlashInPath);
    return this;
  }

  @Override
  public BeadledomWebTarget resolveTemplateFromEncoded(String name, Object value) {
    webTarget.resolveTemplateFromEncoded(name, value);
    return this;
  }

  @Override
  public BeadledomWebTarget resolveTemplates(Map<String, Object> templateValues) {
    webTarget.resolveTemplates(templateValues);
    return this;
  }

  @Override
  public BeadledomWebTarget resolveTemplates(
      Map<String, Object> templateValues, boolean encodeSlashInPath) {
    webTarget.resolveTemplates(templateValues, encodeSlashInPath);
    return this;
  }

  @Override
  public BeadledomWebTarget resolveTemplatesFromEncoded(Map<String, Object> templateValues) {
    webTarget.resolveTemplatesFromEncoded(templateValues);
    return this;
  }

  @Override
  public BeadledomWebTarget matrixParam(String name, Object... values) {
    webTarget.matrixParam(name, values);
    return this;
  }

  @Override
  public BeadledomWebTarget queryParam(String name, Object... values) {
    webTarget.queryParam(name, values);
    return this;
  }

  @Override
  public Invocation.Builder request() {
    return webTarget.request();
  }

  @Override
  public Invocation.Builder request(String... acceptedResponseTypes) {
    return webTarget.request(acceptedResponseTypes);
  }

  @Override
  public Invocation.Builder request(MediaType... acceptedResponseTypes) {
    return webTarget.request(acceptedResponseTypes);
  }

  @Override
  public Configuration getConfiguration() {
    return webTarget.getConfiguration();
  }

  @Override
  public BeadledomWebTarget property(String name, Object value) {
    webTarget.property(name, value);
    return this;
  }

  @Override
  public BeadledomWebTarget register(Class<?> componentClass) {
    webTarget.register(componentClass);
    return this;
  }

  @Override
  public BeadledomWebTarget register(Class<?> componentClass, int priority) {
    webTarget.register(componentClass, priority);
    return this;
  }

  @Override
  public BeadledomWebTarget register(Class<?> componentClass, Class<?>... contracts) {
    webTarget.register(componentClass, contracts);
    return this;
  }

  @Override
  public BeadledomWebTarget register(
      Class<?> componentClass, Map<Class<?>, Integer> contracts) {
    webTarget.register(componentClass, contracts);
    return this;
  }

  @Override
  public BeadledomWebTarget register(Object component) {
    webTarget.register(component);
    return this;
  }

  @Override
  public BeadledomWebTarget register(Object component, int priority) {
    webTarget.register(component, priority);
    return this;
  }

  @Override
  public BeadledomWebTarget register(Object component, Class<?>... contracts) {
    webTarget.register(component, contracts);
    return this;
  }

  @Override
  public BeadledomWebTarget register(
      Object component, Map<Class<?>, Integer> contracts) {
    webTarget.register(component, contracts);
    return this;
  }
}
