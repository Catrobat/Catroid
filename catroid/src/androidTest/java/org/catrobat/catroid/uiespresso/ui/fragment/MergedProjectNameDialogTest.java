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

package org.catrobat.catroid.uiespresso.ui.fragment;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.ProjectListActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;

@RunWith(AndroidJUnit4.class)
public class MergedProjectNameDialogTest {

	@Rule
	public BaseActivityInstrumentationRule<ProjectListActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProjectListActivity.class, true, false);

	private String firstProjectName = "firstProjectName";
	private String secondProjectName = "secondProjectName";

	@Before
	public void setUp() throws Exception {
		createProject(firstProjectName);
		createProject(secondProjectName);

		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void mergedDialogTitleTest() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

		onView(withText(R.string.merge))
				.perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItem();

		onRecyclerView().atPosition(1)
				.performCheckItem();
		onView(withId(R.id.confirm)).perform(click());

		onView(withText(R.string.merge_project)).inRoot(isDialog())
				.check(matches(isDisplayed()));
	}

	private void createProject(String projectName) {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Sprite sprite = new SingleSprite("firstSprite");

		Script script = new StartScript();
		script.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		script.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		sprite.addScript(script);

		project.getDefaultScene().addSprite(sprite);

		XstreamSerializer.getInstance().saveProject(project);
	}
}
