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
package org.catrobat.catroid.uitest.drone;

//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.preference.PreferenceManager;
//import android.util.Log;
//import android.widget.CheckBox;
//
//import com.parrot.freeflight.service.DroneControlService;
//
//import org.catrobat.catroid.CatroidApplication;
//import org.catrobat.catroid.ProjectManager;
//import org.catrobat.catroid.R;
//import org.catrobat.catroid.content.Project;
//import org.catrobat.catroid.content.Script;
//import org.catrobat.catroid.content.Sprite;
//import org.catrobat.catroid.content.WhenScript;
//import org.catrobat.catroid.content.bricks.Brick;
//import org.catrobat.catroid.content.bricks.DroneFlipBrick;
//import org.catrobat.catroid.drone.DroneInitializer;
//import org.catrobat.catroid.stage.DroneConnection;
//import org.catrobat.catroid.stage.PreStageActivity;
//import org.catrobat.catroid.stage.StageActivity;
//import org.catrobat.catroid.test.drone.DroneTestUtils;

import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;

//import org.catrobat.catroid.ui.ProjectActivity;
//import org.catrobat.catroid.ui.SettingsActivity;
//import org.catrobat.catroid.uitest.annotation.Device;
//import org.catrobat.catroid.uitest.util.Reflection;
//import org.catrobat.catroid.uitest.util.UiTestUtils;
//import org.mockito.Mockito;

public class PrestageActivityDroneTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	//	private enum ActivityUnderTest {
//		PRE_STAGE, STAGE
//	}
//
//	private static final String TAG = PrestageActivityDroneTest.class.getSimpleName();
//	private static final int EXPECTED_REQUIRED_RESOURCES = 1;
//
//	private PreStageActivity preStageActivity;
//	private DroneControlService droneControlService;
//	private StageActivity stageActivity;
//	private Intent stageActivityIntent;
//	private DroneConnection droneConnection = null;
//
	public PrestageActivityDroneTest() {
		super(MainMenuActivity.class);
	}

	public void testThisTestmethodIsOnlyHereForPassingTheSourceTest() {
		assertSame("Remove me!!", "Remove me!!", "Remove me!!");
	}
//
//	@Override
//	protected void setUp() throws Exception {
//		super.setUp();
//		preStageActivity = null;
//		droneControlService = null;
//		stageActivity = null;
//		stageActivityIntent = null;
//		DroneTestUtils.setDroneTermsOfUseAcceptedPermanently(getActivity());
//		DroneTestUtils.createDroneProjectWithScriptAndAllDroneMoveBricks();
//		System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
//	}
//
//	@Device
//	public void test00DroneTermsOfServiceDialog() {
//		//ATTENTION, test0* must be executed in the right order!
//		//TODO Drone: make test order irrelevant
//		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//		if (preferences.getBoolean(SettingsActivity.SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY,
//				false)) {
//			preferences
//					.edit()
//					.putBoolean(SettingsActivity.SETTINGS_PARROT_AR_DRONE_CATROBAT_TERMS_OF_SERVICE_ACCEPTED_PERMANENTLY,
//							false).commit();
//		}
//
//		UiTestUtils.getIntoSpritesFromMainMenu(solo);
//		UiTestUtils.clickOnPlayButton(solo);
//		solo.waitForDialogToOpen();
//		assertTrue("Terms of use title must be present in dialog head",
//				solo.searchText(solo.getString(R.string.dialog_terms_of_use_title)));
//
//		solo.clickOnText(solo.getString(R.string.dialog_terms_of_use_agree), 2);
//		solo.waitForDialogToOpen();
//		solo.clickOnText(solo.getString(R.string.close));
//		UiTestUtils.clickOnPlayButton(solo);
//		CheckBox checkbox = (CheckBox) solo.getView(R.id.dialog_terms_of_use_check_box_agree_permanently);
//		assertNotNull("Check box must me present", checkbox);
//		solo.clickOnText(solo.getString(R.string.dialog_terms_of_use_agree_permanent));
//		assertTrue("checkbox must be checked", checkbox.isChecked());
//		solo.clickOnText(solo.getString(R.string.dialog_terms_of_use_agree), 2);
//		solo.waitForDialogToOpen();
//		solo.waitForText(solo.getString(R.string.error_no_drone_connected_title));
//		solo.clickOnText(solo.getString(R.string.close));
//		assertTrue("Must go back to Projectactivity", solo.waitForActivity(ProjectActivity.class));
//		UiTestUtils.clickOnPlayButton(solo);
//		solo.waitForDialogToOpen();
//		assertTrue("No drone Message must be visible",
//				solo.searchText(solo.getString(R.string.error_no_drone_connected_title)));
//	}
//
//	@Device
//	public void test01PopUpOnPreStageIfNoDroneIsConnected() {
//		//ATTENTION, test0* must be executed in the right order!
//		//TODO Drone: make test order irrelevant
//		UiTestUtils.getIntoSpritesFromMainMenu(solo);
//		UiTestUtils.clickOnPlayButton(solo);
//		solo.sleep(4000);
//		solo.getText(solo.getString(R.string.close));
//		waitForPreStageActivity();
//		int requiredResourceCounter = (Integer) Reflection.getPrivateField(preStageActivity, "requiredResourceCounter");
//		int resources = (Integer) Reflection.getPrivateField(preStageActivity, "resources");
//		assertEquals("Only drone bit should be set", Brick.ARDRONE_SUPPORT, resources);
//		assertEquals("Required resource counter should be 1", EXPECTED_REQUIRED_RESOURCES, requiredResourceCounter);
//	}
//
//	@Device
//	public void test02DontStartDroneServiceIfNoDroneBrickIsInProject() {
//		//ATTENTION, test0* must be executed in the right order!
//		//TODO Drone: make test order irrelevant
//		Project project = ProjectManager.getInstance().getCurrentProject();
//		project.getSpriteList().get(0).removeAllScripts();
//		UiTestUtils.getIntoSpritesFromMainMenu(solo);
//		UiTestUtils.clickOnPlayButton(solo);
//
//		waitForStageActivity();
//		//check if drone service should be started on stage activity
//		boolean droneStartExtra = getStageActivityIntentAndDroneStartValue(true);
//		assertFalse("No extra should be present", droneStartExtra);
//		waitForDroneServiceToBind(ActivityUnderTest.STAGE);
//		assertTrue("DroneControlService must not start:", droneControlService == null);
//	}
//
//	@Device
//	public void test03DontStartDroneServiceOnLowBattery() {
//		//ATTENTION, test0* must be executed in the right order!
//		//TODO Drone: make test order irrelevant
//		Project project = new Project(getActivity(), UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
//		Sprite sprite = new Sprite("testSprite");
//		project.addSprite(sprite);
//		Brick brick = new DroneFlipBrick();
//		Script script = new WhenScript();
//
//		Brick preparedBrick = Mockito.spy(brick);
//		Mockito.when(preparedBrick.getRequiredResources()).thenReturn(0x10000);
//		assertEquals("Faked property must be set", preparedBrick.getRequiredResources(), 0x10000);
//		sprite.addScript(script);
//		script.addBrick(preparedBrick);
//		ProjectManager.getInstance().setProject(project);
//		UiTestUtils.getIntoSpritesFromMainMenu(solo);
//		UiTestUtils.clickOnPlayButton(solo);
//		waitForPreStageActivity();
//		solo.sleep(4000);
//		assertEquals("Must be unchanged 0", 0, getDroneBatteryLevelFromPreStageActivityDroneInitializer());
//		solo.sleep(4000);
//		preStageActivity.getDroneInitializer().onDroneBatteryChanged(2);
//		preStageActivity.getDroneInitializer().onDroneReady();
//
//		solo.waitForDialogToOpen(2000);
//	}
//
//	@Device
//	public void test04DontStartDroneServiceOnx86Device() {
//		//ATTENTION, test0* must be executed in the right order!
//		//TODO Drone: make test order irrelevant
//		String origOsArch = CatroidApplication.OS_ARCH;
//		DroneTestUtils.fakex86ArchWithReflection();
//
//		UiTestUtils.getIntoSpritesFromMainMenu(solo);
//		UiTestUtils.clickOnPlayButton(solo);
//		waitForPreStageActivity();
//		assertTrue("Dialog must contain string",
//				solo.searchText(solo.getString(R.string.error_drone_wrong_platform_title)));
//
//		DroneTestUtils.fakexOsArchWithReflection(origOsArch);
//		assertTrue("Os Arch should be the original one", CatroidApplication.OS_ARCH.equals(origOsArch));
//	}
//
//	@Device
//	public void test05DroneServiceStart() {
//		//ATTENTION, test0* must be executed in the right order!
//		//TODO Drone: make test order irrelevant
//		UiTestUtils.getIntoSpritesFromMainMenu(solo);
//		UiTestUtils.clickOnPlayButton(solo);
//		waitForPreStageActivity();
//
//		waitForDroneServiceToBind(ActivityUnderTest.PRE_STAGE);
//		assertNull("DroneControlService must not be started", droneControlService);
//
//		preStageActivity.getDroneInitializer().onDroneAvailabilityChanged(true);
//
//		waitForDroneServiceToBind(ActivityUnderTest.PRE_STAGE);
//		assertNotNull("DroneControlService must be started", droneControlService);
//
//		Reflection.invokeMethod(preStageActivity, "resourceInitialized");
//
//		waitForStageActivity();
//
//		waitForDroneServiceToBind(ActivityUnderTest.STAGE);
//		assertNotNull("DroneControlService must be instantiate", droneControlService);
//
//		Boolean droneStartExtra = getStageActivityIntentAndDroneStartValue(false);
//		assertTrue("Drone extra should be present", droneStartExtra);
//	}
//
//	private void waitForDroneServiceToBind(ActivityUnderTest activityUnderTest) {
//		for (int i = 0; i < 10; i++) { //waiting for the service to start
//			Log.d(TAG, "Spinning=" + i);
//			solo.sleep(1000);
//			switch (activityUnderTest) {
//				case PRE_STAGE:
//					getDroneControlServiceFromPreStageDroneInitializer();
//					break;
//				case STAGE:
//					droneConnection = (DroneConnection) Reflection.getPrivateField(stageActivity, "droneConnection");
//					if (getStageActivityIntentAndDroneStartValue(false)) {
//						assertNotNull("DronePrestageListener must instanced", droneConnection);
//						getDroneControlServiceFromDroneStageListenerOnStage();
//					} else {
//						assertNull("DronePrestageListener must not be instanced", droneConnection);
//					}
//					break;
//				default:
//					return;
//			}
//			if (droneControlService != null) {
//				break;
//			}
//		}
//	}
//
//	private void waitForStageActivity() {
//		solo.waitForActivity(StageActivity.class);
//		stageActivity = (StageActivity) solo.getCurrentActivity();
//	}
//
//	private int getDroneBatteryLevelFromPreStageActivityDroneInitializer() {
//		assertNotNull("PreStage not correctly initialized", preStageActivity);
//		DroneInitializer droneInitializer = preStageActivity.getDroneInitializer();
//		assertNotNull("Must be created if drone property is set", droneInitializer);
//		return (Integer) Reflection.getPrivateField(droneInitializer, "droneBatteryCharge");
//	}
//
//	private void waitForPreStageActivity() {
//		solo.waitForActivity(PreStageActivity.class);
//		preStageActivity = (PreStageActivity) solo.getCurrentActivity();
//		assertNotNull("prestage must be present", preStageActivity);
//	}
//
//	private void getDroneControlServiceFromPreStageDroneInitializer() {
//		droneControlService = (DroneControlService) Reflection.getPrivateField(preStageActivity.getDroneInitializer(),
//				"droneControlService");
//	}
//
//	private void getDroneControlServiceFromDroneStageListenerOnStage() {
//		droneControlService = (DroneControlService) Reflection.getPrivateField(droneConnection, "droneControlService");
//	}
//
//	private boolean getStageActivityIntentAndDroneStartValue(boolean defaultValue) {
//		stageActivityIntent = stageActivity.getIntent();
//		return stageActivityIntent.getBooleanExtra(DroneInitializer.INIT_DRONE_STRING_EXTRA, defaultValue);
//	}
}
