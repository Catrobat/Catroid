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

public final class HttpTransport implements Transport {
  private final HttpEngine httpEngine;
  private final HttpConnection httpConnection;

  public HttpTransport(HttpEngine httpEngine, HttpConnection httpConnection) {
    this.httpEngine = httpEngine;
    this.httpConnection = httpConnection;
  }

  @Override public Sink createRequestBody(Request request) throws IOException {
    long contentLength = OkHeaders.contentLength(request);

    if (httpEngine.bufferRequestBody) {
      if (contentLength > Integer.MAX_VALUE) {
        throw new IllegalStateException("Use setFixedLengthStreamingMode() or "
            + "setChunkedStreamingMode() for requests larger than 2 GiB.");
      }

      if (contentLength != -1) {
        // Buffer a request body of a known length.
        writeRequestHeaders(request);
        return new RetryableSink((int) contentLength);
      } else {
        // Buffer a request body of an unknown length. Don't write request
        // headers until the entire body is ready; otherwise we can't set the
        // Content-Length header correctly.
        return new RetryableSink();
      }
    }

    if ("chunked".equalsIgnoreCase(request.header("Transfer-Encoding"))) {
      // Stream a request body of unknown length.
      writeRequestHeaders(request);
      return httpConnection.newChunkedSink();
    }

    if (contentLength != -1) {
      // Stream a request body of a known length.
      writeRequestHeaders(request);
      return httpConnection.newFixedLengthSink(contentLength);
    }

    throw new IllegalStateException(
        "Cannot stream a request body without chunked encoding or a known content length!");
  }

  @Override public void flushRequest() throws IOException {
    httpConnection.flush();
  }

  @Override public void writeRequestBody(RetryableSink requestBody) throws IOException {
    httpConnection.writeRequestBody(requestBody);
  }

  /**
   * Prepares the HTTP headers and sends them to the server.
   *
   * <p>For streaming requests with a body, headers must be prepared
   * <strong>before</strong> the output stream has been written to. Otherwise
   * the body would need to be buffered!
   *
   * <p>For non-streaming requests with a body, headers must be prepared
   * <strong>after</strong> the output stream has been written to and closed.
   * This ensures that the {@code Content-Length} header field receives the
   * proper value.
   */
  public void writeRequestHeaders(Request request) throws IOException {
    httpEngine.writingRequestHeaders();
    String requestLine = RequestLine.get(request,
        httpEngine.getConnection().getRoute().getProxy().type(),
        httpEngine.getConnection().getProtocol());
    httpConnection.writeRequest(request.headers(), requestLine);
  }

  @Override public Response.Builder readResponseHeaders() throws IOException {
    return httpConnection.readResponse();
  }

  @Override public void releaseConnectionOnIdle() throws IOException {
    if (canReuseConnection()) {
      httpConnection.poolOnIdle();
    } else {
      httpConnection.closeOnIdle();
    }
  }

  @Override public boolean canReuseConnection() {
    // If the request specified that the connection shouldn't be reused, don't reuse it.
    if ("close".equalsIgnoreCase(httpEngine.getRequest().header("Connection"))) {
      return false;
    }

    // If the response specified that the connection shouldn't be reused, don't reuse it.
    if ("close".equalsIgnoreCase(httpEngine.getResponse().header("Connection"))) {
      return false;
    }

    if (httpConnection.isClosed()) {
      return false;
    }

    return true;
  }

  @Override public void emptyTransferStream() throws IOException {
    httpConnection.emptyResponseBody();
  }

  @Override public Source getTransferStream(CacheRequest cacheRequest) throws IOException {
    if (!httpEngine.hasResponseBody()) {
      return httpConnection.newFixedLengthSource(cacheRequest, 0);
    }

    if ("chunked".equalsIgnoreCase(httpEngine.getResponse().header("Transfer-Encoding"))) {
      return httpConnection.newChunkedSource(cacheRequest, httpEngine);
    }

    long contentLength = OkHeaders.contentLength(httpEngine.getResponse());
    if (contentLength != -1) {
      return httpConnection.newFixedLengthSource(cacheRequest, contentLength);
    }

    // Wrap the input stream from the connection (rather than just returning
    // "socketIn" directly here), so that we can control its use after the
    // reference escapes.
    return httpConnection.newUnknownLengthSource(cacheRequest);
  }

  @Override public void disconnect(HttpEngine engine) throws IOException {
    httpConnection.closeIfOwnedBy(engine);
  }
}
