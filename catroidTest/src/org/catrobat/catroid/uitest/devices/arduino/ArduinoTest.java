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

package org.catrobat.catroid.uitest.devices.arduino;

import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.ConnectBluetoothDeviceActivity;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.bluetooth.BluetoothTestUtils;
import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.common.firmata.FirmataMessage;
import org.catrobat.catroid.common.firmata.FirmataUtils;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;
import java.util.ArrayList;

public class ArduinoTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public ArduinoTest() {
		super(MainMenuActivity.class);
	}

	private static final int PWM_PIN = 3;

	private static final int PWM_PIN_GROUP_1 = 3;
	private static final int PWM_PIN_GROUP_2_MIN = 5;
	private static final int PWM_PIN_GROUP_2_MAX = 6;
	private static final int PWM_PIN_GROUP_3_MIN = 9;
	private static final int PWM_PIN_GROUP_3_MAX = 11;

	private static final int MIN_SENSOR_PIN = 0;
	private static final int MAX_SENSOR_PIN = 5;

	private static final int PWM_MODE = 3;

	private static final int SET_PIN_MODE_COMMAND = 0xF4;
	private static final int REPORT_ANALOG_PIN_COMMAND = 0xC0;

	private static final int IMAGE_FILE_ID = org.catrobat.catroid.test.R.raw.icon;

	private final String projectName = UiTestUtils.PROJECTNAME1;
	private static final String LOCAL_BLUETOOTH_TEST_DUMMY_DEVICE_NAME = "dummy_device";
	private final String spriteName = "testSprite";

	private static final int PIN_LOW = 0;
	private static final int PIN_HIGH = 255;

	private ConnectionDataLogger logger;
	private FirmataUtils firmataUtils;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();

		logger = ConnectionDataLogger.createLocalConnectionLogger();
		firmataUtils = new FirmataUtils(logger);
	}

	@Override
	protected void tearDown() throws Exception {
		logger.disconnectAndDestroy();
		super.tearDown();
	}

	@Device
	public void testArduinoFunctionality() {
		ArrayList<int[]> commands = createTestproject(projectName);

		BluetoothTestUtils.enableBluetooth();

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

		doTestFirmataInitialization();

		solo.clickOnScreen(ScreenValues.SCREEN_WIDTH / 2, ScreenValues.SCREEN_HEIGHT / 2);

		FirmataMessage m;
		for (int[] item : commands) {

			switch (item[0]) {
				case PIN_HIGH:
					m = firmataUtils.getAnalogMesageData();
					assertEquals("Wrong pin", item[1], m.getPin());
					assertEquals("Wrong speed", percentToSpeed(item[2]), m.getData());
					break;
				case PIN_LOW:
					m = firmataUtils.getAnalogMesageData();
					assertEquals("Wrong pin", item[1], m.getPin());
					assertEquals("Wrong speed", percentToSpeed(item[2]), m.getData());
					break;
			}
		}

		solo.goBack();
		solo.goBack();
	}

	private ArrayList<int[]> createTestproject(String projectName) {
		ArrayList<int[]> commands = new ArrayList<int[]>();

		Sprite firstSprite = new Sprite(spriteName);
		Script startScript = new StartScript();
		Script whenScript = new WhenScript();
		SetLookBrick setLookBrick = new SetLookBrick();

		ArduinoSendPWMValueBrick arduinoArduinoSendPWMValueBrick1 = new ArduinoSendPWMValueBrick(
				PWM_PIN, PIN_HIGH);
		commands.add(new int[]{PIN_HIGH, PWM_PIN, PIN_HIGH});
		WaitBrick firstWaitBrick = new WaitBrick(100);

		ArduinoSendPWMValueBrick arduinoArduinoSendPWMValueBrick2 = new ArduinoSendPWMValueBrick(
				PWM_PIN, PIN_LOW);
		commands.add(new int[]{PIN_LOW, PWM_PIN, PIN_LOW});

		whenScript.addBrick(arduinoArduinoSendPWMValueBrick1);
		whenScript.addBrick(firstWaitBrick);
		whenScript.addBrick(arduinoArduinoSendPWMValueBrick2);

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

	private void doTestFirmataInitialization() {
		for (int i = PWM_PIN_GROUP_1; i <= PWM_PIN_GROUP_1; ++i) {
			FirmataMessage m = firmataUtils.getSetPinModeMessage();

			assertEquals("Wrong Command, SET_PIN_MODE command expected", SET_PIN_MODE_COMMAND, m.getCommand());
			assertEquals("Wrong pin used to set pin mode", i, m.getPin());
			assertEquals("Wrong pin mode is used", PWM_MODE, m.getData());
		}

		for (int i = PWM_PIN_GROUP_2_MIN; i <= PWM_PIN_GROUP_2_MAX; ++i) {
			FirmataMessage m = firmataUtils.getSetPinModeMessage();

			assertEquals("Wrong Command, SET_PIN_MODE command expected", SET_PIN_MODE_COMMAND, m.getCommand());
			assertEquals("Wrong pin used to set pin mode", i, m.getPin());
			assertEquals("Wrong pin mode is used", PWM_MODE, m.getData());
		}

		for (int i = PWM_PIN_GROUP_3_MIN; i <= PWM_PIN_GROUP_3_MAX; ++i) {
			FirmataMessage m = firmataUtils.getSetPinModeMessage();

			assertEquals("Wrong Command, SET_PIN_MODE command expected", SET_PIN_MODE_COMMAND, m.getCommand());
			assertEquals("Wrong pin used to set pin mode", i, m.getPin());
			assertEquals(
					"Wrong pin mode is used", PWM_MODE, m.getData());
		}
		testReportAnalogPin(true);
	}

	private void testReportAnalogPin(boolean enable) {
		for (int i = MIN_SENSOR_PIN; i <= MAX_SENSOR_PIN; ++i) {
			FirmataMessage m = firmataUtils.getReportAnalogPinMessage();

			assertEquals("Wrong Command, REPORT_ANALOG_PIN command expected", REPORT_ANALOG_PIN_COMMAND, m.getCommand());
			assertEquals("Wrong pin used to set pin mode", i, m.getPin());
			assertEquals("Wrong pin mode is used", enable ? 1 : 0, m.getData());
		}
	}

	private int percentToSpeed(int percent) {
		if (percent <= 0) {
			return 0;
		}
		if (percent >= 100) {
			return 255;
		}

		return (int) (percent * 2.55);
	}
}
