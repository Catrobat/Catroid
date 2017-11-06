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

package org.catrobat.catroid.uiespresso.ui.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.TemplateData;
import org.catrobat.catroid.ui.BaseSettingsActivity;
import org.catrobat.catroid.ui.TemplateActivity;
import org.catrobat.catroid.ui.fragment.TemplatesFragment;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.mocks.MockWebComponent;
import org.catrobat.catroid.uiespresso.util.mocks.MockWebRequestModule;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.catrobat.catroid.utils.SnackbarUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.fail;

import static org.catrobat.catroid.uiespresso.util.matchers.TemplateListMatchers.isTemplateDataListView;
import static org.catrobat.catroid.uiespresso.util.matchers.TemplateListMatchers.withTemplateName;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class TemplateActivityTest {

	private static final String TAG = TemplateActivityTest.class.getSimpleName();

	private List<String> templateNames = new ArrayList<>(Arrays.asList("Action", "Adventure", "Puzzle", "Quiz",
			"Action platform", "Action shooter", "Interactive book", "Physical simulation", "Lego EV3", "Lego NXT",
			"Race simulation", "Adventure RPG", "Life simulation", "Strategy"));

	private static final String PORTRAIT_ONLY_TEMPLATE = "Action";
	private static final String LANDSCAPE_ONLY_TEMPLATE = "Adventure";
	private static final String PORTRAIT_AND_LANDSCAPE_TEMPLATE = "Puzzle";

	private Set<String> hintListToRestore;
	private boolean hintSettingToRestore;

	@Rule
	public BaseActivityInstrumentationRule<TemplateActivity> activityTestRule = new
			BaseActivityInstrumentationRule<>(TemplateActivity.class, true, false);

	@Rule
	public DaggerMockRule<MockWebComponent> daggerRule = new
			DaggerMockRule<>(MockWebComponent.class, new MockWebRequestModule());

	@Before
	public void setUp() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());

		hintListToRestore = sharedPreferences.getStringSet(SnackbarUtil.SHOWN_HINT_LIST, new HashSet<String>());
		hintSettingToRestore = sharedPreferences.getBoolean(BaseSettingsActivity.SETTINGS_SHOW_HINTS, false);

		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putStringSet(SnackbarUtil.SHOWN_HINT_LIST, new HashSet<String>());
		editor.putBoolean(BaseSettingsActivity.SETTINGS_SHOW_HINTS, true);

		editor.apply();

		activityTestRule.launchActivity(null);

		daggerRule.set(new DaggerMockRule.ComponentSetter<MockWebComponent>() {
			@Override
			public void setComponent(final MockWebComponent component) {
				final TemplatesFragment templatesFragment = (TemplatesFragment) activityTestRule.getActivity().getFragmentManager()
						.findFragmentById(R.id.fragment_templates_list);

				InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
					@Override
					public void run() {
						templatesFragment.getTemplateAdapter().setWebComponent(component);
					}
				});
			}
		});

		daggerRule.initMocks(this);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void templatesCorrectlyShownTest() {
		for (String template : templateNames) {
			onTemplateWithName(template)
					.check(matches(isDisplayed()));
		}
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void ratingAndSettingsMenuHiddenTest() {
		try {
			openActionBarOverflowOrOptionsMenu(activityTestRule.getActivity());
			fail("Overflow menu present, but it should be hidden!");
		} catch (NoMatchingViewException e) {
			Log.d(TAG, "This is expected behavior. The overflow menu should not be present.");
		}
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void landscapeTemplateTest() {
		onTemplateWithName(LANDSCAPE_ONLY_TEMPLATE)
				.perform(click());

		fillNewProjectDialog();

		onView(withId(R.id.landscape_mode))
				.check(matches(allOf(isEnabled(), isChecked())));

		onView(withId(R.id.portrait))
				.check(matches(allOf(not(isEnabled()), not(isChecked()))));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void portraitTemplateTest() {
		onTemplateWithName(PORTRAIT_ONLY_TEMPLATE)
				.perform(click());

		fillNewProjectDialog();

		onView(withId(R.id.portrait))
				.check(matches(allOf(isEnabled(), isChecked())));

		onView(withId(R.id.landscape_mode))
				.check(matches(allOf(not(isEnabled()), not(isChecked()))));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void portraitAndLandscapeTemplateTest() {
		onTemplateWithName(PORTRAIT_AND_LANDSCAPE_TEMPLATE)
				.perform(click());

		fillNewProjectDialog();

		onView(withId(R.id.portrait))
				.check(matches(allOf(isEnabled(), isChecked())));

		onView(withId(R.id.landscape_mode))
				.check(matches(allOf(isEnabled(), not(isChecked()))));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void snackBarHintTest() {
		onView(withText(R.string.hint_templates))
				.check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
	}

	@After
	public void tearDown() {
		PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext())
				.edit()
				.putStringSet(SnackbarUtil.SHOWN_HINT_LIST, hintListToRestore)
				.putBoolean(BaseSettingsActivity.SETTINGS_SHOW_HINTS, hintSettingToRestore)
				.apply();
	}

	private void fillNewProjectDialog() {
		onView(withId(R.id.project_name_edittext))
				.perform(clearText(), typeText("testproject" + System.currentTimeMillis()), closeSoftKeyboard());

		onView(withText(R.string.ok))
				.perform(click());
	}

	private DataInteraction onTemplateWithName(String name) {
		return onData(allOf(instanceOf(TemplateData.class), withTemplateName(name)))
				.inAdapterView(isTemplateDataListView());
	}
}
