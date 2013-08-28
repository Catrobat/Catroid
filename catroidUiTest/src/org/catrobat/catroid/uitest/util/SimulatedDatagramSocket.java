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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

public class SimulatedDatagramSocket extends DatagramSocket {

	private SocketAddress socketAddress;
	private String serverIp;
	private int serverPort;
	private String broadcastAddress;
	private int receiveCount = 0;
	private boolean receiveSomething;

	public SimulatedDatagramSocket() throws SocketException {
		super();
		receiveSomething = true;
	}

	@Override
	public void send(DatagramPacket packet) {
		socketAddress = packet.getSocketAddress();
		broadcastAddress = socketAddress.toString();
	}

	@Override
	public void receive(DatagramPacket packet) throws IOException {
		if (receiveSomething) {
			socketAddress = new InetSocketAddress(serverIp, serverPort);
			packet.setSocketAddress(socketAddress);
		} else {
			throw new IOException();
		}

		if (receiveCount > 0) {
			throw new IOException();
		} else {
			String serverName = "strawBerry";
			byte[] serverNameBytes = serverName.getBytes();
			packet.setData(serverNameBytes);
		}
		receiveCount++;
	}

	@Override
	public void setSoTimeout(int sec) {
	}

	@Override
	public void setBroadcast(boolean value) {

	}

	@Override
	public void close() {

	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getBroadcastAddress() {
		return broadcastAddress;
	}

	public void setFindNetwork(boolean receive) {
		receiveSomething = receive;
	}
}