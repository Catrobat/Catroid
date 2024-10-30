/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.content.brick.stage;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.hardware.SensorTestArduinoServerConnection;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class PlaySoundBrickStageTest {

	private String soundName = "testSound1";
	private File soundFile;
	private List<SoundInfo> soundInfoList;
	private int waitingTime = 2500;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> programMenuActivityRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@After
	public void tearDown() {
		if (soundFile != null && soundFile.exists()) {
			soundFile.delete();
		}
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class, Cat.Gadgets.class, Cat.SettingsAndPermissions
			.class, Cat.SensorBox.class})
	@Test
	public void testSoundPlayedFromPlaySoundBrick() throws IOException {
		createProjectWithSound();
		programMenuActivityRule.launchActivity();
		SensorTestArduinoServerConnection.checkAudioSensorValue(SensorTestArduinoServerConnection
				.SET_AUDIO_OFF_VALUE, waitingTime);

		onView(withId(R.id.button_play))
				.perform(click());

		SensorTestArduinoServerConnection.checkAudioSensorValue(SensorTestArduinoServerConnection
				.SET_AUDIO_ON_VALUE, waitingTime);
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class, Cat.Gadgets.class, Cat.SettingsAndPermissions
			.class, Cat.SensorBox.class})
	@Test
	public void testNoSoundPlayed() {
		createProjectWithOutSound();
		programMenuActivityRule.launchActivity();

		SensorTestArduinoServerConnection.checkAudioSensorValue(SensorTestArduinoServerConnection
				.SET_AUDIO_OFF_VALUE, waitingTime);

		onView(withId(R.id.button_play))
				.perform(click());

		SensorTestArduinoServerConnection.checkAudioSensorValue(SensorTestArduinoServerConnection
				.SET_AUDIO_OFF_VALUE, waitingTime);
	}

	private void createProjectWithSound() throws IOException {
		String projectName = "playSoundStageTest";
		SoundManager.getInstance();

		Project project = UiTestUtils.createDefaultTestProject(projectName);
		Script startScript = UiTestUtils.getDefaultTestScript(project);

		startScript.addBrick(new PlaySoundBrick());

		File directory = new File(project.getDefaultScene().getDirectory(), SOUND_DIRECTORY_NAME);
		directory.mkdirs();

		soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getInstrumentation().getContext().getResources(),
				org.catrobat.catroid.test.R.raw.longsound, directory, "longsound.mp3");

		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setFile(soundFile);
		soundInfo.setName(soundName);
		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		soundInfoList.add(soundInfo);
	}

	private void createProjectWithOutSound() {
		String projectName = "playNoSoundStageTest";
		SoundManager.getInstance();
		Script startScript = UiTestUtils.createProjectAndGetStartScript(projectName);
		startScript.addBrick(new PlaySoundBrick());
	}
}
