/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

public class SimulatedSocket extends Socket {

	private String clientIp;
	private int clientport;
	private InputStream mockInputStream;
	private OutputStream mockOutputStream;

	public SimulatedSocket(InputStream mockInputStream, OutputStream mockOutputStream) throws SocketException {
		super();
		this.mockInputStream = mockInputStream;
		this.mockOutputStream = mockOutputStream;
	}

	@Override
	public void connect(SocketAddress remoteAddress, int timeout) throws IOException {
		clientIp = stripPort(remoteAddress.toString());
		clientport = stripIp(remoteAddress.toString());
	}

	@Override
	public synchronized void setSoTimeout(int timeout) {

	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return mockOutputStream;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return mockInputStream;
	}

	public String stripPort(String ipWithPort) {
		int pos = ipWithPort.indexOf(":");
		if (pos != -1) {
			ipWithPort = ipWithPort.substring(1, pos);
		}
		return ipWithPort;
	}

	public int stripIp(String ipWithPort) {
		int pos = ipWithPort.indexOf(":");
		if (pos != -1) {
			ipWithPort = ipWithPort.substring(pos + 1, ipWithPort.length());
		}
		return Integer.valueOf(ipWithPort);
	}

	public int getClientPort() {
		return clientport;
	}

	public String getClientIp() {
		return clientIp;
	}
}