/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
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
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.SoundActivity;
import org.catrobat.catroid.ui.adapter.SoundAdapter;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;

import com.jayway.android.robotium.solo.Solo;

public class SoundFragmentTest extends ActivityInstrumentationTestCase2<SoundActivity> {
	private final int RESOURCE_SOUND = org.catrobat.catroid.uitest.R.raw.longsound;
	private final int RESOURCE_SOUND2 = org.catrobat.catroid.uitest.R.raw.testsoundui;

	private Solo solo;
	private String soundName = "testSound1";
	private String soundName2 = "testSound2";
	private File soundFile;
	private File soundFile2;
	private File externalSoundFile;
	private ArrayList<SoundInfo> soundInfoList;

	public SoundFragmentTest() {
		super(SoundActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();
		soundInfoList = ProjectManager.INSTANCE.getCurrentSprite().getSoundList();

		soundFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "longsound.mp3",
				RESOURCE_SOUND, getInstrumentation().getContext(), UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(soundName);

		soundFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "testsoundui.mp3",
				RESOURCE_SOUND2, getInstrumentation().getContext(), UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo2 = new SoundInfo();
		soundInfo2.setSoundFileName(soundFile2.getName());
		soundInfo2.setTitle(soundName2);

		soundInfoList.add(soundInfo);
		soundInfoList.add(soundInfo2);
		ProjectManager.INSTANCE.getFileChecksumContainer().addChecksum(soundInfo.getChecksum(),
				soundInfo.getAbsolutePath());
		ProjectManager.INSTANCE.getFileChecksumContainer().addChecksum(soundInfo2.getChecksum(),
				soundInfo2.getAbsolutePath());

		externalSoundFile = UiTestUtils.createTestMediaFile(Constants.DEFAULT_ROOT + "/externalSoundFile.mp3",
				RESOURCE_SOUND, getActivity());

		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		externalSoundFile.delete();
		super.tearDown();
		solo = null;
	}

	public void testInitialLayout() {
		assertTrue("Play button not visible", solo.getView(R.id.btn_sound_play).getVisibility() == View.VISIBLE);
		assertTrue("Pause button not gone although not playing",
				solo.getView(R.id.btn_sound_pause).getVisibility() == View.GONE);
		assertTrue("Sound name not visible", solo.getView(R.id.sound_title).getVisibility() == View.VISIBLE);
		assertTrue("Sound duration not visible", solo.getView(R.id.sound_duration).getVisibility() == View.VISIBLE);
		assertTrue("Chronometer not gone although not playing", solo.getView(R.id.sound_chronometer_time_played)
				.getVisibility() == View.GONE);
		assertFalse("Initially showing details", getSoundAdapter().getShowDetails());
		assertTrue("Size not gone although no details should be displayed", solo.getView(R.id.sound_size)
				.getVisibility() == View.GONE);
	}

	private SoundAdapter getSoundAdapter() {
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		SoundFragment soundFragment = (SoundFragment) fragmentManager.findFragmentById(R.id.fr_sound);
		return (SoundAdapter) soundFragment.getListAdapter();
	}

	public void testDeleteSound() {
		SoundAdapter adapter = getSoundAdapter();
		assertNotNull("Could not get Adapter", adapter);

		int oldCount = adapter.getCount();

		clickOnContextMenuItem(soundName2, solo.getString(R.string.delete));
		solo.waitForText(solo.getString(R.string.delete_sound_dialog));
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(500);

		int newCount = adapter.getCount();

		assertEquals("Old count is not correct", 2, oldCount);
		assertEquals("New count is not correct - one sound should be deleted", 1, newCount);
		assertEquals("Count of the soundList is not right", 1, soundInfoList.size());
	}

	public void testRenameSound() {
		String newSoundName = "TeStSoUNd1";

		clickOnContextMenuItem(soundName, solo.getString(R.string.rename));
		assertTrue("Wrong title of dialog", solo.searchText(solo.getString(R.string.rename_sound_dialog)));
		assertTrue("No EditText with actual soundname", solo.searchEditText(soundName));

		solo.clearEditText(0);
		solo.enterText(0, newSoundName);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);

		soundInfoList = ProjectManager.INSTANCE.getCurrentSprite().getSoundList();
		assertEquals("Sound is not renamed in SoundList", newSoundName, soundInfoList.get(0).getTitle());
		assertTrue("Sound not renamed in actual view", solo.searchText(newSoundName));
	}

	public void testPlayAndStopSound() {
		SoundInfo soundInfo = soundInfoList.get(0);
		assertFalse("Mediaplayer is playing although no play button was touched", soundInfo.isPlaying);

		ImageButton playImageButton = (ImageButton) solo.getView(R.id.btn_sound_play);
		ImageButton pauseImageButton = (ImageButton) solo.getView(R.id.btn_sound_pause);
		Chronometer timePlayedChronometer = (Chronometer) solo.getView(R.id.sound_chronometer_time_played);

		solo.clickOnView(playImageButton);
		solo.sleep(100);

		assertTrue("Play button not gone although playing", playImageButton.getVisibility() == View.GONE);
		assertTrue("Pause button not visible although playing", pauseImageButton.getVisibility() == View.VISIBLE);
		assertTrue("Mediaplayer is not playing although play button was touched", soundInfo.isPlaying);
		assertTrue("Chronometer not visible although playing", timePlayedChronometer.getVisibility() == View.VISIBLE);

		solo.clickOnView(pauseImageButton);
		solo.sleep(100);

		assertFalse("Mediaplayer is playing after touching stop button", soundInfo.isPlaying);
		assertTrue("Play button not visible although stopped playing", playImageButton.getVisibility() == View.VISIBLE);
		assertTrue("Pause button not gone although not playing anymore", pauseImageButton.getVisibility() == View.GONE);
		assertTrue("Chronometer not gone although not playing anymore",
				timePlayedChronometer.getVisibility() == View.GONE);
	}

	private void clickOnContextMenuItem(String soundName, String itemName) {
		solo.clickLongOnText(soundName);
		solo.waitForText(itemName);
		solo.clickOnText(itemName);
	}
}
