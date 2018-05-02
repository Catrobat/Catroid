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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.ui.SpriteAttributesActivity;
import org.catrobat.catroid.uiespresso.annotations.Device;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewActions;
import org.catrobat.catroid.uiespresso.util.FileTestUtils;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class PlaySoundAndWaitBrickTest {
	private static final int RESOURCE_SOUND = org.catrobat.catroid.test.R.raw.longsound;
	private static final int RESOURCE_SOUND2 = org.catrobat.catroid.test.R.raw.testsoundui;

	private String soundName = "testSound1";
	private String soundName2 = "testSound2";
	private File soundFile;
	private File soundFile2;
	private List<SoundInfo> soundInfoList;

	private int playSoundAndWaitBrickPosition;
	private int playSoundBrickPosition;

	@Rule
	public BaseActivityInstrumentationRule<SpriteAttributesActivity> programMenuActivityRule = new
			BaseActivityInstrumentationRule<>(SpriteAttributesActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		programMenuActivityRule.launchActivity(null);
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

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testBasicLayout() {
		onView(withText(R.string.scripts))
				.perform(click());

		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(playSoundAndWaitBrickPosition).checkShowsText(R.string.brick_play_sound_and_wait);
		onBrickAtPosition(playSoundBrickPosition).checkShowsText(R.string.brick_play_sound);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testPlaySoundUpdateDelete() {
		onView(withText(R.string.scripts))
				.perform(click());

		onBrickAtPosition(playSoundAndWaitBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);
		onBrickAtPosition(playSoundBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);
		pressBack();

		deleteSound(0);

		onView(withText(R.string.scripts))
				.perform(click());

		onBrickAtPosition(playSoundAndWaitBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName2);
		onBrickAtPosition(playSoundBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName2);
	}

	@Category({Cat.AppUi.class, Level.Functional.class, Cat.Device.class})
	@Test
	@Device
	public void testPlaySoundBrickUpdateAddNew() {
		onView(withText(R.string.scripts))
				.perform(click());

		onBrickAtPosition(playSoundAndWaitBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);
		onBrickAtPosition(playSoundBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);

		onBrickAtPosition(playSoundAndWaitBrickPosition).onChildView(withId(R.id.brick_play_sound_spinner))
				.perform(click());

		recordNewSound(3000);

		onBrickAtPosition(playSoundAndWaitBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(R.string.soundrecorder_recorded_filename);
		onBrickAtPosition(playSoundBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testPlaySoundBrickUpdateRename() {
		String newName = "newName";

		onView(withText(R.string.scripts))
				.perform(click());

		onBrickAtPosition(playSoundAndWaitBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);
		onBrickAtPosition(playSoundBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);

		pressBack();

		renameSound(0, soundName, newName);

		onView(withText(R.string.scripts))
				.perform(click());

		onBrickAtPosition(playSoundAndWaitBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(newName);
		onBrickAtPosition(playSoundBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(newName);
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
		onView(allOf(withId(android.R.id.button2), withText(R.string.no)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.perform(click());

		pressBack();
	}

	private void renameSound(int position, String oldName, String newName) {
		onView(withText(R.string.sounds))
				.perform(click());
		RecyclerViewActions.openOverflowMenu();
		onView(withText(R.string.rename))
				.perform(click());
		onRecyclerView().atPosition(position)
				.performCheckItem();

		onView(withText(R.string.confirm))
				.perform(click());

		onView(withText(R.string.rename_sound_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));
		onView(allOf(withText(oldName), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(newName));
		closeSoftKeyboard();
		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.perform(click());

		onView(withText(newName))
				.check(matches(isDisplayed()));
		onView(withText(oldName))
				.check(doesNotExist());

		pressBack();
	}

	private void recordNewSound(int durationMillis) {
		onView(withText(R.string.brick_variable_spinner_create_new_variable))
				.perform(click());

		onView(withText(R.string.add_sound_from_recorder))
				.perform(click());

		onView(withId(R.id.soundrecorder_record_button))
				.perform(click());

		onView(isRoot()).perform(CustomActions.wait(durationMillis));

		onView(withId(R.id.soundrecorder_record_button))
				.perform(click());
	}

	private void createProject() throws IOException {
		String projectName = "playSoundAndWaitBrickTest";
		SoundManager.getInstance();
		Script startScript = BrickTestUtils.createProjectAndGetStartScript(projectName);

		playSoundAndWaitBrickPosition = 1;
		startScript.addBrick(new PlaySoundAndWaitBrick());
		playSoundBrickPosition = 2;
		startScript.addBrick(new PlaySoundBrick());

		soundFile = FileTestUtils
				.copyResourceFileToProject(projectName, ProjectManager.getInstance().getCurrentScene().getName(),
						"longsound.mp3", RESOURCE_SOUND,
						InstrumentationRegistry.getContext(), FileTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setFile(soundFile);
		soundInfo.setName(soundName);

		soundFile2 = FileTestUtils.copyResourceFileToProject(projectName, ProjectManager.getInstance().getCurrentScene()
						.getName(),
				"testsoundui.mp3", RESOURCE_SOUND2, InstrumentationRegistry.getContext(),
				FileTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo2 = new SoundInfo();
		soundInfo2.setFile(soundFile2);
		soundInfo2.setName(soundName2);

		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		soundInfoList.add(soundInfo);
		soundInfoList.add(soundInfo2);
	}
}
