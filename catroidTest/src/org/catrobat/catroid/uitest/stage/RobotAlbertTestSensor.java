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

import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;

import org.apache.http.util.ByteArrayBuffer;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.DeviceListActivity;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.RobotAlbertFrontLedBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BTDummyClient;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class RobotAlbertTestSensor extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final int IMAGE_FILE_ID = org.catrobat.catroid.test.R.raw.icon;
	private static final int MOTOR_ACTION = 0;

	// needed for testdevices
	// Bluetooth server is running with a name that starts with 'kitty'
	// e.g. kittyroid-0, kittyslave-0
	private static final String PAIRED_BLUETOOTH_SERVER_DEVICE_NAME = "T420-40:2C:F4:69:D0:21";//"michael";
	//private static final String PAIRED_BLUETOOTH_SERVER_DEVICE_NAME = "michael-ThinkPad-T420-0-40:2C:F4:69:D0:21";//"michael";
	//private static final String PAIRED_BLUETOOTH_SERVER_DEVICE_NAME = "michael-ThinkPad-T420-0";//"michael";

	// needed for testdevices
	// unavailable device is paired with a name that starts with 'SWEET'
	// e.g. SWEETHEART

	private static final String PAIRED_UNAVAILABLE_DEVICE_NAME = "SWEET";
	private static final String PAIRED_UNAVAILABLE_DEVICE_MAC = "00:23:4D:F5:A6:18";

	private final String projectName = UiTestUtils.PROJECTNAME1;
	private final String spriteName = "testSprite";

	private Project project;
	private UserVariablesContainer userVariablesContainer;
	private SetVariableBrick setVariableBrick;
	private SetVariableBrick setVariableBrick2;
	private Sprite sprite;

	//ArrayList<byte[]> sentCommands = new ArrayList<byte[]>();
	ByteArrayBuffer sendCommands = new ByteArrayBuffer(1024);

	public RobotAlbertTestSensor() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		//createTestProject();
		UiTestUtils.prepareStageForTest();
		//UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
		//UiTestUtils.prepareStageForTest();
	}

	@Override
	public void tearDown() throws Exception {
		userVariablesContainer.deleteUserVariableByName("p1");
		userVariablesContainer.deleteUserVariableByName("p2");
		userVariablesContainer.deleteUserVariableByName("sprite_var1");
		userVariablesContainer.deleteUserVariableByName("sprite_var2");
		super.tearDown();
	}

	public void testVariableBricks() {

		//		BTDummyClient dummy = new BTDummyClient();
		//		dummy.initializeAndConnectToServer(BTDummyClient.SERVERDUMMYROBOTALBERT);

		createTestProject();

		//		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		//		assertTrue("Bluetooth not supported on device", bluetoothAdapter != null);
		//		if (!bluetoothAdapter.isEnabled()) {
		//			bluetoothAdapter.enable();
		//			solo.sleep(5000);
		//		}
		//
		//		ArrayList<String> autoConnectIDs = new ArrayList<String>();
		//		autoConnectIDs.add("IM_NOT_A_MAC_ADDRESS");
		//		DeviceListActivity deviceListActivity = new DeviceListActivity();
		//		Reflection.setPrivateField(deviceListActivity, "autoConnectIDs", autoConnectIDs);

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

		//enable albert bricks, if disabled at start
		//solo.clickOnMenuItem(solo.getString(R.string.settings));
		//solo.goBack();

		if (!preferences.getBoolean("setting_robot_albert_bricks", false)) {
			Log.d("RobotAlbertTest", "enabling albert bricks");
			solo.clickOnMenuItem(solo.getString(R.string.settings));
			solo.clickOnText(solo.getString(R.string.pref_enable_robot_albert_bricks));
			solo.goBack();
		}
		Log.d("RobotAlbertTest", "check finished");
		solo.sleep(1000);

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		//		Spinner setVariableSpinner = solo.getCurrentViews(Spinner.class).get(0);
		//
		//		solo.clickOnView(setVariableSpinner);
		//		solo.clickOnText("p1");
		//		solo.clickOnText("0");
		//		solo.sleep(1000);
		//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		//		solo.sleep(1000);
		//		solo.waitForText(getActivity().getString(R.string.formula_editor_sensor_albert_robot_distance_left));
		//		solo.clickOnText(getActivity().getString(R.string.formula_editor_sensor_albert_robot_distance_left));
		//		solo.sleep(1000);
		//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		//
		//		solo.clickOnText("1.1");
		//		solo.sleep(1000);
		//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		//		solo.sleep(1000);
		//		solo.waitForText(getActivity().getString(R.string.formula_editor_sensor_albert_robot_distance_right));
		//		solo.clickOnText(getActivity().getString(R.string.formula_editor_sensor_albert_robot_distance_right));
		//		solo.sleep(1000);
		//		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));
		//
		//		//disable albert bricks. Even if disabled, programs using Albert-bricks should work
		//		if (preferences.getBoolean("setting_robot_albert_bricks", false)) {
		//			solo.clickOnMenuItem(solo.getString(R.string.settings));
		//			solo.clickOnText(solo.getString(R.string.pref_enable_robot_albert_bricks));
		//			solo.goBack();
		//		}

		BTDummyClient dummy = new BTDummyClient();
		dummy.initializeAndConnectToServer(BTDummyClient.SERVERDUMMYROBOTALBERT);

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		assertTrue("Bluetooth not supported on device", bluetoothAdapter != null);
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
			solo.sleep(5000);
		}

		ArrayList<String> autoConnectIDs = new ArrayList<String>();
		autoConnectIDs.add("IM_NOT_A_MAC_ADDRESS");
		DeviceListActivity deviceListActivity = new DeviceListActivity();
		Reflection.setPrivateField(deviceListActivity, "autoConnectIDs", autoConnectIDs);

		//solo.waitForView(solo.getView(R.id.button_play));
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		Log.d("RobotAlbertTest", "1");
		//solo.waitForActivity(StageActivity.class.getSimpleName());
		Log.d("RobotAlbertTest", "2");
		solo.sleep(2000);

		Log.d("RobotAlbertTest", "in Stage");

		ListView deviceList = solo.getCurrentViews(ListView.class).get(0);
		String connectedDeviceName = null;
		for (int i = 0; i < deviceList.getCount(); i++) {
			String deviceName = (String) deviceList.getItemAtPosition(i);
			if (deviceName.startsWith(PAIRED_BLUETOOTH_SERVER_DEVICE_NAME)) {
				connectedDeviceName = deviceName;
				break;
			}
		}
		Log.d("Robot Albert Test", "connectedDeviceName=" + connectedDeviceName + "  deviceList.getItemAtPosition(0)"
				+ deviceList.getItemAtPosition(0));
		solo.clickOnText(connectedDeviceName);

		solo.sleep(5000);
		solo.assertCurrentActivity("Not in stage - connection to bluetooth-device failed", StageActivity.class);
		solo.clickOnScreen(ScreenValues.SCREEN_WIDTH / 2, ScreenValues.SCREEN_HEIGHT / 2);
		solo.sleep(9000);

		double distanceLeft = userVariablesContainer.getUserVariable("p1", sprite).getValue();
		double distanceRight = userVariablesContainer.getUserVariable("p2", sprite).getValue();
		Log.d("RobotAlbertTest", "left=" + distanceLeft);
		Log.d("RobotAlbertTest", "right=" + distanceRight);
		assertEquals("Variable has the wrong value after stage", 50.0, distanceLeft);
		assertEquals("Variable has the wrong value after stage", 50.0, distanceRight);

		solo.goBack();
		solo.sleep(1000);
		solo.goBack();
		solo.sleep(1000);
		solo.goBack();

		//		assertEquals("Variable has the wrong value after stage", 42.0,
		//				userVariablesContainer.getUserVariable("p2", sprite).getValue());

	}

	// This test requires the NXTBTTestServer to be running or a LegoNXT Robot to run! Check connect string to see if you connect to the right device!
	//	@Device
	//	public void testAlbertFunctionality() {
	//		Log.d("TestRobotAlbert", "initialized BTDummyClient");
	//
	//		BTDummyClient dummy = new BTDummyClient();
	//		dummy.initializeAndConnectToServer(BTDummyClient.SERVERDUMMYROBOTALBERT);
	//
	//		//createTestProject();
	//
	//		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	//		assertTrue("Bluetooth not supported on device", bluetoothAdapter != null);
	//		if (!bluetoothAdapter.isEnabled()) {
	//			bluetoothAdapter.enable();
	//			solo.sleep(5000);
	//		}
	//
	//		ArrayList<String> autoConnectIDs = new ArrayList<String>();
	//		autoConnectIDs.add("IM_NOT_A_MAC_ADDRESS");
	//		DeviceListActivity deviceListActivity = new DeviceListActivity();
	//		Reflection.setPrivateField(deviceListActivity, "autoConnectIDs", autoConnectIDs);
	//
	//		solo.clickOnText(solo.getString(R.string.main_menu_continue));
	//		solo.waitForActivity(ProjectActivity.class.getSimpleName());
	//
	//		solo.clickOnText("testSprite");
	//		solo.sleep(5000);
	//		solo.clickOnText(solo.getString(R.string.scripts));
	//		solo.sleep(5000);
	//
	//		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
	//		solo.sleep(2000);
	//
	//		ListView deviceList = solo.getCurrentViews(ListView.class).get(0);
	//		String connectedDeviceName = null;
	//		for (int i = 0; i < deviceList.getCount(); i++) {
	//			String deviceName = (String) deviceList.getItemAtPosition(i);
	//			if (deviceName.startsWith(PAIRED_BLUETOOTH_SERVER_DEVICE_NAME)) {
	//				connectedDeviceName = deviceName;
	//				break;
	//			}
	//		}
	//		Log.d("Robot Albert Test", "connectedDeviceName=" + connectedDeviceName + "  deviceList.getItemAtPosition(0)"
	//				+ deviceList.getItemAtPosition(0));
	//		solo.clickOnText(connectedDeviceName);
	//
	//		solo.sleep(5000);
	//		solo.assertCurrentActivity("Not in stage - connection to bluetooth-device failed", StageActivity.class);
	//
	//		solo.clickOnScreen(ScreenValues.SCREEN_WIDTH / 2, ScreenValues.SCREEN_HEIGHT / 2);
	//		solo.sleep(5000);
	//
	//		ByteArrayBuffer receivedBufferOld = dummy.getReceivedFeedback();
	//		ByteArrayBuffer receivedBuffer = removeSensorCommands(receivedBufferOld);
	//
	//		boolean ok = Arrays.equals(sendCommands.toByteArray(), receivedBuffer.toByteArray());
	//
	//		Log.d("TestRobotAlbert_New", receivedBuffer.toByteArray().toString());
	//		Log.d("TestRobotAlbert", "Array comparision successful: " + ok);
	//
	//		//dummy.sendSetVariableCommandToDummyServer(name, value)
	//
	//		int lenRec = receivedBuffer.length();
	//		int lenSent1 = sendCommands.length();
	//
	//		Log.d("TestRobotAlbert",
	//				"lenRec=" + lenRec + "\nlenSent1=" + lenSent1 + "\nlenWithSensor=" + receivedBufferOld.length());
	//		assertTrue("messages reveived and sent are not equal", ok == true);
	//		Log.d("temp", receivedBuffer.toString());
	//
	//		solo.sleep(1000);
	//		Log.d("TestRobotAlbert", "before goback");
	//		solo.goBack();
	//		solo.sleep(100);
	//		solo.goBack();
	//		solo.sleep(100);
	//		solo.goBack();
	//		solo.sleep(100);
	//		solo.goBack();
	//
	//		Log.d("TestRobotAlbert", "after goback");
	//		//RobotAlbertBtCommunicator.enableRequestConfirmFromDevice(false);
	//	}

	/*
	 * @Device
	 * public void NXTConnectionDialogGoBack() {
	 * 
	 * createTestProject();
	 * 
	 * ArrayList<String> autoConnectIDs = new ArrayList<String>();
	 * autoConnectIDs.add("IM_NOT_A_MAC_ADDRESS");
	 * DeviceListActivity deviceListActivity = new DeviceListActivity();
	 * Reflection.setPrivateField(deviceListActivity, "autoConnectIDs", autoConnectIDs);
	 * 
	 * BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	 * assertTrue("Bluetooth not supported on device", bluetoothAdapter != null);
	 * if (!bluetoothAdapter.isEnabled()) {
	 * bluetoothAdapter.enable();
	 * solo.sleep(5000);
	 * }
	 * 
	 * solo.clickOnText(solo.getString(R.string.main_menu_continue));
	 * solo.waitForActivity(ProjectActivity.class.getSimpleName());
	 * UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
	 * solo.sleep(1000);
	 * solo.assertCurrentActivity("Devicelist not shown!", DeviceListActivity.class);
	 * solo.goBack();
	 * solo.sleep(1000);
	 * UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
	 * solo.sleep(1000);
	 * solo.assertCurrentActivity("Devicelist not shown!", DeviceListActivity.class);
	 * 
	 * }
	 */

	private void createTestProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);

		//RobotAlbertFrontLedBrick robotAlbertFrontLedBrick = new RobotAlbertFrontLedBrick(sprite, new Formula(1));
		//script.addBrick(robotAlbertFrontLedBrick);
		//RepeatBrickTest rep = new Rep
		//ForeverBrick loop = new ForeverBrick(sprite);
		//script.addBrick(loop);

		userVariablesContainer = project.getUserVariables();
		userVariablesContainer.addProjectUserVariable("p1");
		userVariablesContainer.addProjectUserVariable("p2");
		userVariablesContainer.addSpriteUserVariable("sprite_var1");
		userVariablesContainer.addSpriteUserVariable("sprite_var2");

		setVariableBrick = new SetVariableBrick(sprite, 0.0);
		//script.addBrick(setVariableBrick);
		setVariableBrick2 = new SetVariableBrick(sprite, 1.1);
		//script.addBrick(setVariableBrick2);

		RobotAlbertFrontLedBrick robotAlbertFrontLedBrick = new RobotAlbertFrontLedBrick(sprite, new Formula(1));
		script.addBrick(robotAlbertFrontLedBrick);
		WaitBrick waitBrick = new WaitBrick(sprite, 5000);
		script.addBrick(waitBrick);

		sprite.addScript(script);
		project.addSprite(sprite);
	}

}
