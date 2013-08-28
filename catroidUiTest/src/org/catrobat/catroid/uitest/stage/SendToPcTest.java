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
package org.catrobat.catroid.uitest.stage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SendToPcBrick;
import org.catrobat.catroid.content.bricks.WhenBrick;
import org.catrobat.catroid.io.Command;
import org.catrobat.catroid.io.Command.commandType;
import org.catrobat.catroid.io.Confirmation;
import org.catrobat.catroid.io.Confirmation.ConfirmationState;
import org.catrobat.catroid.io.Connection;
import org.catrobat.catroid.io.CustomKeyboard;
import org.catrobat.catroid.io.PcConnectionManager;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.SimulatedDatagramSocket;
import org.catrobat.catroid.uitest.util.SimulatedPcConnectionManager;
import org.catrobat.catroid.uitest.util.SimulatedSocket;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class SendToPcTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private WifiManager mockWifiManager;
	private SimulatedDatagramSocket simulatedDatagramSocket;
	private String serverIp = "192.163.27.3";
	private String broadcastAddress;
	private ObjectInputStream inputStream;
	private Connection connection;
	private byte[] receivedObject;
	private int timeout;
	private boolean wifiOn = false;

	public SendToPcTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		createProject();
		receivedObject = new byte[1000];
		timeout = 0;
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		UiTestUtils.clearProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		receivedObject = null;
		WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			if (wifiOn) {
				turnWiFiAPOnOff(getActivity(), false);
			}
			wifiManager.setWifiEnabled(true);
		}
		super.tearDown();
	}

	public void testBroadCastWithWifiFindElement() throws InterruptedException, SocketException {

		PcConnectionManager connectionManager = PcConnectionManager.getInstance(null);
		setupMocks(true);
		simulatedDatagramSocket.setFindNetwork(true);
		startProject();
		Spinner currentSpinner = waitForSpinner();
		assertTrue("Spinner should be initialized!", timeout < 15);
		assertTrue("Spinner should contain one ipAddress!", currentSpinner.getAdapter().getCount() == 1);
		assertTrue("BroadcastAddress wasn't calculated correctly!", simulatedDatagramSocket.getBroadcastAddress()
				.equals(broadcastAddress));
		assertTrue("Client has wrong serverIpAddress",
				serverIp.equals(Reflection.getPrivateField(PcConnectionManager.class, connectionManager, "ip")));
		solo.clickOnText(solo.getString(R.string.cancel_button));
	}

	public void testBroadCastWithWifiFindNoElement() throws InterruptedException, SocketException {

		setupMocks(true);
		simulatedDatagramSocket.setFindNetwork(false);
		startProject();
		Spinner currentSpinner = waitForSpinner();
		assertTrue("Spinner should be initialized!", timeout < 15);
		assertTrue("Spinner should be empty!", currentSpinner.getAdapter().getCount() == 0);
		assertTrue("BroadcastAddress wasn't calculated correctly!", simulatedDatagramSocket.getBroadcastAddress()
				.equals(broadcastAddress));
		solo.clickOnText(solo.getString(R.string.cancel_button));
	}

	// When tests using a hotspot AP fail locally --> more information in 
	// PcConnectionManager.getIpAddressForHotspot()
	@Device
	public void testBroadCastWithHotspotFindElement() throws InterruptedException, SocketException {

		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			return;
		}
		PcConnectionManager connectionManager = PcConnectionManager.getInstance(null);
		turnWiFiAPOnOff(getActivity(), true);
		wifiOn = true;
		solo.sleep(5000);
		setupMocks(false);
		simulatedDatagramSocket.setFindNetwork(true);

		startProject();
		Spinner currentSpinner = waitForSpinner();
		boolean checkInPrivateNetwork = false;
		if (simulatedDatagramSocket.getBroadcastAddress().contains("192.168")
				|| simulatedDatagramSocket.getBroadcastAddress().contains("10.0")
				|| simulatedDatagramSocket.getBroadcastAddress().contains("172.16")) {
			checkInPrivateNetwork = true;
		}
		String hallo = "BroadcastAddress is not in private network! --> "
				+ simulatedDatagramSocket.getBroadcastAddress().toString();
		assertTrue("Spinner should be initialized!", timeout < 15);
		assertTrue("Spinner should contain one ipAddress!", currentSpinner.getAdapter().getCount() == 1);
		assertTrue(hallo, checkInPrivateNetwork);
		assertTrue("Address is no broadcastAddress!", simulatedDatagramSocket.getBroadcastAddress().contains("255"));
		assertTrue("Socket uses wrong port!",
				simulatedDatagramSocket.getBroadcastAddress().contains(String.valueOf(connectionManager.getPort())));
		assertTrue("Client has wrong serverIpAddress",
				serverIp.equals(Reflection.getPrivateField(PcConnectionManager.class, connectionManager, "ip")));
		turnWiFiAPOnOff(getActivity(), false);
		wifiOn = false;
		solo.clickOnText(solo.getString(R.string.cancel_button));
	}

	// When tests using a hotspot AP fail locally --> more information in 
	// PcConnectionManager.getIpAddressForHotspot()
	@Device
	public void testBroadCastWithHotspotFindNoElement() throws InterruptedException, SocketException {

		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
			return;
		}
		PcConnectionManager connectionManager = PcConnectionManager.getInstance(null);
		turnWiFiAPOnOff(getActivity(), true);
		wifiOn = true;
		setupMocks(false);
		simulatedDatagramSocket.setFindNetwork(false);
		startProject();
		Spinner currentSpinner = waitForSpinner();
		boolean checkInPrivateNetwork = false;
		if (simulatedDatagramSocket.getBroadcastAddress().contains("192.168")
				|| simulatedDatagramSocket.getBroadcastAddress().contains("10.0")
				|| simulatedDatagramSocket.getBroadcastAddress().contains("172.16")) {
			checkInPrivateNetwork = true;
		}
		assertTrue("Spinner should be initialized!", timeout < 15);
		assertTrue("Spinner should be empty!", currentSpinner.getAdapter().getCount() == 0);
		assertTrue("BroadcastAddress is not in private network!", checkInPrivateNetwork);
		assertTrue("Address is no broadcastAddress!", simulatedDatagramSocket.getBroadcastAddress().contains("255"));
		assertTrue("Socket uses wrong port!",
				simulatedDatagramSocket.getBroadcastAddress().contains(String.valueOf(connectionManager.getPort())));
		turnWiFiAPOnOff(getActivity(), false);
		wifiOn = false;
		solo.clickOnText(solo.getString(R.string.cancel_button));
	}

	public void testSetUpConnectionSuccess() throws InterruptedException, IOException, ClassNotFoundException {

		Object[] objectsForInputStream = new Object[2];
		int startRegistration = 1;
		Confirmation confirmation = new Confirmation(ConfirmationState.LEGAL_VERSION_ID);
		confirmation.setVersionId(PcConnectionManager.getInstance(null).getServerVersionId());
		objectsForInputStream[0] = startRegistration;
		objectsForInputStream[1] = confirmation;
		SimulatedSocket simulatedSocket = setupSimulatedSocket(objectsForInputStream);
		Object answer = startConnection(serverIp, simulatedSocket);
		assertTrue("Invalid incoming object to start syncronisation!", answer instanceof Integer);
		assertTrue("Invalid incoming parameter to start syncronisation!", startRegistration == 1);
		assertTrue("ConnectionThread didn't start!", connection.isAlive());
	}

	public void testSetUpConnectionFail() throws InterruptedException, IOException, ClassNotFoundException {

		Object[] objectsForInputStream = new Object[2];
		int startRegistration = 1;
		Confirmation confirmation = new Confirmation(ConfirmationState.ILLEGAL_VERSION_ID);
		confirmation.setVersionId(PcConnectionManager.getInstance(null).getServerVersionId() + 1);
		objectsForInputStream[0] = startRegistration;
		objectsForInputStream[1] = confirmation;
		SimulatedSocket simulatedSocket = setupSimulatedSocket(objectsForInputStream);
		Object answer = startConnection(serverIp, simulatedSocket);
		assertTrue("Invalid incoming object to start syncronisation!", answer instanceof Integer);
		assertTrue("Invalid incoming param to start syncronisation!", startRegistration == 1);
		assertTrue("ConnectionThread shouldn't have started!", !connection.isAlive());

	}

	public void testSendCommand() throws SocketException, IOException, InterruptedException, ClassNotFoundException {

		Object[] objectsForInputStream = new Object[3];
		int startRegistration = 1;
		objectsForInputStream[0] = startRegistration;
		objectsForInputStream[1] = new Confirmation(ConfirmationState.LEGAL_VERSION_ID);
		objectsForInputStream[2] = new Confirmation(ConfirmationState.COMMAND_SEND_SUCCESSFULL);
		SimulatedSocket simulatedSocket = setupSimulatedSocket(objectsForInputStream);
		startConnection(serverIp, simulatedSocket);
		assertTrue("ConnectionThread didn't start!", connection.isAlive());
		Command command = new Command(CustomKeyboard.KEY_CONTROL, commandType.SINGLE_KEY);
		ArrayList<Command> commandList = new ArrayList<Command>();
		commandList.add(command);
		Reflection.setPrivateField(connection, "commandList", commandList);
		solo.sleep(2000);
		Object answerClientName = deserialize(receivedObject);
		Object answerCommand = deserialize(receivedObject);
		assertTrue("Sent clientName was not of type String!", answerClientName instanceof String);
		assertTrue("Sent command was not of type command!", answerCommand instanceof Command);
		assertTrue("Command was changed!", ((Command) answerCommand).getKey() == command.getKey());
		assertTrue("ConnectionThread should still be alive!", connection.isAlive());
	}

	public void testWhenConnectionBreaks() throws Exception {

		PcConnectionManager connectionManager = PcConnectionManager.getInstance(null);
		Object[] objectsForInputStream = new Object[1];
		int startRegistration = 1;
		objectsForInputStream[0] = startRegistration;
		SimulatedSocket simulatedSocket = setupSimulatedSocket(objectsForInputStream);
		StageActivity stageActivity = new StageActivity();
		Reflection.setPrivateField(PcConnectionManager.class, connectionManager, "stageActivity", stageActivity);
		Command command = new Command(CustomKeyboard.KEY_CONTROL, commandType.SINGLE_KEY);
		ArrayList<Command> commandList = new ArrayList<Command>();
		commandList.add(command);
		startConnection(serverIp, simulatedSocket);
		assertTrue("ConnectionThread didn't start!", connection.isAlive());
		Reflection.setPrivateField(connection, "commandList", commandList);
		solo.sleep(1000);
		assertTrue("Error dialog should be shown!", Reflection.getPrivateField(connection, "errorDialogOnScreen")
				.equals(true));
		assertTrue("ConnectionThread shouldn't still be alive!", !connection.isAlive());
	}

	public void testAllInOne() throws IOException {
		Object[] objectsForInputStream = new Object[3];
		int startRegistration = 1;
		Confirmation confirmation = new Confirmation(ConfirmationState.LEGAL_VERSION_ID);
		confirmation.setVersionId(PcConnectionManager.getInstance(null).getServerVersionId());
		objectsForInputStream[0] = startRegistration;
		objectsForInputStream[1] = confirmation;
		objectsForInputStream[2] = new Confirmation(ConfirmationState.COMMAND_SEND_SUCCESSFULL);
		SimulatedSocket simulatedSocket = setupSimulatedSocket(objectsForInputStream);
		Reflection.setPrivateField(PcConnectionManager.class, PcConnectionManager.getInstance(null), "instance", null);
		PcConnectionManager.setCreator(new SimulatedPcConnectionManager(simulatedSocket, serverIp, "kitten"));
		PcConnectionManager connectionManager = PcConnectionManager.getInstance(null);
		setupMocks(true);
		simulatedDatagramSocket.setFindNetwork(true);
		startProject();
		Spinner currentSpinner = waitForSpinner();
		assertTrue("Spinner should be initialized!", timeout < 15);
		assertTrue("Spinner should contain one ipAddress!", currentSpinner.getAdapter().getCount() == 1);
		assertTrue("BroadcastAddress wasn't calculated correctly!", simulatedDatagramSocket.getBroadcastAddress()
				.equals(broadcastAddress));
		assertTrue("Client has wrong serverIpAddress",
				serverIp.equals(Reflection.getPrivateField(PcConnectionManager.class, connectionManager, "ip")));
		solo.sleep(100);
		solo.clickOnText(solo.getString(R.string.ok));
		assertTrue("StageActivity should have started!", solo.waitForActivity(StageActivity.class));
		solo.clickOnScreen(200, 200);
		solo.sleep(3000);
		assertTrue("ConnectionThread should still be alive!", connectionManager.getConnection().isAlive());
		Reflection.setPrivateField(PcConnectionManager.class, connectionManager, "instance", null);
	}

	public void startProject() {
		Reflection.setPrivateField(PcConnectionManager.class, PcConnectionManager.getInstance(null), "dataSocket",
				simulatedDatagramSocket);
		Reflection.setPrivateField(PcConnectionManager.class, PcConnectionManager.getInstance(null), "wifiManager",
				mockWifiManager);
		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(solo.getCurrentActivity());
		if (!(sharedPreferences.getBoolean("setting_pc_connection_bricks", false))) {
			solo.clickOnText(solo.getString(R.string.ok));
		}
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
	}

	private void createProject() throws SocketException {
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		WhenBrick whenBrick = new WhenBrick();
		SendToPcBrick sendToPcBrick = new SendToPcBrick(sprite);
		script.addBrick(whenBrick);
		script.addBrick(sendToPcBrick);
		sprite.addScript(script);
		project.addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	public Object startConnection(String ip, SimulatedSocket simulatedSocket) throws InterruptedException,
			StreamCorruptedException, IOException, ClassNotFoundException {
		connection = new Connection(ip, PcConnectionManager.getInstance(null), "testServer");
		Reflection.setPrivateField(connection, "client", simulatedSocket);
		connection.start();
		solo.sleep(1000);
		return deserialize(receivedObject);
	}

	public void setupMocks(boolean wifi) throws SocketException {

		String clientNetmask = "255.255.255.0";
		String clientIp = "10.0.0.3";
		simulatedDatagramSocket = new SimulatedDatagramSocket();
		simulatedDatagramSocket.setServerIp(serverIp);
		mockWifiManager = Mockito.mock(WifiManager.class);
		WifiInfo mockWifiInfo = Mockito.mock(WifiInfo.class);
		DhcpInfo mockDhcpInfo = Mockito.mock(DhcpInfo.class);
		Mockito.when(mockWifiManager.getConnectionInfo()).thenReturn(mockWifiInfo);
		if (wifi) {
			broadcastAddress = "/10.0.0.255";
			broadcastAddress = addPort(broadcastAddress);
			Mockito.when(mockWifiInfo.getIpAddress()).thenReturn(ipAddressToInt(clientIp));
		} else { // hotspot is activated
			Mockito.when(mockWifiInfo.getIpAddress()).thenReturn(0);
			broadcastAddress = addPort(broadcastAddress);
		}
		mockDhcpInfo.netmask = ipAddressToInt(clientNetmask);
		Mockito.when(mockWifiManager.getDhcpInfo()).thenReturn(mockDhcpInfo);
	}

	@SuppressLint("DefaultLocale")
	private int ipAddressToInt(String ip) {
		String subString1 = ip.substring(0, ip.indexOf('.'));
		ip = ip.substring(ip.indexOf('.') + 1, ip.length());
		String subString2 = ip.substring(0, ip.indexOf('.'));
		ip = ip.substring(ip.indexOf('.') + 1, ip.length());
		String subString3 = ip.substring(0, ip.indexOf('.'));
		ip = ip.substring(ip.indexOf('.') + 1, ip.length());
		String subString4 = ip;
		return (Integer.valueOf(subString4) << 24) | (Integer.valueOf(subString3) << 16)
				| (Integer.valueOf(subString2) << 8) | (Integer.valueOf(subString1));
	}

	public SimulatedSocket setupSimulatedSocket(Object[] objectsForInputStream) throws SocketException, IOException {
		return new SimulatedSocket(serialize(objectsForInputStream), new OutputStream() {

			private int byteCount = 0;

			@Override
			public void write(int actualByte) throws IOException {
				receivedObject[byteCount] = (byte) actualByte;
				byteCount++;
			}
		});
	}

	private String addPort(String ip) {
		return ip + ":" + PcConnectionManager.getInstance(null).getPort();
	}

	private InputStream serialize(Object[] object) throws IOException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int size = object.length;
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		for (int i = 0; i < size; i++) {
			objectOutputStream.writeObject(object[i]);
		}
		objectOutputStream.flush();
		objectOutputStream.close();
		InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		return inputStream;
	}

	private Object deserialize(Object data) throws IOException, ClassNotFoundException {

		if (data instanceof byte[]) {
			ByteArrayInputStream input = new ByteArrayInputStream((byte[]) data);
			if (inputStream == null) {
				inputStream = new ObjectInputStream(input);
			}
		} else {
			inputStream = (ObjectInputStream) data;
		}
		return inputStream.readObject();
	}

	public void turnWiFiAPOnOff(Context context, boolean enabled) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		try {
			wifiManager.setWifiEnabled(false);
			Method method = wifiManager.getClass()
					.getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
			method.invoke(wifiManager, null, enabled);
		} catch (Exception e) {
			Log.e(Context.WIFI_SERVICE, e.getMessage());
		}
		int loopMax = 10;
		if (!enabled) {
			while (loopMax > 0) {
				try {
					solo.sleep(500);
					loopMax--;
				} catch (Exception e) {
				}
			}
		} else {
			while (loopMax > 0) {
				try {
					solo.sleep(500);
					loopMax--;
				} catch (Exception e) {
				}
			}
		}
	}

	public Spinner waitForSpinner() {
		Spinner currentSpinner;
		while (true) {
			if (solo.getCurrentViews(Spinner.class).size() != 0 || timeout > 15) {
				currentSpinner = solo.getCurrentViews(Spinner.class).get(0);
				break;
			} else {
				solo.sleep(1000);
			}
		}
		return currentSpinner;
	}
}
