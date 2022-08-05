/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.activity;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;

import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectListActivity;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_DETAILS_PROJECTS_PREFERENCE_KEY;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ProjectListActivityTest {

	private static final String PROJECT_NAME = "projectName";
	private static final String INVALID_PROJECT_XML = "invalid_project.xml";

	@Rule
	public BaseActivityTestRule<ProjectListActivity> activityTestRule =
			new BaseActivityTestRule<>(ProjectListActivity.class, false, false);

	@Before
	public void setUp() throws Exception {
		activityTestRule.deleteAllProjects();

		getDefaultSharedPreferences().edit()
				.putBoolean(SHOW_DETAILS_PROJECTS_PREFERENCE_KEY, true)
				.apply();
	}

	@After
	public void tearDown() throws Exception {
		getDefaultSharedPreferences().edit()
				.remove(SHOW_DETAILS_PROJECTS_PREFERENCE_KEY)
				.apply();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testInvalidMetaDataDoesNotCrash() throws IOException {
		File projectDirectory = new File(DEFAULT_ROOT_DIRECTORY, PROJECT_NAME);
		File codeXML = new File(projectDirectory, CODE_XML_FILE_NAME);
		assertTrue(projectDirectory.mkdir());
		assertTrue(codeXML.createNewFile());

		activityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testInvalidProjectXMLDoesNotCrashWhenShowDetailsEnabled() throws IOException {
		File projectDirectory = new File(DEFAULT_ROOT_DIRECTORY, PROJECT_NAME);
		File codeXML = new File(projectDirectory, CODE_XML_FILE_NAME);

		assertTrue(projectDirectory.mkdir());
		InputStream inputStream = getAssets().open(INVALID_PROJECT_XML);
		StorageOperations.copyStreamToFile(inputStream, codeXML);

		activityTestRule.launchActivity(null);
	}

	private AssetManager getAssets() {
		return InstrumentationRegistry.getInstrumentation().getContext().getAssets();
	}

	private SharedPreferences getDefaultSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());
	}
}
