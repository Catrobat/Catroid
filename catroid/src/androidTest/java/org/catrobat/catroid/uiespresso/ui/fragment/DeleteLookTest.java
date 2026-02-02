/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.ui.fragment.actionutils.ActionUtils;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.IOException;

import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.koin.java.KoinJavaComponent.inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.util.TreeIterables.breadthFirstViewTraversal;

@RunWith(AndroidJUnit4.class)
public class DeleteLookTest {

	final ProjectManager projectManager = inject(ProjectManager.class).getValue();

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_LOOKS);

	private final String toBeDeletedLookName = "testLook2";

	@Before
	public void setUp() throws IOException {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	public static ViewAction waitForView(final Matcher<View> viewMatcher, final long timeoutMs) {
		return new ViewAction() {
			@Override public Matcher<View> getConstraints() { return isRoot(); }

			@Override public String getDescription() {
				return "wait up to " + timeoutMs + "ms for view matching: " + viewMatcher;
			}

			@Override public void perform(UiController uiController, View rootView) {
				long start = System.currentTimeMillis();
				do {
					for (View child : breadthFirstViewTraversal(rootView)) {
						if (viewMatcher.matches(child) && child.isShown()) {
							return;
						}
					}
					uiController.loopMainThreadForAtLeast(50);
				} while (System.currentTimeMillis() - start < timeoutMs);

				throw new AssertionError("Timed out after " + timeoutMs + "ms waiting for: " + viewMatcher);
			}
		};
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void deleteLookMultipleElementsListTest() throws IOException {
		ActionUtils.addLook(projectManager, "testLook1");
		ActionUtils.addLook(projectManager, toBeDeletedLookName);

		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
		onView(withText(R.string.delete)).perform(click());

		onRecyclerView().atPosition(1)
				.performCheckItemClick();

		onView(withId(R.id.confirm)).perform(click());

		onView(withText(UiTestUtils.getResources().getQuantityString(R.plurals.delete_looks, 1)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(withText(R.string.dialog_confirm_delete)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
				.perform(click());

		onView(withText(toBeDeletedLookName))
				.check(doesNotExist());
	}

	@Test
	public void deleteLookSingleElementListTest() throws IOException {
		ActionUtils.addLook(projectManager, toBeDeletedLookName);

		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
		onView(withText(R.string.delete)).perform(click());

		onView(withText(toBeDeletedLookName))
				.check(doesNotExist());
	}

@Category({Cat.AppUi.class, Level.Smoke.class})
@Test
public void selectFragmentToDeleteTest() throws IOException {
	ActionUtils.addLook(projectManager, "testLook1");

	// Act: open the settings menu for the first recycler view item
	onRecyclerView().clickChildAtPosition(0, R.id.settings_button);

	onView(withText(R.string.delete)).perform(click());

	// Assert: wait until the custom dialog view exists, then verify it
	onView(isRoot()).perform(waitForView(withId(R.id.deleteDialogTitle), 2000));

	onView(withId(R.id.deleteDialogTitle))
			.inRoot(isDialog())
			.check(matches(allOf(isDisplayed(), withText(R.string.delete_look_title))));

	onView(withId(R.id.deleteDialogMessage))
			.inRoot(isDialog())
			.check(matches(isDisplayed()));

	onView(withId(R.id.deleteDialogCancel))
			.inRoot(isDialog())
			.check(matches(isDisplayed()));

	onView(withId(R.id.deleteDialogConfirm))
			.inRoot(isDialog())
			.check(matches(isDisplayed()));
}


	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void cancelDeleteLookTest() throws IOException {
		ActionUtils.addLook(projectManager, "testLook1");
		ActionUtils.addLook(projectManager, toBeDeletedLookName);

		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
		onView(withText(R.string.delete)).perform(click());

		onRecyclerView().atPosition(1)
				.performCheckItemClick();

		onView(withId(R.id.confirm)).perform(click());

		onView(withText(UiTestUtils.getResources().getQuantityString(R.plurals.delete_looks, 1)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(withText(R.string.dialog_confirm_delete)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
				.perform(click());

		onView(withText(toBeDeletedLookName))
				.check(matches(isDisplayed()));
	}

	private void createProject() {
		String projectName = "deleteLookFragmentTest";
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);

		Sprite sprite = new Sprite("testSprite");
		project.getDefaultScene().addSprite(sprite);

		projectManager.setCurrentProject(project);
		projectManager.setCurrentSprite(sprite);
		XstreamSerializer.getInstance().saveProject(project);
	}
}
