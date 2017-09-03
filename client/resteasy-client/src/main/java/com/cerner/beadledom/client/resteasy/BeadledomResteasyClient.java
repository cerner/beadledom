package com.cerner.beadledom.client.resteasy;

import com.cerner.beadledom.client.BeadledomClient;
import java.net.URI;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;

public class BeadledomResteasyClient extends BeadledomClient {
  private final ResteasyClient client;

  private BeadledomResteasyClient(ResteasyClient client) {
    this.client = client;
  }

  /**
   * Creates an instance of {@link BeadledomResteasyClient}.
   */
  public static BeadledomResteasyClient create(ResteasyClient client) {
    if (client == null) {
      throw new NullPointerException("client: null");
    }

    return new BeadledomResteasyClient(client);
  }

  @Override
  public void close() {
    client.close();
  }

  @Override
  public boolean isClosed() {
    return client.isClosed();
  }

  @Override
  public BeadledomResteasyWebTarget target(String uri) {
    return BeadledomResteasyWebTarget.create(client.target(uri));
  }

  @Override
  public BeadledomResteasyWebTarget target(URI uri) {
    return BeadledomResteasyWebTarget.create(client.target(uri));
  }

  @Override
  public BeadledomResteasyWebTarget target(UriBuilder uriBuilder) {
    return BeadledomResteasyWebTarget.create(client.target(uriBuilder));
  }

  @Override
  public BeadledomResteasyWebTarget target(Link link) {
    return BeadledomResteasyWebTarget.create(client.target(link));
  }

  @Override
  public Invocation.Builder invocation(Link link) {
    return client.invocation(link);
  }

  @Override
  public SSLContext getSslContext() {
    return client.getSslContext();
  }

  @Override
  public HostnameVerifier getHostnameVerifier() {
    return client.getHostnameVerifier();
  }

  @Override
  public Configuration getConfiguration() {
    return client.getConfiguration();
  }

  @Override
  public BeadledomResteasyClient property(String name, Object value) {
    client.property(name, value);
    return this;
  }

  @Override
  public BeadledomResteasyClient register(Class<?> componentClass) {
    client.register(componentClass);
    return this;
  }

  @Override
  public BeadledomResteasyClient register(Class<?> componentClass, int priority) {
    client.register(componentClass, priority);
    return this;
  }

  @Override
  public BeadledomResteasyClient register(Class<?> componentClass, Class<?>... contracts) {
    client.register(componentClass, contracts);
    return this;
  }

  @Override
  public BeadledomResteasyClient register(
      Class<?> componentClass, Map<Class<?>, Integer> contracts) {
    client.register(componentClass, contracts);
    return this;
  }

  @Override
  public BeadledomResteasyClient register(Object component) {
    client.register(component);
    return this;
  }

  @Override
  public BeadledomResteasyClient register(Object component, int priority) {
    client.register(component, priority);
    return this;
  }

  @Override
  public BeadledomResteasyClient register(Object component, Class<?>... contracts) {
    client.register(component, contracts);
    return this;
  }

  @Override
  public BeadledomResteasyClient register(Object component, Map<Class<?>, Integer> contracts) {
    client.register(component, contracts);
    return this;
  }
}
