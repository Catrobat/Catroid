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

package org.catrobat.catroid.uiespresso.ui.fragment;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.UserDefinedScript;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.DataInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class CopyBrickTest {

	int firstScriptEndIndex = 7;
	int secondScriptEndIndex = 9;
	int firstIndexComposite = 1;
	int lastIndexComposite = 5;
	int userDefinedScriptIndex = 10;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@After
	public void tearDown() throws IOException {
		baseActivityTestRule.getActivity().finish();
		TestUtils.deleteProjects(CopyBrickTest.class.getSimpleName());
	}

	@Test
	public void testNoItemSelected() {
		openContextualActionModeOverflowMenu();
		onView(withText(R.string.copy))
				.check(matches(isDisplayed()));

		onView(withText(R.string.copy))
				.perform(click());

		for (int brickIndex = 0; brickIndex <= secondScriptEndIndex; brickIndex++) {
			getCheckbox(brickIndex)
					.check(matches(isEnabled()))
					.check(matches(not(isChecked())));
		}
	}

	@Test
	public void testSelectWholeScript() {
		openContextualActionModeOverflowMenu();
		onView(withText(R.string.copy)).perform(click());

		int brickIndex = 0;
		getCheckbox(brickIndex)
				.perform(click())
				.check(matches(isChecked()));

		for (brickIndex++; brickIndex <= firstScriptEndIndex; brickIndex++) {
			getCheckbox(brickIndex)
					.check(matches(not(isEnabled())))
					.check(matches(isChecked()));
		}
		for (; brickIndex <= secondScriptEndIndex; brickIndex++) {
			getCheckbox(brickIndex)
					.check(matches(not(isEnabled())))
					.check(matches(not(isChecked())));
		}
	}

	@Test
	public void testSelectSingleBrick() {
		openContextualActionModeOverflowMenu();
		onView(withText(R.string.copy)).perform(click());

		int brickIndex = 6;
		getCheckbox(brickIndex)
				.perform(click())
				.check(matches(isChecked()))
				.check(matches(isEnabled()));

		getCheckbox(brickIndex - 1)
				.check(matches(isEnabled()))
				.check(matches(not(isChecked())));

		getCheckbox(brickIndex + 1)
				.check(matches(isEnabled()))
				.check(matches(not(isChecked())));

		getCheckbox(brickIndex)
				.perform(click())
				.check(matches(not(isChecked())))
				.check(matches(isEnabled()));

		getCheckbox(brickIndex + 1)
				.perform(click())
				.check(matches(isChecked()))
				.check(matches(isEnabled()));

		getCheckbox(brickIndex + 2)
				.perform(click())
				.check(matches(not(isChecked())))
				.check(matches(not(isEnabled())));
	}

	@Test
	public void testSelectCompositeBrick() {
		openContextualActionModeOverflowMenu();
		onView(withText(R.string.copy)).perform(click());

		getCheckbox(lastIndexComposite)
				.perform(click())
				.check(matches(isChecked()))
				.check(matches(isEnabled()));

		getCheckbox(firstIndexComposite)
				.check(matches(isChecked()))
				.check(matches(isEnabled()));

		for (int brickIndex = firstIndexComposite + 1; brickIndex < lastIndexComposite; brickIndex++) {
			getCheckbox(brickIndex)
					.check(matches(isChecked()))
					.check(matches(not(isEnabled())));
		}

		getCheckbox(firstIndexComposite - 1)
				.check(matches(isEnabled()))
				.check(matches(not(isChecked())));
		getCheckbox(lastIndexComposite + 1)
				.check(matches(isEnabled()))
				.check(matches(not(isChecked())));
	}

	@Test
	public void testUserDefinedScriptNotEnabled() {
		openContextualActionModeOverflowMenu();
		onView(withText(R.string.copy)).perform(click());

		getCheckbox(userDefinedScriptIndex)
				.check(matches(not(isEnabled())))
				.check(matches(not(isChecked())));

		getCheckbox(userDefinedScriptIndex + 1)
				.perform(click());

		getCheckbox(userDefinedScriptIndex)
				.check(matches(not(isEnabled())))
				.check(matches(not(isChecked())));
	}

	private DataInteraction getCheckbox(int brickIndex) {
		return onBrickAtPosition(brickIndex).onChildView(allOf(withId(R.id.brick_checkbox), isDisplayed()));
	}

	private void createProject() {
		Project project = new Project(ApplicationProvider.getApplicationContext(), CopyBrickTest.class.getSimpleName());

		Sprite sprite = new Sprite("testSprite");
		project.getDefaultScene().addSprite(sprite);

		Script startScript = new StartScript();
		IfLogicBeginBrick ifBrick = new IfLogicBeginBrick();
		ifBrick.addBrickToIfBranch(new SetXBrick());
		ifBrick.addBrickToElseBranch(new ChangeXByNBrick());
		startScript.addBrick(ifBrick);
		startScript.addBrick(new SetVariableBrick());
		startScript.addBrick(new GlideToBrick());
		startScript.setParents();

		Script secondScript = new StartScript();
		SetXBrick setXBrick = new SetXBrick();
		secondScript.addBrick(setXBrick);
		secondScript.setParents();

		UserDefinedBrick userDefinedBrick = new UserDefinedBrick();
		UserDefinedScript thirdScript = new UserDefinedScript();
		thirdScript.addBrick(new SetXBrick());
		thirdScript.setScriptBrick(new UserDefinedReceiverBrick(userDefinedBrick));

		sprite.addScript(startScript);
		sprite.addScript(secondScript);
		sprite.addScript(thirdScript);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
	}
}
