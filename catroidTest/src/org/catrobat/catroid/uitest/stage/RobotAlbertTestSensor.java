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

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BTDummyClient;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;
import java.util.ArrayList;

public class RobotAlbertTestSensor extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final int IMAGE_FILE_ID = org.catrobat.catroid.test.R.raw.icon;

	// needed for testdevices
	// Bluetooth server is running with a name that starts with 'kitty'
	// e.g. kittyroid-0, kittyslave-0
	private static final String PAIRED_BLUETOOTH_SERVER_DEVICE_NAME = "kitty";

	private final String projectName = UiTestUtils.PROJECTNAME1;
	private final String spriteName = "testSprite";

	UserVariablesContainer userVariablesContainer = null;
	private Sprite sprite;

	public RobotAlbertTestSensor() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
	}

	// This test requires the AlbertTestServer to be running
	@Device
	public void testAlbertSensorFunctionality() {
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

		//disable albert bricks, bricks should work regardless 
		if (preferences.getBoolean("setting_robot_albert_bricks", false)) {
			solo.clickOnMenuItem(solo.getString(R.string.settings));
			solo.clickOnText(solo.getString(R.string.pref_enable_robot_albert_bricks));
			solo.goBack();
		}

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
		solo.sleep(2000);
		solo.assertCurrentActivity("Not in stage - connection to bluetooth-device failed", StageActivity.class);

		solo.clickOnScreen(ScreenValues.SCREEN_WIDTH / 2, ScreenValues.SCREEN_HEIGHT / 2);
		solo.sleep(5000);
		
		solo.sleep(2000);
		double distanceLeft = userVariablesContainer.getUserVariable("p1", sprite).getValue();
		Log.d("RobotAlbertTest", "left=" + distanceLeft);
		assertEquals("Variable has the wrong value after stage", 50.0, distanceLeft);

		solo.sleep(1000);
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
		Script startScript = new StartScript(firstSprite);
		Script whenScript = new WhenScript(firstSprite);
		SetLookBrick setLookBrick = new SetLookBrick(firstSprite);
		sprite = firstSprite;

		WaitBrick waitBrick = new WaitBrick(firstSprite, 4000);

		SetVariableBrick setVariableBrick = new SetVariableBrick(firstSprite, 0.0);

		whenScript.addBrick(waitBrick);
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
}
