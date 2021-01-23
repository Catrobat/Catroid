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

import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.uiespresso.ui.actionbar.utils.ActionModeWrapper.onActionMode;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.openActionBar;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(AndroidJUnit4.class)
public class RenameLookTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_LOOKS);

	private String oldLookName = "oldLookName";
	private String newLookName = "newLookName";
	private String secondLookName = "secondLookName";

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@Test
	public void renameLookTest() {
		openActionBar();
		onView(withText(R.string.rename)).perform(click());

		onRecyclerView().atPosition(0)
				.perform(click());

		onView(withText(R.string.rename_look_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(allOf(withText(oldLookName), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(newLookName));
		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.perform(click());

		onView(withText(newLookName)).check(matches(isDisplayed()));
	}

	@Test
	public void cancelRenameLookTest() {
		openActionBar();
		onView(withText(R.string.rename)).perform(click());

		onRecyclerView().atPosition(0)
				.perform(click());

		onView(withText(R.string.rename_look_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));
		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
				.perform(click());

		onView(withText(oldLookName)).check(matches(isDisplayed()));
	}

	@Test
	public void invalidInputRenameLookTest() {
		openActionBar();
		onView(withText(R.string.rename)).perform(click());

		onRecyclerView().atPosition(0)
				.perform(click());

		onView(withText(R.string.rename_look_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		String emptyInput = "";
		String spacesOnlyInput = "   ";

		onView(allOf(withText(oldLookName), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(emptyInput));
		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), not(isEnabled()))));

		onView(allOf(withText(emptyInput), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(spacesOnlyInput));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), not(isEnabled()))));

		onView(allOf(withText(spacesOnlyInput), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(secondLookName));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), not(isEnabled()))));

		onView(allOf(withText(secondLookName), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(newLookName));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), isEnabled())));
	}

	@Test
	public void renameSingleLookTest() {
		openActionBar();
		onView(withText(R.string.delete)).perform(click());

		onRecyclerView().atPosition(1).performCheckItem();

		onActionMode().performConfirm();

		onView(withText(R.string.yes)).perform(click());

		openActionBar();
		onView(withText(R.string.rename)).perform(click());

		onView(withText(R.string.rename_look_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));
	}

	private void createProject() throws IOException {
		String projectName = "renameLookFragmentTest";
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);

		Sprite sprite = new Sprite("testSprite");
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		XstreamSerializer.getInstance().saveProject(project);

		File imageFile0 = ResourceImporter.createImageFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.drawable.catroid_banzai,
				new File(project.getDefaultScene().getDirectory(), IMAGE_DIRECTORY_NAME),
				"catroid_sunglasses.png",
				1);

		List<LookData> lookDataList = ProjectManager.getInstance().getCurrentSprite().getLookList();
		LookData lookData0 = new LookData();
		lookData0.setFile(imageFile0);
		lookData0.setName(oldLookName);
		lookDataList.add(lookData0);

		File imageFile1 = StorageOperations.duplicateFile(imageFile0);
		LookData lookData1 = new LookData();
		lookData1.setFile(imageFile1);
		lookData1.setName(secondLookName);
		lookDataList.add(lookData1);
	}
}
