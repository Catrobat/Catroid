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
package org.catrobat.catroid.uitest.content.brick;

import android.util.Log;

import junit.framework.Assert;

import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;

public class LedBrickTest extends BaseActivityInstrumentationTestCase<ScriptActivity> {

	private static final String TAG = LedBrickTest.class.getSimpleName();

	//private static final int LED_DELAY_MS = 8000;
	//private static final int WLAN_DELAY_MS = 700;

	//private LedOffBrick ledOffBrick;
	//private LedOnBrick ledOnBrick;
	//private Project project;

	public LedBrickTest() {
		super(ScriptActivity.class);
	}

/*	@Override
	protected void setUp() throws Exception {
		createProject();
		if (hasLedSystemFeature()) {
		super.setUp();
			SensorTestServerConnection.connectToArduinoServer();
			setActivityInitialTouchMode(false);
			SensorTestServerConnection.closeConnection();
		} else {
			Log.d(TAG, " setUp() - no flash led available");
		}
	}

	@Override
	protected void tearDown() throws Exception {
		SensorTestServerConnection.closeConnection();
		setActivityInitialTouchMode(true);
		super.tearDown();
	}*/

	@Device
	public void testLedBricks() {

		Assert.assertTrue("fix this test", true);
		//TODO: fix this test
		/*
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();

		assertEquals("Incorrect number of bricks.", 6, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 2, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks", 2, projectBrickList.size());

		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.brick_led_off)));

		Log.d(TAG, "LED value set to " + SensorTestServerConnection.SET_LED_OFF_VALUE);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());

		solo.sleep(LED_DELAY_MS);
		Log.d(TAG, "checking sensor value");
		SensorTestServerConnection.checkLightSensorValue(SensorTestServerConnection.SET_LED_OFF_VALUE);
		solo.sleep(WLAN_DELAY_MS);
		SensorTestServerConnection.checkLightSensorValue(SensorTestServerConnection.SET_LED_OFF_VALUE);
		solo.sleep(WLAN_DELAY_MS);
		SensorTestServerConnection.checkLightSensorValue(SensorTestServerConnection.SET_LED_OFF_VALUE);
		solo.sleep(WLAN_DELAY_MS);

		Log.d(TAG, "tapping the screen should turn on the led");
		UiTestUtils.clickOnStageCoordinates(solo, 100, 200, 480, 800);

		// wait a long time, then check the sensor value weather the light is really on
		solo.sleep(LED_DELAY_MS);
		Log.d(TAG, "checking sensor value");
		SensorTestServerConnection.checkLightSensorValue(SensorTestServerConnection.SET_LED_ON_VALUE);
		solo.sleep(WLAN_DELAY_MS);
		SensorTestServerConnection.checkLightSensorValue(SensorTestServerConnection.SET_LED_ON_VALUE);
		solo.sleep(WLAN_DELAY_MS);
		SensorTestServerConnection.checkLightSensorValue(SensorTestServerConnection.SET_LED_ON_VALUE);
		solo.sleep(WLAN_DELAY_MS);

		Log.d(TAG, "pause StageActivity - this should turn off the led");
		solo.goBack();

		// pausing the activity should turn the light off. again, check the sensor value
		solo.sleep(LED_DELAY_MS);
		Log.d(TAG, "checking sensor value");
		SensorTestServerConnection.checkLightSensorValue(SensorTestServerConnection.SET_LED_OFF_VALUE);
		solo.sleep(WLAN_DELAY_MS);
		SensorTestServerConnection.checkLightSensorValue(SensorTestServerConnection.SET_LED_OFF_VALUE);
		solo.sleep(WLAN_DELAY_MS);
		SensorTestServerConnection.checkLightSensorValue(SensorTestServerConnection.SET_LED_OFF_VALUE);
		solo.sleep(WLAN_DELAY_MS);

		// resuming the activity should turn the led on again
		Log.d(TAG, "resume StageActivity - this should turn the led on again");
		solo.clickOnButton(solo.getString(R.string.stage_dialog_resume));
		solo.sleep(6000);
		// wait a long time, then check the sensor value weather the light is really on
		solo.sleep(LED_DELAY_MS);
		Log.d(TAG, "checking sensor value");
		SensorTestServerConnection.checkLightSensorValue(SensorTestServerConnection.SET_LED_ON_VALUE);
		solo.sleep(WLAN_DELAY_MS);
		SensorTestServerConnection.checkLightSensorValue(SensorTestServerConnection.SET_LED_ON_VALUE);
		solo.sleep(WLAN_DELAY_MS);
		SensorTestServerConnection.checkLightSensorValue(SensorTestServerConnection.SET_LED_ON_VALUE);
		solo.sleep(WLAN_DELAY_MS);
		*/

		Log.d(TAG, "testLedBrick() finished");
	}

	/*private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script startScript = new StartScript();
		Script tappedScript = new WhenScript();

		ledOnBrick = new LedOnBrick();
		ledOffBrick = new LedOffBrick();

		sprite.addScript(startScript);
		startScript.addBrick(ledOffBrick);
		startScript.addBrick(ledOffBrick);
		sprite.addScript(tappedScript);
		tappedScript.addBrick(ledOnBrick);
		tappedScript.addBrick(ledOnBrick);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(startScript);
	}

	private boolean hasLedSystemFeature() {
		boolean hasCamera = this.getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
		boolean hasLed = this.getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

		if (!hasCamera || !hasLed) {
			return false;
		}

		Camera camera = null;

		try {
			camera = Camera.open();
		} catch (Exception exception) {
			Log.e(TAG, "failed to open Camera", exception);
		}

		if (camera == null) {
			return false;
		}

		Camera.Parameters parameters = camera.getParameters();

		if (parameters.getFlashMode() == null) {
			camera.release();
			camera = null;
			return false;
		}

		List<String> supportedFlashModes = parameters.getSupportedFlashModes();
		if (supportedFlashModes == null || supportedFlashModes.isEmpty() ||
				supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {
			camera.release();
			camera = null;
			return false;
		}

		camera.release();
		camera = null;

		return true;
	}*/
}
