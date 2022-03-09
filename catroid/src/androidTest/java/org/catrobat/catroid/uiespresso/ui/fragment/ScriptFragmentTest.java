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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.SetYBrick;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.BrickCategoryListMatchers;
import org.catrobat.catroid.uiespresso.util.matchers.BrickPrototypeListMatchers;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.TestCase.assertTrue;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.koin.java.KoinJavaComponent.inject;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ScriptFragmentTest {

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
	public void tearDown() {
		baseActivityTestRule.getActivity().finish();
	}

	@Test
	public void testCancelInsertingBrickOnBackPressed() {
		onView(withId(R.id.button_add))
				.perform(click());
		onData(allOf(is(instanceOf(String.class)), is(UiTestUtils.getResourcesString(R.string.category_motion))))
				.inAdapterView(BrickCategoryListMatchers.isBrickCategoryView())
				.perform(click());
		onData(is(instanceOf(SetYBrick.class))).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.perform(click());

		boolean isInsertedBrickHovering = ((ScriptFragment) baseActivityTestRule.getActivity()
				.getSupportFragmentManager().findFragmentByTag(ScriptFragment.TAG)).isCurrentlyMoving();

		assertTrue(isInsertedBrickHovering);

		pressBack();

		onView(withText(R.string.brick_set_y))
				.check(doesNotExist());
	}

	@Test
	public void testHighlightInsertingBrickOnHomeButtonPressed() {
		onView(withId(R.id.button_add))
				.perform(click());
		onData(allOf(is(instanceOf(String.class)), is(UiTestUtils.getResourcesString(R.string.category_motion))))
				.inAdapterView(BrickCategoryListMatchers.isBrickCategoryView())
				.perform(click());
		onData(is(instanceOf(SetYBrick.class))).inAdapterView(BrickPrototypeListMatchers.isBrickPrototypeView())
				.perform(click());

		boolean isInsertedBrickHovering = ((ScriptFragment) baseActivityTestRule.getActivity()
				.getSupportFragmentManager().findFragmentByTag(ScriptFragment.TAG)).isCurrentlyMoving();

		assertTrue(isInsertedBrickHovering);

		onView(withContentDescription(R.string.abc_action_bar_up_description))
				.perform(click());

		isInsertedBrickHovering = ((ScriptFragment) baseActivityTestRule.getActivity()
				.getSupportFragmentManager().findFragmentByTag(ScriptFragment.TAG)).isCurrentlyMoving();

		assertTrue(isInsertedBrickHovering);

		pressBack();
	}

	@Test
	public void testCommentOutScript() {
		onBrickAtPosition(0)
				.performClick();
		onView(withText(R.string.brick_context_dialog_comment_out_script))
				.perform(click());

		openContextualActionModeOverflowMenu();
		onView(withText(R.string.comment_in_out))
				.perform(click());

		onBrickAtPosition(0)
				.onCheckBox().check(matches(allOf(isChecked(), isEnabled())));
		onBrickAtPosition(1)
				.onCheckBox().check(matches(allOf(isChecked(), not(isEnabled()))));
		onBrickAtPosition(2)
				.onCheckBox().check(matches(allOf(isChecked(), not(isEnabled()))));
		onBrickAtPosition(3)
				.onCheckBox().check(matches(allOf(isChecked(), not(isEnabled()))));
		onBrickAtPosition(4)
				.onCheckBox().check(matches(allOf(isChecked(), not(isEnabled()))));
		onBrickAtPosition(5)
				.onCheckBox().check(matches(allOf(isChecked(), not(isEnabled()))));
	}

	@Test
	public void testCheckControlStructure() {
		openContextualActionModeOverflowMenu();
		onView(withText(R.string.comment_in_out))
				.perform(click());

		onBrickAtPosition(1)
				.checkShowsText(R.string.brick_if_begin)
				.checkShowsText(R.string.brick_if_begin_second_part);

		onBrickAtPosition(1)
				.onCheckBox().perform(click());

		onBrickAtPosition(0)
				.onCheckBox().check(matches(allOf(not(isChecked()), isEnabled())));
		onBrickAtPosition(1)
				.onCheckBox().check(matches(allOf(isChecked(), isEnabled())));
		onBrickAtPosition(2)
				.onCheckBox().check(matches(allOf(isChecked(), not(isEnabled()))));
		onBrickAtPosition(3)
				.onCheckBox().check(matches(allOf(isChecked(), not(isEnabled()))));
		onBrickAtPosition(4)
				.onCheckBox().check(matches(allOf(isChecked(), not(isEnabled()))));
		onBrickAtPosition(5)
				.onCheckBox().check(matches(allOf(isChecked(), not(isEnabled()))));

		onBrickAtPosition(1)
				.onCheckBox().perform(click());

		onBrickAtPosition(0)
				.onCheckBox().check(matches(allOf(not(isChecked()), isEnabled())));
		onBrickAtPosition(1)
				.onCheckBox().check(matches(allOf(not(isChecked()), isEnabled())));
		onBrickAtPosition(2)
				.onCheckBox().check(matches(allOf(not(isChecked()), isEnabled())));
		onBrickAtPosition(3)
				.onCheckBox().check(matches(allOf(not(isChecked()), isEnabled())));
		onBrickAtPosition(4)
				.onCheckBox().check(matches(allOf(not(isChecked()), isEnabled())));
		onBrickAtPosition(5)
				.onCheckBox().check(matches(allOf(not(isChecked()), isEnabled())));

		onBrickAtPosition(5)
				.checkShowsText(R.string.brick_if_end);

		onBrickAtPosition(5)
				.onCheckBox().perform(click());

		onBrickAtPosition(0)
				.onCheckBox().check(matches(allOf(not(isChecked()), isEnabled())));
		onBrickAtPosition(1)
				.onCheckBox().check(matches(allOf(isChecked(), isEnabled())));
		onBrickAtPosition(2)
				.onCheckBox().check(matches(allOf(isChecked(), not(isEnabled()))));
		onBrickAtPosition(3)
				.onCheckBox().check(matches(allOf(isChecked(), not(isEnabled()))));
		onBrickAtPosition(4)
				.onCheckBox().check(matches(allOf(isChecked(), not(isEnabled()))));
		onBrickAtPosition(5)
				.onCheckBox().check(matches(allOf(isChecked(), not(isEnabled()))));

		onBrickAtPosition(1)
				.onCheckBox().perform(click());

		onBrickAtPosition(0)
				.onCheckBox().check(matches(allOf(not(isChecked()), isEnabled())));
		onBrickAtPosition(1)
				.onCheckBox().check(matches(allOf(not(isChecked()), isEnabled())));
		onBrickAtPosition(2)
				.onCheckBox().check(matches(allOf(not(isChecked()), isEnabled())));
		onBrickAtPosition(3)
				.onCheckBox().check(matches(allOf(not(isChecked()), isEnabled())));
		onBrickAtPosition(4)
				.onCheckBox().check(matches(allOf(not(isChecked()), isEnabled())));
		onBrickAtPosition(5)
				.onCheckBox().check(matches(allOf(not(isChecked()), isEnabled())));
	}

	private void createProject() {
		String projectName = getClass().getSimpleName();
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);

		Sprite sprite = new Sprite("testSprite");
		project.getDefaultScene().addSprite(sprite);

		Script startScript = new StartScript();
		IfLogicBeginBrick ifBrick = new IfLogicBeginBrick();
		ifBrick.addBrickToIfBranch(new SetXBrick());
		ifBrick.addBrickToElseBranch(new ChangeXByNBrick());
		startScript.addBrick(ifBrick);
		startScript.setParents();

		sprite.addScript(startScript);

		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		projectManager.setCurrentProject(project);
		projectManager.setCurrentSprite(sprite);
	}
}
