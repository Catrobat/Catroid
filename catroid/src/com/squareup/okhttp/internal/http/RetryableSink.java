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
 * Copyright (C) 2010 The Android Open Source Project
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

import java.io.IOException;
import java.net.ProtocolException;
import okio.Buffer;
import okio.BufferedSink;
import okio.Sink;
import okio.Timeout;

import static com.squareup.okhttp.internal.Util.checkOffsetAndCount;

/**
 * An HTTP request body that's completely buffered in memory. This allows
 * the post body to be transparently re-sent if the HTTP request must be
 * sent multiple times.
 */
public final class RetryableSink implements Sink {
  private boolean closed;
  private final int limit;
  private final Buffer content = new Buffer();

  public RetryableSink(int limit) {
    this.limit = limit;
  }

  public RetryableSink() {
    this(-1);
  }

  @Override public void close() throws IOException {
    if (closed) return;
    closed = true;
    if (content.size() < limit) {
      throw new ProtocolException(
          "content-length promised " + limit + " bytes, but received " + content.size());
    }
  }

  @Override public void write(Buffer source, long byteCount) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    checkOffsetAndCount(source.size(), 0, byteCount);
    if (limit != -1 && content.size() > limit - byteCount) {
      throw new ProtocolException("exceeded content-length limit of " + limit + " bytes");
    }
    content.write(source, byteCount);
  }

  @Override public void flush() throws IOException {
  }

  @Override public Timeout timeout() {
    return Timeout.NONE;
  }

  public long contentLength() throws IOException {
    return content.size();
  }

  public void writeToSocket(BufferedSink socketOut) throws IOException {
    // Clone the content; otherwise we won't have data to retry.
    socketOut.writeAll(content.clone());
  }
}
