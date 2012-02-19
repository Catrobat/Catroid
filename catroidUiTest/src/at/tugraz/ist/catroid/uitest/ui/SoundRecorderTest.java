/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.uitest.ui;

import java.io.File;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.soundrecorder.SoundRecorderActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.SoundActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class SoundRecorderTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;

	public SoundRecorderTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();

		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();

	}

	public void testRecordSound() throws InterruptedException {
		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.waitForActivity(SoundActivity.class.getSimpleName());

		solo.clickOnText(getActivity().getString(R.string.add));
		String soundRecorderText = getActivity().getString(R.string.soundrecorder_name);
		solo.waitForText(soundRecorderText);
		assertTrue("Catroid Sound Recorder is not present", solo.searchText(soundRecorderText));

		solo.clickOnText(soundRecorderText);
		recordSound();

		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		ArrayList<SoundInfo> soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		assertEquals("wrong number of items in the list ", 1, soundInfoList.size());

		String projectName = ProjectManager.getInstance().getCurrentProject().getName();
		File recordedFileInCatroid = new File(Consts.DEFAULT_ROOT + "/" + projectName + "/" + Consts.SOUND_DIRECTORY
				+ "/");
		assertTrue("recorded sound file not found in file system", recordedFileInCatroid.exists());
	}

	private void recordSound() throws InterruptedException {
		solo.waitForActivity(SoundRecorderActivity.class.getSimpleName());
		solo.clickOnText(getActivity().getString(R.string.soundrecorder_record_start));
		// record 500ms, solo.sleep() does not work here
		Thread.sleep(500);
		//solo.wait(500);
		solo.clickOnText(getActivity().getString(R.string.soundrecorder_record_stop));

		String path = Consts.DEFAULT_ROOT + "/soundrecorder/mytestfile.mp3";
		File recordedFile = new File(path);
		assertTrue("recorded sound file not found in file system", recordedFile.exists());
		solo.goBack();
	}
}
