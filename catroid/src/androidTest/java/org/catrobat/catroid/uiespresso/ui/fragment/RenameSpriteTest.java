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
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.annotations.Flaky;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class RenameSpriteTest {

	@Rule
	public BaseActivityInstrumentationRule<ProjectActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProjectActivity.class, ProjectActivity.EXTRA_FRAGMENT_POSITION,
			ProjectActivity.FRAGMENT_SPRITES);

	private String firstSpriteName = "firstSprite";
	private String secondSpriteName = "secondSprite";

	@Before
	public void setUp() throws Exception {
		createProject("renameSpriteDialogTest");

		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	@Flaky
	public void renameSpriteDialogTest() {
		String newSpriteName = "renamedSprite";
		renameSpriteTo(newSpriteName);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void cancelRenameSpriteDialogTest() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.rename)).perform(click());

		onRecyclerView().atPosition(2)
				.performCheckItem();

		onView(withId(R.id.confirm)).perform(click());

		onView(withText(R.string.rename_sprite_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
				.perform(click());

		onView(withText(secondSpriteName))
				.check(matches(isDisplayed()));

		onView(withText(InstrumentationRegistry.getTargetContext()
				.getResources().getQuantityString(R.plurals.am_rename_sprites_title, 1, 1)))
				.check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void invalidInputRenameSoundTest() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.rename)).perform(click());

		onRecyclerView().atPosition(1)
				.performCheckItem();

		onView(withId(R.id.confirm)).perform(click());

		onView(withText(R.string.rename_sprite_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		String emptyInput = "";
		String spacesOnlyInput = "   ";

		onView(allOf(withText(firstSpriteName), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(emptyInput));
		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), not(isEnabled()))));

		onView(allOf(withText(emptyInput), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(spacesOnlyInput));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), not(isEnabled()))));

		onView(allOf(withText(spacesOnlyInput), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(secondSpriteName));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), not(isEnabled()))));

		onView(allOf(withText(secondSpriteName), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText("newSpriteName"));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), isEnabled())));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	@Flaky
	public void renameSpriteSwitchCaseDialogTest() {
		String newSpriteName = "SeConDspRite";
		renameSpriteTo(newSpriteName);
	}

	private void renameSpriteTo(String newSpriteName) {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.rename)).perform(click());

		onRecyclerView().atPosition(2)
				.performCheckItem();

		onView(withId(R.id.confirm)).perform(click());

		onView(withText(R.string.rename_sprite_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));
		onView(allOf(withText(secondSpriteName), isDisplayed()))
				.perform(replaceText(newSpriteName));

		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.perform(click());

		onView(withText(newSpriteName))
				.check(matches(isDisplayed()));
		onView(withText(secondSpriteName))
				.check(doesNotExist());
	}

	private void createProject(String projectName) {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);

		Sprite firstSprite = new SingleSprite(firstSpriteName);
		Sprite secondSprite = new SingleSprite(secondSpriteName);

		project.getDefaultScene().addSprite(firstSprite);
		project.getDefaultScene().addSprite(secondSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
	}
}
