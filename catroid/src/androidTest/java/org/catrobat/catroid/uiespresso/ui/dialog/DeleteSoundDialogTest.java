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

package org.catrobat.catroid.uiespresso.ui.dialog;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
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
import static org.hamcrest.Matchers.allOf;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class DeleteSoundDialogTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SOUNDS);
	private String toBeDeletedSoundName = "testLook2";

	@Before
	public void setUp() throws Exception {
		createProject("deleteSoundDialogTest");

		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void deleteSoundDialogTest() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.delete)).perform(click());

		onRecyclerView().atPosition(1)
				.performCheckItem();

		onView(withId(R.id.confirm)).perform(click());

		onView(withText(UiTestUtils.getResources().getQuantityString(R.plurals.delete_sounds, 1)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(withText(R.string.dialog_confirm_delete)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
				.check(matches(isDisplayed()));
		onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
				.perform(click());

		onView(withText(toBeDeletedSoundName))
				.check(doesNotExist());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void cancelDeleteSoundDialogTest() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.delete)).perform(click());

		onRecyclerView().atPosition(1)
				.performCheckItem();

		onView(withId(R.id.confirm)).perform(click());

		onView(withText(UiTestUtils.getResources().getQuantityString(R.plurals.delete_sounds, 1)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(withText(R.string.dialog_confirm_delete)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
				.check(matches(isDisplayed()));
		onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
				.perform(click());

		onView(withText(toBeDeletedSoundName))
				.check(matches(isDisplayed()));

		onActionMode().checkTitleMatches(UiTestUtils.getResourcesString(R.string.delete) + " 1");
	}

	private void createProject(String projectName) throws IOException {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);

		Sprite sprite = new Sprite("testSprite");
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		XstreamSerializer.getInstance().saveProject(project);

		File soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.longsound,
				new File(project.getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME),
				"longsound.mp3");

		File soundFile2 = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.testsoundui,
				new File(project.getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME),
				"testsoundui.mp3");

		List<SoundInfo> soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setFile(soundFile);
		soundInfo.setName("testLook1");
		soundInfoList.add(soundInfo);

		SoundInfo soundInfo2 = new SoundInfo();
		soundInfo2.setFile(soundFile2);
		soundInfo2.setName(toBeDeletedSoundName);
		soundInfoList.add(soundInfo2);
	}
}
