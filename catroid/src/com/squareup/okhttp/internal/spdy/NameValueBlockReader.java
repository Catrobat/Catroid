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
 * Copyright (C) 2013 Square, Inc.
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
package com.squareup.okhttp.internal.spdy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.ForwardingSource;
import okio.InflaterSource;
import okio.Okio;
import okio.Source;

/**
 * Reads a SPDY/3 Name/Value header block. This class is made complicated by the
 * requirement that we're strict with which bytes we put in the compressed bytes
 * buffer. We need to put all compressed bytes into that buffer -- but no other
 * bytes.
 */
class NameValueBlockReader {
  /** This source transforms compressed bytes into uncompressed bytes. */
  private final InflaterSource inflaterSource;

  /**
   * How many compressed bytes must be read into inflaterSource before
   * {@link #readNameValueBlock} returns.
   */
  private int compressedLimit;

  /** This source holds inflated bytes. */
  private final BufferedSource source;

  public NameValueBlockReader(BufferedSource source) {
    // Limit the inflater input stream to only those bytes in the Name/Value
    // block. We cut the inflater off at its source because we can't predict the
    // ratio of compressed bytes to uncompressed bytes.
    Source throttleSource = new ForwardingSource(source) {
      @Override public long read(Buffer sink, long byteCount) throws IOException {
        if (compressedLimit == 0) return -1; // Out of data for the current block.
        long read = super.read(sink, Math.min(byteCount, compressedLimit));
        if (read == -1) return -1;
        compressedLimit -= read;
        return read;
      }
    };

    // Subclass inflater to install a dictionary when it's needed.
    Inflater inflater = new Inflater() {
      @Override public int inflate(byte[] buffer, int offset, int count)
          throws DataFormatException {
        int result = super.inflate(buffer, offset, count);
        if (result == 0 && needsDictionary()) {
          setDictionary(Spdy3.DICTIONARY);
          result = super.inflate(buffer, offset, count);
        }
        return result;
      }
    };

    this.inflaterSource = new InflaterSource(throttleSource, inflater);
    this.source = Okio.buffer(inflaterSource);
  }

  public List<Header> readNameValueBlock(int length) throws IOException {
    this.compressedLimit += length;

    int numberOfPairs = source.readInt();
    if (numberOfPairs < 0) throw new IOException("numberOfPairs < 0: " + numberOfPairs);
    if (numberOfPairs > 1024) throw new IOException("numberOfPairs > 1024: " + numberOfPairs);

    List<Header> entries = new ArrayList<Header>(numberOfPairs);
    for (int i = 0; i < numberOfPairs; i++) {
      ByteString name = readByteString().toAsciiLowercase();
      ByteString values = readByteString();
      if (name.size() == 0) throw new IOException("name.size == 0");
      entries.add(new Header(name, values));
    }

    doneReading();
    return entries;
  }

  private ByteString readByteString() throws IOException {
    int length = source.readInt();
    return source.readByteString(length);
  }

  private void doneReading() throws IOException {
    // Move any outstanding unread bytes into the inflater. One side-effect of
    // deflate compression is that sometimes there are bytes remaining in the
    // stream after we've consumed all of the content.
    if (compressedLimit > 0) {
      inflaterSource.refill();
      if (compressedLimit != 0) throw new IOException("compressedLimit > 0: " + compressedLimit);
    }
  }

  public void close() throws IOException {
    source.close();
  }
}
