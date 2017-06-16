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
package org.catrobat.catroid.createatschool.uitest.ui.fragment;

import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.createatschool.ui.CreateAtSchoolMainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.TemplatesActivity;
import org.catrobat.catroid.ui.fragment.SceneListFragment;
import org.catrobat.catroid.ui.fragment.TemplatesFragment;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class TemplatesFragmentTest extends BaseActivityInstrumentationTestCase<CreateAtSchoolMainMenuActivity> {

	private static final String TEMPLATE_ACTION_NAME = "Action";
	private static final String TEMPLATE_ADVENTURE_NAME = "Adventure";
	private static final String TEMPLATE_PUZZLE_NAME = "Puzzle";
	private static final String TEMPLATE_QUIZ_NAME = "Quiz";

	private String projectName = "Unzipped Template Adventure";
	private static final String GERMAN = "de";
	private String localeToReset;

	public TemplatesFragmentTest() {
		super(CreateAtSchoolMainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		UiTestUtils.setFakeToken(getActivity(), true);
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		UtilFile.deleteDirectory(new File(Utils.buildProjectPath(projectName)));
		if (localeToReset != null && !localeToReset.isEmpty()) {
			setLocale(localeToReset);
		}
		super.tearDown();
	}

	public void testTemplatesPresent() {
		Button templatesButton = (Button) getActivity().findViewById(R.id.main_menu_button_templates);
		assertEquals("Templates Button is not visible!", View.VISIBLE, templatesButton.getVisibility());

		openTemplatesFragment();

		assertTrue("Action template not visible", solo.searchText(TEMPLATE_ACTION_NAME, 1, false, true));
		assertTrue("Adventure template not visible", solo.searchText(TEMPLATE_ADVENTURE_NAME, 1, false, true));
		assertTrue("Puzzle template not visible", solo.searchText(TEMPLATE_PUZZLE_NAME, 1, false, true));
		assertTrue("Quiz template not visible", solo.searchText(TEMPLATE_QUIZ_NAME, 1, false, true));
	}

	public void testRatingDialogGone() {
		assertFalse("RatingDialog menu is present!", UiTestUtils.isActionModeItemPresent(solo,
				solo.getString(R.string.main_menu_rate_app)));

		openTemplatesFragment();

		assertFalse("RatingDialog menu is present!", UiTestUtils.isActionModeItemPresent(solo,
				solo.getString(R.string.main_menu_rate_app)));
	}

	public void testCreateLandscapeModeProgramFromTemplate() {
		openTemplatesFragment();
		createProgramFromTemplate(TEMPLATE_ADVENTURE_NAME, projectName, true);
		checkAdventureTemplate(true);
		solo.goBack();
		solo.sleep(250);
		solo.assertCurrentActivity("Did not return to TemplatesActivity on back button press", TemplatesActivity.class);
	}

	public void testCreatePortraitModeProgramFromTemplate() {
		openTemplatesFragment();
		createProgramFromTemplate(TEMPLATE_ADVENTURE_NAME, projectName, false);
		checkAdventureTemplate(false);
		solo.goBack();
		solo.sleep(250);
		solo.assertCurrentActivity("Did not return to TemplatesActivity on back button press", TemplatesActivity.class);
	}

	public void testReturnToMainMenuActivity() {
		openTemplatesFragment();
		UiTestUtils.clickOnHomeActionBarButton(solo);

		solo.waitForActivity(CreateAtSchoolMainMenuActivity.class);
		assertTrue("Did not return to CreateAtSchoolMainMenuActivity",
				solo.getCurrentActivity() instanceof CreateAtSchoolMainMenuActivity);
	}

	public void testSettingsOnlyVisibleInCreateAtSchoolMainMenuActivity() {
		assertTrue("Settings menu is not present!", UiTestUtils.isActionModeItemPresent(solo,
				solo.getString(R.string.settings)));

		openTemplatesFragment();

		assertFalse("Settings menu is present!", UiTestUtils.isActionModeItemPresent(solo,
				solo.getString(R.string.settings)));
	}

	public void testTemplatesSnackBarHint() {
		enableOrDisableSnackBarHints(true);
		openTemplatesFragment();

		assertTrue("Hint not shown!", solo.searchText(solo.getString(R.string.hint_templates), 1, false, true));

		enableOrDisableSnackBarHints(false);
	}

	public void testTemplatesTranslation() {
		setLocale(GERMAN);

		openTemplatesFragment();
		createProgramFromTemplate(TEMPLATE_ADVENTURE_NAME, projectName, false);

		Project project = ProjectManager.getInstance().getCurrentProject();
		assertEquals("Project was not translated!", "Beispiel-Level 1", project.getSceneList().get(1).getName());
	}

	private void setLocale(String languageCode) {
		Resources resources = getActivity().getResources();
		DisplayMetrics displayMetrics = resources.getDisplayMetrics();
		android.content.res.Configuration configuration = resources.getConfiguration();
		localeToReset = configuration.locale.getLanguage();
		configuration.locale = new Locale(languageCode.toLowerCase(Locale.US));
		resources.updateConfiguration(configuration, displayMetrics);
	}

	private void enableOrDisableSnackBarHints(boolean enabled) {
		PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
				.putBoolean(SettingsActivity.SETTINGS_SHOW_HINTS, enabled).commit();
	}

	private void openTemplatesFragment() {
		Button templatesButton = (Button) getActivity().findViewById(R.id.main_menu_button_templates);
		solo.clickOnView(templatesButton);
		solo.waitForActivity(TemplatesActivity.class);
		solo.waitForFragmentByTag(TemplatesFragment.TAG);
	}

	private void createProgramFromTemplate(String templateName, String projectName, boolean landscapeMode) {
		String ok = solo.getString(R.string.ok);

		solo.clickOnText(templateName);
		solo.waitForDialogToOpen();
		UiTestUtils.enterText(solo, 0, projectName);
		solo.clickOnText(ok);
		solo.waitForDialogToClose();

		solo.waitForText(solo.getString(R.string.project_orientation_title), 1, 1500);
		if (landscapeMode) {
			solo.clickOnRadioButton(1);
		}
		solo.clickOnText(ok);
		solo.waitForDialogToClose();

		solo.waitForActivity(ProjectActivity.class);
		solo.waitForFragmentByTag(SceneListFragment.TAG);
		solo.sleep(500);
	}

	private void checkAdventureTemplate(boolean landscapeMode) {
		Project project = ProjectManager.getInstance().getCurrentProject();
		List<Scene> scenes = project.getSceneList();

		assertEquals("Wrong program name", projectName, project.getName());
		assertEquals("Wrong number of Scenes", 5, scenes.size());
		assertEquals("Wrong scene name", "Start", scenes.get(0).getName());
		assertEquals("Wrong scene name", "Example level 1", scenes.get(1).getName());
		assertEquals("Wrong scene name", "Example level 2", scenes.get(2).getName());
		assertEquals("Wrong scene name", "Template level", scenes.get(3).getName());
		assertEquals("Wrong scene name", "End (Last Scene)", scenes.get(4).getName());

		int height = project.getXmlHeader().getVirtualScreenHeight();
		int width = project.getXmlHeader().getVirtualScreenWidth();
		if (landscapeMode) {
			assertTrue("Wrong width or height of landscape project. Width: " + width + ". Height: " + height,
					width > height);
		} else {
			assertTrue("Wrong width or height of portrait project. Width: " + width + ". Height: " + height,
					width < height);
		}
	}
}
