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

package org.catrobat.catroid.uiespresso.content.brick;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uitest.annotation.Device;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfSpinnerOnBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.onScriptList;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class PlaySoundBrickTest {
	private String projectName = "playSoundBrickTest";

	private static final int RESOURCE_SOUND = org.catrobat.catroid.test.R.raw.longsound;
	private static final int RESOURCE_SOUND2 = org.catrobat.catroid.test.R.raw.testsoundui;

	private String soundName = "testSound1";
	private String soundName2 = "testSound2";
	private File soundFile;
	private File soundFile2;
	private List<SoundInfo> soundInfoList;

	private int brickPosition;

	@Rule
	public BaseActivityInstrumentationRule<ProgramMenuActivity> programMenuActivityRule = new
			BaseActivityInstrumentationRule<>(ProgramMenuActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		SoundManager.getInstance();
		brickPosition = 1;
		BrickTestUtils.createProjectAndGetStartScript(projectName).addBrick(new PlaySoundBrick());
		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();

		soundFile = UiTestUtils.saveFileToProject(projectName, ProjectManager.getInstance().getCurrentScene().getName(),
				"longsound.mp3", RESOURCE_SOUND, InstrumentationRegistry.getTargetContext(),
				UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(soundName);

		soundFile2 = UiTestUtils.saveFileToProject(projectName, ProjectManager.getInstance().getCurrentScene().getName(),
				"testsoundui.mp3", RESOURCE_SOUND2, InstrumentationRegistry.getTargetContext(),
				UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo2 = new SoundInfo();
		soundInfo2.setSoundFileName(soundFile2.getName());
		soundInfo2.setTitle(soundName2);

		soundInfoList.add(soundInfo);
		soundInfoList.add(soundInfo2);

		programMenuActivityRule.launchActivity(null);

		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(soundInfo2.getChecksum(), soundInfo2.getAbsolutePath());
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

	@Test
	public void testPlaySoundBrickBasicLayout() {
		onView(withId(R.id.program_menu_button_scripts))
				.perform(click());

		checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
		checkIfBrickAtPositionShowsString(brickPosition, R.string.brick_play_sound);
	}

	@Test
	public void testPlaySoundBrickUpdateDelete() {
		onView(withId(R.id.program_menu_button_scripts))
				.perform(click());

		checkIfSpinnerOnBrickAtPositionShowsString(R.id.playsound_spinner, brickPosition, soundName);
		pressBack();

		deleteSoundByName(soundName);
		onView(withId(R.id.program_menu_button_scripts))
				.perform(click());

		checkIfSpinnerOnBrickAtPositionShowsString(R.id.playsound_spinner, brickPosition, soundName2);
	}

	@Test
	@Device
	public void testPlaySoundBrickUpdateAddNew() {
		onView(withId(R.id.program_menu_button_scripts))
				.perform(click());

		checkIfSpinnerOnBrickAtPositionShowsString(R.id.playsound_spinner, brickPosition, soundName);

		onScriptList().atPosition(brickPosition).onChildView(withId(R.id.playsound_spinner))
				.perform(click());
		onView(withText(R.string.brick_variable_spinner_create_new_variable))
				.perform(click());
		onView(withText(R.string.add_sound_from_recorder))
				.perform(click());
		onView(withId(R.id.soundrecorder_record_button))
				.perform(click());

		onView(isRoot()).perform(CustomActions.wait(3000));

		onView(withId(R.id.soundrecorder_record_button))
				.perform(click());

		checkIfSpinnerOnBrickAtPositionShowsString(R.id.playsound_spinner, brickPosition,
				R.string.soundrecorder_recorded_filename);
	}

	@Test
	public void testPlaySoundBrickUpdateRename() {
		String newName = "newName";

		onView(withId(R.id.program_menu_button_scripts))
				.perform(click());

		checkIfSpinnerOnBrickAtPositionShowsString(R.id.playsound_spinner, brickPosition, soundName);

		pressBack();
		renameSound(soundName, newName);
		onView(withId(R.id.program_menu_button_scripts))
				.perform(click());
		checkIfSpinnerOnBrickAtPositionShowsString(R.id.playsound_spinner, brickPosition, newName);
	}

	private void deleteSoundByName(String soundName) {
		onView(withId(R.id.program_menu_button_sounds))
				.perform(click());
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
		onView(withText(R.string.delete))
				.perform(click());
		onView(withText(soundName))
				.perform(click());
		onView(withContentDescription(R.string.done))
				.perform(click());

		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.check(matches(isDisplayed()));
		onView(allOf(withId(android.R.id.button2), withText(R.string.no)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.perform(click());

		pressBack();
	}

	private void renameSound(String oldName, String newName) {
		onView(withId(R.id.program_menu_button_sounds))
				.perform(click());
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
		onView(withText(R.string.rename))
				.perform(click());
		onView(withText(oldName))
				.perform(click());
		onView(withContentDescription(R.string.done))
				.perform(click());

		onView(withText(R.string.rename_sound_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));
		onView(allOf(withId(R.id.edit_text), withText(oldName), isDisplayed()))
				.perform(replaceText(newName));

		closeSoftKeyboard();

		// todo: see CAT-2099 [https://jira.catrob.at/browse/CAT-2099]
		Assert.fail();
		/*
		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.perform(click());

		onView(withText(newName))
				.check(matches(isDisplayed()));
		onView(withText(oldName))
				.check(doesNotExist());

		pressBack();
		*/
	}
}
