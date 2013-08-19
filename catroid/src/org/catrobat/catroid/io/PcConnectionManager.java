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
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.catrobat.catroid.R;
import org.catrobat.catroid.io.Connection.connectionState;
import org.catrobat.catroid.stage.StageActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class PcConnectionManager {

	private final int versionId = 1;

	private static PcConnectionManager instance = null;
	private Context context;
	private final int port = 64000;
	private ProgressDialog connectingProgressDialog;
	private HashMap<String, String> availableIpsList;
	private boolean connectionAlreadySetUp = false;
	private StageActivity stageActivity;
	private Connection connection;
	private int serverVersionId;

	private PcConnectionManager() {
		connection = null;
	}

	public static PcConnectionManager getInstance(Context context) {
		if (instance == null) {
			instance = new PcConnectionManager();
		}
		if (context != null) {
			instance.context = context;
		}
		return instance;
	}

	public void broadcast(Spinner ipSpinner) {
		BroadcastThread broadcastThread = new BroadcastThread(ipSpinner);
		broadcastThread.start();

	}

	public class BroadcastThread extends Thread {

		private Spinner ipSpinner;

		public BroadcastThread(Spinner ipSpinner) {
			this.ipSpinner = ipSpinner;
		}

		@Override
		public void run() {
			Activity activity = (Activity) context;
			if (activity == null) {
				return;
			}
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					connectingProgressDialog = ProgressDialog.show(context, "",
							context.getString(R.string.prog_dialog_scan_wait), true);
				}
			});
			byte[] message = new byte[1];
			String broadcastAdressString = getBroadcastAddress();
			InetAddress broadcastAddressInet = stringToInetAddress(broadcastAdressString);
			DatagramPacket dataPacket = null;
			if (broadcastAddressInet != null) {
				dataPacket = new DatagramPacket(message, message.length, broadcastAddressInet, port);
			}
			availableIpsList = new HashMap<String, String>();
			DatagramSocket dataSocket = null;
			try {
				dataSocket = new DatagramSocket();
				dataSocket.setSoTimeout(3000);
			} catch (SocketException e) {
				e.printStackTrace();
				return;
			}
			try {
				dataSocket.setBroadcast(true);
				dataSocket.send(dataPacket);
			} catch (IOException e) {
				e.printStackTrace();
				dataSocket.close();
				return;
			}
			byte[] ipAddrServer = new byte[64];
			DatagramPacket dataRec = new DatagramPacket(ipAddrServer, ipAddrServer.length);
			while (true) {
				try {
					dataSocket.receive(dataRec);
					String ipServer = new String(ipAddrServer, 0, dataRec.getLength());
					availableIpsList.put(ipServer, dataRec.getSocketAddress().toString());
				} catch (IOException e) {
					if (availableIpsList.size() == 0) {
						activity = (Activity) context;
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(context, R.string.no_devices_found, Toast.LENGTH_SHORT).show();
							}
						});
					} else {
					}
					dataSocket.close();
					break;
				}
			}
			finishBroadcast(ipSpinner);
		}
	};

	public void finishBroadcast(final Spinner ipSpinner) {
		Activity act = (Activity) context;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				connectingProgressDialog.dismiss();
				ArrayList<String> ipList = new ArrayList<String>();
				Iterator<Entry<String, String>> it = availableIpsList.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, String> pairs = it.next();
					ipList.add(pairs.getKey());
				}
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
						android.R.layout.simple_spinner_item, ipList);
				dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				ipSpinner.setAdapter(dataAdapter);
				ipSpinner.setClickable(true);
			}
		});

	}

	public String getBroadcastAddress() {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		int ipAddress = 0;
		int netmask = 0;
		ipAddress = wifiManager.getConnectionInfo().getIpAddress();
		if (ipAddress == 0) {
			InetAddress inetAddress = getIpForHotspot();
			if (inetAddress != null) {
				ipAddress = InetAddrToInt(inetAddress);
				netmask = getNetMaskForHotspot(inetAddress);
			} else {
				return null;
			}
		} else {
			netmask = wifiManager.getDhcpInfo().netmask;
		}
		int hostBits = ~netmask;
		int broadcastAddressInt = hostBits | ipAddress;
		String broadcastAddress = intToIPAddress(broadcastAddressInt);
		return broadcastAddress;
	}

	@SuppressLint("DefaultLocale")
	public String intToIPAddress(int ipInt) {
		return String.format("%d.%d.%d.%d", (ipInt & 0xff), (ipInt >> 8 & 0xff), (ipInt >> 16 & 0xff),
				(ipInt >> 24 & 0xff));
	}

	@SuppressLint("NewApi")
	public int getNetMaskForHotspot(InetAddress inetAddress) {
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

	public int InetAddrToInt(InetAddress inetAddress) {
		int inetAddressInt = 0;
		byte[] inetByte = inetAddress.getAddress();
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			inetAddressInt += (inetByte[3 - i] & 0x000000FF) << shift;
		}
		return inetAddressInt;
	}

	public InetAddress getIpForHotspot() {
		InetAddress hotspotAddress = null;
		try {
			InetAddress inetAddress = null;
			for (Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces(); networkInterface
					.hasMoreElements();) {
				NetworkInterface singleInterface = networkInterface.nextElement();
				for (Enumeration<InetAddress> IpAddresses = singleInterface.getInetAddresses(); IpAddresses
						.hasMoreElements();) {
					inetAddress = IpAddresses.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& (singleInterface.getDisplayName().contains("wlan0") || singleInterface.getDisplayName()
									.contains("eth0"))) {
						hotspotAddress = inetAddress;
					}
				}
			}
		} catch (SocketException ex) {
			ex.getMessage();
		}
		return hotspotAddress;
	}

	public InetAddress stringToInetAddress(String broadcastAddress) {
		InetAddress broadcastAddressInet = null;
		try {
			broadcastAddressInet = InetAddress.getByName(broadcastAddress);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		return broadcastAddressInet;
	}

	public HashMap<String, String> getAvailableIps() {
		return availableIpsList;
	}

	public connectionState setUpConnection(Spinner ipSpinner) {
		connectionState state;
		if (ipSpinner == null) {
			state = connectionState.UNCONNECTED;
			return state;
		}
		if (ipSpinner.getSelectedItem() == null) {
			state = connectionState.UNCONNECTED;
			return state;
		}
		String serverName = ipSpinner.getSelectedItem().toString();
		if (!availableIpsList.containsKey(serverName)) {
			state = connectionState.UNCONNECTED;
			return state;
		}
		String ip = stripPort(availableIpsList.get(serverName));
		return (waitForAcceptance(ip, serverName));
	}

	public void cancelConnection() {
		if (connection != null) {
			connection.stopThread();
			connection = null;
		}
	}

	public connectionState waitForAcceptance(String ip, String serverName) {
		Connection newConnection = null;
		connectionState state;
		if (serverName != null) {
			newConnection = new Connection(ip, this, serverName);
			newConnection.start();
		} else {
			state = connectionState.UNCONNECTED;
			return state;
		}
		while (true) {
			state = newConnection.getConnectionState();
			switch (state) {
				case UNDEFINED:
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				case CONNECTED:
					break;
				case UNCONNECTED:
					newConnection.stopThread();
					PcConnectionManager.getInstance(context).setConnectionAlreadySetUp(false);
					break;
				case UNCONNECTED_ILLEGALVERSION:
					newConnection.stopThread();
					PcConnectionManager.getInstance(context).setConnectionAlreadySetUp(false);
					break;
				default:
					Log.w("PcConnectionManager", "Not handled ConnectionState");
					PcConnectionManager.getInstance(context).setConnectionAlreadySetUp(false);
			}
			break;
		}
		return state;
	}

	public String stripPort(String ipWithPort) {
		int pos = ipWithPort.indexOf(":");
		if (pos != -1) {
			ipWithPort = ipWithPort.substring(1, pos);
		}
		return ipWithPort;
	}

	public void setConnectionAlreadySetUp(boolean state) {
		connectionAlreadySetUp = state;
	}

	public boolean getConnectionAlreadySetUp() {
		return connectionAlreadySetUp;
	}

	public StageActivity getStageActivity() {
		return stageActivity;
	}

	public void setStageActivity(StageActivity stageActivity) {
		this.stageActivity = stageActivity;
	}

	public void setConnection(Connection connection) {
		instance.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}

	public int getVersionId() {
		return versionId;
	}

	public int getServerVersionId() {
		return serverVersionId;
	}

	public void setServerVersionId(int serverVersionId) {
		this.serverVersionId = serverVersionId;
	}

}
