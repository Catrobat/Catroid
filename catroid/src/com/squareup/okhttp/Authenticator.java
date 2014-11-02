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
package com.squareup.okhttp;

import java.io.IOException;
import java.net.Proxy;

/**
 * Responds to authentication challenges from the remote web or proxy server.
 */
public interface Authenticator {
  /**
   * Returns a request that includes a credential to satisfy an authentication
   * challenge in {@code response}. Returns null if the challenge cannot be
   * satisfied. This method is called in response to an HTTP 401 unauthorized
   * status code sent by the origin server.
   *
   * <p>Typical implementations will look up a credential and create a request
   * derived from the initial request by setting the "Authorization" header.
   * <pre>   {@code
   *
   *    String credential = Credentials.basic(...)
   *    return response.request().newBuilder()
   *        .header("Authorization", credential)
   *        .build();
   * }</pre>
   */
  Request authenticate(Proxy proxy, Response response) throws IOException;

  /**
   * Returns a request that includes a credential to satisfy an authentication
   * challenge made by {@code response}. Returns null if the challenge cannot be
   * satisfied. This method is called in response to an HTTP 407 unauthorized
   * status code sent by the proxy server.
   *
   * <p>Typical implementations will look up a credential and create a request
   * derived from the initial request by setting the "Proxy-Authorization"
   * header. <pre>   {@code
   *
   *    String credential = Credentials.basic(...)
   *    return response.request().newBuilder()
   *        .header("Proxy-Authorization", credential)
   *        .build();
   * }</pre>
   */
  Request authenticateProxy(Proxy proxy, Response response) throws IOException;
}
