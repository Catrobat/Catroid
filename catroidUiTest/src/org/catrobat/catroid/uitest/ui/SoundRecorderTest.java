/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.ui;

import java.io.File;
import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.soundrecorder.SoundRecorderActivity;
import org.catrobat.catroid.ui.ScriptTabActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import android.test.ActivityInstrumentationTestCase2;
import org.catrobat.catroid.R;

import com.jayway.android.robotium.solo.Solo;

public class SoundRecorderTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private Solo solo;

	public SoundRecorderTest() {
		super(ScriptTabActivity.class);
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
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testRecordMultipleSounds() throws InterruptedException {
		prepareRecording();
		recordSoundWithChangingOrientation();
		assertSoundRecording(1);

		prepareRecording();
		recordSoundGoBackWhileRecording();
		assertSoundRecording(2);
	}

	public void recordSoundWithChangingOrientation() throws InterruptedException {
		solo.waitForActivity(SoundRecorderActivity.class.getSimpleName());
		solo.clickOnText(solo.getString(R.string.soundrecorder_record_start));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.clickOnText(solo.getString(R.string.soundrecorder_record_stop));
	}

	public void recordSoundGoBackWhileRecording() throws InterruptedException {
		solo.waitForActivity(SoundRecorderActivity.class.getSimpleName());
		solo.clickOnText(solo.getString(R.string.soundrecorder_record_start));
		solo.setActivityOrientation(Solo.LANDSCAPE);

		solo.goBack();
		solo.setActivityOrientation(Solo.PORTRAIT);
	}

	private void prepareRecording() {
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.clickOnText(solo.getString(R.string.sounds));

		UiTestUtils.clickOnActionBar(solo, R.id.menu_add);
		String soundRecorderText = solo.getString(R.string.soundrecorder_name);
		solo.waitForText(soundRecorderText);
		assertTrue("Catroid Sound Recorder is not present", solo.searchText(soundRecorderText));

		solo.clickOnText(soundRecorderText);
	}

	private void assertSoundRecording(int recordNumber) {
		String recordPath = Utils.buildPath(Constants.TMP_PATH,
				solo.getString(R.string.soundrecorder_recorded_filename) + Constants.RECORDING_EXTENTION);
		File recordedFile = new File(recordPath);
		assertTrue("recorded sound file not found in file system", recordedFile.exists());

		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		String recordTitle = solo.getString(R.string.soundrecorder_recorded_filename);
		if (recordNumber > 1) {
			recordTitle += (recordNumber - 1);
		}

		ArrayList<SoundInfo> soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		assertEquals("wrong number of items in the list ", recordNumber, soundInfoList.size());
		SoundInfo lastAddedSoundInfo = soundInfoList.get(soundInfoList.size() - 1);
		assertEquals("recorded sound not found in project", recordTitle, lastAddedSoundInfo.getTitle());

		File lastAddedSoundFile = new File(lastAddedSoundInfo.getAbsolutePath());
		assertTrue("recorded sound file not found in project", lastAddedSoundFile.exists());
	}
}
