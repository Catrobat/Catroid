/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.Manifest;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.defaultprojectcreators.DefaultProjectCreator;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.common.FlavoredConstants.EXTERNAL_STORAGE_ROOT_DIRECTORY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY;
import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ImportProjectsFromExternalStorageTest {

	@Rule
	public BaseActivityInstrumentationRule<MainMenuActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(MainMenuActivity.class, true, false);

	@Rule
	public GrantPermissionRule runtimePermissionRule = GrantPermissionRule
			.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE);

	private String projectName = "testImportProjectsFromExternalStorage";
	private String renamedProjectName = "testImportProjectsFromExternalStorage (1)";

	private boolean bufferedPrivacyPolicyPreferenceSetting;
	private boolean bufferedImportFromExternalStoragePreferenceSetting;

	@Before
	public void setUp() throws Exception {
		TestUtils.deleteProjects(projectName, renamedProjectName);

		new DefaultProjectCreator()
				.createDefaultProject(projectName, InstrumentationRegistry.getTargetContext(), false);

		if (!EXTERNAL_STORAGE_ROOT_DIRECTORY.exists()) {
			EXTERNAL_STORAGE_ROOT_DIRECTORY.mkdirs();
		}

		StorageOperations.copyDir(
				new File(DEFAULT_ROOT_DIRECTORY, projectName),
				new File(EXTERNAL_STORAGE_ROOT_DIRECTORY, projectName));

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());

		bufferedPrivacyPolicyPreferenceSetting = sharedPreferences
				.getBoolean(AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY, false);

		bufferedImportFromExternalStoragePreferenceSetting = sharedPreferences
				.getBoolean(SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG, false);

		sharedPreferences.edit()
				.putBoolean(AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY, true)
				.putBoolean(SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG, true)
				.commit();

		baseActivityTestRule.launchActivity();
	}

	@After
	public void tearDown() throws IOException {
		PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext())
				.edit()
				.putBoolean(AGREED_TO_PRIVACY_POLICY_PREFERENCE_KEY, bufferedPrivacyPolicyPreferenceSetting)
				.putBoolean(SHOW_COPY_PROJECTS_FROM_EXTERNAL_STORAGE_DIALOG, bufferedImportFromExternalStoragePreferenceSetting)
				.commit();

		if (EXTERNAL_STORAGE_ROOT_DIRECTORY.exists()) {
			StorageOperations.deleteDir(EXTERNAL_STORAGE_ROOT_DIRECTORY);
		}

		TestUtils.deleteProjects(projectName, renamedProjectName);
	}

	@Test
	public void testCopyingProjects() {
		onView(withText(R.string.import_dialog_title))
				.check(matches(isDisplayed()));
		onView(withText(R.string.import_dialog_message))
				.check(matches(isDisplayed()));

		onView(withText(R.string.import_dialog_copy_btn))
				.perform(click());

		assertTrue(FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).contains(projectName));
		assertTrue(FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).contains(renamedProjectName));
		assertTrue(new File(EXTERNAL_STORAGE_ROOT_DIRECTORY, projectName).exists());
	}

	@Test
	public void testMovingProjects() {
		onView(withText(R.string.import_dialog_title))
				.check(matches(isDisplayed()));
		onView(withText(R.string.import_dialog_message))
				.check(matches(isDisplayed()));

		onView(withText(R.string.import_dialog_move_btn))
				.perform(click());

		assertTrue(FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).contains(projectName));
		assertTrue(FileMetaDataExtractor.getProjectNames(DEFAULT_ROOT_DIRECTORY).contains(renamedProjectName));
	}
}
