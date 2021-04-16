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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.Manifest;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.annotations.Device;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.After;
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
import androidx.test.rule.GrantPermissionRule;

import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.openActionBar;
import static org.catrobat.catroid.uiespresso.util.actions.TabActionsKt.selectTabAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class PlaySoundAndWaitBrickTest {

	private String soundName = "testSound1";
	private String soundName2 = "testSound2";
	private File soundFile;
	private File soundFile2;
	private List<SoundInfo> soundInfoList;

	private int playSoundAndWaitBrickPosition;
	private int playSoundBrickPosition;

	@Rule
	public BaseActivityTestRule<SpriteActivity> programMenuActivityRule = new
			BaseActivityTestRule<>(SpriteActivity.class, true, false);

	@Rule
	public GrantPermissionRule runtimePermissionRule = GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO);

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

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void testPlaySoundUpdateDelete() {
		onView(withId(R.id.tab_layout))
				.perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SCRIPTS));

		onBrickAtPosition(playSoundAndWaitBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);
		onBrickAtPosition(playSoundBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);

		deleteSound(0);

		onView(withId(R.id.tab_layout))
				.perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SCRIPTS));

		onBrickAtPosition(playSoundAndWaitBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName2);
		onBrickAtPosition(playSoundBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName2);
	}

	@Category({Cat.AppUi.class, Level.Functional.class, Cat.Device.class})
	@Test
	@Device
	public void testPlaySoundBrickUpdateAddNew() {
		onView(withId(R.id.tab_layout))
				.perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SCRIPTS));

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

		onView(withId(R.id.tab_layout))
				.perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SCRIPTS));

		onBrickAtPosition(playSoundAndWaitBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);
		onBrickAtPosition(playSoundBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);

		renameSound(0, soundName, newName);

		onView(withId(R.id.tab_layout))
				.perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SCRIPTS));

		onBrickAtPosition(playSoundAndWaitBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(newName);
		onBrickAtPosition(playSoundBrickPosition).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(newName);
	}

	private void deleteSound(int position) {
		onView(withId(R.id.tab_layout))
				.perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SOUNDS));
		openActionBar();
		onView(withText(R.string.delete))
				.perform(click());
		onRecyclerView().atPosition(position)
				.performCheckItem();
		onView(withId(R.id.confirm))
				.perform(click());

		onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
				.check(matches(isDisplayed()));
		onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.delete)))
				.perform(click());
	}

	private void renameSound(int position, String oldName, String newName) {
		onView(withId(R.id.tab_layout))
				.perform(selectTabAtPosition(SpriteActivity.FRAGMENT_SOUNDS));
		openActionBar();
		onView(withText(R.string.rename))
				.perform(click());
		onRecyclerView().atPosition(position)
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
	}

	private void recordNewSound(int durationMillis) {
		onView(withText(R.string.new_option))
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
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		Sprite sprite = new Sprite("testSprite");
		Script startScript = new StartScript();

		sprite.addScript(startScript);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		XstreamSerializer.getInstance().saveProject(project);

		playSoundAndWaitBrickPosition = 1;
		startScript.addBrick(new PlaySoundAndWaitBrick());
		playSoundBrickPosition = 2;
		startScript.addBrick(new PlaySoundBrick());

		soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.longsound,
				new File(project.getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME),
				"longsound.mp3");

		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setFile(soundFile);
		soundInfo.setName(soundName);

		soundFile2 = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.testsoundui,
				new File(project.getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME),
				"testsoundui.mp3");

		SoundInfo soundInfo2 = new SoundInfo();
		soundInfo2.setFile(soundFile2);
		soundInfo2.setName(soundName2);

		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		soundInfoList.add(soundInfo);
		soundInfoList.add(soundInfo2);
	}
}
