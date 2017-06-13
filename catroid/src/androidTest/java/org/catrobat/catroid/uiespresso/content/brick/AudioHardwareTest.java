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
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.PlaySoundAndWaitBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.FileTestUtils;
import org.catrobat.catroid.uiespresso.util.hardware.SensorTestArduinoServerConnection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

//TODO incomplete Test!

@RunWith(AndroidJUnit4.class)
public class AudioHardwareTest {
	private int brickPosition;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testAudioHardware() {
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_play_sound_and_wait);

		onView(withId(R.id.button_play)).perform(click());
		SensorTestArduinoServerConnection.checkAudioSensorValue(1);
	}

	private void createProject() {
		String soundName = "testSound1";
		String projectName = "testProject";
		File soundFile;
		List<SoundInfo> soundInfoList;
		brickPosition = 1;
		Script script = BrickTestUtils.createProjectAndGetStartScript(projectName);
		script.addBrick(new PlaySoundAndWaitBrick());

		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();

		soundFile = FileTestUtils.saveFileToProject(projectName, ProjectManager.getInstance().getCurrentProject()
						.getDefaultScene().getName(),
				"longsound.mp3",
				org.catrobat.catroid.test.R.raw.longtestsound,
				InstrumentationRegistry.getContext(),
				FileTestUtils.FileTypes.SOUND);

		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(soundName);

		soundInfoList.add(soundInfo);

		ProjectManager.getInstance().getFileChecksumContainer()
				.addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
	}
}
