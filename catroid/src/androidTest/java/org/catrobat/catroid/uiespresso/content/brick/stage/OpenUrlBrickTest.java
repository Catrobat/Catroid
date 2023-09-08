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

package org.catrobat.catroid.uiespresso.content.brick.stage;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.OpenUrlBrick;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.ui.fragment.FormulaEditorFragment.DO_NOT_SHOW_WARNING;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.hamcrest.Matchers.allOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class OpenUrlBrickTest {

	private int openUrlBrickPosition;
	private final String url = BrickValues.OPEN_IN_BROWSER;
	private Matcher expectedIntent;
	private final String projectName = "openUrlBrickTest";
	private boolean bufferedWarningPreferenceSetting = false;
	private final Context applicationContext = ApplicationProvider.getApplicationContext();

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() {
		bufferedWarningPreferenceSetting = PreferenceManager
			.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
			.getBoolean(DO_NOT_SHOW_WARNING, false);

		PreferenceManager.getDefaultSharedPreferences(applicationContext.getApplicationContext())
			.edit()
			.putBoolean(
				DO_NOT_SHOW_WARNING,
				true
			)
			.commit();

		openUrlBrickPosition = 1;
		Script script = UiTestUtils.createProjectAndGetStartScript(projectName);
		script.addBrick(new OpenUrlBrick());
		baseActivityTestRule.launchActivity(new Intent());
		Intents.init();

		expectedIntent = allOf(hasAction(Intent.ACTION_VIEW), hasData(url));

		Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, null);
		intending(expectedIntent).respondWith(result);
	}

	@Test
	public void testOpenUrlIntent() {
		onBrickAtPosition(openUrlBrickPosition).onFormulaTextField(R.id.brick_open_url_edit_text).performEnterString(url);
		pressBack();
		onView(withId(R.id.button_play)).perform(click());
		onView(isRoot()).perform(CustomActions.wait(2000));
		intended(expectedIntent);
	}

	@After
	public void tearDown() {
		PreferenceManager.getDefaultSharedPreferences(applicationContext)
			.edit()
			.putBoolean(
				DO_NOT_SHOW_WARNING,
				bufferedWarningPreferenceSetting
			)
			.commit();

		Intents.release();
		baseActivityTestRule.finishActivity();
		try {
			StorageOperations.deleteDir(new File(DEFAULT_ROOT_DIRECTORY, projectName));
		} catch (IOException e) {
			Log.d(getClass().getSimpleName(), "Cannot delete test project in tear down.");
		}
	}
}
