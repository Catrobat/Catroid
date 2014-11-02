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
 * Copyright (C) 2012 The Android Open Source Project
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

package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import okio.Sink;
import okio.Source;

public interface Transport {
  /**
   * The timeout to use while discarding a stream of input data. Since this is
   * used for connection reuse, this timeout should be significantly less than
   * the time it takes to establish a new connection.
   */
  int DISCARD_STREAM_TIMEOUT_MILLIS = 100;

  /**
   * Returns an output stream where the request body can be written. The
   * returned stream will of one of two types:
   * <ul>
   * <li><strong>Direct.</strong> Bytes are written to the socket and
   * forgotten. This is most efficient, particularly for large request
   * bodies. The returned stream may be buffered; the caller must call
   * {@link #flushRequest} before reading the response.</li>
   * <li><strong>Buffered.</strong> Bytes are written to an in memory
   * buffer, and must be explicitly flushed with a call to {@link
   * #writeRequestBody}. This allows HTTP authorization (401, 407)
   * responses to be retransmitted transparently.</li>
   * </ul>
   */
  // TODO: don't bother retransmitting the request body? It's quite a corner
  // case and there's uncertainty whether Firefox or Chrome do this
  Sink createRequestBody(Request request) throws IOException;

  /** This should update the HTTP engine's sentRequestMillis field. */
  void writeRequestHeaders(Request request) throws IOException;

  /**
   * Sends the request body returned by {@link #createRequestBody} to the
   * remote peer.
   */
  void writeRequestBody(RetryableSink requestBody) throws IOException;

  /** Flush the request body to the underlying socket. */
  void flushRequest() throws IOException;

  /** Read response headers and update the cookie manager. */
  Response.Builder readResponseHeaders() throws IOException;

  /** Notify the transport that no response body will be read. */
  void emptyTransferStream() throws IOException;

  // TODO: make this the content stream?
  Source getTransferStream(CacheRequest cacheRequest) throws IOException;

  /**
   * Configures the response body to pool or close the socket connection when
   * the response body is closed.
   */
  void releaseConnectionOnIdle() throws IOException;

  void disconnect(HttpEngine engine) throws IOException;

  /**
   * Returns true if the socket connection held by this transport can be reused
   * for a follow-up exchange.
   */
  boolean canReuseConnection();
}
