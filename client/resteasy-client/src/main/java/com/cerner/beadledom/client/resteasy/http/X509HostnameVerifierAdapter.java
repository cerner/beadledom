package com.cerner.beadledom.client.resteasy.http;

import java.io.IOException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.apache.http.conn.ssl.X509HostnameVerifier;

/**
 * An adapter to make a {@link HostnameVerifier} compatible with the HttpClient
 * {@link X509HostnameVerifier}.
 *
 * @author John Leacox
 * @since 1.0
 */
public class X509HostnameVerifierAdapter implements X509HostnameVerifier {
  private final HostnameVerifier verifier;

  private X509HostnameVerifierAdapter(HostnameVerifier verifier) {
    this.verifier = verifier;
  }

  /**
   * Adapts a {@link HostnameVerifier} to be compatible with the HttpClient
   * {@link X509HostnameVerifier}.
   */
  public static X509HostnameVerifier adapt(HostnameVerifier verifier) {
    if (verifier == null) {
      throw new NullPointerException("verifier:null");
    }

    if (verifier instanceof X509HostnameVerifier) {
      return (X509HostnameVerifier) verifier;
    }

    return new X509HostnameVerifierAdapter(verifier);
  }

  @Override
  public void verify(String host, SSLSocket ssl) throws IOException {
    if (!verifier.verify(host, ssl.getSession())) {
      throw new SSLException("Hostname verification failed for " + host);
    }
  }

  @Override
  public void verify(String host, X509Certificate cert) throws SSLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean verify(String hostname, SSLSession session) {
    return verifier.verify(hostname, session);
  }
}
