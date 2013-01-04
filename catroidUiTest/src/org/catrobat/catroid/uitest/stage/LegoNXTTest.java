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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.catrobat.catroid.R;
import org.catrobat.catroid.LegoNXT.LegoNXTBtCommunicator;
import org.catrobat.catroid.LegoNXT.LegoNXTCommunicator;
import org.catrobat.catroid.bluetooth.DeviceListActivity;
import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.common.Values;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.LegoNxtMotorActionBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorStopBrick;
import org.catrobat.catroid.content.bricks.LegoNxtMotorTurnAngleBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.SetCostumeBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptTabActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class LegoNXTTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private static final int IMAGE_FILE_ID = org.catrobat.catroid.uitest.R.raw.icon;
	private static final int MOTOR_ACTION = 0;
	private static final int MOTOR_STOP = 1;
	private static final int MOTOR_TURN = 2;

	// needed for testdevices
	// Bluetooth server is running with a name that starts with 'kitty'
	// e.g. kittyroid-0, kittyslave-0
	private static final String PAIRED_BlUETOOTH_SERVER_DEVICE_NAME = "kitty";

	// needed for testdevices
	// unavailable device is paired with a name that starts with 'SWEET'
	// e.g. SWEETHEART
	private static final String PAIRED_UNAVAILABLE_DEVICE_NAME = "SWEET";
	private static final String PAIRED_UNAVAILABLE_DEVICE_MAC = "00:23:4D:F5:A6:18";

	private Solo solo;
	private final String projectName = UiTestUtils.PROJECTNAME1;

	ArrayList<int[]> commands = new ArrayList<int[]>();

	public LegoNXTTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		UiTestUtils.clearAllUtilTestProjects();
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	// This test requires the NXTBTTestServer to be running or a LegoNXT Robot to run! Check connect string to see if you connect to the right device!
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
		DeviceListActivity dla = new DeviceListActivity();
		UiTestUtils.setPrivateField("autoConnectIDs", dla, autoConnectIDs, false);

		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.btn_play);

		solo.sleep(2000);

		ListView list = solo.getCurrentListViews().get(0);
		String fullConnectionString = null;
		for (int i = 0; i < solo.getCurrentListViews().get(0).getCount(); i++) {

			String current = (String) list.getItemAtPosition(i);
			if (current.startsWith(PAIRED_BlUETOOTH_SERVER_DEVICE_NAME)) {
				fullConnectionString = current;
				break;
			}
		}

		solo.clickOnText(fullConnectionString);
		solo.sleep(8000);
		solo.assertCurrentActivity("Not in stage - connection to bt-device failed", StageActivity.class);

		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		solo.sleep(10000);

		Log.i("bt", "" + LegoNXTCommunicator.getReceivedMessageList().size());
		solo.sleep(2000);

		ArrayList<byte[]> executed_commands = LegoNXTCommunicator.getReceivedMessageList();
		assertEquals("Commands seem to have not been executed! Connected to correct device??", commands.size(),
				executed_commands.size());

		int i = 0;
		for (int[] item : commands) {

			switch (item[0]) {
				case MOTOR_ACTION:
					assertEquals("Wrong motor was used!", item[1], executed_commands.get(i)[3]);
					assertEquals("Wrong speed was used!", item[2], executed_commands.get(i)[4]);
					break;
				case MOTOR_STOP:
					assertEquals("Wrong motor was used!", item[1], executed_commands.get(i)[3]);
					assertEquals("Motor didnt actually stop!", 0, executed_commands.get(i)[4]);
					break;
				case MOTOR_TURN:
					for (int j = 0; j < executed_commands.get(i).length; j++) {
						Log.i("bt", "i" + j + ": " + (int) executed_commands.get(i)[j]);
					}
					assertEquals("Wrong motor was used!", item[1], executed_commands.get(i)[3]);
					int turnValue = 0;
					turnValue = (0x000000FF & executed_commands.get(i)[9]); //unsigned types would be too smart for java, sorry no chance mate!
					turnValue += ((0x000000FF & executed_commands.get(i)[10]) << 8);
					turnValue += ((0x000000FF & executed_commands.get(i)[11]) << 16);
					turnValue += ((0x000000FF & executed_commands.get(i)[12]) << 24);

					int turnSpeed = 30; //fixed value in Brick, however LegoBot needs negative speed instead of negative angles 
					if (item[2] < 0) {
						item[2] += -2 * item[2];
						turnSpeed -= 2 * turnSpeed;
					}

					assertEquals("Motor turned wrong angle", item[2], turnValue);
					assertEquals("Motor didnt turn with fixed value 30!", turnSpeed, executed_commands.get(i)[4]);
					break;

			}
			i++;
		}
		LegoNXTBtCommunicator.enableRequestConfirmFromDevice(false);
	}

	// This test requires the NXTBTTestServer to be running or a LegoNXT Robot to run! Check connect string to see if you connect to the right device!
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
		String deviceMacAdress = null;
		while (iterator.hasNext()) {
			BluetoothDevice device = iterator.next();
			if (device.getName().startsWith("kitty")) {
				deviceMacAdress = device.getAddress();
			}
		}

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.clickOnText("sprite1");
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		ArrayList<String> autoConnectIDs = new ArrayList<String>();
		autoConnectIDs.add(deviceMacAdress);
		DeviceListActivity dla = new DeviceListActivity();
		UiTestUtils.setPrivateField("autoConnectIDs", dla, autoConnectIDs, false);

		UiTestUtils.clickOnActionBar(solo, R.id.menu_start);
		solo.sleep(6500);// increase this sleep if probs!

		solo.goBack();
		solo.sleep(500);
		solo.goBack();
		solo.sleep(1000);
		solo.goBack();
		solo.sleep(1000);
		//Device is still connected (until visiting main menu or exiting program)!
		UiTestUtils.clickOnBottomBar(solo, R.id.btn_play);
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
		UiTestUtils.setPrivateField("autoConnectIDs", dla, autoConnectIDs, false);

		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.btn_play);
		solo.sleep(10000); //yes, has to be that long! waiting for auto connection timeout!

		assertTrue("I should be on the bluetooth device choosing screen, but am not!", solo.searchText(deviceMacAdress));

		solo.clickOnText(PAIRED_UNAVAILABLE_DEVICE_NAME);
		solo.sleep(8000);
		solo.assertCurrentActivity("Incorrect Activity reached!", ProjectActivity.class);

	}

	public void testNXTConnectionDialogGoBack() {
		createTestproject(projectName);

		ArrayList<String> autoConnectIDs = new ArrayList<String>();
		autoConnectIDs.add("IM_NOT_A_MAC_ADDRESS");
		DeviceListActivity dla = new DeviceListActivity();
		UiTestUtils.setPrivateField("autoConnectIDs", dla, autoConnectIDs, false);

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		assertTrue("Bluetooth not supported on device", bluetoothAdapter != null);
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
			solo.sleep(5000);
		}

		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.btn_play);
		solo.sleep(1000);
		solo.assertCurrentActivity("Not in PreStage Activity!", DeviceListActivity.class);
		solo.goBack();
		solo.sleep(1000);
		UiTestUtils.clickOnBottomBar(solo, R.id.btn_play);
		solo.sleep(1000);
		solo.assertCurrentActivity("Not in PreStage Activity!", DeviceListActivity.class);

	}

	public void createTestproject(String projectName) {
		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new StartScript(firstSprite);
		Script whenScript = new WhenScript(firstSprite);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);

		LegoNxtMotorActionBrick nxt = new LegoNxtMotorActionBrick(firstSprite, LegoNxtMotorActionBrick.Motor.MOTOR_A_C,
				100);
		commands.add(new int[] { MOTOR_ACTION, 0, 100 }); //motor = 3 means brick will move motors A and C.
		commands.add(new int[] { MOTOR_ACTION, 2, 100 });
		WaitBrick wait = new WaitBrick(firstSprite, 500);

		LegoNxtMotorStopBrick nxtStop = new LegoNxtMotorStopBrick(firstSprite, LegoNxtMotorStopBrick.Motor.MOTOR_A_C);
		commands.add(new int[] { MOTOR_STOP, 0 });
		commands.add(new int[] { MOTOR_STOP, 2 });
		WaitBrick wait2 = new WaitBrick(firstSprite, 500);

		LegoNxtMotorTurnAngleBrick nxtTurn = new LegoNxtMotorTurnAngleBrick(firstSprite,
				LegoNxtMotorTurnAngleBrick.Motor.MOTOR_C, 515);
		commands.add(new int[] { MOTOR_TURN, 2, 515 });

		WaitBrick wait3 = new WaitBrick(firstSprite, 500);
		LegoNxtPlayToneBrick nxtTone = new LegoNxtPlayToneBrick(firstSprite, 5000, 1000);
		//Tone does not return a command

		whenScript.addBrick(nxt);
		whenScript.addBrick(wait);
		whenScript.addBrick(nxtStop);
		whenScript.addBrick(wait2);
		whenScript.addBrick(nxtTurn);
		whenScript.addBrick(wait3);
		whenScript.addBrick(nxtTone);

		startScript.addBrick(setCostumeBrick);
		firstSprite.addScript(startScript);
		firstSprite.addScript(whenScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());

		String imageName = "image";
		File image = UiTestUtils.saveFileToProject(projectName, imageName, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), UiTestUtils.FileTypes.IMAGE);
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(image.getAbsolutePath(), bitmapOptions);

		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(image.getName());
		costumeData.setCostumeName(imageName);
		setCostumeBrick.setCostume(costumeData);
		firstSprite.getCostumeDataList().add(costumeData);

		StorageHandler.getInstance().saveProject(project);
	}
}
