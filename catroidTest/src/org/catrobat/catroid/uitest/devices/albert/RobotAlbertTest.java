/*
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.uitest.devices.albert;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;

import org.apache.http.util.ByteArrayBuffer;
import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.ConnectBluetoothDeviceActivity;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.common.bluetooth.BluetoothTestUtils;
import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.common.bluetooth.models.AlbertModel;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.RobotAlbertBodyLedBrick;
import org.catrobat.catroid.content.bricks.RobotAlbertBuzzerBrick;
import org.catrobat.catroid.content.bricks.RobotAlbertFrontLedBrick;
import org.catrobat.catroid.content.bricks.RobotAlbertMotorBrick;
import org.catrobat.catroid.content.bricks.RobotAlbertRgbLedEyeBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.devices.albert.Albert;
import org.catrobat.catroid.devices.albert.AlbertSendCommands;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;
import java.util.ArrayList;

public class RobotAlbertTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private final String projectName = UiTestUtils.PROJECTNAME1;
	private final String spriteName = "testSprite";
	private static final int IMAGE_FILE_ID = org.catrobat.catroid.test.R.raw.icon;
	private String DUMMY_DEVICE_NAME = "AlbertDummy";
	private String REAL_DEVICE_NAME = "Albert";
	private boolean useRealRobot = false;

	ByteArrayBuffer sendCommands = new ByteArrayBuffer(1024);
	DataContainer userVariablesContainer = null;
	private BluetoothDeviceService btService;
	ConnectionDataLogger logger;
	Albert albert;
	AlbertModel model;

	public RobotAlbertTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		this.btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);
		this.albert = this.btService.getDevice(BluetoothDevice.ALBERT);
		this.logger = ConnectionDataLogger.createBluetoothConnectionLogger();
		this.model = new AlbertModel();
	}

	// This test requires the AlbertTestServer to be running
	public void testAlbertFunctionality() {
		Log.d("TestRobotAlbert", "initialized BTDummyClient");

		createTestproject(projectName);

		//enable albert bricks, if disabled at start
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (!preferences.getBoolean("setting_robot_albert_bricks", false)) {
			Log.d("RobotAlbertTest", "enabling albert bricks");
			solo.clickOnMenuItem(solo.getString(R.string.settings));
			solo.clickOnText(solo.getString(R.string.preference_title_enable_robot_albert_bricks));
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

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(2500);

		solo.waitForActivity(ConnectBluetoothDeviceActivity.class);

		BluetoothTestUtils.addPairedDevice(DUMMY_DEVICE_NAME, (ConnectBluetoothDeviceActivity) solo.getCurrentActivity(),
				getInstrumentation());
		ListView deviceList = solo.getCurrentViews(ListView.class).get(0);
		String connectedDeviceName = null;
		String device;
		if (useRealRobot) {
			device = REAL_DEVICE_NAME;
		} else {
			device = DUMMY_DEVICE_NAME;
		}
		for (int i = 0; i < deviceList.getCount(); i++) {
			String deviceName = (String) deviceList.getItemAtPosition(i);
			if (deviceName.startsWith(device)) {
				connectedDeviceName = deviceName;
				break;
			}
		}
		solo.clickOnText(connectedDeviceName);

		solo.sleep(6000);
		if (useRealRobot) {
			solo.assertCurrentActivity("Not in stage - connection to bluetooth-device failed", StageActivity.class);
		}
		solo.clickOnScreen(ScreenValues.SCREEN_WIDTH / 2, ScreenValues.SCREEN_HEIGHT / 2);
		solo.sleep(5000);

		solo.sleep(500);
		solo.goBack();
		solo.sleep(100);
		solo.goBack();
		solo.sleep(100);
		solo.goBack();
		solo.sleep(100);
		solo.goBack();
	}

	private void createTestproject(String projectName) {

		Sprite firstSprite = new Sprite(spriteName);
		Script startScript = new StartScript();
		Script whenScript = new WhenScript();
		SetLookBrick setLookBrick = new SetLookBrick();

		RobotAlbertMotorBrick legoMotorActionBrick = new RobotAlbertMotorBrick(
				RobotAlbertMotorBrick.Motor.Both, 100);
		AlbertSendCommands commands = new AlbertSendCommands();
		commands.setSpeedOfLeftMotor(100);
		commands.setSpeedOfRightMotor(100);
		byte[] command = commands.getCommandMessage();
		int commandLength = command.length;
		sendCommands.append(command, 0, commandLength);

		RobotAlbertFrontLedBrick robotAlbertFrontLedBrick = new RobotAlbertFrontLedBrick(1);
		commands.setFrontLed(1);
		command = commands.getCommandMessage();
		commandLength = command.length;
		sendCommands.append(command, 0, commandLength);

		RobotAlbertBuzzerBrick robotAlbertBuzzerBrick = new RobotAlbertBuzzerBrick(50);
		commands.setBuzzer(50);
		command = commands.getCommandMessage();
		commandLength = command.length;
		sendCommands.append(command, 0, commandLength);

		RobotAlbertRgbLedEyeBrick robotAlbertRgbLedEyeActionBrick = new RobotAlbertRgbLedEyeBrick(
				RobotAlbertRgbLedEyeBrick.Eye.Both, new Formula(255), new Formula(255), new Formula(
				255));
		commands.setLeftEye(255, 255, 255);
		commands.setRightEye(255, 255, 255);
		command = commands.getCommandMessage();
		commandLength = command.length;
		sendCommands.append(command, 0, commandLength);

		RobotAlbertBodyLedBrick robotAlbertBodyLedBrick = new RobotAlbertBodyLedBrick(255);
		commands.setBodyLed(255);
		command = commands.getCommandMessage();
		commandLength = command.length;
		sendCommands.append(command, 0, commandLength);

		SetVariableBrick setVariableBrick = new SetVariableBrick(0.0);

		whenScript.addBrick(legoMotorActionBrick);
		whenScript.addBrick(robotAlbertFrontLedBrick);
		whenScript.addBrick(robotAlbertBuzzerBrick);
		whenScript.addBrick(robotAlbertRgbLedEyeActionBrick);
		whenScript.addBrick(setVariableBrick);
		whenScript.addBrick(robotAlbertBodyLedBrick);

		startScript.addBrick(setLookBrick);
		firstSprite.addScript(startScript);
		firstSprite.addScript(whenScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());
		userVariablesContainer = project.getDataContainer();
		userVariablesContainer.addProjectUserVariable("p1");
		userVariablesContainer.addSpriteUserVariable("sprite_var1");

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
