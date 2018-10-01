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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.facebook.FacebookSdk;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.defaultprojectcreators.DefaultProjectCreator;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.catrobat.catroid.web.ServerCalls;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertTrue;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class UploadDefaultProgramFromProjectActivityTest {
	String token = Constants.NO_TOKEN;
	String projectName = "UploadDefaultProgramFromProjectActivityTest";
	private String testUser = "testUser" + System.currentTimeMillis();
	String testEmail = testUser + "@gmail.com";
	private String testPassword = "pwspws";
	private SharedPreferences sharedPreferences;

	@Rule
	public BaseActivityInstrumentationRule<ProjectActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProjectActivity.class, ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);

	@Before
	public void setUp() throws Exception {
		ServerCalls.useTestUrl = true;
		FacebookSdk.sdkInitialize(InstrumentationRegistry.getTargetContext());
		new DefaultProjectCreator().createDefaultProject(projectName, InstrumentationRegistry.getTargetContext(), false);

		boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail,
				"de", "at", token, InstrumentationRegistry.getTargetContext());

		assertTrue(userRegistered);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
		token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		boolean tokenOk = ServerCalls.getInstance().checkToken(token, testUser);
		assertTrue(tokenOk);
		baseActivityTestRule.launchActivity();
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(projectName);
		ServerCalls.useTestUrl = false;
	}

	@Test
	public void testUploadDefaultProgram() {
		openContextualActionModeOverflowMenu();

		onView(withText(R.string.upload_button))
				.perform(click());

		onView(withText(R.string.error_upload_default_project))
				.inRoot(withDecorView(not(is(baseActivityTestRule.getActivity().getWindow().getDecorView()))))
				.check(matches(isDisplayed()));

		onView(isRoot())
				.perform(CustomActions.wait(500));
	}
}
