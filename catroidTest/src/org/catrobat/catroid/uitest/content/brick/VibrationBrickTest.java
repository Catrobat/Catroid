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
package org.catrobat.catroid.uitest.content.brick;

import android.util.Log;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.VibrationBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.SensorServerUtils;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class VibrationBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {

	private static final String LOG_VIB_TEST = "VibrationBrickTest::";

	private static final int WLAN_DELAY_MS = 500;
	private static final int VIBRATION_DURATION_MILLIS = 1000;

	private VibrationBrick vibrationBrick2Seconds;
	private VibrationBrick vibrationBrick15Seconds;
	private Project project;


	public VibrationBrickTest() {
		super(ScriptActivity.class);
	}

	@Override
	protected void setUp() throws Exception {

		createProject();
		super.setUp();

		// create server connection
		SensorServerUtils.connectToArduinoServer();

		// disable touch screen while testing
		setActivityInitialTouchMode(false);

		Log.d(LOG_VIB_TEST, "setUp() - no flash led available");

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		SensorServerUtils.closeConnection();

		setActivityInitialTouchMode(true);
	}

	@Device
	public void testVibrationBrick() {
		SensorServerUtils.calibrateVibrationSensor();

		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals( "Incorrect number of bricks.", 4, dragDropListView.getChildCount() );
		assertEquals( "Incorrect number of bricks.", 1, childrenCount );

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals( "Incorrect number of bricks", 1, projectBrickList.size() );

		//assertEquals( "Wrong brick instance", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0) );

		try {
			Log.d(LOG_VIB_TEST, "checking vibration sensor value");
			SensorServerUtils.checkVibrationSensorValue(SensorServerUtils.SET_VIBRATION_OFF_VALUE);
			Thread.sleep(WLAN_DELAY_MS);
			SensorServerUtils.checkVibrationSensorValue(SensorServerUtils.SET_VIBRATION_OFF_VALUE);
			Thread.sleep(WLAN_DELAY_MS);
		} catch (Exception e) {
			Log.e(LOG_VIB_TEST, e.getMessage());
		}

		Log.d(LOG_VIB_TEST, "Vibration starts after pressing play");
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());

		try {
<<<<<<< HEAD
			Thread.sleep(WLAN_DELAY_MS);
			SensorServerUtils.checkVibrationSensorValue(1);
		} catch (InterruptedException ie) {
			Log.e(LOG_VIB_TEST, "ERROR: " + ie.getMessage());
=======
			Thread.sleep(6000);
			SensorServerUtils.checkVibrationSensorValue(SensorServerUtils.SET_VIBRATION_ON_VALUE);
			Thread.sleep(WLAN_DELAY_MS);
		} catch (Exception e) {
			Log.e(LOG_VIB_TEST, e.getMessage());
		}

		Log.d(LOG_VIB_TEST, "sleep two seconds. the phone should have stopped vibrating");

		try {
			Thread.sleep(2000);
			Log.d(LOG_VIB_TEST, "checking vibration sensor value");
			SensorServerUtils.checkVibrationSensorValue(SensorServerUtils.SET_VIBRATION_OFF_VALUE);
			Thread.sleep(WLAN_DELAY_MS);
			SensorServerUtils.checkVibrationSensorValue(SensorServerUtils.SET_VIBRATION_OFF_VALUE);
			Thread.sleep(WLAN_DELAY_MS);
		} catch (Exception e) {
			Log.e(LOG_VIB_TEST, e.getMessage());
		}

		Log.d(LOG_VIB_TEST, "tapping the screen should turn on the vibrator");
		UiTestUtils.clickOnStageCoordinates(solo, 100, 200, 480, 800);

		try {
			Thread.sleep(5000);
			SensorServerUtils.checkVibrationSensorValue(SensorServerUtils.SET_VIBRATION_ON_VALUE);
			Thread.sleep(WLAN_DELAY_MS);
		} catch (Exception e) {
			Log.e(LOG_VIB_TEST, e.getMessage());
		}

		Log.d(LOG_VIB_TEST, "pause StageActivity - this should turn off the vibrator");
		solo.goBack();

		try {
			Log.d(LOG_VIB_TEST, "checking vibration sensor value");
			SensorServerUtils.checkVibrationSensorValue(SensorServerUtils.SET_VIBRATION_OFF_VALUE);
			Thread.sleep(WLAN_DELAY_MS);
			SensorServerUtils.checkVibrationSensorValue(SensorServerUtils.SET_VIBRATION_OFF_VALUE);
			Thread.sleep(WLAN_DELAY_MS);
		} catch (Exception e) {
			Log.e(LOG_VIB_TEST, e.getMessage());
		}

		Log.d(LOG_VIB_TEST, "resume StageActivity - this should turn the vibrator on again");
		solo.clickOnButton(solo.getString(R.string.stage_dialog_resume));

		try {
			Thread.sleep(5000);
			SensorServerUtils.checkVibrationSensorValue(SensorServerUtils.SET_VIBRATION_ON_VALUE);
			Thread.sleep(WLAN_DELAY_MS);
		} catch (Exception e) {
			Log.e(LOG_VIB_TEST, e.getMessage());
>>>>>>> vibration brick test implemented
		}

		Log.d(LOG_VIB_TEST, "testVibrationBrick() finished");
	}

	private void createProject () {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script startScript = new StartScript(sprite);
		Script tappedScript = new WhenScript(sprite);

		vibrationBrick2Seconds = new VibrationBrick(sprite, 2000);
		vibrationBrick15Seconds = new VibrationBrick(sprite, 15000);

		sprite.addScript(startScript);
		startScript.addBrick(vibrationBrick2Seconds);
		sprite.addScript(tappedScript);
		tappedScript.addBrick(vibrationBrick15Seconds);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(startScript);
	}

}
