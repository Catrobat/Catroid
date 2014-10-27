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

package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.net.ProtocolException;

/** An HTTP response status line like "HTTP/1.1 200 OK". */
public final class StatusLine {
  /** Numeric status code, 307: Temporary Redirect. */
  public static final int HTTP_TEMP_REDIRECT = 307;
  public static final int HTTP_PERM_REDIRECT = 308;
  public static final int HTTP_CONTINUE = 100;

  public final Protocol protocol;
  public final int code;
  public final String message;

  public StatusLine(Protocol protocol, int code, String message) {
    this.protocol = protocol;
    this.code = code;
    this.message = message;
  }

  public static StatusLine get(Response response) {
    return new StatusLine(response.protocol(), response.code(), response.message());
  }

  public static StatusLine parse(String statusLine) throws IOException {
    // H T T P / 1 . 1   2 0 0   T e m p o r a r y   R e d i r e c t
    // 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0

    // Parse protocol like "HTTP/1.1" followed by a space.
    int codeStart;
    Protocol protocol;
    if (statusLine.startsWith("HTTP/1.")) {
      if (statusLine.length() < 9 || statusLine.charAt(8) != ' ') {
        throw new ProtocolException("Unexpected status line: " + statusLine);
      }
      int httpMinorVersion = statusLine.charAt(7) - '0';
      codeStart = 9;
      if (httpMinorVersion == 0) {
        protocol = Protocol.HTTP_1_0;
      } else if (httpMinorVersion == 1) {
        protocol = Protocol.HTTP_1_1;
      } else {
        throw new ProtocolException("Unexpected status line: " + statusLine);
      }
    } else if (statusLine.startsWith("ICY ")) {
      // Shoutcast uses ICY instead of "HTTP/1.0".
      protocol = Protocol.HTTP_1_0;
      codeStart = 4;
    } else {
      throw new ProtocolException("Unexpected status line: " + statusLine);
    }

    // Parse response code like "200". Always 3 digits.
    if (statusLine.length() < codeStart + 3) {
      throw new ProtocolException("Unexpected status line: " + statusLine);
    }
    int code;
    try {
      code = Integer.parseInt(statusLine.substring(codeStart, codeStart + 3));
    } catch (NumberFormatException e) {
      throw new ProtocolException("Unexpected status line: " + statusLine);
    }

    // Parse an optional response message like "OK" or "Not Modified". If it
    // exists, it is separated from the response code by a space.
    String message = "";
    if (statusLine.length() > codeStart + 3) {
      if (statusLine.charAt(codeStart + 3) != ' ') {
        throw new ProtocolException("Unexpected status line: " + statusLine);
      }
      message = statusLine.substring(codeStart + 4);
    }

    return new StatusLine(protocol, code, message);
  }

  @Override public String toString() {
    StringBuilder result = new StringBuilder();
    result.append(protocol == Protocol.HTTP_1_0 ? "HTTP/1.0" : "HTTP/1.1");
    result.append(' ').append(code);
    if (message != null) {
      result.append(' ').append(message);
    }
    return result.toString();
  }
}
