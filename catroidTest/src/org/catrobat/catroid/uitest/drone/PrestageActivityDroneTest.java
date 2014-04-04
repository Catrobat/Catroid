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
package org.catrobat.catroid.uitest.drone;

import android.content.Intent;
import android.util.Log;

import com.parrot.freeflight.service.DroneControlService;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.drone.DroneTestUtils;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class PrestageActivityDroneTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private enum ActivityUnderTest {
		PRE_STAGE, STAGE
	};

	private static final String TAG = PrestageActivityDroneTest.class.getSimpleName();
	private static final Integer EXPECTED_REQUIRED_RESOURCES = 1;

	PreStageActivity preStageActivity;
	DroneControlService droneControlService;
	StageActivity stageActivity;
	Intent stageActivityIntent;

	public PrestageActivityDroneTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		preStageActivity = null;
		droneControlService = null;
		stageActivity = null;
		stageActivityIntent = null;
		DroneTestUtils.createDroneProjectWithScriptAndAllDroneMoveBricks();
		System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
	}

	@Device
	public void test01PopUpOnPreStageIfNoDroneIsConnected() {
		//ATTENTION, test0* must be executed in the right order!
		//TODO Drone: make test order irrelevant
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnPlayButton(solo);
		solo.sleep(4000);
		solo.getText(solo.getString(R.string.close));
		waitForPreStageActivity();
		Integer requiredResourceCounter = (Integer) Reflection.getPrivateField(preStageActivity,
				"requiredResourceCounter");
		Integer resources = (Integer) Reflection.getPrivateField(preStageActivity, "resources");
		assertEquals("Only drone bit should be set", Brick.ARDRONE_SUPPORT, resources.intValue());
		assertEquals("Required resource counter should be 1", EXPECTED_REQUIRED_RESOURCES, requiredResourceCounter);
	}

	@Device
	public void test02DontStartDroneServiceIfNoDroneBrickIsInProject() {
		//ATTENTION, test0* must be executed in the right order!
		//TODO Drone: make test order irrelevant
		Project project = ProjectManager.getInstance().getCurrentProject();
		project.getSpriteList().get(0).removeAllScripts();
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnPlayButton(solo);

		waitForStageActivity();
		//check if drone service should be started on stage activity
		boolean droneStartExtra = getStageActivityIntentAndDroneStartValue(true);
		assertFalse("No extra should be present", droneStartExtra);
		waitForDroneServiceToBindOnActivity(ActivityUnderTest.STAGE);
		assertTrue("DroneControlService must not start:", droneControlService == null);
	}

	@Device
	public void tst03DontStartDroneServiceOnLowBattery() {
		//ATTENTION, test0* must be executed in the right order!
		//TODO Drone: make test order irrelevant
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnPlayButton(solo);
		waitForPreStageActivity();
		assertEquals("Must be unchanged 0", 0, getDroneBatteryLevelFromPreStageActivity());
		preStageActivity.onDroneReady();
		assertTrue("Dialog must be present", solo.searchText(solo.getString(R.string.error_drone_low_battery_title)));
	}

	@Device
	public void test04DontStartDroneServiceOnx86Device() {
		//ATTENTION, test0* must be executed in the right order!
		//TODO Drone: make test order irrelevant
		String origOsArch = CatroidApplication.OS_ARCH;
		DroneTestUtils.fakex86ArchWithReflection();

		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnPlayButton(solo);
		waitForPreStageActivity();
		assertTrue("Dialog must contain string",
				solo.searchText(solo.getString(R.string.error_drone_wrong_platform_title)));

		DroneTestUtils.fakexOsArchWithReflection(origOsArch);
		assertTrue("Os Arch should be the original one", CatroidApplication.OS_ARCH.equals(origOsArch));
	}

	@Device
	public void test05DroneServiceStart() {
		//ATTENTION, test0* must be executed in the right order!
		//TODO Drone: make test order irrelevant
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnPlayButton(solo);
		waitForPreStageActivity();

		waitForDroneServiceToBindOnActivity(ActivityUnderTest.PRE_STAGE);
		assertNull("DroneControlService must not be started", droneControlService);

		preStageActivity.onDroneAvailabilityChanged(true);

		waitForDroneServiceToBindOnActivity(ActivityUnderTest.PRE_STAGE);
		assertNotNull("DroneControlService must be started", droneControlService);

		Reflection.invokeMethod(preStageActivity, "resourceInitialized");

		waitForStageActivity();

		waitForDroneServiceToBindOnActivity(ActivityUnderTest.STAGE);
		assertNotNull("DroneControlService must be instantiate", droneControlService);

		Boolean droneStartExtra = getStageActivityIntentAndDroneStartValue(false);
		assertTrue("Drone extra should be present", droneStartExtra);
	}

	private void waitForDroneServiceToBindOnActivity(ActivityUnderTest activityUnderTest) {
		for (int i = 0; i < 10; i++) { //waiting for the service to start
			Log.d(TAG, "Spinning=" + i);
			solo.sleep(500);
			switch (activityUnderTest) {
				case PRE_STAGE:
					getDroneControlServiceFromPreStage(preStageActivity);
					break;
				case STAGE:
					getDroneControlServiceFromStage(stageActivity);
					break;
				default:
					return;
			}
			if (droneControlService != null) {
				break;
			}
		}
	}

	private void waitForStageActivity() {
		solo.waitForActivity(StageActivity.class);
		stageActivity = (StageActivity) solo.getCurrentActivity();
	}

	private int getDroneBatteryLevelFromPreStageActivity() {
		assertNotNull("PreStage not correctly initialized", preStageActivity);
		return (Integer) Reflection.getPrivateField(preStageActivity, "droneBatteryCharge");
	}

	private void waitForPreStageActivity() {
		solo.waitForActivity(PreStageActivity.class);
		preStageActivity = (PreStageActivity) solo.getCurrentActivity();
	}

	private void getDroneControlServiceFromPreStage(PreStageActivity preStage) {
		droneControlService = (DroneControlService) Reflection.getPrivateField(preStage, "droneControlService");
	}

	private void getDroneControlServiceFromStage(StageActivity stage) {
		droneControlService = (DroneControlService) Reflection.getPrivateField(stage, "droneControlService");
	}

	private boolean getStageActivityIntentAndDroneStartValue(boolean defaultValue) {
		stageActivityIntent = stageActivity.getIntent();
		return stageActivityIntent.getBooleanExtra(PreStageActivity.STRING_EXTRA_INIT_DRONE, defaultValue);
	}
}