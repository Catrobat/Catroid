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
package org.catrobat.catroid.uiespresso.ui.fragment;

import android.widget.EditText;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectListActivity;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MergeProjectNameTest {
	static final String FIRSTPROJECTNAME = "firstProjectName";
	static final String SECONDPROJECTNAMW = "secondProjectName";
	static final String VALIDNAME = "ValidName";
	static final String INVALIDNAME = FIRSTPROJECTNAME;

	@Rule
	public BaseActivityTestRule<ProjectListActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(ProjectListActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject(FIRSTPROJECTNAME);
		createProject(SECONDPROJECTNAMW);

		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void mergeProjectsValidNameTests() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.merge)).perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItemClick();

		onRecyclerView().atPosition(1)
				.performCheckItemClick();

		onView(withId(R.id.confirm)).perform(click());

		onView(allOf(withText(""), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(VALIDNAME));

		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(Matchers.allOf(isDisplayed(), isEnabled())));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void mergeProjectsInvalidSameNameTests() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.merge)).perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItemClick();

		onRecyclerView().atPosition(1)
				.performCheckItemClick();

		onView(withId(R.id.confirm)).perform(click());

		onView(allOf(withText(""), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(INVALIDNAME));

		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(Matchers.allOf(isDisplayed(), not(isEnabled()))));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void mergeProjectsInvalidEmptyNameTests() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.merge)).perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItemClick();

		onRecyclerView().atPosition(1)
				.performCheckItemClick();

		onView(withId(R.id.confirm)).perform(click());

		onView(withId(R.id.input_edit_text)).perform(replaceText(" "));

		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(Matchers.allOf(isDisplayed(), not(isEnabled()))));
	}

	private void createProject(String projectName) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		Sprite sprite = new Sprite("firstSprite");

		Script script = new StartScript();
		script.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		script.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		sprite.addScript(script);

		project.getDefaultScene().addSprite(sprite);

		XstreamSerializer.getInstance().saveProject(project);
	}
}
