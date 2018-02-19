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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.ui.SpriteAttributesActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewActions;
import org.catrobat.catroid.uiespresso.util.FileTestUtils;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class PlaySoundBrickTest {
	private String soundName = "testSound1";
	private String soundName2 = "testSound2";
	private File soundFile;
	private File soundFile2;

	@Rule
	public BaseActivityInstrumentationRule<SpriteAttributesActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteAttributesActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);
	}

	private void renameSound(int position, String newSoundName) {
		onView(withText(R.string.sounds))
				.perform(click());
		RecyclerViewActions.openOverflowMenu();
		onView(withText(R.string.rename))
				.perform(click());
		onRecyclerView().atPosition(position)
				.performCheckItem();
		onView(withText(R.string.confirm))
				.perform(click());
		onView(allOf(withText(soundName), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(newSoundName));
		onView(allOf(withId(android.R.id.button1), withText(R.string.ok), isDisplayed()))
				.perform(closeSoftKeyboard())
				.perform(click());
		pressBack();
	}

	private void deleteSound(int position) {
		onView(withText(R.string.sounds))
				.perform(click());
		RecyclerViewActions.openOverflowMenu();
		onView(withText(R.string.delete))
				.perform(click());
		onRecyclerView().atPosition(position)
				.performCheckItem();
		onView(withText(R.string.confirm))
				.perform(click());
		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.check(matches(isDisplayed()));
		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.perform(click());
		pressBack();
	}

	@Test
	public void testRecordNewSound() {
		onView(withText(R.string.scripts))
				.perform(click());

		onBrickAtPosition(1).onSpinner(R.id.playsound_spinner)
				.checkShowsText(soundName);
		onBrickAtPosition(2).onSpinner(R.id.playsound_spinner)
				.checkShowsText(soundName);

		onBrickAtPosition(1).onSpinner(R.id.playsound_spinner)
				.perform(click());
		onView(withText(R.string.brick_variable_spinner_create_new_variable))
				.perform(click());
		onView(withText(R.string.add_sound_from_recorder))
				.perform(click());
		onView(withId(R.id.soundrecorder_record_button))
				.perform(click());
		onView(isRoot()).perform(CustomActions.wait(1000));
		onView(withId(R.id.soundrecorder_record_button))
				.perform(click());

		onBrickAtPosition(1).onSpinner(R.id.playsound_spinner)
				.checkShowsText(R.string.soundrecorder_recorded_filename);
		onBrickAtPosition(2).onSpinner(R.id.playsound_spinner)
				.checkShowsText(soundName);
	}

	@Test
	public void testDeleteSound() {
		onView(withText(R.string.scripts))
				.perform(click());

		onBrickAtPosition(1).onSpinner(R.id.playsound_spinner)
				.checkShowsText(soundName);

		pressBack();
		deleteSound(0);
		onView(withText(R.string.scripts))
				.perform(click());

		onBrickAtPosition(1).onSpinner(R.id.playsound_spinner)
				.checkShowsText(soundName2);
		onBrickAtPosition(2).onSpinner(R.id.playsound_spinner)
				.checkShowsText(soundName2);
	}

	@Test
	public void testRenameSound() {
		String newSoundName = "newName";
		onView(withText(R.string.scripts))
				.perform(click());

		onBrickAtPosition(1).onSpinner(R.id.playsound_spinner)
				.checkShowsText(soundName);
		onBrickAtPosition(2).onSpinner(R.id.playsound_spinner)
				.checkShowsText(soundName);

		pressBack();
		renameSound(0, newSoundName);
		onView(withText(R.string.scripts))
				.perform(click());

		onBrickAtPosition(1).onSpinner(R.id.playsound_spinner)
				.checkShowsText(newSoundName);
		onBrickAtPosition(2).onSpinner(R.id.playsound_spinner)
				.checkShowsText(newSoundName);
	}

	private void createProject() {
		SoundManager.getInstance();
		Script startScript = BrickTestUtils.createProjectAndGetStartScript("PlaySoundBrickTest");
		startScript.addBrick(new PlaySoundBrick());
		startScript.addBrick(new PlaySoundBrick());

		soundFile = FileTestUtils.saveFileToProject("PlaySoundBrickTest",
				ProjectManager.getInstance().getCurrentScene().getName(),
				"longsound.mp3", org.catrobat.catroid.test.R.raw.longsound,
				InstrumentationRegistry.getContext(), FileTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setFileName(soundFile.getName());
		soundInfo.setName(soundName);

		soundFile2 = FileTestUtils.saveFileToProject("PlaySoundBrickTest",
				ProjectManager.getInstance().getCurrentScene().getName(),
				"testsoundui.mp3", org.catrobat.catroid.test.R.raw.testsoundui,
				InstrumentationRegistry.getContext(), FileTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo2 = new SoundInfo();
		soundInfo2.setFileName(soundFile2.getName());
		soundInfo2.setName(soundName2);

		List<SoundInfo> soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		soundInfoList.add(soundInfo);
		soundInfoList.add(soundInfo2);
	}

	@After
	public void tearDown() {
		if (soundFile != null && soundFile.exists()) {
			soundFile.delete();
		}
		if (soundFile2 != null && soundFile2.exists()) {
			soundFile2.delete();
		}
	}
}
