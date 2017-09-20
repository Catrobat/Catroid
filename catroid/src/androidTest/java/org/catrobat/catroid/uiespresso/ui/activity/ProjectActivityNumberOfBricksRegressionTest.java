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

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class ProjectActivityNumberOfBricksRegressionTest {

	@Rule
	public BaseActivityInstrumentationRule<ProjectActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProjectActivity.class);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);

		getInstrumentation().runOnMainSync(new Runnable() {
			public void run() {
				baseActivityTestRule.getActivity().getSpritesListFragment().setShowDetails(true);
			}
		});
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void numberOfBricksDetailsRegressionTest() throws Exception {
		onView(allOf(withId(R.id.textView_number_of_scripts), isDisplayed()))
				.check(matches(withText(UiTestUtils.getResourcesString(R.string.number_of_scripts).concat(" 2"))));

		onView(allOf(withId(R.id.textView_number_of_bricks), isDisplayed()))
				.check(matches(withText(UiTestUtils.getResourcesString(R.string.number_of_bricks).concat(" 7"))));

		onView(allOf(withId(R.id.textView_number_of_looks), isDisplayed()))
				.check(matches(withText(UiTestUtils.getResourcesString(R.string.number_of_looks).concat(" 2"))));
	}

	private void createProject() {
		Project project = new Project(null, "ProjectActivityNumberOfBricksRegressionTest");

		Sprite firstSprite = new SingleSprite("firstSprite");

		Script firstScript = new StartScript();
		firstScript.addBrick(new SetXBrick());
		firstScript.addBrick(new SetXBrick());
		firstSprite.addScript(firstScript);

		Script secondScript = new StartScript();
		secondScript.addBrick(new SetXBrick());
		secondScript.addBrick(new SetXBrick());
		secondScript.addBrick(new SetXBrick());
		firstSprite.addScript(secondScript);

		LookData lookData = new LookData();
		lookData.setLookFilename("red");
		lookData.setLookName("red");
		firstSprite.getLookDataList().add(lookData);

		LookData anotherLookData = new LookData();
		anotherLookData.setLookFilename("blue");
		anotherLookData.setLookName("blue");
		firstSprite.getLookDataList().add(anotherLookData);

		project.getDefaultScene().addSprite(firstSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
	}
}
