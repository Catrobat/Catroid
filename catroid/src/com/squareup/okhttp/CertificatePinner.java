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

import com.squareup.okhttp.internal.Util;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLPeerUnverifiedException;
import okio.ByteString;

import static java.util.Collections.unmodifiableList;

/**
 * Constrains which certificates are trusted. Pinning certificates defends
 * against attacks on certificate authorities. It also prevents connections
 * through man-in-the-middle certificate authorities either known or unknown to
 * the application's user.
 *
 * <p>This class currently pins a certificate's Subject Public Key Info as
 * described on <a href="http://goo.gl/AIx3e5">Adam Langley's Weblog</a>. Pins
 * are base-64 SHA-1 hashes, consistent with the format Chromium uses for <a
 * href="http://goo.gl/XDh6je">static certificates</a>. See Chromium's <a
 * href="http://goo.gl/4CCnGs">pinsets</a> for hostnames that are pinned in that
 * browser.
 *
 * <h3>Setting up Certificate Pinning</h3>
 * The easiest way to pin a host is turn on pinning with a broken configuration
 * and read the expected configuration when the connection fails. Be sure to
 * do this on a trusted network, and without man-in-the-middle tools like <a
 * href="http://charlesproxy.com">Charles</a> or <a
 * href="http://fiddlertool.com">Fiddler</a>.
 *
 * <p>For example, to pin {@code https://publicobject.com}, start with a broken
 * configuration: <pre>   {@code
 *
 *     String hostname = "publicobject.com";
 *     CertificatePinner certificatePinner = new CertificatePinner.Builder()
 *         .add(hostname, "sha1/BOGUSPIN")
 *         .build();
 *     OkHttpClient client = new OkHttpClient();
 *     client.setCertificatePinner(certificatePinner);
 *
 *     Request request = new Request.Builder()
 *         .url("https://" + hostname)
 *         .build();
 *     client.newCall(request).execute();
 * }</pre>
 *
 * As expected, this fails with a certificate pinning exception: <pre>   {@code
 *
 * javax.net.ssl.SSLPeerUnverifiedException: Certificate pinning failure!
 *   Peer certificate chain:
 *     sha1/DmxUShsZuNiqPQsX2Oi9uv2sCnw=: CN=publicobject.com, OU=PositiveSSL
 *     sha1/SXxoaOSEzPC6BgGmxAt/EAcsajw=: CN=COMODO RSA Domain Validation Secure Server CA
 *     sha1/blhOM3W9V/bVQhsWAcLYwPU6n24=: CN=COMODO RSA Certification Authority
 *     sha1/T5x9IXmcrQ7YuQxXnxoCmeeQ84c=: CN=AddTrust External CA Root
 *   Pinned certificates for publicobject.com:
 *     sha1/BOGUSPIN
 *   at com.squareup.okhttp.CertificatePinner.check(CertificatePinner.java)
 *   at com.squareup.okhttp.Connection.upgradeToTls(Connection.java)
 *   at com.squareup.okhttp.Connection.connect(Connection.java)
 *   at com.squareup.okhttp.Connection.connectAndSetOwner(Connection.java)
 * }</pre>
 *
 * Follow up by pasting the public key hashes from the exception into the
 * certificate pinner's configuration: <pre>   {@code
 *
 *     CertificatePinner certificatePinner = new CertificatePinner.Builder()
 *       .add("publicobject.com", "sha1/DmxUShsZuNiqPQsX2Oi9uv2sCnw=")
 *       .add("publicobject.com", "sha1/SXxoaOSEzPC6BgGmxAt/EAcsajw=")
 *       .add("publicobject.com", "sha1/blhOM3W9V/bVQhsWAcLYwPU6n24=")
 *       .add("publicobject.com", "sha1/T5x9IXmcrQ7YuQxXnxoCmeeQ84c=")
 *       .build();
 * }</pre>
 *
 * Pinning is per-hostname. To pin both {@code publicobject.com} and {@code
 * www.publicobject.com}, you must configure both hostnames.
 *
 * <h3>Warning: Certificate Pinning is Dangerous!</h3>
 * Pinning certificates limits your server team's abilities to update their TLS
 * certificates. By pinning certificates, you take on additional operational
 * complexity and limit your ability to migrate between certificate authorities.
 * Do not use certificate pinning without the blessing of your server's TLS
 * administrator!
 */
public final class CertificatePinner {
  public static final CertificatePinner DEFAULT = new Builder().build();

  private final Map<String, List<ByteString>> hostnameToPins;

  private CertificatePinner(Builder builder) {
    hostnameToPins = Util.immutableMap(builder.hostnameToPins);
  }

  /**
   * Confirms that at least one of the certificates pinned for {@code hostname}
   * is in {@code peerCertificates}. Does nothing if there are no certificates
   * pinned for {@code hostname}. OkHttp calls this after a successful TLS
   * handshake, but before the connection is used.
   *
   * @throws javax.net.ssl.SSLPeerUnverifiedException if {@code peerCertificates} don't match
   *     the certificates pinned for {@code hostname}.
   */
  public void check(String hostname, Certificate... peerCertificates)
      throws SSLPeerUnverifiedException {
    List<ByteString> pins = hostnameToPins.get(hostname);
    if (pins == null) return;

    for (Certificate c : peerCertificates) {
      X509Certificate x509Certificate = (X509Certificate) c;
      if (pins.contains(sha1(x509Certificate))) return; // Success!
    }

    // If we couldn't find a matching pin, format a nice exception.
    StringBuilder message = new StringBuilder()
        .append("Certificate pinning failure!")
        .append("\n  Peer certificate chain:");
    for (Certificate c : peerCertificates) {
      X509Certificate x509Certificate = (X509Certificate) c;
      message.append("\n    sha1/").append(sha1(x509Certificate).base64()).append(": ")
          .append(x509Certificate.getSubjectDN().getName());
    }
    message.append("\n  Pinned certificates for ").append(hostname).append(":");
    for (ByteString pin : pins) {
      message.append("\n    sha1/").append(pin.base64());
    }
    throw new SSLPeerUnverifiedException(message.toString());
  }

  /**
   * Returns the SHA-1 of {@code certificate}'s public key. This uses the
   * mechanism Moxie Marlinspike describes in <a
   * href="https://github.com/moxie0/AndroidPinning">Android Pinning</a>.
   */
  public static String pin(Certificate certificate) {
    if (!(certificate instanceof X509Certificate)) {
      throw new IllegalArgumentException("Certificate pinning requires X509 certificates");
    }
    return "sha1/" + sha1((X509Certificate) certificate).base64();
  }

  private static ByteString sha1(X509Certificate x509Certificate) {
    return Util.sha1(ByteString.of(x509Certificate.getPublicKey().getEncoded()));
  }

  /** Builds a configured certificate pinner. */
  public static final class Builder {
    private final Map<String, List<ByteString>> hostnameToPins = new LinkedHashMap<String, List<ByteString>>();

    /**
     * Pins certificates for {@code hostname}. Each pin is a SHA-1 hash of a
     * certificate's Subject Public Key Info, base64-encoded and prefixed with
     * "sha1/".
     */
    public Builder add(String hostname, String... pins) {
      if (hostname == null) throw new IllegalArgumentException("hostname == null");

      List<ByteString> hostPins = new ArrayList<ByteString>();
      List<ByteString> previousPins = hostnameToPins.put(hostname, unmodifiableList(hostPins));
      if (previousPins != null) {
        hostPins.addAll(previousPins);
      }

      for (String pin : pins) {
        if (!pin.startsWith("sha1/")) {
          throw new IllegalArgumentException("pins must start with 'sha1/': " + pin);
        }
        ByteString decodedPin = ByteString.decodeBase64(pin.substring("sha1/".length()));
        if (decodedPin == null) {
          throw new IllegalArgumentException("pins must be base64: " + pin);
        }
        hostPins.add(decodedPin);
      }

      return this;
    }

    public CertificatePinner build() {
      return new CertificatePinner(this);
    }
  }
}
