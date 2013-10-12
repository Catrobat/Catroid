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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SendToPcBrick;
import org.catrobat.catroid.io.Connection.connectionState;
import org.catrobat.catroid.stage.StageActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

public class PcConnectionManager {

	private final int versionId = 3;

	private static PcConnectionManager instance = null;
	private static PcConnectionManagerCreator creator;
	private Context context;
	private static final int port = 64000;
	private String ip;

	private ProgressDialog connectingProgressDialog;
	private HashMap<String, String> availableIpsList;
	private boolean connectionAlreadySetUp = false;
	private StageActivity stageActivity;
	private Connection connection;
	private int serverVersionId;
	private DatagramSocket dataSocket;
	private WifiManager wifiManager;
	private AlertDialog infoSettingsDialog;

	protected PcConnectionManager(Context context) {
		connection = null;
		if (context != null) {
			wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		}
		try {
			dataSocket = new DatagramSocket();
		} catch (SocketException e) {
			return;
		}
	}

	public static PcConnectionManager getInstance(Context context) {
		if (instance == null) {
			if (creator == null) {
				instance = new PcConnectionManager(context);
			} else {
				instance = creator.create();
			}
		}
		if (context != null) {
			instance.context = context;
		}
		return instance;
	}

	public static void setCreator(PcConnectionManagerCreator creator) {
		PcConnectionManager.creator = creator;
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
			InetAddress broadcastInetAddress = IPv4Manager.stringToInetAddress(broadcastAdressString);
			DatagramPacket dataPacket = null;
			if (broadcastInetAddress != null) {
				dataPacket = new DatagramPacket(message, message.length, broadcastInetAddress, port);
			}
			availableIpsList = new HashMap<String, String>();
			if (dataSocket.isClosed()) {
				try {
					dataSocket = new DatagramSocket();
				} catch (SocketException e) {
				}
			}
			try {
				dataSocket.setSoTimeout(3000);
			} catch (SocketException e) {
				Log.v("PcConnectionManager", "Unable to set SocketTimeout");
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
			byte[] ipAddressServer = new byte[64];
			DatagramPacket dataPacketReceived = new DatagramPacket(ipAddressServer, ipAddressServer.length);
			while (true) {
				try {
					dataSocket.receive(dataPacketReceived);
					String ipServer = new String(dataPacketReceived.getData(), 0, dataPacketReceived.getLength());
					ip = IPv4Manager.stripPort(dataPacketReceived.getSocketAddress().toString());
					availableIpsList.put(ipServer, ip);
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
		Activity activity = (Activity) context;
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				connectingProgressDialog.dismiss();
				ArrayList<String> ipList = new ArrayList<String>();
				Set<String> keys = availableIpsList.keySet();
				for (String singleKey : keys) {
					ipList.add(singleKey);
				}
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
						android.R.layout.simple_spinner_item, ipList);
				dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				ipSpinner.setAdapter(dataAdapter);
				if (ipList.size() > 0) {
					ipSpinner.setClickable(true);
				}
			}
		});
		try {
			dataSocket.setBroadcast(false);
		} catch (SocketException e) {
		}
	}

	@SuppressWarnings("deprecation")
	public String getBroadcastAddress() {
		int ipAddress = 0;
		int netmask = 0;
		ipAddress = wifiManager.getConnectionInfo().getIpAddress();
		if (ipAddress == 0) {
			InetAddress inetAddress = null;
			try {
				inetAddress = getIpAddressForHotspot();
			} catch (SocketException e) {
			}
			if (inetAddress != null) {
				ipAddress = IPv4Manager.inetAddressToInt(inetAddress);
				netmask = IPv4Manager.getNetMaskForHotspot(inetAddress);
			} else {
				return null;
			}
		} else {
			netmask = wifiManager.getDhcpInfo().netmask;
		}
		int hostBits = ~netmask;
		int broadcastAddressInt = hostBits | ipAddress;
		String broadcastAddress = IPv4Manager.intToIPAddress(broadcastAddressInt);
		return broadcastAddress;
	}

	public InetAddress getIpAddressForHotspot() throws SocketException {
		InetAddress hotspotIpAddress = null;
		InetAddress inetAddress = null;
		for (Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces(); networkInterface
				.hasMoreElements();) {
			NetworkInterface singleInterface = networkInterface.nextElement();
			for (Enumeration<InetAddress> ipAddresses = singleInterface.getInetAddresses(); ipAddresses
					.hasMoreElements();) {
				inetAddress = ipAddresses.nextElement();
				// "wlan0" and "wl0.1" are device-dependent:
				// when this function crashes, you have to search the interface of your device
				// (via adb-shell -> # netcfg) and add it below
				if (!inetAddress.isLoopbackAddress()
						&& (singleInterface.getDisplayName().contains("wlan0")
						/* || singleInterface.getDisplayName().contains("eth0") */|| singleInterface.getDisplayName()
								.contains("wl0.1"))) {
					hotspotIpAddress = inetAddress;
				}
			}
		}
		return hotspotIpAddress;
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
		String ip = IPv4Manager.stripPort(availableIpsList.get(serverName));
		return (waitForAcceptance(ip, serverName));
	}

	public void cancelConnection() {
		if (connection != null) {
			try {
				connection.stopThread();
			} catch (IOException e) {
				e.printStackTrace();
			}
			connection = null;
		}
	}

	public connectionState waitForAcceptance(String ip, String serverName) {
		Connection newConnection = null;
		connectionState state;
		if (serverName != null) {
			newConnection = createNewConnection(serverName);
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
					try {
						newConnection.stopThread();
					} catch (IOException e) {
						e.printStackTrace();
					}
					PcConnectionManager.getInstance(context).setConnectionAlreadySetUp(false);
					break;
				case UNCONNECTED_ILLEGALVERSION:
					try {
						newConnection.stopThread();
					} catch (IOException e) {
						e.printStackTrace();
					}
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

	public void checkSettingsIfPcConnectionEnabeled() {

		if (ProjectManager.getInstance().getCurrentProject() == null) {
			return;
		}
		ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		for (Sprite sprite : spriteList) {
			for (int i = 0; i < sprite.getNumberOfScripts(); i++) {
				Script script = sprite.getScript(i);
				for (Brick brick : script.getBrickList()) {
					if (brick instanceof SendToPcBrick
							&& !sharedPreferences.getBoolean("setting_pc_connection_bricks", false)) {
						Activity activity = (Activity) context;
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								startInfoSettingsDialog();
							}
						});
						return;
					}
				}
			}
		}
	}

	public void startInfoSettingsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getString(R.string.info_project_contains_send_to_pc_brick)).setCancelable(false)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						infoSettingsDialog.dismiss();
					}
				});
		infoSettingsDialog = builder.create();
		infoSettingsDialog.show();
	}

	public Connection createNewConnection(String serverName) {
		return new Connection(ip, this, serverName);
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

	public int getPort() {
		return port;
	}

	public static interface PcConnectionManagerCreator {
		public PcConnectionManager create();
	}
}
