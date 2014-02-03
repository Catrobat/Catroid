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
import android.util.Log;
import android.widget.ListView;

import org.apache.http.util.ByteArrayBuffer;
import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.DeviceListActivity;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.RobotAlbertBuzzerBrick;
import org.catrobat.catroid.content.bricks.RobotAlbertFrontLedBrick;
import org.catrobat.catroid.content.bricks.RobotAlbertMotorActionBrick;
import org.catrobat.catroid.content.bricks.RobotAlbertRgbLedEyeActionBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.robot.albert.ControlCommands;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BTDummyClient;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class RobotAlbertTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
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

	//ArrayList<byte[]> sentCommands = new ArrayList<byte[]>();
	ByteArrayBuffer sendCommands = new ByteArrayBuffer(1024);

	public RobotAlbertTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
	}

	// This test requires the NXTBTTestServer to be running or a LegoNXT Robot to run! Check connect string to see if you connect to the right device!
	@Device
	public void testAlbertFunctionality() {
		Log.d("TestRobotAlbert", "initialized BTDummyClient");

		BTDummyClient dummy = new BTDummyClient();
		dummy.initializeAndConnectToServer(BTDummyClient.SERVERDUMMYROBOTALBERT);

		createTestproject(projectName);

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

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(2000);

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

		solo.sleep(8000);
		solo.assertCurrentActivity("Not in stage - connection to bluetooth-device failed", StageActivity.class);

		solo.clickOnScreen(ScreenValues.SCREEN_WIDTH / 2, ScreenValues.SCREEN_HEIGHT / 2);
		solo.sleep(5000);

		ByteArrayBuffer receivedBuffer = dummy.getReceivedFeedback();
		boolean ok = Arrays.equals(sendCommands.toByteArray(), receivedBuffer.toByteArray());

		Log.d("TestRobotAlbert_New", receivedBuffer.toByteArray().toString());
		Log.d("TestRobotAlbert", "Array comparision successful: " + ok);

		int lenRec = receivedBuffer.length();
		int lenSent1 = sendCommands.length();

		Log.d("TestRobotAlbert", "lenRec=" + lenRec + "\nlenSent1=" + lenSent1);
		assertTrue("messages reveived and sent are not equal", ok == true);

		solo.sleep(1000);
		Log.d("TestRobotAlbert", "before goback");
		solo.goBack();
		solo.sleep(100);
		solo.goBack();
		solo.sleep(100);
		solo.goBack();
		solo.sleep(100);
		solo.goBack();

		Log.d("TestRobotAlbert", "after goback");
		//RobotAlbertBtCommunicator.enableRequestConfirmFromDevice(false);
	}

	@Device
	public void NXTConnectionDialogGoBack() {

		createTestproject(projectName);

		ArrayList<String> autoConnectIDs = new ArrayList<String>();
		autoConnectIDs.add("IM_NOT_A_MAC_ADDRESS");
		DeviceListActivity deviceListActivity = new DeviceListActivity();
		Reflection.setPrivateField(deviceListActivity, "autoConnectIDs", autoConnectIDs);

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		assertTrue("Bluetooth not supported on device", bluetoothAdapter != null);
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
			solo.sleep(5000);
		}

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(1000);
		solo.assertCurrentActivity("Devicelist not shown!", DeviceListActivity.class);
		solo.goBack();
		solo.sleep(1000);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(1000);
		solo.assertCurrentActivity("Devicelist not shown!", DeviceListActivity.class);

	}

	private void createTestproject(String projectName) {

		Sprite firstSprite = new Sprite(spriteName);
		Script startScript = new StartScript(firstSprite);
		Script whenScript = new WhenScript(firstSprite);
		SetLookBrick setLookBrick = new SetLookBrick(firstSprite);

		RobotAlbertMotorActionBrick legoMotorActionBrick = new RobotAlbertMotorActionBrick(firstSprite,
				RobotAlbertMotorActionBrick.Motor.Both, 100);
		ControlCommands commands = new ControlCommands();
		commands.setSpeedOfLeftMotor(100);
		commands.setSpeedOfRightMotor(100);
		byte[] command = commands.getCommandMessage();
		int commandLength = command.length;
		sendCommands.append(command, 0, commandLength);
		//Log.d("TestRobotAlbert", "size=" + commands.getCommandMessage().length + "size=" + len);

		RobotAlbertFrontLedBrick robotAlbertFrontLedBrick = new RobotAlbertFrontLedBrick(firstSprite, new Formula(1));
		commands.setFrontLed(1);
		command = commands.getCommandMessage();
		commandLength = command.length;
		sendCommands.append(command, 0, commandLength);

		RobotAlbertBuzzerBrick robotAlbertBuzzerBrick = new RobotAlbertBuzzerBrick(firstSprite, new Formula(50));
		commands.setBuzzer(50);
		command = commands.getCommandMessage();
		commandLength = command.length;
		sendCommands.append(command, 0, commandLength);

		RobotAlbertRgbLedEyeActionBrick robotAlbertRgbLedEyeActionBrick = new RobotAlbertRgbLedEyeActionBrick(
				firstSprite, RobotAlbertRgbLedEyeActionBrick.Eye.Both, new Formula(255), new Formula(255), new Formula(
						255));
		commands.setLeftEye(255, 255, 255);
		commands.setRightEye(255, 255, 255);
		command = commands.getCommandMessage();
		commandLength = command.length;
		sendCommands.append(command, 0, commandLength);

		whenScript.addBrick(legoMotorActionBrick);
		whenScript.addBrick(robotAlbertFrontLedBrick);
		whenScript.addBrick(robotAlbertBuzzerBrick);
		whenScript.addBrick(robotAlbertRgbLedEyeActionBrick);

		startScript.addBrick(setLookBrick);
		firstSprite.addScript(startScript);
		firstSprite.addScript(whenScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());

		String imageName = "image";
		File image = UiTestUtils.saveFileToProject(projectName, imageName, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), UiTestUtils.FileTypes.IMAGE);

		LookData lookData = new LookData();
		lookData.setLookFilename(image.getName());
		lookData.setLookName(imageName);
		setLookBrick.setLook(lookData);
		firstSprite.getLookDataList().add(lookData);

		StorageHandler.getInstance().saveProject(project);

	}
}
