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

import java.io.IOException;

/**
 * Protocols that OkHttp implements for <a
 * href="http://tools.ietf.org/html/draft-agl-tls-nextprotoneg-04">NPN</a> and
 * <a href="http://tools.ietf.org/html/draft-ietf-tls-applayerprotoneg">ALPN</a>
 * selection.
 *
 * <h3>Protocol vs Scheme</h3>
 * Despite its name, {@link java.net.URL#getProtocol()} returns the
 * {@linkplain java.net.URI#getScheme() scheme} (http, https, etc.) of the URL, not
 * the protocol (http/1.1, spdy/3.1, etc.). OkHttp uses the word <i>protocol</i>
 * to identify how HTTP messages are framed.
 */
public enum Protocol {
  /**
   * An obsolete plaintext framing that does not use persistent sockets by
   * default.
   */
  HTTP_1_0("http/1.0"),

  /**
   * A plaintext framing that includes persistent connections.
   *
   * <p>This version of OkHttp implements <a
   * href="http://www.ietf.org/rfc/rfc2616.txt">RFC 2616</a>, and tracks
   * revisions to that spec.
   */
  HTTP_1_1("http/1.1"),

  /**
   * Chromium's binary-framed protocol that includes header compression,
   * multiplexing multiple requests on the same socket, and server-push.
   * HTTP/1.1 semantics are layered on SPDY/3.
   *
   * <p>This version of OkHttp implements SPDY 3 <a
   * href="http://dev.chromium.org/spdy/spdy-protocol/spdy-protocol-draft3-1">draft
   * 3.1</a>. Future releases of OkHttp may use this identifier for a newer draft
   * of the SPDY spec.
   */
  SPDY_3("spdy/3.1"),

  /**
   * The IETF's binary-framed protocol that includes header compression,
   * multiplexing multiple requests on the same socket, and server-push.
   * HTTP/1.1 semantics are layered on HTTP/2.
   *
   * <p>This version of OkHttp implements HTTP/2 <a
   * href="http://tools.ietf.org/html/draft-ietf-httpbis-http2-14">draft 12</a>
   * with HPACK <a
   * href="http://tools.ietf.org/html/draft-ietf-httpbis-header-compression-09">draft
   * 6</a>. Future releases of OkHttp may use this identifier for a newer draft
   * of these specs.
   */
  HTTP_2("h2-14");

  private final String protocol;

  Protocol(String protocol) {
    this.protocol = protocol;
  }

  /**
   * Returns the protocol identified by {@code protocol}.
   * @throws java.io.IOException if {@code protocol} is unknown.
   */
  public static Protocol get(String protocol) throws IOException {
    // Unroll the loop over values() to save an allocation.
    if (protocol.equals(HTTP_1_0.protocol)) return HTTP_1_0;
    if (protocol.equals(HTTP_1_1.protocol)) return HTTP_1_1;
    if (protocol.equals(HTTP_2.protocol)) return HTTP_2;
    if (protocol.equals(SPDY_3.protocol)) return SPDY_3;
    throw new IOException("Unexpected protocol: " + protocol);
  }

  /**
   * Returns the string used to identify this protocol for ALPN and NPN, like
   * "http/1.1", "spdy/3.1" or "h2-14".
   */
  @Override public String toString() {
    return protocol;
  }
}
