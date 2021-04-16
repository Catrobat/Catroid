/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.TestCase.assertEquals;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.web.assertion.WebViewAssertions.webContent;
import static androidx.test.espresso.web.matcher.DomMatchers.hasElementWithXpath;
import static androidx.test.espresso.web.sugar.Web.onWebView;

@RunWith(AndroidJUnit4.class)
public class CatblocksScriptFragmentTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		SettingsFragment.setUseCatBlocks(ApplicationProvider.getApplicationContext(), false);
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@After
	public void tearDown() {
		SettingsFragment.setUseCatBlocks(ApplicationProvider.getApplicationContext(), false);
		baseActivityTestRule.getActivity().finish();
	}

	@Test
	public void testContextMenuItems() {

		openContextualActionModeOverflowMenu();

		onView(withText(R.string.catblocks_reorder)).check(doesNotExist());

		onView(withText(R.string.catblocks)).perform(click());
		openContextualActionModeOverflowMenu();

		onView(withText(R.string.catblocks_reorder)).check(matches(isDisplayed()));

		onView(withText(R.string.undo)).check(doesNotExist());
		onView(withText(R.string.backpack)).check(doesNotExist());
		onView(withText(R.string.copy)).check(doesNotExist());
		onView(withText(R.string.delete)).check(doesNotExist());
		onView(withText(R.string.rename)).check(doesNotExist());
		onView(withText(R.string.show_details)).check(doesNotExist());
		onView(withText(R.string.comment_in_out)).check(doesNotExist());

		onView(withText(R.string.catblocks)).perform(click());

		openContextualActionModeOverflowMenu();

		onView(withText(R.string.catblocks_reorder)).check(doesNotExist());

		onView(withText(R.string.backpack)).check(matches(isDisplayed()));
		onView(withText(R.string.copy)).check(matches(isDisplayed()));
		onView(withText(R.string.comment_in_out)).check(matches(isDisplayed()));
		onView(withText(R.string.catblocks)).check(matches(isDisplayed()));
	}

	@Test
	public void testReorderScript() throws InterruptedException {
		openContextualActionModeOverflowMenu();
		onView(withText(R.string.catblocks)).perform(click());

		int loadCounter = 0;
		while (true) {
			loadCounter++;
			try {
				onWebView().check(webContent(hasElementWithXpath(
						"//*[@id=\"catroid-catblocks-container\"]/div/svg[1]/g")));

				break;
			} catch (Throwable throwable) {
				if (loadCounter >= 25) {
					throw throwable;
				}
				Thread.sleep(100);
			}
		}

		ProjectManager.getInstance().getCurrentSprite().getScript(0).setPosX(50);
		ProjectManager.getInstance().getCurrentSprite().getScript(0).setPosY(50);

		openContextualActionModeOverflowMenu();
		onView(withText(R.string.catblocks_reorder)).perform(click());

		assertEquals(ProjectManager.getInstance().getCurrentSprite().getScript(0).getPosX(), 0.0f);
		assertEquals(ProjectManager.getInstance().getCurrentSprite().getScript(0).getPosY(), 0.0f);
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

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
	}
}
