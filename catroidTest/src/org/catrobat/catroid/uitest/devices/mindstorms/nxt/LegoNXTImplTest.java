/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.devices.mindstorms.nxt;

import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.ConnectBluetoothDeviceActivity;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.bluetooth.BluetoothTestUtils;
import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;
import java.util.ArrayList;

public class LegoNXTImplTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final int IMAGE_FILE_ID = org.catrobat.catroid.test.R.raw.icon;
	private static final int MOTOR_ACTION = 0;
	private static final int MOTOR_STOP = 1;
	private static final int MOTOR_TURN = 2;
	private static final int PLAY_TONE = 3;

	private static final String LOCAL_BLUETOOTH_TEST_DUMMY_DEVICE_NAME = "dummy_device";

	private final String projectName = UiTestUtils.PROJECTNAME1;
	private final String spriteName = "testSprite";

	public LegoNXTImplTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		disableSensors();
		SettingsActivity.disableLegoMindstormsSensorInfoDialog(
				this.getInstrumentation().getTargetContext().getApplicationContext());
	}

	private void disableSensors() {
		SettingsActivity.setLegoMindstormsNXTSensorMapping(this.getInstrumentation().getTargetContext(),
				new NXTSensor.Sensor[] { NXTSensor.Sensor.NO_SENSOR, NXTSensor.Sensor.NO_SENSOR, NXTSensor.Sensor.NO_SENSOR, NXTSensor.Sensor.NO_SENSOR });
	}

	@Device
	public void testNXTFunctionality() {
		ArrayList<int[]> commands = createTestproject(projectName);

		BluetoothTestUtils.enableBluetooth();

		ConnectionDataLogger logger = ConnectionDataLogger.createLocalConnectionLogger();

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(1000);

		solo.assertCurrentActivity("Not in BTConnectDeviceActivity", ConnectBluetoothDeviceActivity.class);

		// use this only, if ConnectionDataLogger is in local mode (localProxy)
		BluetoothTestUtils.addPairedDevice(LOCAL_BLUETOOTH_TEST_DUMMY_DEVICE_NAME,
				(ConnectBluetoothDeviceActivity) solo.getCurrentActivity(), getInstrumentation());

		ListView deviceList = solo.getCurrentViews(ListView.class).get(0);
		String connectedDeviceName = null;
		for (int i = 0; i < deviceList.getCount(); i++) {
			String deviceName = (String) deviceList.getItemAtPosition(i);
			if (deviceName.startsWith(LOCAL_BLUETOOTH_TEST_DUMMY_DEVICE_NAME)) {
				connectedDeviceName = deviceName;
				break;
			}
		}

		solo.clickOnText(connectedDeviceName);
		solo.sleep(2000);
		solo.assertCurrentActivity("Not in stage - connection to bluetooth-device failed", StageActivity.class);

		solo.clickOnScreen(ScreenValues.SCREEN_WIDTH / 2, ScreenValues.SCREEN_HEIGHT / 2);

		ArrayList<byte[]> executedCommands = logger.getSentMessages(2, commands.size());
		assertEquals("Commands seem to have not been executed! Connected to correct device??", commands.size(),
				executedCommands.size());

		solo.goBack();
		solo.goBack();

		int i = 0;
		for (int[] item : commands) {
			switch (item[0]) {
				case MOTOR_ACTION:
					assertEquals("Wrong motor was used!", item[1], executedCommands.get(i)[2]);
					assertEquals("Wrong speed was used!", item[2], executedCommands.get(i)[3]);
					break;
				case MOTOR_STOP:
					assertEquals("Wrong motor was used!", item[1], executedCommands.get(i)[2]);
					assertEquals("Motor didn't actually stop!", 0, executedCommands.get(i)[3]);
					break;
				case MOTOR_TURN:
					assertEquals("Wrong motor was used!", item[1], executedCommands.get(i)[2]);
					int turnValue = (0x000000FF & executedCommands.get(i)[8]); //unsigned types would be too smart for java, sorry no chance mate!
					turnValue += ((0x000000FF & executedCommands.get(i)[9]) << 8);
					turnValue += ((0x000000FF & executedCommands.get(i)[10]) << 16);
					turnValue += ((0x000000FF & executedCommands.get(i)[11]) << 24);

					int turnSpeed = 30; //fixed value in Brick, however LegoBot needs negative speed instead of negative angles
					if (item[2] < 0) {
						item[2] += -2 * item[2];
						turnSpeed -= 2 * turnSpeed;
					}

					assertEquals("Motor turned wrong angle", item[2], turnValue);
					assertEquals("Motor didn't turn with fixed value 30!", turnSpeed, executedCommands.get(i)[3]);
					break;
				case PLAY_TONE:
					int frequency = (0x000000FF & executedCommands.get(i)[2]);
					frequency += ((0x000000FF & executedCommands.get(i)[3]) << 8);
					assertEquals("wrong frequency used", item[1], frequency);

					int duration = (0x000000FF & executedCommands.get(i)[4]);
					duration += ((0x000000FF & executedCommands.get(i)[5]) << 8);
					assertEquals("wrong duration used", item[2], duration);
			}
			i++;
		}

		logger.disconnectAndDestroy();
	}

	@Device
	public void testNXTConnectionDialogGoBack() {
		createTestproject(projectName);

		BluetoothTestUtils.enableBluetooth();

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(1000);
		solo.assertCurrentActivity("Devicelist not shown!", ConnectBluetoothDeviceActivity.class);
		solo.goBack();
		solo.sleep(1000);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(1000);
		solo.assertCurrentActivity("Devicelist not shown!", ConnectBluetoothDeviceActivity.class);
	}

	private ArrayList<int[]> createTestproject(String projectName) {
		ArrayList<int[]> commands = new ArrayList<int[]>();

		Sprite firstSprite = new Sprite(spriteName);
		Script startScript = new StartScript();
		Script whenScript = new WhenScript();
		SetLookBrick setLookBrick = new SetLookBrick();

		LegoNxtMotorMoveBrick legoMotorActionBrick = new LegoNxtMotorMoveBrick(
				LegoNxtMotorMoveBrick.Motor.MOTOR_B_C, 100);
		commands.add(new int[] { MOTOR_ACTION, 1, 100 });
		commands.add(new int[] { MOTOR_ACTION, 2, 100 });
		WaitBrick firstWaitBrick = new WaitBrick(500);

		LegoNxtMotorStopBrick legoMotorStopBrick = new LegoNxtMotorStopBrick(
				LegoNxtMotorStopBrick.Motor.MOTOR_B_C);
		commands.add(new int[] { MOTOR_STOP, 1 });
		commands.add(new int[] { MOTOR_STOP, 2 });
		WaitBrick secondWaitBrick = new WaitBrick(500);

		LegoNxtMotorTurnAngleBrick legoMotorTurnAngleBrick = new LegoNxtMotorTurnAngleBrick(
				LegoNxtMotorTurnAngleBrick.Motor.MOTOR_C, 515);
		commands.add(new int[] { MOTOR_TURN, 2, 515 });

		WaitBrick thirdWaitBrick = new WaitBrick(500);
		LegoNxtPlayToneBrick legoPlayToneBrick = new LegoNxtPlayToneBrick(50, 1.5f);
		//Tone does not return a command
		commands.add(new int[] { PLAY_TONE, 5000, 1500 });

		whenScript.addBrick(legoMotorActionBrick);
		whenScript.addBrick(firstWaitBrick);
		whenScript.addBrick(legoMotorStopBrick);
		whenScript.addBrick(secondWaitBrick);
		whenScript.addBrick(legoMotorTurnAngleBrick);
		whenScript.addBrick(thirdWaitBrick);
		whenScript.addBrick(legoPlayToneBrick);

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

		return commands;
	}
}
