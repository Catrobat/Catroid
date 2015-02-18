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
package org.catrobat.catroid.uitest.stage;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.DeviceListActivity;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.LegoNxtMotorActionBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.legonxt.LegoNXTBtCommunicator;
import org.catrobat.catroid.legonxt.LegoNXTCommunicator;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class LegoNXTTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final int IMAGE_FILE_ID = org.catrobat.catroid.test.R.raw.icon;
	private static final int MOTOR_ACTION = 0;
	private static final int MOTOR_STOP = 1;
	private static final int MOTOR_TURN = 2;

	// needed for testdevices
	// Bluetooth server is running with a name that starts with 'kitty'
	// e.g. kittyroid-0, kittyslave-0
	private static final String PAIRED_BLUETOOTH_SERVER_DEVICE_NAME = "kitty";

	// needed for testdevices
	// unavailable device is paired with a name that starts with 'SWEET'
	// e.g. SWEETHEART

	private static final String PAIRED_UNAVAILABLE_DEVICE_NAME = "SWEET";
	private static final String PAIRED_UNAVAILABLE_DEVICE_MAC = "00:23:4D:F5:A6:18";

	private final String projectName = UiTestUtils.PROJECTNAME1;
	private final String spriteName = "testSprite";

	ArrayList<int[]> commands = new ArrayList<int[]>();

	public LegoNXTTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
	}

	// This test requires the NXTBTTestServer to be running or a LegoNXT Robot to run! Check connect string to see if you connect to the right device!
	@Device
	public void testNXTFunctionality() {
		createTestproject(projectName);

		LegoNXTBtCommunicator.enableRequestConfirmFromDevice(true);
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

		solo.clickOnText(connectedDeviceName);
		solo.sleep(8000);
		solo.assertCurrentActivity("Not in stage - connection to bluetooth-device failed", StageActivity.class);

		solo.clickOnScreen(ScreenValues.SCREEN_WIDTH / 2, ScreenValues.SCREEN_HEIGHT / 2);
		solo.sleep(10000);

		ArrayList<byte[]> executedCommands = LegoNXTCommunicator.getReceivedMessageList();
		assertEquals("Commands seem to have not been executed! Connected to correct device??", commands.size(),
				executedCommands.size());

		int i = 0;
		for (int[] item : commands) {
			switch (item[0]) {
				case MOTOR_ACTION:
					assertEquals("Wrong motor was used!", item[1], executedCommands.get(i)[3]);
					assertEquals("Wrong speed was used!", item[2], executedCommands.get(i)[4]);
					break;
				case MOTOR_STOP:
					assertEquals("Wrong motor was used!", item[1], executedCommands.get(i)[3]);
					assertEquals("Motor didnt actually stop!", 0, executedCommands.get(i)[4]);
					break;
				case MOTOR_TURN:
					assertEquals("Wrong motor was used!", item[1], executedCommands.get(i)[3]);
					int turnValue = 0;
					turnValue = (0x000000FF & executedCommands.get(i)[9]); //unsigned types would be too smart for java, sorry no chance mate!
					turnValue += ((0x000000FF & executedCommands.get(i)[10]) << 8);
					turnValue += ((0x000000FF & executedCommands.get(i)[11]) << 16);
					turnValue += ((0x000000FF & executedCommands.get(i)[12]) << 24);

					int turnSpeed = 30; //fixed value in Brick, however LegoBot needs negative speed instead of negative angles 
					if (item[2] < 0) {
						item[2] += -2 * item[2];
						turnSpeed -= 2 * turnSpeed;
					}

					assertEquals("Motor turned wrong angle", item[2], turnValue);
					assertEquals("Motor didnt turn with fixed value 30!", turnSpeed, executedCommands.get(i)[4]);
					break;
			}
			i++;
		}
		LegoNXTBtCommunicator.enableRequestConfirmFromDevice(false);
	}

	// This test requires the NXTBTTestServer to be running or a LegoNXT Robot to run! Check connect string to see if you connect to the right device!
	@Device
	public void testNXTPersistentConnection() {
		createTestproject(projectName);

		LegoNXTBtCommunicator.enableRequestConfirmFromDevice(false);
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		assertTrue("Bluetooth not supported on device", bluetoothAdapter != null);
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
			solo.sleep(5000);
		}
		Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
		Iterator<BluetoothDevice> iterator = bondedDevices.iterator();
		String connectedDeviceMacAdress = null;
		while (iterator.hasNext()) {
			BluetoothDevice device = iterator.next();
			if (device.getName().startsWith(PAIRED_BLUETOOTH_SERVER_DEVICE_NAME)) {
				connectedDeviceMacAdress = device.getAddress();
			}
		}

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.clickOnText(spriteName);
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		ArrayList<String> autoConnectIDs = new ArrayList<String>();
		autoConnectIDs.add(connectedDeviceMacAdress);
		DeviceListActivity deviceListActivity = new DeviceListActivity();
		Reflection.setPrivateField(deviceListActivity, "autoConnectIDs", autoConnectIDs);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(6500);// increase this sleep if probs!

		solo.goBack();
		solo.sleep(500);
		solo.goBack();
		solo.sleep(1000);
		solo.goBack();
		solo.sleep(1000);
		//Device is still connected (until visiting main menu or exiting program)!
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(1000);
		solo.assertCurrentActivity("BT connection was not there anymore!!!", StageActivity.class);

		solo.goBack();
		solo.sleep(500);
		solo.goBack();
		solo.sleep(1000);
		solo.goBack();
		solo.sleep(1000);
		solo.goBack();
		solo.sleep(2000);
		//main menu => device disconnected!

		autoConnectIDs = new ArrayList<String>();
		autoConnectIDs.add(PAIRED_UNAVAILABLE_DEVICE_MAC);
		Reflection.setPrivateField(deviceListActivity, "autoConnectIDs", autoConnectIDs);

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.sleep(10000); //yes, has to be that long! waiting for auto connection timeout!

		assertTrue("I should be on the bluetooth device choosing screen, but am not!",
				solo.searchText(connectedDeviceMacAdress));

		solo.clickOnText(PAIRED_UNAVAILABLE_DEVICE_NAME);
		solo.waitForText(solo.getString(R.string.brick_when_started), 1, 20000);
		solo.assertCurrentActivity("Incorrect Activity reached!", ProjectActivity.class);
	}

	@Device
	public void testNXTConnectionDialogGoBack() {
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
		Script startScript = new StartScript();
		Script whenScript = new WhenScript();
		SetLookBrick setLookBrick = new SetLookBrick();

		LegoNxtMotorActionBrick legoMotorActionBrick = new LegoNxtMotorActionBrick(
				LegoNxtMotorActionBrick.Motor.MOTOR_A_C, 100);
		commands.add(new int[] { MOTOR_ACTION, 0, 100 }); //motor = 3 means brick will move motors A and C.
		commands.add(new int[] { MOTOR_ACTION, 2, 100 });
		WaitBrick firstWaitBrick = new WaitBrick(500);

		LegoNxtMotorStopBrick legoMotorStopBrick = new LegoNxtMotorStopBrick(
				LegoNxtMotorStopBrick.Motor.MOTOR_A_C);
		commands.add(new int[] { MOTOR_STOP, 0 });
		commands.add(new int[] { MOTOR_STOP, 2 });
		WaitBrick secondWaitBrick = new WaitBrick(500);

		LegoNxtMotorTurnAngleBrick legoMotorTurnAngleBrick = new LegoNxtMotorTurnAngleBrick(
				LegoNxtMotorTurnAngleBrick.Motor.MOTOR_C, 515);
		commands.add(new int[] { MOTOR_TURN, 2, 515 });

		WaitBrick thirdWaitBrick = new WaitBrick(500);
		LegoNxtPlayToneBrick legoPlayToneBrick = new LegoNxtPlayToneBrick(5000, 1000);
		//Tone does not return a command

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
	}
}
