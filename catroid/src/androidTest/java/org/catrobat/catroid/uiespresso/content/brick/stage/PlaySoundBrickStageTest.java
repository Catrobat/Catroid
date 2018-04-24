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

package org.catrobat.catroid.uiespresso.content.brick.stage;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.FileTestUtils;
import org.catrobat.catroid.uiespresso.util.hardware.SensorTestArduinoServerConnection;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class PlaySoundBrickStageTest {

	private static final int RESOURCE_SOUND = org.catrobat.catroid.test.R.raw.longsound;
	private String soundName = "testSound1";
	private File soundFile;
	private List<SoundInfo> soundInfoList;
	private int waitingTime = 2500;

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> programMenuActivityRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

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
		Script startScript = BrickTestUtils.createProjectAndGetStartScript(projectName);
		startScript.addBrick(new PlaySoundBrick());

		soundFile = FileTestUtils
				.copyResourceFileToProject(projectName, ProjectManager.getInstance().getCurrentScene().getName(),
						"longsound.mp3", RESOURCE_SOUND,
						InstrumentationRegistry.getContext(), FileTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setFileName(soundFile.getName());
		soundInfo.setName(soundName);
		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		soundInfoList.add(soundInfo);
	}

	private void createProjectWithOutSound() {
		String projectName = "playNoSoundStageTest";
		SoundManager.getInstance();
		Script startScript = BrickTestUtils.createProjectAndGetStartScript(projectName);
		startScript.addBrick(new PlaySoundBrick());
	}
}
