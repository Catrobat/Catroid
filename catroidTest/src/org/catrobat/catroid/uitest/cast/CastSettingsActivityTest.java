/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.widget.ListView;
import android.widget.RadioButton;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class CastSettingsActivityTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

    public CastSettingsActivityTest() {
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

    public void testCreateProjectAndStartWithoutConnection() {
        solo.waitForActivity(MainMenuActivity.class);
        solo.clickOnText(solo.getString(R.string.main_menu_new));
        solo.waitForText(solo.getString(R.string.new_project_dialog_title));
        solo.enterText(0, UiTestUtils.PROJECTNAME1);
        solo.clickOnText(solo.getString(R.string.new_project_empty));
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
    }

    public void testIfCastCategoryShowsUpInNonCastProject() {

        UiTestUtils.createEmptyProject();
        solo.waitForActivity(MainMenuActivity.class);
        UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
        solo.waitForActivity(ScriptActivity.class);
        UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
        solo.waitForText(solo.getString(R.string.category_control));
        ListView fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
        solo.scrollListToBottom(fragmentListView);

        assertFalse("Cast category showing up in non cast project",
                solo.searchText(solo.getString(R.string.category_cast)));

    }

    public void testIfCastBricksSensorsAndCategoryDisplayed() {

        ProjectManager projectManager = ProjectManager.getInstance();

        Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME, true, true);
        Sprite firstSprite = new Sprite("cat");
        Script testScript = new StartScript();

        firstSprite.addScript(testScript);
        project.addSprite(firstSprite);

        projectManager.setFileChecksumContainer(new FileChecksumContainer());
        projectManager.setProject(project);
        projectManager.setCurrentSprite(firstSprite);
        projectManager.setCurrentScript(testScript);

        solo.waitForActivity(MainMenuActivity.class);
        UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
        solo.waitForActivity(ScriptActivity.class);
        UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
        solo.waitForText(solo.getString(R.string.category_control));

        ListView fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
        solo.scrollListToBottom(fragmentListView);

        assertTrue("Cast brick category not showing up", solo.searchText(solo.getString(R.string.category_cast)));

        solo.clickOnText(solo.getString(R.string.category_cast));
        solo.waitForText(solo.getString(R.string.category_cast));

        ArrayList<Boolean> findResults = new ArrayList<>();
        findResults.add(solo.searchText(solo.getString(R.string.brick_when_gamepad_button)));
        findResults.add(solo.searchText(solo.getString(R.string.formula_editor_sensor_gamepad_a_pressed)));
        findResults.add(solo.searchText(solo.getString(R.string.formula_editor_sensor_gamepad_b_pressed)));

        ListView bricksView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
        solo.scrollListToBottom(bricksView);

        findResults.add(solo.searchText(solo.getString(R.string.formula_editor_sensor_gamepad_up_pressed)));
        findResults.add(solo.searchText(solo.getString(R.string.formula_editor_sensor_gamepad_down_pressed)));
        findResults.add(solo.searchText(solo.getString(R.string.formula_editor_sensor_gamepad_left_pressed)));
        findResults.add(solo.searchText(solo.getString(R.string.formula_editor_sensor_gamepad_right_pressed)));

        assertFalse("Not all cast category bricks shown", findResults.contains(false));
    }
}