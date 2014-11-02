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
 * Copyright (C) 2012 Square, Inc.
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
package com.squareup.okhttp.internal;

import android.util.Log;

import com.squareup.okhttp.Protocol;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;
import okio.Buffer;

/**
 * Access to Platform-specific features necessary for SPDY and advanced TLS.
 * This includes Server Name Indication (SNI) and session tickets.
 *
 * <h3>ALPN and NPN</h3>
 * This class uses TLS extensions ALPN and NPN to negotiate the upgrade from
 * HTTP/1.1 (the default protocol to use with TLS on port 443) to either SPDY
 * or HTTP/2.
 *
 * <p>NPN (Next Protocol Negotiation) was developed for SPDY. It is widely
 * available and we support it on both Android (4.1+) and OpenJDK 7 (via the
 * Jetty Alpn-boot library). NPN is not yet available on OpenJDK 8.
 *
 * <p>ALPN (Application Layer Protocol Negotiation) is the successor to NPN. It
 * has some technical advantages over NPN. ALPN first arrived in Android 4.4,
 * but that release suffers a <a href="http://goo.gl/y5izPP">concurrency bug</a>
 * so we don't use it. ALPN is supported on OpenJDK 7 and 8 (via the Jetty
 * ALPN-boot library).
 *
 * <p>On platforms that support both extensions, OkHttp will use both,
 * preferring ALPN's result. Future versions of OkHttp will drop support for
 * NPN.
 */
public class Platform {
  private static final Platform PLATFORM = findPlatform();

  public static Platform get() {
    return PLATFORM;
  }

  /** Prefix used on custom headers. */
  public String getPrefix() {
    return "OkHttp";
  }

  public void logW(String warning) {
	 Log.d("com.squareup.okhttp", warning);
  }

  public void tagSocket(Socket socket) throws SocketException {
  }

  public void untagSocket(Socket socket) throws SocketException {
  }

  public URI toUriLenient(URL url) throws URISyntaxException {
    return url.toURI(); // this isn't as good as the built-in toUriLenient
  }

  /**
   * Configure TLS extensions on {@code sslSocket} for {@code route}.
   *
   * @param hostname non-null for client-side handshakes; null for
   *     server-side handshakes.
   */
  public void configureTlsExtensions(SSLSocket sslSocket, String hostname,
      List<Protocol> protocols) {
  }

  /** Returns the negotiated protocol, or null if no protocol was negotiated. */
  public String getSelectedProtocol(SSLSocket socket) {
    return null;
  }

  public void connectSocket(Socket socket, InetSocketAddress address,
      int connectTimeout) throws IOException {
    socket.connect(address, connectTimeout);
  }

  /** Attempt to match the host runtime to a capable Platform implementation. */
  private static Platform findPlatform() {
    // Attempt to find Android 2.3+ APIs.
    Class<?> openSslSocketClass;
    Method setUseSessionTickets;
    Method setHostname;
    try {
      try {
        openSslSocketClass = Class.forName("com.android.org.conscrypt.OpenSSLSocketImpl");
      } catch (ClassNotFoundException ignored) {
        // Older platform before being unbundled.
        openSslSocketClass = Class.forName(
            "org.apache.harmony.xnet.provider.jsse.OpenSSLSocketImpl");
      }

      setUseSessionTickets = openSslSocketClass.getMethod("setUseSessionTickets", boolean.class);
      setHostname = openSslSocketClass.getMethod("setHostname", String.class);

      // Attempt to find Android 4.0+ APIs.
      Method trafficStatsTagSocket = null;
      Method trafficStatsUntagSocket = null;
      try {
        Class<?> trafficStats = Class.forName("android.net.TrafficStats");
        trafficStatsTagSocket = trafficStats.getMethod("tagSocket", Socket.class);
        trafficStatsUntagSocket = trafficStats.getMethod("untagSocket", Socket.class);
      } catch (ClassNotFoundException ignored) {
      } catch (NoSuchMethodException ignored) {
      }

      // Attempt to find Android 4.1+ APIs.
      Method setNpnProtocols = null;
      Method getNpnSelectedProtocol = null;
      try {
        setNpnProtocols = openSslSocketClass.getMethod("setNpnProtocols", byte[].class);
        getNpnSelectedProtocol = openSslSocketClass.getMethod("getNpnSelectedProtocol");
      } catch (NoSuchMethodException ignored) {
      }

      return new Android(openSslSocketClass, setUseSessionTickets, setHostname,
          trafficStatsTagSocket, trafficStatsUntagSocket, setNpnProtocols,
          getNpnSelectedProtocol);
    } catch (ClassNotFoundException ignored) {
      // This isn't an Android runtime.
    } catch (NoSuchMethodException ignored) {
      // This isn't Android 2.3 or better.
    }

    try { // to find the Jetty's ALPN or NPN extension for OpenJDK.
      String negoClassName = "org.eclipse.jetty.alpn.ALPN";
      Class<?> negoClass;
      try {
        negoClass = Class.forName(negoClassName);
      } catch (ClassNotFoundException ignored) { // ALPN isn't on the classpath.
        negoClassName = "org.eclipse.jetty.npn.NextProtoNego";
        negoClass = Class.forName(negoClassName);
      }
      Class<?> providerClass = Class.forName(negoClassName + "$Provider");
      Class<?> clientProviderClass = Class.forName(negoClassName + "$ClientProvider");
      Class<?> serverProviderClass = Class.forName(negoClassName + "$ServerProvider");
      Method putMethod = negoClass.getMethod("put", SSLSocket.class, providerClass);
      Method getMethod = negoClass.getMethod("get", SSLSocket.class);
      return new JdkWithJettyBootPlatform(
          putMethod, getMethod, clientProviderClass, serverProviderClass);
    } catch (ClassNotFoundException ignored) { // NPN isn't on the classpath.
    } catch (NoSuchMethodException ignored) { // The ALPN or NPN version isn't what we expect.
    }

    return new Platform();
  }

  /**
   * Android 2.3 or better. Version 2.3 supports TLS session tickets and server
   * name indication (SNI). Versions 4.1 supports NPN.
   */
  private static class Android extends Platform {
    // Non-null.
    protected final Class<?> openSslSocketClass;
    private final Method setUseSessionTickets;
    private final Method setHostname;

    // Non-null on Android 4.0+.
    private final Method trafficStatsTagSocket;
    private final Method trafficStatsUntagSocket;

    // Non-null on Android 4.1+.
    private final Method setNpnProtocols;
    private final Method getNpnSelectedProtocol;

    private Android(Class<?> openSslSocketClass, Method setUseSessionTickets, Method setHostname,
        Method trafficStatsTagSocket, Method trafficStatsUntagSocket, Method setNpnProtocols,
        Method getNpnSelectedProtocol) {
      this.openSslSocketClass = openSslSocketClass;
      this.setUseSessionTickets = setUseSessionTickets;
      this.setHostname = setHostname;
      this.trafficStatsTagSocket = trafficStatsTagSocket;
      this.trafficStatsUntagSocket = trafficStatsUntagSocket;
      this.setNpnProtocols = setNpnProtocols;
      this.getNpnSelectedProtocol = getNpnSelectedProtocol;
    }

    @Override public void connectSocket(Socket socket, InetSocketAddress address,
        int connectTimeout) throws IOException {
      try {
        socket.connect(address, connectTimeout);
      } catch (SecurityException se) {
        // Before android 4.3, socket.connect could throw a SecurityException
        // if opening a socket resulted in an EACCES error.
        IOException ioException = new IOException("Exception in connect");
        ioException.initCause(se);
        throw ioException;
      }
    }

    @Override public void configureTlsExtensions(
        SSLSocket sslSocket, String hostname, List<Protocol> protocols) {
      if (!openSslSocketClass.isInstance(sslSocket)) return;

      // Enable SNI and session tickets.
      if (hostname != null) {
        try {
          setUseSessionTickets.invoke(sslSocket, true);
          setHostname.invoke(sslSocket, hostname);
        } catch (InvocationTargetException e) {
          throw new RuntimeException(e.getCause());
        } catch (IllegalAccessException e) {
          throw new AssertionError(e);
        }
      }

      // Enable NPN.
      if (setNpnProtocols != null) {
        try {
          Object[] parameters = { concatLengthPrefixed(protocols) };
          setNpnProtocols.invoke(sslSocket, parameters);
        } catch (IllegalAccessException e) {
          throw new AssertionError(e);
        } catch (InvocationTargetException e) {
          throw new RuntimeException(e.getCause());
        }
      }
    }

    @Override public String getSelectedProtocol(SSLSocket socket) {
      if (getNpnSelectedProtocol == null) return null;
      if (!openSslSocketClass.isInstance(socket)) return null;
      try {
        byte[] npnResult = (byte[]) getNpnSelectedProtocol.invoke(socket);
        if (npnResult == null) return null;
        return new String(npnResult, Util.UTF_8);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(e.getCause());
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }

    @Override public void tagSocket(Socket socket) throws SocketException {
      if (trafficStatsTagSocket == null) return;

      try {
        trafficStatsTagSocket.invoke(null, socket);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(e.getCause());
      }
    }

    @Override public void untagSocket(Socket socket) throws SocketException {
      if (trafficStatsUntagSocket == null) return;

      try {
        trafficStatsUntagSocket.invoke(null, socket);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(e.getCause());
      }
    }
  }

  /**
   * OpenJDK 7+ with {@code org.mortbay.jetty.npn/npn-boot} or
   * {@code org.mortbay.jetty.alpn/alpn-boot} in the boot class path.
   */
  private static class JdkWithJettyBootPlatform extends Platform {
    private final Method getMethod;
    private final Method putMethod;
    private final Class<?> clientProviderClass;
    private final Class<?> serverProviderClass;

    public JdkWithJettyBootPlatform(Method putMethod, Method getMethod,
        Class<?> clientProviderClass, Class<?> serverProviderClass) {
      this.putMethod = putMethod;
      this.getMethod = getMethod;
      this.clientProviderClass = clientProviderClass;
      this.serverProviderClass = serverProviderClass;
    }

    @Override public void configureTlsExtensions(
        SSLSocket sslSocket, String hostname, List<Protocol> protocols) {
      List<String> names = new ArrayList<String>(protocols.size());
      for (int i = 0, size = protocols.size(); i < size; i++) {
        Protocol protocol = protocols.get(i);
        if (protocol == Protocol.HTTP_1_0) continue; // No HTTP/1.0 for NPN or ALPN.
        names.add(protocol.toString());
      }
      try {
        Object provider = Proxy.newProxyInstance(Platform.class.getClassLoader(),
            new Class[] { clientProviderClass, serverProviderClass }, new JettyNegoProvider(names));
        putMethod.invoke(null, sslSocket, provider);
      } catch (InvocationTargetException e) {
        throw new AssertionError(e);
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }

    @Override public String getSelectedProtocol(SSLSocket socket) {
      try {
        JettyNegoProvider provider =
            (JettyNegoProvider) Proxy.getInvocationHandler(getMethod.invoke(null, socket));
        if (!provider.unsupported && provider.selected == null) {
          Logger logger = Logger.getLogger("com.squareup.okhttp.OkHttpClient");
          logger.log(Level.INFO, "NPN/ALPN callback dropped: SPDY and HTTP/2 are disabled. "
                  + "Is npn-boot or alpn-boot on the boot class path?");
          return null;
        }
        return provider.unsupported ? null : provider.selected;
      } catch (InvocationTargetException e) {
        throw new AssertionError();
      } catch (IllegalAccessException e) {
        throw new AssertionError();
      }
    }
  }

  /**
   * Handle the methods of NPN or ALPN's ClientProvider and ServerProvider
   * without a compile-time dependency on those interfaces.
   */
  private static class JettyNegoProvider implements InvocationHandler {
    /** This peer's supported protocols. */
    private final List<String> protocols;
    /** Set when remote peer notifies NPN or ALPN is unsupported. */
    private boolean unsupported;
    /** The protocol the client (NPN) or server (ALPN) selected. */
    private String selected;

    public JettyNegoProvider(List<String> protocols) {
      this.protocols = protocols;
    }

    @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      String methodName = method.getName();
      Class<?> returnType = method.getReturnType();
      if (args == null) {
        args = Util.EMPTY_STRING_ARRAY;
      }
      if (methodName.equals("supports") && boolean.class == returnType) {
        return true; // NPN or ALPN is supported.
      } else if (methodName.equals("unsupported") && void.class == returnType) {
        this.unsupported = true; // Peer doesn't support NPN or ALPN.
        return null;
      } else if (methodName.equals("protocols") && args.length == 0) {
        return protocols; // Server (NPN) or Client (ALPN) advertises these protocols.
      } else if ((methodName.equals("selectProtocol") || methodName.equals("select"))
          && String.class == returnType && args.length == 1 && args[0] instanceof List) {
        List<String> peerProtocols = (List) args[0];
        // Pick the first known protocol the peer advertises.
        for (int i = 0, size = peerProtocols.size(); i < size; i++) {
          if (protocols.contains(peerProtocols.get(i))) {
            return selected = peerProtocols.get(i);
          }
        }
        return selected = protocols.get(0); // On no intersection, try peer's first protocol.
      } else if ((methodName.equals("protocolSelected") || methodName.equals("selected"))
          && args.length == 1) {
        this.selected = (String) args[0]; // Client (NPN) or Server (ALPN) selected this protocol.
        return null;
      } else {
        return method.invoke(this, args);
      }
    }
  }

  /**
   * Returns the concatenation of 8-bit, length prefixed protocol names.
   * http://tools.ietf.org/html/draft-agl-tls-nextprotoneg-04#page-4
   */
  static byte[] concatLengthPrefixed(List<Protocol> protocols) {
    Buffer result = new Buffer();
    for (int i = 0, size = protocols.size(); i < size; i++) {
      Protocol protocol = protocols.get(i);
      if (protocol == Protocol.HTTP_1_0) continue; // No HTTP/1.0 for NPN.
      result.writeByte(protocol.toString().length());
      result.writeUtf8(protocol.toString());
    }
    return result.readByteArray();
  }
}
