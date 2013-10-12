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
package org.catrobat.catroid.io;

import android.annotation.SuppressLint;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class IPv4Manager {

	@SuppressLint("DefaultLocale")
	public static String intToIPAddress(int ipInt) {
		return String.format("%d.%d.%d.%d", (ipInt & 0xff), (ipInt >> 8 & 0xff), (ipInt >> 16 & 0xff),
				(ipInt >> 24 & 0xff));
	}

	public static int inetAddressToInt(InetAddress inetAddress) {
		int inetAddressInt = 0;
		byte[] inetByte = inetAddress.getAddress();
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			inetAddressInt += (inetByte[3 - i] & 0x000000FF) << shift;
		}
		return inetAddressInt;
	}

	public static InetAddress stringToInetAddress(String broadcastAddress) {
		InetAddress broadcastAddressInet = null;
		try {
			broadcastAddressInet = InetAddress.getByName(broadcastAddress);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		return broadcastAddressInet;
	}

	public static String stripPort(String ipWithPort) {
		int position = ipWithPort.indexOf(":");
		if (position != -1) {
			ipWithPort = ipWithPort.substring(1, position);
		}
		return ipWithPort;
	}

	public static int getNetMaskForHotspot(InetAddress inetAddress) {
		NetworkInterface network;
		network = null;
		try {
			network = NetworkInterface.getByInetAddress(inetAddress);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		int numOfHostbits = network.getInterfaceAddresses().get(0).getNetworkPrefixLength();
		int maskTemp = 0xFFFFFFFF << (32 - numOfHostbits);

		int mask = 0;
		mask |= (((maskTemp & 0xFF000000) >> 24 & 0xFF));
		mask |= ((maskTemp & 0x00FF0000) >> 8 & 0xFF00);
		mask |= ((maskTemp & 0x0000FF00) << 8);
		mask |= ((maskTemp & 0x000000FF) << 24);

		return mask;
	}

}
