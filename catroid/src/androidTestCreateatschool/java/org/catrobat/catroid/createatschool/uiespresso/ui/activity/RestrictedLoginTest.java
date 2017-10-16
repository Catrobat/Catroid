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

package org.catrobat.catroid.createatschool.uiespresso.ui.activity;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.dialogs.PrivacyPolicyDialogFragment;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class RestrictedLoginTest {

	private static final String VALID_FAKE_TOKEN = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
	private static final String INVALID_FAKE_TOKEN = "x";

	@Rule
	public BaseActivityInstrumentationRule<MainMenuActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(MainMenuActivity.class, true, false);

	private List<String> allSettings = new ArrayList<>(Arrays.asList(Constants.RESTRICTED_LOGIN_ACCEPTED, Constants.CREATE_AT_SCHOOL_USER));
	private Map<String, Boolean> initialSettings = new HashMap<>();
	private boolean privacyPolicyInitialValue = PrivacyPolicyDialogFragment.userHasAcceptedPrivacyPolicy(InstrumentationRegistry.getTargetContext());
	private String initialTokenSetting;
	private IdlingResource idlingResource;

	@Before
	public void setUp() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());

		for (String setting : allSettings) {
			initialSettings.put(setting, sharedPreferences.getBoolean(setting, false));
		}
		initialTokenSetting = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		privacyPolicyInitialValue = PrivacyPolicyDialogFragment.userHasAcceptedPrivacyPolicy(InstrumentationRegistry.getTargetContext());

		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(Constants.RESTRICTED_LOGIN_ACCEPTED, false);
		editor.putBoolean(Constants.CREATE_AT_SCHOOL_USER, false);
		editor.apply();

		Reflection.invokeMethod(PrivacyPolicyDialogFragment.class, null, "setUserHasAcceptedPrivacyPolicy", InstrumentationRegistry.getTargetContext(), false);

		ServerCalls.useTestUrl = true;

		baseActivityTestRule.launchActivity(null);

		idlingResource = baseActivityTestRule.getActivity().getIdlingResource();
		Espresso.registerIdlingResources(idlingResource);

		setFakeToken(baseActivityTestRule.getActivity(), false);
	}

	@Test
	public void loginDialogsUiBehaviorTest() {
		onView(withText(R.string.dialog_restricted_login_title))
				.check(matches(isDisplayed()));
		onView(withText(R.string.cancel))
				.check(doesNotExist());
		pressBack();

		onView(withText(R.string.dialog_restricted_login_title))
				.check(matches(isDisplayed()));

		onView(withId(android.R.id.button1))
				.perform(click());

		onView(withText(R.string.agree))
				.check(doesNotExist());

		onView(withText(R.string.disagree))
				.check(doesNotExist());

		onView(withText(R.string.dialog_restricted_login_policy_text))
				.check(matches(isDisplayed()));

		pressBack();

		onView(withText(R.string.dialog_restricted_login_policy_text))
				.check(matches(isDisplayed()));

		onView(withText(R.string.ok))
				.perform(click());

		onView(allOf(withText(R.string.login), withResourceName("alertTitle")))
				.check(matches(isDisplayed()));
		onView(withText(R.string.password_forgotten))
				.check(doesNotExist());
		onView(withText(R.string.cancel))
				.check(doesNotExist());

		pressBack();

		onView(allOf(withText(R.string.login), withResourceName("alertTitle")))
				.check(matches(isDisplayed()));
	}

	@Test
	public void restrictedLoginDialogLinkTest() {
		String link = Locale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage())
				? UiTestUtils.getResourcesString(R.string.schoolreg_link_german)
				: UiTestUtils.getResourcesString(R.string.schoolreg_link);

		Intents.init();
		Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_VIEW), hasData(link));
		intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
		Espresso.onView(ViewMatchers.withId(R.id.dialog_restricted_login_text))
				.perform(ViewActions.openLinkWithText(link));
		intended(expectedIntent);
		Intents.release();
	}

	@Test
	public void restrictedLoginDialogNotShownAfterConfirmationTest() {
		onView(withId(android.R.id.button1))
				.perform(click());

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseActivityTestRule.getActivity());
		assertTrue("Token has been set but shouldn't be!", sharedPreferences.getBoolean(Constants.RESTRICTED_LOGIN_ACCEPTED, false));
	}

	@Test
	public void loginDialogAppearsAfterLogoutTest() {
		setFakeToken(baseActivityTestRule.getActivity(), true);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseActivityTestRule.getActivity());
		sharedPreferences.edit().putBoolean(Constants.CREATE_AT_SCHOOL_USER, true).apply();

		onView(withId(android.R.id.button1))
				.perform(click());

		onView(withText(R.string.dialog_restricted_login_title))
				.check(doesNotExist());
		onView(allOf(withText(R.string.login), withResourceName("alertTitle")))
				.check(doesNotExist());

		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
		onView(withText(R.string.main_menu_logout))
				.perform(click());

		onView(withText(R.string.ok))
				.perform(click());

		onView(allOf(withText(R.string.login), withResourceName("alertTitle")))
				.check(matches(isDisplayed()));
	}

	@Test
	public void loginWithOtherThanCreateAtSchoolUserTest() throws WebconnectionException {
		String testUserName = "testUser" + System.currentTimeMillis();
		String testPassword = "password";
		String testUserMail = testUserName + "@catrob.at";
		createValidUserWithCredentials(testUserName, testPassword, testUserMail);

		onView(withId(android.R.id.button1))
				.perform(click());

		onView(withText(R.string.ok))
				.perform(click());

		onView(withId(R.id.dialog_login_username))
				.perform(clearText(), typeText(testUserName), closeSoftKeyboard());

		onView(withId(R.id.dialog_login_password))
				.perform(clearText(), typeText(testPassword), closeSoftKeyboard());

		onView(allOf(withText(R.string.login), withResourceName("button1")))
				.perform(click());

		onView(withText(R.string.error_no_nolb_user))
				.check(matches(isDisplayed()));

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseActivityTestRule.getActivity());
		assertEquals("Token has been set but shouldn't be!", Constants.NO_TOKEN, sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN));
		assertEquals("Username has been set but shouldn't be!", Constants.NO_USERNAME, sharedPreferences.getString(Constants.USERNAME, Constants.NO_USERNAME));
	}

	private void setFakeToken(Context context, boolean valid) {
		String token = valid ? VALID_FAKE_TOKEN : INVALID_FAKE_TOKEN;
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences.edit().putString(Constants.TOKEN, token).apply();
	}

	private void createValidUserWithCredentials(String testUser, String testPassword, String testEmail) throws WebconnectionException {
		boolean userRegistered = ServerCalls.getInstance().register(testUser, testPassword, testEmail, "de", "at", Constants.NO_TOKEN, InstrumentationRegistry.getTargetContext());
		assertTrue("User has not been registered", userRegistered);
	}

	@After
	public void tearDown() {
		SharedPreferences.Editor sharedPreferencesEditor = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext()).edit();
		for (String setting : initialSettings.keySet()) {
			sharedPreferencesEditor.putBoolean(setting, initialSettings.get(setting));
		}
		sharedPreferencesEditor.putString(Constants.TOKEN, initialTokenSetting);
		sharedPreferencesEditor.apply();

		Reflection.invokeMethod(PrivacyPolicyDialogFragment.class, null, "setUserHasAcceptedPrivacyPolicy", InstrumentationRegistry.getTargetContext(), privacyPolicyInitialValue);

		Espresso.unregisterIdlingResources(idlingResource);

		ServerCalls.useTestUrl = false;
	}
}
