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

import android.Manifest;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.SpriteAttributesActivity;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
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
	public BaseActivityTestRule<SpriteAttributesActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(SpriteAttributesActivity.class, true, false);

	@Rule
	public GrantPermissionRule runtimePermissionRule = GrantPermissionRule.grant(Manifest.permission.RECORD_AUDIO);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);
	}

	private void renameSound(int position, String newSoundName) {
		onView(withText(R.string.sounds))
				.perform(click());
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.rename))
				.perform(click());
		onRecyclerView().atPosition(position)
				.performCheckItem();
		onView(withId(R.id.confirm))
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
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText(R.string.delete))
				.perform(click());
		onRecyclerView().atPosition(position)
				.performCheckItem();
		onView(withId(R.id.confirm))
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

		onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);
		onBrickAtPosition(2).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);

		onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner)
				.perform(click());
		onView(withText(R.string.new_option))
				.perform(click());
		onView(withText(R.string.add_sound_from_recorder))
				.perform(click());
		onView(withId(R.id.soundrecorder_record_button))
				.perform(click());
		onView(isRoot()).perform(CustomActions.wait(1000));
		onView(withId(R.id.soundrecorder_record_button))
				.perform(click());

		onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(R.string.soundrecorder_recorded_filename);
		onBrickAtPosition(2).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);
	}

	@Test
	public void testDeleteSound() {
		onView(withText(R.string.scripts))
				.perform(click());

		onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);

		pressBack();
		deleteSound(0);
		onView(withText(R.string.scripts))
				.perform(click());

		onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName2);
		onBrickAtPosition(2).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName2);
	}

	@Test
	public void testRenameSound() {
		String newSoundName = "newName";
		onView(withText(R.string.scripts))
				.perform(click());

		onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);
		onBrickAtPosition(2).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(soundName);

		pressBack();
		renameSound(0, newSoundName);
		onView(withText(R.string.scripts))
				.perform(click());

		onBrickAtPosition(1).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(newSoundName);
		onBrickAtPosition(2).onSpinner(R.id.brick_play_sound_spinner)
				.checkShowsText(newSoundName);
	}

	private void createProject() throws IOException {
		SoundManager.getInstance();
		Project project = new Project(InstrumentationRegistry.getTargetContext(), getClass().getSimpleName());
		Sprite sprite = new Sprite("testSprite");
		Script startScript = new StartScript();

		sprite.addScript(startScript);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		XstreamSerializer.getInstance().saveProject(project);

		startScript.addBrick(new PlaySoundBrick());
		startScript.addBrick(new PlaySoundBrick());

		soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				org.catrobat.catroid.test.R.raw.longsound,
				new File(project.getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME),
				"longsound.mp3");

		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setFile(soundFile);
		soundInfo.setName(soundName);

		soundFile2 = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				org.catrobat.catroid.test.R.raw.testsoundui,
				new File(project.getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME),
				"testsoundui.mp3");

		SoundInfo soundInfo2 = new SoundInfo();
		soundInfo2.setFile(soundFile2);
		soundInfo2.setName(soundName2);

		List<SoundInfo> soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		soundInfoList.add(soundInfo);
		soundInfoList.add(soundInfo2);
	}

	@After
	public void tearDown() throws IOException {
		TestUtils.deleteProjects(getClass().getSimpleName());

		if (soundFile != null && soundFile.exists()) {
			soundFile.delete();
		}
		if (soundFile2 != null && soundFile2.exists()) {
			soundFile2.delete();
		}
	}
}
