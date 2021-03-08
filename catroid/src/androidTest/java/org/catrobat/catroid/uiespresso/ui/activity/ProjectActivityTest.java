/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ProjectUploadActivity;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.catroid.uiespresso.util.UiTestUtils.openActionBar;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ProjectActivityTest {

	private static final String PROJECT_NAME = "projectName";

	@Rule
	public FragmentActivityTestRule<ProjectActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(ProjectActivity.class,
			ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);

	@Before
	public void setUp() {
		Project project = new Project(ApplicationProvider.getApplicationContext(), PROJECT_NAME);
		Sprite firstSprite = new Sprite("firstSprite");
		project.getDefaultScene().addSprite(firstSprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		Intents.init();
	}

	@After
	public void tearDown() throws Exception {
		Intents.release();
		TestUtils.deleteProjects(PROJECT_NAME);
		ProjectManager.getInstance().setCurrentProject(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testLastUsedProjectPreference() {
		getDefaultSharedPreferences().edit()
				.remove(Constants.PREF_PROJECTNAME_KEY)
				.apply();

		baseActivityTestRule.launchActivity();
		baseActivityTestRule.getActivity().finish();
		InstrumentationRegistry.getInstrumentation().waitForIdleSync();

		String prefProjectName = getDefaultSharedPreferences()
				.getString(Constants.PREF_PROJECTNAME_KEY, null);
		assertEquals(PROJECT_NAME, prefProjectName);
	}

	@Test
	public void testUploadActivityLaunchedWhenUploadButtonClicked() {
		Intent intent = new Intent();
		Instrumentation.ActivityResult intentResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);

		baseActivityTestRule.launchActivity();
		openActionBar();

		intending(anyIntent()).respondWith(intentResult);

		onView(withText(R.string.upload_button)).perform(click());
		intended(allOf(hasComponent(ProjectUploadActivity.class.getName())));
	}

	@Test
	public void projectNotSavedOnReloadFromUploadActivityTest() {
		baseActivityTestRule.launchActivity();
		openActionBar();
		onView(withText(R.string.upload_button)).perform(click());
		pressBack();

		Sprite sprite = new Sprite("nextSprite");
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		currentScene.addSprite(sprite);

		assertTrue(ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().contains(sprite));
	}

	private SharedPreferences getDefaultSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext());
	}
}
