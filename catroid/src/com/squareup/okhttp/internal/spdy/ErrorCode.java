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

// http://tools.ietf.org/html/draft-ietf-httpbis-http2-14#section-7
public enum ErrorCode {
  /** Not an error! For SPDY stream resets, prefer null over NO_ERROR. */
  NO_ERROR(0, -1, 0),

  PROTOCOL_ERROR(1, 1, 1),

  /** A subtype of PROTOCOL_ERROR used by SPDY. */
  INVALID_STREAM(1, 2, -1),

  /** A subtype of PROTOCOL_ERROR used by SPDY. */
  UNSUPPORTED_VERSION(1, 4, -1),

  /** A subtype of PROTOCOL_ERROR used by SPDY. */
  STREAM_IN_USE(1, 8, -1),

  /** A subtype of PROTOCOL_ERROR used by SPDY. */
  STREAM_ALREADY_CLOSED(1, 9, -1),

  INTERNAL_ERROR(2, 6, 2),

  FLOW_CONTROL_ERROR(3, 7, -1),

  STREAM_CLOSED(5, -1, -1),

  FRAME_TOO_LARGE(6, 11, -1),

  REFUSED_STREAM(7, 3, -1),

  CANCEL(8, 5, -1),

  COMPRESSION_ERROR(9, -1, -1),

  CONNECT_ERROR(10, -1, -1),

  ENHANCE_YOUR_CALM(11, -1, -1),

  INADEQUATE_SECURITY(12, -1, -1),

  INVALID_CREDENTIALS(-1, 10, -1);

  public final int httpCode;
  public final int spdyRstCode;
  public final int spdyGoAwayCode;

  private ErrorCode(int httpCode, int spdyRstCode, int spdyGoAwayCode) {
    this.httpCode = httpCode;
    this.spdyRstCode = spdyRstCode;
    this.spdyGoAwayCode = spdyGoAwayCode;
  }

  public static ErrorCode fromSpdy3Rst(int code) {
    for (ErrorCode errorCode : ErrorCode.values()) {
      if (errorCode.spdyRstCode == code) return errorCode;
    }
    return null;
  }

  public static ErrorCode fromHttp2(int code) {
    for (ErrorCode errorCode : ErrorCode.values()) {
      if (errorCode.httpCode == code) return errorCode;
    }
    return null;
  }

  public static ErrorCode fromSpdyGoAway(int code) {
    for (ErrorCode errorCode : ErrorCode.values()) {
      if (errorCode.spdyGoAwayCode == code) return errorCode;
    }
    return null;
  }
}
