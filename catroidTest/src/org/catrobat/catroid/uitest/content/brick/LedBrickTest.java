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

import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.LedBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.SensorServerUtils;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class LedBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {

	private static final String LOG_LED_TEST = "LedBrickTest::";

	private static final int LED_DELAY_MS = 8000;
	private static final int WLAN_DELAY_MS = 500;

	private LedBrick ledBrick;
	private Project project;


	public LedBrickTest() {
		super(ScriptActivity.class);
	}

	@Override
	protected void setUp() throws Exception {

		if ( createProject() ) {
			super.setUp();

			// create server connection
			SensorServerUtils.connectToArduinoServer();

			// disable touch screen while testing
			setActivityInitialTouchMode(false);
		} else {
			Log.d(LOG_LED_TEST, "setUp() - no flash led available");
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		SensorServerUtils.closeConnection();

		setActivityInitialTouchMode(true);
	}

	@Device
	public void testLedBrick() {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals( "Incorrect number of bricks.", 2, dragDropListView.getChildCount() );
		assertEquals( "Incorrect number of bricks.", 1, childrenCount );

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals( "Incorrect number of bricks", 1, projectBrickList.size() );

		assertEquals( "Wrong brick instance", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0) );
		assertNotNull( "TextView does not exist.", solo.getText(solo.getString(R.string.brick_led)));

		UiTestUtils.testBrickWithFormulaEditor(solo, R.id.brick_led_edit_text, SensorServerUtils.SET_LED_ON_VALUE,
				"lightValue", ledBrick);

		Log.d(LOG_LED_TEST, "LED value set to " + SensorServerUtils.SET_LED_ON_VALUE);

		// executing the script should turn on the LED
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());

		try {
			// wait a long time, then check the sensor value weather the light is really on
			Thread.sleep(LED_DELAY_MS);
			Log.d(LOG_LED_TEST, "checking sensor value");
			SensorServerUtils.checkSensorValue(SensorServerUtils.SET_LED_ON_VALUE);
			Thread.sleep(WLAN_DELAY_MS);
			SensorServerUtils.checkSensorValue(SensorServerUtils.SET_LED_ON_VALUE);
			Thread.sleep(WLAN_DELAY_MS);
			SensorServerUtils.checkSensorValue(SensorServerUtils.SET_LED_ON_VALUE);
			Thread.sleep(WLAN_DELAY_MS);
		} catch (Exception e) {
			Log.e(LOG_LED_TEST, e.getMessage());
		}

		Log.d(LOG_LED_TEST, "pause StageActivity - this should turn off the led");
		solo.goBack();

		// pausing the activity should turn the light off. again, check the sensor value
		try {
			Thread.sleep(LED_DELAY_MS);
			Log.d(LOG_LED_TEST, "checking sensor value");
			SensorServerUtils.checkSensorValue(SensorServerUtils.SET_LED_OFF_VALUE);
			Thread.sleep(WLAN_DELAY_MS);
			SensorServerUtils.checkSensorValue(SensorServerUtils.SET_LED_OFF_VALUE);
			Thread.sleep(WLAN_DELAY_MS);
			SensorServerUtils.checkSensorValue(SensorServerUtils.SET_LED_OFF_VALUE);
			Thread.sleep(WLAN_DELAY_MS);
		} catch (Exception e) {
			Log.e(LOG_LED_TEST, e.getMessage());
		}

		Log.d(LOG_LED_TEST, "testLedBrick() finished");
	}

	@Device
	public void testMultipleLedBricks() {

	}

	private boolean createProject () {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);

		ledBrick = new LedBrick(sprite);

		script.addBrick(ledBrick);
		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);

		boolean hasCamera = this.getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
		boolean hasLed = this.getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

		return (hasCamera && hasLed);
	}

}
