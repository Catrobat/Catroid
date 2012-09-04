/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.uitest.stage;

import java.io.File;
import java.util.ArrayList;

import android.bluetooth.BluetoothAdapter;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXTBtCommunicator;
import at.tugraz.ist.catroid.LegoNXT.LegoNXTCommunicator;
import at.tugraz.ist.catroid.bluetooth.DeviceListActivity;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.NXTMotorActionBrick;
import at.tugraz.ist.catroid.content.bricks.NXTMotorStopBrick;
import at.tugraz.ist.catroid.content.bricks.NXTMotorTurnAngleBrick;
import at.tugraz.ist.catroid.content.bricks.NXTPlayToneBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class LegoNXTTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private static final int IMAGE_FILE_ID = at.tugraz.ist.catroid.uitest.R.raw.icon;
	private static final int MOTOR_ACTION = 0;
	private static final int MOTOR_STOP = 1;
	private static final int MOTOR_TURN = 2;

	public static final String LEGO_NXT_NAME = "NXT";
	public static final String TEST_SERVER_NAME = "kitty";
	public static final String PAIRED_UNAVAILABLE_DEVICE_NAME = "SWEET";
	public static final String KITTYROID_MAC_ADDRESS = "00:15:83:3F:E3:2C";
	public static final String SOME_OTHER_MAC = "00:0D:F0:48:01:93";
	public static final String PAIRED_UNAVAILABLE_DEVICE_MAC = "00:23:4D:F5:A6:18";

	private Solo solo;
	private StorageHandler storageHandler;
	private final String projectName = UiTestUtils.PROJECTNAME1;

	private File image1;
	private String imageName1 = "image1";

	ArrayList<int[]> commands = new ArrayList<int[]>();

	public LegoNXTTest() {
		super(MainMenuActivity.class);
		storageHandler = StorageHandler.getInstance();
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

		UiTestUtils.clickOnActionBar(solo, R.id.menu_start);

		solo.sleep(2000);

		ListView list = solo.getCurrentListViews().get(0);
		String fullConnectionString = null;
		for (int i = 0; i < solo.getCurrentListViews().get(0).getCount(); i++) {

			String current = (String) list.getItemAtPosition(i);
			if (current.startsWith(TEST_SERVER_NAME)) {
				fullConnectionString = current;
				break;
			}
		}

		solo.clickOnText(fullConnectionString);
		solo.sleep(8000);

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

		solo.clickOnButton(0);
		solo.sleep(1000);
		solo.clickOnText("sprite1");
		solo.sleep(1000);

		ArrayList<String> autoConnectIDs = new ArrayList<String>();
		autoConnectIDs.add(KITTYROID_MAC_ADDRESS);
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
		UiTestUtils.clickOnActionBar(solo, R.id.menu_start);
		solo.sleep(1000);
		solo.assertCurrentActivity("BT connection was not there anymore!!!", StageActivity.class);

		solo.goBack();
		solo.sleep(500);
		solo.goBack();
		solo.sleep(1000);
		solo.goBack();
		solo.sleep(2000);
		//main menu => device disconnected!

		autoConnectIDs = new ArrayList<String>();
		autoConnectIDs.add(PAIRED_UNAVAILABLE_DEVICE_MAC);
		UiTestUtils.setPrivateField("autoConnectIDs", dla, autoConnectIDs, false);

		UiTestUtils.clickOnActionBar(solo, R.id.menu_start);
		solo.sleep(10000); //yes, has to be that long! waiting for auto connection timeout!

		assertTrue("I should be on the bluetooth device choosing screen, but am not!",
				solo.searchText(KITTYROID_MAC_ADDRESS));

		solo.clickOnText(PAIRED_UNAVAILABLE_DEVICE_NAME);
		solo.sleep(8000);
		solo.assertCurrentActivity("I should be in the main menu, but am not!", MainMenuActivity.class);

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

		UiTestUtils.clickOnActionBar(solo, R.id.menu_start);
		solo.sleep(1000);
		solo.assertCurrentActivity("Not in PreStage Activity!", DeviceListActivity.class);
		solo.goBack();
		solo.sleep(1000);
		UiTestUtils.clickOnActionBar(solo, R.id.menu_start);
		solo.sleep(1000);
		solo.assertCurrentActivity("Not in PreStage Activity!", DeviceListActivity.class);

	}

	public void createTestproject(String projectName) {
		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new StartScript(firstSprite);
		Script whenScript = new WhenScript(firstSprite);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);

		NXTMotorActionBrick nxt = new NXTMotorActionBrick(firstSprite, NXTMotorActionBrick.Motor.MOTOR_A_C, 100);
		commands.add(new int[] { MOTOR_ACTION, 0, 100 }); //motor = 3 means brick will move motors A and C.
		commands.add(new int[] { MOTOR_ACTION, 2, 100 });
		WaitBrick wait = new WaitBrick(firstSprite, 500);

		NXTMotorStopBrick nxtStop = new NXTMotorStopBrick(firstSprite, NXTMotorStopBrick.Motor.MOTOR_A_C);
		commands.add(new int[] { MOTOR_STOP, 0 });
		commands.add(new int[] { MOTOR_STOP, 2 });
		WaitBrick wait2 = new WaitBrick(firstSprite, 500);

		NXTMotorTurnAngleBrick nxtTurn = new NXTMotorTurnAngleBrick(firstSprite, NXTMotorTurnAngleBrick.Motor.MOTOR_C,
				515);
		commands.add(new int[] { MOTOR_TURN, 2, 515 });

		WaitBrick wait3 = new WaitBrick(firstSprite, 500);
		NXTPlayToneBrick nxtTone = new NXTPlayToneBrick(firstSprite, 5000, 1000);
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

		image1 = UiTestUtils.saveFileToProject(projectName, imageName1, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), UiTestUtils.FileTypes.IMAGE);
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(image1.getAbsolutePath(), o);

		CostumeData costumeData = new CostumeData();
		costumeData.setCostumeFilename(image1.getName());
		costumeData.setCostumeName("image1");
		setCostumeBrick.setCostume(costumeData);
		firstSprite.getCostumeDataList().add(costumeData);

		storageHandler.saveProject(project);
	}
}
