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
import org.catrobat.catroid.common.SoundInfo;
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

import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
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

@RunWith(AndroidJUnit4.class)
public class RenameSoundTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SOUNDS);

	private String oldSoundName = "oldSoundName";
	private String newSoundName = "newSoundName";
	private String secondSoundName = "secondSoundName";

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void renameSoundTest() {
		openActionBar();
		onView(withText(R.string.rename)).perform(click());

		onRecyclerView().atPosition(0)
				.perform(click());

		onView(withText(R.string.rename_sound_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(allOf(withText(oldSoundName), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(newSoundName));
		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.perform(click());

		onView(withText(newSoundName)).check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void cancelRenameSoundTest() {
		openActionBar();
		onView(withText(R.string.rename)).perform(click());

		onRecyclerView().atPosition(0)
				.perform(click());

		onView(withText(R.string.rename_sound_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
				.perform(click());

		onView(withText(oldSoundName)).check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void invalidInputRenameSoundTest() {
		openActionBar();
		onView(withText(R.string.rename)).perform(click());

		onRecyclerView().atPosition(0)
				.perform(click());

		onView(withText(R.string.rename_sound_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		String emptyInput = "";
		String spacesOnlyInput = "   ";

		onView(allOf(withText(oldSoundName), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(emptyInput));
		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), not(isEnabled()))));

		onView(allOf(withText(emptyInput), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(spacesOnlyInput));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), not(isEnabled()))));

		onView(allOf(withText(spacesOnlyInput), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(secondSoundName));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), not(isEnabled()))));

		onView(allOf(withText(secondSoundName), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(newSoundName));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), isEnabled())));
	}

	@Test
	public void renameSingleSoundTest() {
		openActionBar();
		onView(withText(R.string.delete)).perform(click());

		onRecyclerView().atPosition(1).performCheckItem();

		onActionMode().performConfirm();

		onView(withText(R.string.yes)).perform(click());

		openActionBar();
		onView(withText(R.string.rename)).perform(click());

		onView(withText(R.string.rename_sound_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));
	}

	private void createProject() throws IOException {
		String projectName = "renameSoundFragmentTest";
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);

		Sprite sprite = new Sprite("testSprite");
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
		ProjectManager.getInstance().setCurrentSprite(sprite);
		XstreamSerializer.getInstance().saveProject(project);

		File soundFile0 = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.longsound,
				new File(project.getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME),
				"longsound.mp3");

		List<SoundInfo> soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		SoundInfo soundInfo0 = new SoundInfo();
		soundInfo0.setFile(soundFile0);
		soundInfo0.setName(oldSoundName);
		soundInfoList.add(soundInfo0);

		File soundFile1 = StorageOperations.duplicateFile(soundFile0);
		SoundInfo soundInfo1 = new SoundInfo();
		soundInfo1.setFile(soundFile1);
		soundInfo1.setName(secondSoundName);
		soundInfoList.add(soundInfo1);
	}
}
