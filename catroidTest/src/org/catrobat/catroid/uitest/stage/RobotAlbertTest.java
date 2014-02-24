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
import org.catrobat.catroid.R;
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
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.robot.albert.ControlCommands;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BTDummyClient;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class RobotAlbertTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final int IMAGE_FILE_ID = org.catrobat.catroid.test.R.raw.icon;

	// needed for testdevices
	// Bluetooth server is running with a name that starts with 'kitty'
	// e.g. kittyroid-0, kittyslave-0
	private static final String PAIRED_BLUETOOTH_SERVER_DEVICE_NAME = "kittyslave";

	private final String projectName = UiTestUtils.PROJECTNAME1;
	private final String spriteName = "testSprite";

	ByteArrayBuffer sendCommands = new ByteArrayBuffer(1024);
	UserVariablesContainer userVariablesContainer = null;
	private Sprite sprite;

	public RobotAlbertTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
	}

	// This test requires the AlbertTestServer to be running
	@Device
	public void testAlbertFunctionality() {
		Log.d("TestRobotAlbert", "initialized BTDummyClient");

		BTDummyClient dummy = new BTDummyClient();
		dummy.initializeAndConnectToServer(BTDummyClient.SERVERDUMMYROBOTALBERT);

		createTestproject(projectName);

		//enable albert bricks, if disabled at start
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (!preferences.getBoolean("setting_robot_albert_bricks", false)) {
			Log.d("RobotAlbertTest", "enabling albert bricks");
			solo.clickOnMenuItem(solo.getString(R.string.settings));
			solo.clickOnText(solo.getString(R.string.pref_enable_robot_albert_bricks));
			solo.goBack();
		}

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.sleep(500);
		solo.clickOnText(spriteName);
		solo.sleep(500);
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.sleep(1000);

		solo.clickOnText("0.0");
		solo.sleep(1000);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_sensors));
		solo.sleep(1000);
		solo.waitForText(getActivity().getString(R.string.formula_editor_sensor_albert_robot_distance_left));
		solo.clickOnText(getActivity().getString(R.string.formula_editor_sensor_albert_robot_distance_left));
		solo.sleep(1000);
		solo.clickOnView(solo.getView(R.id.formula_editor_keyboard_ok));

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		assertTrue("Bluetooth not supported on device", bluetoothAdapter != null);
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
			solo.sleep(5000);
		}

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

		solo.sleep(5000);
		solo.sleep(3000);
		solo.assertCurrentActivity("Not in stage - connection to bluetooth-device failed", StageActivity.class);

		solo.clickOnScreen(ScreenValues.SCREEN_WIDTH / 2, ScreenValues.SCREEN_HEIGHT / 2);
		solo.sleep(5000);

		ByteArrayBuffer receivedBufferOld = dummy.getReceivedFeedback();
		ByteArrayBuffer receivedBuffer = removeSensorCommands(receivedBufferOld);

		boolean ok = Arrays.equals(sendCommands.toByteArray(), receivedBuffer.toByteArray());

		Log.d("TestRobotAlbert_New", receivedBuffer.toByteArray().toString());
		Log.d("TestRobotAlbert", "Array comparision successful: " + ok);

		double distanceLeft = userVariablesContainer.getUserVariable("p1", sprite).getValue();
		Log.d("RobotAlbertTest", "left=" + distanceLeft);
		//BluetoothServer always sends a distance of 50
		assertEquals("Variable has the wrong value after stage", 50.0, distanceLeft);

		int lenRec = receivedBuffer.length();
		int lenSent1 = sendCommands.length();

		Log.d("TestRobotAlbert",
				"lenRec=" + lenRec + "\nlenSent1=" + lenSent1 + "\nlenWithSensor=" + receivedBufferOld.length());
		assertTrue("messages reveived and sent are not equal", ok == true);
		Log.d("temp", receivedBuffer.toString());

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
	}

	private void createTestproject(String projectName) {

		Sprite firstSprite = new Sprite(spriteName);
		Script startScript = new StartScript(firstSprite);
		Script whenScript = new WhenScript(firstSprite);
		SetLookBrick setLookBrick = new SetLookBrick(firstSprite);
		sprite = firstSprite;

		RobotAlbertMotorActionBrick legoMotorActionBrick = new RobotAlbertMotorActionBrick(firstSprite,
				RobotAlbertMotorActionBrick.Motor.Both, 100);
		ControlCommands commands = new ControlCommands();
		commands.setSpeedOfLeftMotor(100);
		commands.setSpeedOfRightMotor(100);
		byte[] command = commands.getCommandMessage();
		int commandLength = command.length;
		sendCommands.append(command, 0, commandLength);

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

		SetVariableBrick setVariableBrick = new SetVariableBrick(firstSprite, 0.0);

		whenScript.addBrick(legoMotorActionBrick);
		whenScript.addBrick(robotAlbertFrontLedBrick);
		whenScript.addBrick(robotAlbertBuzzerBrick);
		whenScript.addBrick(robotAlbertRgbLedEyeActionBrick);
		whenScript.addBrick(setVariableBrick);

		startScript.addBrick(setLookBrick);
		firstSprite.addScript(startScript);
		firstSprite.addScript(whenScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());
		userVariablesContainer = project.getUserVariables();
		userVariablesContainer.addProjectUserVariable("p1");
		userVariablesContainer.addSpriteUserVariable("sprite_var1");

		setVariableBrick = new SetVariableBrick(firstSprite, 0.0);

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

	private ByteArrayBuffer removeSensorCommands(ByteArrayBuffer buffer) {
		int i;
		int length = buffer.length();

		ByteArrayBuffer array = new ByteArrayBuffer(0);

		for (i = 0; i < length; i++) {
			boolean found = false;

			if (i < length - 51) {
				if ((buffer.toByteArray()[i] == (byte) 0xAA) && (buffer.toByteArray()[i + 1] == (byte) 0x55)
						&& (buffer.toByteArray()[i + 2] == (byte) 52)) {
					if ((buffer.toByteArray()[i + 50] == (byte) 0x0D) && (buffer.toByteArray()[i + 51] == (byte) 0x0A)) {
						i = i + 51;
						found = true;
					}
				}
			}

			if (found == false) {
				array.append(buffer.toByteArray()[i]);
			}
		}
		return array;
	}
}
