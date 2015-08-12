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

package org.catrobat.catroid.uitest.devices.mindstorms.nxt;

import android.content.Context;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.LegoNxtMotorMoveBrick;
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class LegoNXTSensorInfoTests extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private Context applicationContext;

	private final String projectNameNxt = UiTestUtils.PROJECTNAME1;
	private final String projectNameNoNxt = UiTestUtils.PROJECTNAME2;
	private final String spriteName = "testSprite";

	public LegoNXTSensorInfoTests() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		setSensors(NXTSensor.Sensor.TOUCH);

		applicationContext = getInstrumentation().getTargetContext().getApplicationContext();
	}

	private void setSensors(NXTSensor.Sensor sensor) {
		SettingsActivity.setLegoMindstormsNXTSensorMapping(this.getInstrumentation().getTargetContext(),
				new NXTSensor.Sensor[] { sensor, sensor, sensor, sensor });
	}

	public void testNXTSensorInfoDialog() throws InterruptedException {
		createBrickTestproject(projectNameNxt);
		createNoNXTUseTestproject(projectNameNoNxt);

		boolean nxtBricksEnabledStart = SettingsActivity.isMindstormsNXTSharedPreferenceEnabled(applicationContext);

		boolean nxtDialogDisabledStart = SettingsActivity.getShowLegoMindstormsSensorInfoDialog(applicationContext);

		if (!nxtBricksEnabledStart) {
			solo.clickOnActionBarItem(R.id.settings);

			String preferenceTitle = solo.getString(R.string.preference_title_enable_mindstorms_nxt_bricks);
			solo.waitForText(preferenceTitle);
			solo.clickOnText(preferenceTitle);
			solo.waitForText(solo.getString(R.string.preference_title_mindstorms_nxt_sensors));
			solo.clickOnText(preferenceTitle);

			solo.goBack();
			solo.goBack();
		}

		if (nxtDialogDisabledStart) {
			solo.clickOnActionBarItem(R.id.settings);

			String preferenceTitle = solo.getString(R.string.preference_title_enable_mindstorms_nxt_bricks);
			solo.waitForText(preferenceTitle);
			solo.clickOnText(preferenceTitle);
			solo.waitForText(solo.getString(R.string.preference_title_mindstorms_nxt_sensors));
			solo.clickOnText(solo.getString(R.string.preference_disable_nxt_info_dialog));

			solo.goBack();
			solo.goBack();
		}

		solo.waitForText(solo.getString(R.string.main_menu_programs));
		solo.clickOnText(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForText(solo.getString(R.string.programs));

		solo.clickOnText(projectNameNxt);

		assertTrue("NXT Sensor Dialog not shown for Project with NXT Brick",
				solo.waitForText(solo.getString(R.string.lego_nxt_sensor_config_info_title)));

		assertTrue("Wrong Sensors listed in NXT Sensor Dialog",
				solo.searchText(solo.getString(R.string.nxt_sensor_touch), 4));

		solo.goBack();
		solo.goBack();

		setSensors(NXTSensor.Sensor.LIGHT_INACTIVE);

		solo.clickOnText(projectNameNxt);
		solo.waitForText(solo.getString(R.string.lego_nxt_sensor_config_info_title));

		assertTrue("Wrong Sensors listed in NXT Sensor Dialog",
				solo.searchText(solo.getString(R.string.nxt_sensor_light), 4));

		solo.goBack();
		solo.goBack();

		solo.clickOnText(projectNameNoNxt);
		solo.waitForText(solo.getString(R.string.background));
		solo.sleep(200);

		assertFalse("NXT Sensor Dialog was shown for Project without NXT elements",
				solo.searchText(solo.getString(R.string.lego_nxt_sensor_config_info_title)));

		solo.goBack();
		solo.goBack();

		solo.clickOnActionBarItem(R.id.settings);
		String preferenceTitle = solo.getString(R.string.preference_title_enable_mindstorms_nxt_bricks);
		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);
		solo.waitForText(solo.getString(R.string.preference_title_mindstorms_nxt_sensors));
		solo.clickOnText(solo.getString(R.string.preference_disable_nxt_info_dialog));

		solo.goBack();
		solo.goBack();

		solo.waitForText(solo.getString(R.string.main_menu_programs));
		solo.clickOnText(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForText(solo.getString(R.string.programs));

		solo.clickOnText(projectNameNxt);
		solo.waitForText(solo.getString(R.string.spritelist_background_headline));

		assertFalse("NXT Sensor Dialog was shown while disabled in settings",
				solo.searchText(solo.getString(R.string.lego_nxt_sensor_config_info_title)));

		solo.goBack();
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForText(solo.getString(R.string.programs));
		solo.clickOnText(projectNameNoNxt);

		solo.clickOnText(spriteName);
		solo.waitForText(solo.getString(R.string.scripts));
		solo.clickOnText(solo.getString(R.string.scripts));

		solo.waitForText(solo.getString(R.string.brick_wait));
		solo.clickOnText(solo.getString(R.string.brick_wait));
		solo.waitForText(solo.getString(R.string.brick_context_dialog_formula_edit_brick));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_formula_edit_brick));

		solo.waitForText(solo.getString(R.string.formula_editor_sensors));
		solo.clickOnText(solo.getString(R.string.formula_editor_sensors));
		solo.sleep(300);
		solo.waitForText(solo.getString(R.string.formula_editor_sensors));
		solo.clickOnText(solo.getString(R.string.formula_editor_sensor_lego_nxt_1), 1, true);
		solo.clickOnText(solo.getString(R.string.ok));

		solo.goBack();
		solo.goBack();
		solo.goBack();

		solo.clickOnActionBarItem(R.id.settings);
		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);
		solo.waitForText(solo.getString(R.string.preference_title_mindstorms_nxt_sensors));
		solo.clickOnText(solo.getString(R.string.preference_disable_nxt_info_dialog));

		solo.goBack();
		solo.goBack();
		solo.goBack();

		assertTrue("not in main menu2", solo.waitForText(solo.getString(R.string.main_menu_continue)));

		solo.waitForText(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		assertTrue("NXT Sensor Dialog not shown for Project with NXT Sensor but no NXT Brick",
				solo.waitForText(solo.getString(R.string.lego_nxt_sensor_config_info_title)));
	}

	private void createBrickTestproject(String projectName) {

		Sprite firstSprite = new Sprite(spriteName);
		Script startScript = new StartScript();

		LegoNxtMotorMoveBrick legoMotorActionBrick = new LegoNxtMotorMoveBrick(
				LegoNxtMotorMoveBrick.Motor.MOTOR_B_C, 100);
		WaitBrick firstWaitBrick = new WaitBrick(500);

		LegoNxtPlayToneBrick legoPlayToneBrick = new LegoNxtPlayToneBrick(50, 1.5f);

		startScript.addBrick(legoMotorActionBrick);
		startScript.addBrick(firstWaitBrick);
		startScript.addBrick(legoPlayToneBrick);

		firstSprite.addScript(startScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());

		StorageHandler.getInstance().saveProject(project);
	}

	private void createNoNXTUseTestproject(String projectName) {

		Sprite firstSprite = new Sprite(spriteName);
		Script startScript = new StartScript();

		WaitBrick firstWaitBrick = new WaitBrick(123);

		startScript.addBrick(firstWaitBrick);

		firstSprite.addScript(startScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());

		StorageHandler.getInstance().saveProject(project);
	}
}
