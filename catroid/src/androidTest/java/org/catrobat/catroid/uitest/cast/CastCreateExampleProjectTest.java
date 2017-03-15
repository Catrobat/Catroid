/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.uitest.cast;

import android.widget.ImageButton;
import android.widget.RadioButton;

import org.catrobat.catroid.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class CastCreateExampleProjectTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public CastCreateExampleProjectTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		TestUtils.deleteTestProjects();
		SettingsActivity.setCastFeatureAvailability(getActivity(), true);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		SettingsActivity.setCastFeatureAvailability(getActivity(), false);
		TestUtils.deleteTestProjects();
		solo.finishOpenedActivities();
		super.tearDown();
	}
	public void testCreateExampleCastProgram() {
		solo.waitForActivity(MainMenuActivity.class);
		solo.clickOnText(solo.getString(R.string.main_menu_new));
		solo.waitForText(solo.getString(R.string.new_project_default));
		solo.enterText(0, UiTestUtils.PROJECTNAME1);
		solo.clickOnText(solo.getString(R.string.new_project_default));
		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForText(solo.getString(R.string.ok)); // Wait for next dialog

		assertTrue("dialog with correct title not loaded in 5 seconds",
				solo.waitForText(solo.getString(R.string.project_select_screen_title), 0, 5000));

		ArrayList<RadioButton> currentViews = solo.getCurrentViews(RadioButton.class);
		assertTrue("Not enough screen options showing up", currentViews.size() == 3);
		solo.clickOnRadioButton(2);
		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForActivity(ProjectActivity.class);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		assertTrue("\"Cast not connected\" toast is not displayed",
				solo.waitForText(solo.getString(R.string.cast_error_not_connected_msg), 0, 5000));
		assertTrue("\"Cast to\" dialog not opened in 5 sec",
				solo.waitForText(solo.getString(R.string.cast_device_selector_dialog_title), 0, 5000));

		solo.clickOnView(solo.getView(R.id.cast_device_list_view, 0));
		solo.sleep(5000);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForView(ImageButton.class);
		ImageButton imageButtonA = (ImageButton) solo.getView(R.id.gamepadButtonA);
		solo.clickOnView(imageButtonA);
		solo.sleep(2000);
		solo.waitForView(ImageButton.class);
		ImageButton imageButtonB = (ImageButton) solo.getView(R.id.gamepadButtonB);
		solo.clickLongOnView(imageButtonB);
		solo.sleep(2500);
		solo.waitForView(ImageButton.class);
		ImageButton imageButtonLeft = (ImageButton) solo.getView(R.id.gamepadButtonLeft);
		solo.clickLongOnView(imageButtonLeft, 2000);
		solo.waitForView(ImageButton.class);
		ImageButton imageButtonRight = (ImageButton) solo.getView(R.id.gamepadButtonRight);
		solo.clickLongOnView(imageButtonRight, 2000);
		solo.waitForView(ImageButton.class);
		ImageButton imageButtonUp = (ImageButton) solo.getView(R.id.gamepadButtonUp);
		solo.clickLongOnView(imageButtonUp, 1200);
		solo.waitForView(ImageButton.class);
		ImageButton imageButtonDown = (ImageButton) solo.getView(R.id.gamepadButtonDown);
		solo.clickLongOnView(imageButtonDown, 1200);
		solo.waitForView(ImageButton.class);
		ImageButton imageButtonPause = (ImageButton) solo.getView(R.id.gamepadPauseButton);
		solo.clickOnView(imageButtonPause);
		solo.waitForText(solo.getString(R.string.stage_dialog_back));
		solo.clickOnText(solo.getString(R.string.stage_dialog_back));
	}
}
