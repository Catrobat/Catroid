/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.okhttp;

import com.squareup.okhttp.internal.Platform;
import com.squareup.okhttp.internal.Util;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.SSLSocket;

/**
 * Configuration for the socket connection that HTTP traffic travels through.
 * For {@code https:} URLs, this includes the TLS version and ciphers to use
 * when negotiating a secure connection.
 */
public final class ConnectionConfiguration {
  /**
   * This is a subset of the cipher suites supported in Chrome 37, current as of 2014-10-5. All of
   * these suites are available on Android L; earlier releases support a subset of these suites.
   * https://github.com/square/okhttp/issues/330
   */
  private static final String[] CIPHER_SUITES = new String[] {
      "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", // 0xC0,0x2B  Android L
      "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",   // 0xC0,0x2F  Android L
      "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",     // 0x00,0x9E  Android L
      "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",    // 0xC0,0x0A  Android 4.0
      "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",    // 0xC0,0x09  Android 4.0
      "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",      // 0xC0,0x13  Android 4.0
      "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",      // 0xC0,0x14  Android 4.0
      "TLS_ECDHE_ECDSA_WITH_RC4_128_SHA",        // 0xC0,0x07  Android 4.0
      "TLS_ECDHE_RSA_WITH_RC4_128_SHA",          // 0xC0,0x11  Android 4.0
      "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",        // 0x00,0x33  Android 2.3
      "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",        // 0x00,0x32  Android 2.3
      "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",        // 0x00,0x39  Android 2.3
      "TLS_RSA_WITH_AES_128_GCM_SHA256",         // 0x00,0x9C  Android L
      "TLS_RSA_WITH_AES_128_CBC_SHA",            // 0x00,0x2F  Android 2.3
      "TLS_RSA_WITH_AES_256_CBC_SHA",            // 0x00,0x35  Android 2.3
      "SSL_RSA_WITH_3DES_EDE_CBC_SHA",           // 0x00,0x0A  Android 2.3  (Deprecated in L)
      "SSL_RSA_WITH_RC4_128_SHA",                // 0x00,0x05  Android 2.3
      "SSL_RSA_WITH_RC4_128_MD5"                 // 0x00,0x04  Android 2.3  (Deprecated in L)
  };

  private static final String TLS_1_2 = "TLSv1.2"; // 2008.
  private static final String TLS_1_1 = "TLSv1.1"; // 2006.
  private static final String TLS_1_0 = "TLSv1";   // 1999.
  private static final String SSL_3_0 = "SSLv3";   // 1996.

  /** A modern TLS configuration with extensions like SNI and ALPN available. */
  public static final ConnectionConfiguration MODERN_TLS = new ConnectionConfiguration(
      true, CIPHER_SUITES, new String[] { TLS_1_2, TLS_1_1, TLS_1_0, SSL_3_0 }, true);

  /** A backwards-compatible fallback configuration for interop with obsolete servers. */
  public static final ConnectionConfiguration COMPATIBLE_TLS = new ConnectionConfiguration(
      true, CIPHER_SUITES, new String[] { SSL_3_0 }, true);

  /** Unencrypted, unauthenticated connections for {@code http:} URLs. */
  public static final ConnectionConfiguration CLEARTEXT = new ConnectionConfiguration(
      false, new String[0], new String[0], false);

  private final boolean tls;
  private final String[] cipherSuites;
  private final String[] tlsVersions;
  private final boolean supportsTlsExtensions;

  /**
   * Caches the subset of this configuration that's supported by the host
   * platform. It's possible that the platform hosts multiple implementations of
   * {@link javax.net.ssl.SSLSocket}, in which case this cache will be incorrect.
   */
  private ConnectionConfiguration supportedConfiguration;

  private ConnectionConfiguration(boolean tls, String[] cipherSuites, String[] tlsVersions,
      boolean supportsTlsExtensions) {
    this.tls = tls;
    this.cipherSuites = cipherSuites;
    this.tlsVersions = tlsVersions;
    this.supportsTlsExtensions = supportsTlsExtensions;

    if (tls && (cipherSuites.length == 0 || tlsVersions.length == 0)) {
      throw new IllegalArgumentException("Unexpected configuration: " + this);
    }
    if (!tls && (cipherSuites.length != 0 || tlsVersions.length != 0 || supportsTlsExtensions)) {
      throw new IllegalArgumentException("Unexpected configuration: " + this);
    }
  }

  public boolean isTls() {
    return tls;
  }

  public List<String> cipherSuites() {
    return Util.immutableList(cipherSuites);
  }

  public List<String> tlsVersions() {
    return Util.immutableList(tlsVersions);
  }

  public boolean supportsTlsExtensions() {
    return supportsTlsExtensions;
  }

  /** Applies this configuration to {@code sslSocket} for {@code route}. */
  public void apply(SSLSocket sslSocket, Route route) {
    ConnectionConfiguration configurationToApply = supportedConfiguration;
    if (configurationToApply == null) {
      configurationToApply = supportedConfiguration(sslSocket);
      supportedConfiguration = configurationToApply;
    }

    sslSocket.setEnabledProtocols(configurationToApply.tlsVersions);
    sslSocket.setEnabledCipherSuites(configurationToApply.cipherSuites);

    Platform platform = Platform.get();
    if (configurationToApply.supportsTlsExtensions) {
      platform.configureTlsExtensions(sslSocket, route.address.uriHost, route.address.protocols);
    }
  }

  /**
   * Returns a copy of this that omits cipher suites and TLS versions not
   * supported by {@code sslSocket}.
   */
  private ConnectionConfiguration supportedConfiguration(SSLSocket sslSocket) {
    List<String> supportedCipherSuites = Util.intersect(Arrays.asList(cipherSuites),
        Arrays.asList(sslSocket.getSupportedCipherSuites()));
    List<String> supportedTlsVersions = Util.intersect(Arrays.asList(tlsVersions),
        Arrays.asList(sslSocket.getSupportedProtocols()));
    return new ConnectionConfiguration(tls,
        supportedCipherSuites.toArray(new String[supportedCipherSuites.size()]),
        supportedTlsVersions.toArray(new String[supportedTlsVersions.size()]),
        supportsTlsExtensions);
  }

  @Override public String toString() {
    return "ConnectionConfiguration(tls=" + tls
        + ", cipherSuites=" + Arrays.toString(cipherSuites)
        + ", tlsVersions=" + Arrays.toString(tlsVersions)
        + ", supportsTlsExtensions=" + supportsTlsExtensions
        + ")";
  }
}
