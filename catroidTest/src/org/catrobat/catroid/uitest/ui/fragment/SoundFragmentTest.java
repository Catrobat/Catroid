/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.uitest.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robotium.solo.By;
import com.robotium.solo.Solo;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.SoundAdapter;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.controller.SoundController;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.mockups.MockSoundActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class SoundFragmentTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final int RESOURCE_SOUND = org.catrobat.catroid.test.R.raw.longsound;
	private static final int RESOURCE_SOUND2 = org.catrobat.catroid.test.R.raw.testsoundui;
	private static final int RESOURCE_SHORT_SOUND = org.catrobat.catroid.test.R.raw.soundunderonesecond;

	private static final int VISIBLE = View.VISIBLE;
	private static final int GONE = View.GONE;

	private static final int TIME_TO_WAIT = 200;

	private static final int TIME_TO_WAIT_BACKPACK = 1000;

	private static final String FIRST_TEST_SOUND_NAME = "testSound1";
	private static final String SECOND_TEST_SOUND_NAME = "testSound2";

	private static final String FIRST_TEST_UNPACKING_SOUND_NAME = "testSound11";
	private static final String SECOND_TEST_UNPACKING_SOUND_NAME = "testSound21";

	private static final String SECOND_SPRITE_NAME = "second_sprite";

	private String rename;
	private String renameDialogTitle;
	private String delete;
	private String deleteDialogTitle;
	private String backPackDialogTitle;
	private String backPackTitle;

	private SoundInfo soundInfo;
	private SoundInfo soundInfo2;

	private File externalSoundFile;
	private File soundFile;

	private ArrayList<SoundInfo> soundInfoList;

	private CheckBox firstCheckBox;
	private CheckBox secondCheckBox;

	private ProjectManager projectManager;

	private BackPackListManager backPackListManager;

	public SoundFragmentTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject();

		backPackListManager = BackPackListManager.getInstance();
		projectManager = ProjectManager.getInstance();
		soundInfoList = projectManager.getCurrentSprite().getSoundList();

		soundFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "longsound.mp3",
				RESOURCE_SOUND, getInstrumentation().getContext(), UiTestUtils.FileTypes.SOUND);
		soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(FIRST_TEST_SOUND_NAME);

		soundFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "testsoundui.mp3",
				RESOURCE_SOUND2, getInstrumentation().getContext(), UiTestUtils.FileTypes.SOUND);
		soundInfo2 = new SoundInfo();
		soundInfo2.setSoundFileName(soundFile.getName());
		soundInfo2.setTitle(SECOND_TEST_SOUND_NAME);

		soundInfoList.add(soundInfo);
		soundInfoList.add(soundInfo2);

		projectManager.getFileChecksumContainer().addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
		projectManager.getFileChecksumContainer().addChecksum(soundInfo2.getChecksum(), soundInfo2.getAbsolutePath());

		externalSoundFile = UiTestUtils.createTestMediaFile(Constants.DEFAULT_ROOT + "/externalSoundFile.mp3",
				RESOURCE_SOUND, getActivity());

		UiTestUtils.getIntoSoundsFromMainMenu(solo);

		rename = solo.getString(R.string.rename);
		renameDialogTitle = solo.getString(R.string.rename_sound_dialog);
		backPackDialogTitle = solo.getString(R.string.backpack);
		backPackTitle = solo.getString(R.string.backpack_title);
		delete = solo.getString(R.string.delete);
		deleteDialogTitle = solo.getString(R.string.dialog_confirm_delete_sound_title);

		if (getSoundAdapter().getShowDetails()) {
			solo.clickOnMenuItem(solo.getString(R.string.hide_details), true);
			solo.sleep(TIME_TO_WAIT);
		}
	}

	@Override
	public void tearDown() throws Exception {
		externalSoundFile.delete();
		super.tearDown();
	}

	public void testMoveSoundUp() {
		moveSoundUp(SECOND_TEST_SOUND_NAME);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.sounds));

		assertEquals("Sound didn't move up (testMoveSoundUp 1)", SECOND_TEST_SOUND_NAME, getSoundTitle(0));
		assertEquals("Sound didn't move up (testMoveSoundUp 2)", FIRST_TEST_SOUND_NAME, getSoundTitle(1));
	}

	public void testMoveSoundDown() {
		moveSoundDown(FIRST_TEST_SOUND_NAME);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.sounds));

		assertEquals("Sound didn't move down (testMoveSoundDown 1)", SECOND_TEST_SOUND_NAME, getSoundTitle(0));
		assertEquals("Sound didn't move down (testMoveSoundDown 2)", FIRST_TEST_SOUND_NAME, getSoundTitle(1));
	}

	public void testMoveSoundToBottom() {
		moveSoundToBottom(FIRST_TEST_SOUND_NAME);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.sounds));

		assertEquals("Sound didn't move bottom (testMoveSoundToBottom 1)", SECOND_TEST_SOUND_NAME, getSoundTitle(0));
		assertEquals("Sound didn't move bottom (testMoveSoundToBottom 2)", FIRST_TEST_SOUND_NAME, getSoundTitle(1));
	}

	public void testMoveSoundToTop() {
		moveSoundToTop(SECOND_TEST_SOUND_NAME);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.sounds));

		assertEquals("Sound didn't move top (testMoveSoundToTop 1)", SECOND_TEST_SOUND_NAME, getSoundTitle(0));
		assertEquals("Sound didn't move top (testMoveSoundToTop 2)", FIRST_TEST_SOUND_NAME, getSoundTitle(1));
	}

	public void testMoveSoundUpFirstEntry() {
		moveSoundUp(FIRST_TEST_SOUND_NAME);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.sounds));

		assertEquals("Sound moved (testMoveSoundUpFirstEntry 1)", FIRST_TEST_SOUND_NAME, getSoundTitle(0));
		assertEquals("Sound moved (testMoveSoundUpFirstEntry 2)", SECOND_TEST_SOUND_NAME, getSoundTitle(1));
	}

	public void testMoveSoundDownLastEntry() {
		moveSoundDown(SECOND_TEST_SOUND_NAME);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.sounds));

		assertEquals("Sound moved (testMoveSoundDownLastEntry 1)", FIRST_TEST_SOUND_NAME, getSoundTitle(0));
		assertEquals("Sound moved (testMoveSoundDownLastEntry 2)", SECOND_TEST_SOUND_NAME, getSoundTitle(1));
	}

	public void testMoveSoundToTopFirstEntry() {
		moveSoundToTop(FIRST_TEST_SOUND_NAME);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.sounds));

		assertEquals("Sound moved (testMoveSoundToTopFirstEntry 1)", FIRST_TEST_SOUND_NAME, getSoundTitle(0));
		assertEquals("Sound moved (testMoveSoundToTopFirstEntry 2)", SECOND_TEST_SOUND_NAME, getSoundTitle(1));
	}

	public void testMoveSoundToBottomLastEntry() {
		moveSoundToBottom(SECOND_TEST_SOUND_NAME);
		solo.sleep(TIME_TO_WAIT);
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.sounds));

		assertEquals("Sound moved (testMoveSoundToBottomLastEntry 1)", FIRST_TEST_SOUND_NAME, getSoundTitle(0));
		assertEquals("Sound moved (testMoveSoundToBottomLastEntry 2)", SECOND_TEST_SOUND_NAME, getSoundTitle(1));
	}

	public void testInitialLayout() {
		assertFalse("Initially showing details", getSoundAdapter().getShowDetails());
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		checkPlayAndStopButton(R.string.sound_play);
	}

	public void testAddNewSoundDialog() {
		String addSoundFromRecorderText = solo.getString(R.string.add_sound_from_recorder);
		String addSoundFromGalleryText = solo.getString(R.string.add_sound_choose_file);
		String addSoundFromMediaLibrary = solo.getString(R.string.add_look_media_library);

		assertFalse("Entry to add sound from recorder should not be visible", solo.searchText(addSoundFromRecorderText));
		assertFalse("Entry to add sound from gallery should not be visible", solo.searchText(addSoundFromGalleryText));
		assertFalse("Entry to add sound from library should not be visible", solo.searchText(addSoundFromMediaLibrary));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);

		assertTrue("Entry to add sound from recorder not visible", solo.searchText(addSoundFromRecorderText));
		assertTrue("Entry to add sound from gallery not visible", solo.searchText(addSoundFromGalleryText));
		assertTrue("Entry to add sound from library not visible", solo.searchText(addSoundFromMediaLibrary));
	}

	public void testCopySoundContextMenu() {
		SoundAdapter adapter = getSoundAdapter();
		assertNotNull("Could not get Adapter", adapter);

		int oldCount = adapter.getCount();

		clickOnContextMenuItem(FIRST_TEST_SOUND_NAME, solo.getString(R.string.copy));

		solo.waitForDialogToClose(1000);

		int newCount = adapter.getCount();

		assertEquals("Old count was not correct", 2, oldCount);
		assertEquals("New count is not correct - one sound should be copied", 3, newCount);
		assertEquals("Count of the soundList is not correct", newCount, getCurrentNumberOfSounds());
	}

	public void testCopySoundActionBar() {

		int numberOfSoundsBeforeCopy = getCurrentNumberOfSounds();

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		solo.clickOnCheckBox(0);

		solo.clickOnText(SECOND_TEST_SOUND_NAME);
		assertFalse("Mediaplayer is playing within the checkbox action", soundInfo2.isPlaying);
		solo.clickOnText(SECOND_TEST_SOUND_NAME);

		UiTestUtils.acceptAndCloseActionMode(solo);

		int numberOfSoundsAfterCopy = getCurrentNumberOfSounds();

		assertEquals("No sound has been copied!", ++numberOfSoundsBeforeCopy, numberOfSoundsAfterCopy);
	}

	public void testCopySelectAll() {
		int numberOfSoundsBeforeCopy = getCurrentNumberOfSounds();
		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		solo.clickOnText(selectAll);
		solo.sleep(TIME_TO_WAIT);
		for (CheckBox checkBox : solo.getCurrentViews(CheckBox.class)) {
			assertTrue("CheckBox is not Checked!", checkBox.isChecked());
		}
		assertFalse("Select All is still shown", solo.waitForText(selectAll, 1, 200, false, true));
		UiTestUtils.acceptAndCloseActionMode(solo);
		int numberOfSoundsAfterCopy = getCurrentNumberOfSounds();

		assertEquals("No all sounds have been copied!", numberOfSoundsBeforeCopy * 2, numberOfSoundsAfterCopy);
	}

	public void testDeleteSoundContextMenu() {
		SoundAdapter adapter = getSoundAdapter();
		assertNotNull("Could not get Adapter", adapter);

		int oldCount = adapter.getCount();

		clickOnContextMenuItem(SECOND_TEST_SOUND_NAME, solo.getString(R.string.delete));
		solo.waitForText(deleteDialogTitle);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.waitForDialogToClose();

		int newCount = adapter.getCount();

		assertEquals("Old count was not correct", 2, oldCount);
		assertEquals("New count is not correct - one sound should be deleted", 1, newCount);
		assertEquals("Count of the soundList is not correct", newCount, getCurrentNumberOfSounds());
	}

	public void testRenameSoundContextMenu() {
		String newSoundName = "TeStSoUNd1";

		renameSound(FIRST_TEST_SOUND_NAME, newSoundName);
		solo.sleep(50);

		assertEquals("Sound not renamed in SoundList", newSoundName, getSoundTitle(0));
		assertTrue("Sound not renamed in actual view", solo.searchText(newSoundName));
	}

	public void testSoundTimeUnderOneSecond() {
		String soundName = "shortSound";
		addNewSound(soundName, "soundunderonesecond.m4p", RESOURCE_SHORT_SOUND);
		solo.sleep(1000);
		assertTrue("Sound has a length of 00:00", !solo.searchText("00:00"));
	}

	public void testEqualSoundNames() {
		final String assertMessageText = "Sound not renamed correctly";

		String defaultSoundName = "renamedSound";
		String newSoundName = "newTestSound";
		addNewSound(newSoundName, "longsound.mp3", RESOURCE_SOUND);

		renameSound(FIRST_TEST_SOUND_NAME, defaultSoundName);
		renameSound(SECOND_TEST_SOUND_NAME, defaultSoundName);
		renameSound(newSoundName, defaultSoundName);

		String expectedSoundName = defaultSoundName + "1";
		assertEquals(assertMessageText, expectedSoundName, getSoundTitle(1));

		expectedSoundName = defaultSoundName + "2";
		solo.sleep(TIME_TO_WAIT);
		assertEquals(assertMessageText, expectedSoundName, getSoundTitle(2));

		newSoundName = "x";

		expectedSoundName = defaultSoundName + "1";
		renameSound(expectedSoundName, newSoundName);

		assertNotSame("Sound not renamed", expectedSoundName, getSoundTitle(1));
		renameSound(newSoundName, defaultSoundName);
		solo.sleep(TIME_TO_WAIT);
		assertEquals(assertMessageText, expectedSoundName, getSoundTitle(1));
	}

	public void testShowAndHideDetails() {
		int timeToWait = 300;

		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		checkPlayAndStopButton(R.string.sound_play);
		solo.clickOnMenuItem(solo.getString(R.string.show_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);
		checkPlayAndStopButton(R.string.sound_play);

		// Test if showDetails is remembered after pressing back
		solo.goBack();
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnText(solo.getString(R.string.sounds));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, VISIBLE, GONE);
		checkPlayAndStopButton(R.string.sound_play);

		solo.clickOnMenuItem(solo.getString(R.string.hide_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		checkPlayAndStopButton(R.string.sound_play);
	}

	public void testPlayAndStopSound() {
		// Mute before playing sound
		AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);

		int timeToWait = 3000;

		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		SoundInfo soundInfo = soundInfoList.get(0);
		assertFalse("Mediaplayer is playing although no play button was touched", soundInfo.isPlaying);

		ImageButton playAndStopImageButton = (ImageButton) solo.getView(R.id.fragment_sound_item_image_button);

		solo.clickOnView(playAndStopImageButton);
		solo.sleep(timeToWait);

		assertTrue("Mediaplayer is not playing although play button was touched", soundInfo.isPlaying);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		checkPlayAndStopButton(R.string.sound_stop);

		solo.sleep(timeToWait);
		solo.clickOnView(playAndStopImageButton);
		solo.sleep(timeToWait);

		assertFalse("Mediaplayer is playing after touching stop button", soundInfo.isPlaying);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		checkPlayAndStopButton(R.string.sound_play);

		// test the text fields
		solo.clickOnView(playAndStopImageButton);
		solo.sleep(timeToWait);

		assertTrue("Mediaplayer is not playing although play button was touched", soundInfo.isPlaying);
		assertFalse("Mediaplayer is not playing although play button was touched", soundInfo2.isPlaying);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		checkPlayAndStopButton(R.string.sound_stop);

		solo.sleep(timeToWait);
		solo.clickOnText(FIRST_TEST_SOUND_NAME);
		solo.sleep(timeToWait);

		assertFalse("Mediaplayer is playing after touching stop button", soundInfo.isPlaying);
		assertFalse("Mediaplayer is not playing although play button was touched", soundInfo2.isPlaying);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		checkPlayAndStopButton(R.string.sound_play);

		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
	}

	public void testStopSoundOnFragmentChange() {
		// Mute before playing sound
		AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);

		int timeToWait = 1500;

		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		SoundInfo soundInfo = soundInfoList.get(0);
		assertFalse("Mediaplayer is playing although no play button was touched", soundInfo.isPlaying);

		ImageButton playAndStopImageButton = (ImageButton) solo.getView(R.id.fragment_sound_item_image_button);

		solo.clickOnView(playAndStopImageButton);
		solo.sleep(timeToWait);

		assertTrue("Mediaplayer is not playing although play button was touched", soundInfo.isPlaying);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		checkPlayAndStopButton(R.string.sound_stop);

		solo.sleep(timeToWait);
		UiTestUtils.switchToFragmentInScriptActivity(solo, UiTestUtils.LOOKS_INDEX);
		UiTestUtils.switchToFragmentInScriptActivity(solo, UiTestUtils.SOUNDS_INDEX);

		assertFalse("Mediaplayer is playing after switching fragments", soundInfo.isPlaying);
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		checkPlayAndStopButton(R.string.sound_play);

		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
	}

	public void testAddSound() {
		int expectedNumberOfSounds = getCurrentNumberOfSounds() + 1;

		String newSoundName = "Added Sound";
		addNewSound(newSoundName, "longsound.mp3", RESOURCE_SOUND);

		assertEquals("No sound was added", expectedNumberOfSounds, getCurrentNumberOfSounds());
		assertTrue("Sound not added in actual view", solo.searchText(newSoundName));
	}

	public void testGetSoundFromMediaLibrary() {
		String mediaLibraryText = solo.getString(R.string.add_look_media_library);
		int numberSoundsBefore = ProjectManager.getInstance().getCurrentSprite().getSoundList().size();

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(mediaLibraryText);
		solo.clickOnText(mediaLibraryText);
		solo.waitForWebElement(By.className("program"));
		solo.clickOnWebElement(By.className("program"));
		solo.waitForFragmentByTag(SoundFragment.TAG);
		solo.sleep(TIME_TO_WAIT);
		int numberSoundsAfter = ProjectManager.getInstance().getCurrentSprite().getSoundList().size();
		assertEquals("No Sound was added from Media Library!", numberSoundsBefore + 1, numberSoundsAfter);
		String newSoundName = ProjectManager.getInstance().getCurrentSprite().getSoundList().get(numberSoundsBefore).getTitle();
		assertEquals("Temp File for " + newSoundName + " was not deleted!", false, UiTestUtils
				.checkTempFileFromMediaLibrary(Constants.TMP_SOUNDS_PATH, newSoundName));
		solo.sleep(TIME_TO_WAIT);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(mediaLibraryText);
		solo.clickOnText(mediaLibraryText);
		solo.waitForWebElement(By.className("program"));
		solo.clickOnWebElement(By.className("program"));
		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForFragmentByTag(SoundFragment.TAG);
		solo.sleep(TIME_TO_WAIT);
		numberSoundsAfter = ProjectManager.getInstance().getCurrentSprite().getSoundList().size();
		assertEquals("Sound was added from Media Library!", numberSoundsBefore + 1, numberSoundsAfter);
		newSoundName = ProjectManager.getInstance().getCurrentSprite().getSoundList().get(numberSoundsBefore)
				.getTitle();
		assertEquals("Temp File for " + newSoundName + " was not deleted!", false, UiTestUtils
				.checkTempFileFromMediaLibrary(Constants.TMP_SOUNDS_PATH, newSoundName));
		solo.sleep(TIME_TO_WAIT);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(mediaLibraryText);
		solo.clickOnText(mediaLibraryText);
		solo.waitForWebElement(By.className("program"));
		solo.clickOnWebElement(By.className("program"));
		solo.waitForDialogToOpen();
		solo.clickOnView(solo.getView(R.id.dialog_overwrite_media_radio_rename));
		UiTestUtils.enterText(solo, 0, "testMedia");
		solo.waitForText(solo.getString(R.string.ok));
		solo.clickOnText(solo.getString(R.string.ok));
		solo.waitForFragmentByTag(SoundFragment.TAG);
		solo.sleep(TIME_TO_WAIT);
		numberSoundsAfter = ProjectManager.getInstance().getCurrentSprite().getSoundList().size();
		assertEquals("Second Sound was not added from Media Library!", numberSoundsBefore + 2, numberSoundsAfter);
		newSoundName = ProjectManager.getInstance().getCurrentSprite().getSoundList().get(numberSoundsBefore).getTitle();
		assertEquals("Temp File for " + newSoundName + " was not deleted!", false, UiTestUtils
				.checkTempFileFromMediaLibrary(Constants.TMP_SOUNDS_PATH, newSoundName));
		newSoundName = ProjectManager.getInstance().getCurrentSprite().getSoundList().get(numberSoundsBefore + 1)
				.getTitle();
		assertEquals("Temp File for  " + newSoundName + " was not deleted!(", false, UiTestUtils
				.checkTempFileFromMediaLibrary(Constants.TMP_SOUNDS_PATH, newSoundName));
	}

	@Device
	public void testAddSoundFromMediaLibraryWithNoInternet() {
		String mediaLibraryText = solo.getString(R.string.add_look_media_library);
		int retryCounter = 0;
		WifiManager wifiManager = (WifiManager) this.getActivity().getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(false);
		while (Utils.isNetworkAvailable(getActivity())) {
			solo.sleep(2000);
			if (retryCounter > 30) {
				break;
			}
			retryCounter++;
		}
		retryCounter = 0;
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(mediaLibraryText);
		solo.clickOnText(mediaLibraryText);
		assertTrue("Should be in Sound Fragment", solo.waitForText(FIRST_TEST_SOUND_NAME));
		wifiManager.setWifiEnabled(true);
		while (!Utils.isNetworkAvailable(getActivity())) {
			solo.sleep(2000);
			if (retryCounter > 30) {
				break;
			}
			retryCounter++;
		}
	}

	public void testGetSoundFromExternalSource() {
		int expectedNumberOfSounds = getCurrentNumberOfSounds() + 1;
		String checksumExternalSoundFile = Utils.md5Checksum(externalSoundFile);

		// Use of MockSoundActivity
		Bundle bundleForExternalSource = new Bundle();
		bundleForExternalSource.putString("filePath", externalSoundFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(), MockSoundActivity.class);
		intent.putExtras(bundleForExternalSource);

		getSoundFragment().startActivityForResult(intent, SoundController.REQUEST_SELECT_MUSIC);
		solo.sleep(1000);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.assertCurrentActivity("Should be in SoundActivity", ScriptActivity.class.getSimpleName());

		assertTrue("External file not added from mockActivity", solo.searchText("externalSoundFile"));
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(checksumExternalSoundFile));

		boolean isInSoundInfoList = false;
		for (SoundInfo soundInfo : projectManager.getCurrentSprite().getSoundList()) {
			if (soundInfo.getChecksum().equalsIgnoreCase(checksumExternalSoundFile)) {
				isInSoundInfoList = true;
			}
		}
		if (!isInSoundInfoList) {
			fail("File not added in SoundInfoList");
		}

		checkIfNumberOfSoundsIsEqual(expectedNumberOfSounds);
	}

	public void testRenameActionModeChecking() {
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		checkPlayAndStopButton(R.string.sound_play);
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		// Check if checkboxes are visible
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, VISIBLE);
		checkPlayAndStopButton(R.string.sound_play);

		checkIfCheckboxesAreCorrectlyChecked(false, false);
		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(true, false);

		// Check if only single-selection is possible
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
	}

	public void testRenameActionModeIfNothingSelected() {
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		// Check if rename ActionMode disappears if nothing was selected
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("Rename dialog showed up", solo.waitForText(renameDialogTitle, 0, TIME_TO_WAIT));
		assertFalse("ActionMode didn't disappear", solo.waitForText(rename, 0, TIME_TO_WAIT));
	}

	public void testBackPackActionModeIfNothingSelected() {

		if (!BuildConfig.FEATURE_BACKPACK_ENABLED) {
			return;
		}

		UiTestUtils.openActionMode(solo, solo.getString(R.string.backpack), R.id.backpack, getActivity());
		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		// Check if rename ActionMode disappears if nothing was selected
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.sleep(2000);
		assertFalse("BackPack dialog title didn't show up", solo.waitForText(backPackDialogTitle, 0, TIME_TO_WAIT));
	}

	public void testBackPackActionModeIfSomethingSelectedAndPressingBack() {

		if (!BuildConfig.FEATURE_BACKPACK_ENABLED) {
			return;
		}

		UiTestUtils.openActionMode(solo, solo.getString(R.string.backpack), R.id.backpack, getActivity());
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		assertTrue("BackPack dialog title didn't show up", solo.waitForText(backPackDialogTitle, 0, TIME_TO_WAIT));

		solo.goBack();
		assertFalse("BackPack title didn't show up", solo.waitForText(backPackTitle, 0, TIME_TO_WAIT));
	}

	public void testBackPackActionModeIfSomethingSelectedForBackPack() {

		if (!BuildConfig.FEATURE_BACKPACK_ENABLED) {
			return;
		}

		UiTestUtils.openActionMode(solo, solo.getString(R.string.backpack), R.id.backpack, getActivity());
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.waitForActivity(BackPackActivity.class.getSimpleName(), TIME_TO_WAIT_BACKPACK);

		assertTrue("BackPack title didn't show up", solo.waitForText(backPackTitle, 0, TIME_TO_WAIT));
	}

	public void testBackPackSoundContextMenu() {

		if (!BuildConfig.FEATURE_BACKPACK_ENABLED) {
			return;
		}

		SoundAdapter adapter = getSoundAdapter();
		assertNotNull("Could not get Adapter", adapter);
		clickOnContextMenuItem(FIRST_TEST_SOUND_NAME, solo.getString(R.string.backpack));
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("BackPack title didn't show up", solo.waitForText(backPackTitle, 0, TIME_TO_WAIT));
	}

	public void testBackPackSoundContextMenuAndCheckPlaying() {

		if (!BuildConfig.FEATURE_BACKPACK_ENABLED) {
			return;
		}

		ImageButton playAndStopImageButton = (ImageButton) solo.getView(R.id.fragment_sound_item_image_button);
		SoundAdapter adapter = getSoundAdapter();
		assertNotNull("Could not get Adapter", adapter);
		clickOnContextMenuItem(FIRST_TEST_SOUND_NAME, solo.getString(R.string.backpack));
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);

		assertTrue("BackPack title didn't show up", solo.waitForText(backPackTitle, 0, TIME_TO_WAIT));
		soundInfoList = backPackListManager.getSoundInfoArrayList();
		SoundInfo soundInfo = soundInfoList.get(0);
		solo.clickOnView(playAndStopImageButton);
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		assertTrue("Mediaplayer is not playing although play button was touched", soundInfo.isPlaying);
	}

	public void testSimpleUnpacking() {

		if (!BuildConfig.FEATURE_BACKPACK_ENABLED) {
			return;
		}

		SoundAdapter adapter = getSoundAdapter();

		assertNotNull("Could not get Adapter", adapter);
		clickOnContextMenuItem(FIRST_TEST_SOUND_NAME, solo.getString(R.string.backpack));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(FIRST_TEST_UNPACKING_SOUND_NAME, solo.getString(R.string.unpack));
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.goBack();

		assertTrue("Sound wasn't unpacked!", solo.waitForText(FIRST_TEST_UNPACKING_SOUND_NAME, 0, TIME_TO_WAIT));
	}

	public void testSimpleUnpackingAndDelete() {

		if (!BuildConfig.FEATURE_BACKPACK_ENABLED) {
			return;
		}

		SoundAdapter adapter = getSoundAdapter();
		int oldCount = adapter.getCount();

		assertNotNull("Could not get Adapter", adapter);
		clickOnContextMenuItem(FIRST_TEST_SOUND_NAME, solo.getString(R.string.backpack));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.goBack();
		clickOnContextMenuItem(FIRST_TEST_SOUND_NAME, solo.getString(R.string.delete));
		solo.waitForText(deleteDialogTitle);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.sleep(50);
		UiTestUtils.openActionMode(solo, solo.getString(R.string.unpacking), R.id.unpacking, getActivity());
		clickOnContextMenuItem(FIRST_TEST_UNPACKING_SOUND_NAME, solo.getString(R.string.unpack));
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.goBack();

		assertTrue("Sound wasn't unpacked!", solo.waitForText(FIRST_TEST_UNPACKING_SOUND_NAME, 0, TIME_TO_WAIT));

		int newCount = adapter.getCount();
		assertEquals("Counts have to be equal", oldCount, newCount);
	}

	public void testMultipleUnpacking() {

		if (!BuildConfig.FEATURE_BACKPACK_ENABLED) {
			return;
		}

		SoundAdapter adapter = getSoundAdapter();
		int oldCount = adapter.getCount();

		assertNotNull("Could not get Adapter", adapter);
		clickOnContextMenuItem(FIRST_TEST_SOUND_NAME, solo.getString(R.string.backpack));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(FIRST_TEST_UNPACKING_SOUND_NAME, solo.getString(R.string.unpack));
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.goBack();

		assertTrue("Sound wasn't unpacked!", solo.waitForText(FIRST_TEST_UNPACKING_SOUND_NAME, 0, TIME_TO_WAIT));
		clickOnContextMenuItem(SECOND_TEST_SOUND_NAME, solo.getString(R.string.backpack));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(SECOND_TEST_UNPACKING_SOUND_NAME, solo.getString(R.string.unpack));
		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
		solo.goBack();

		assertTrue("Sound wasn't unpacked!", solo.waitForText(SECOND_TEST_UNPACKING_SOUND_NAME, 0, TIME_TO_WAIT));
		int newCount = adapter.getCount();
		assertEquals("There are sounds missing", oldCount + 2, newCount);
	}

	//  TODO: fix original code. now, test should check what really should happen.
	//	public void testBackPackAndUnPackFromDifferentProgrammes() {
	//		UiTestUtils.createTestProject(UiTestUtils.PROJECTNAME1);
	//		SoundAdapter adapter = getSoundAdapter();
	//
	//		assertNotNull("Could not get Adapter", adapter);
	//		clickOnContextMenuItem(FIRST_TEST_SOUND_NAME, solo.getString(R.string.backpack));
	//		solo.sleep(TIME_TO_WAIT_BACKPACK);
	//		UiTestUtils.clickOnHomeActionBarButton(solo);
	//		solo.clickOnText(solo.getString(R.string.programs));
	//		UiTestUtils.clickOnTextInList(solo, solo.getString(R.string.programs));
	//		solo.sleep(TIME_TO_WAIT_BACKPACK);
	//		UiTestUtils.clickOnTextInList(solo, UiTestUtils.PROJECTNAME1);
	//		solo.sleep(TIME_TO_WAIT_BACKPACK);
	//		UiTestUtils.clickOnTextInList(solo, "cat");
	//		solo.sleep(TIME_TO_WAIT_BACKPACK);
	//		solo.clickOnText(solo.getString(R.string.sounds));
	//		UiTestUtils.openActionMode(solo, solo.getString(R.string.unpacking), R.id.unpacking, getActivity());
	//		solo.sleep(TIME_TO_WAIT_BACKPACK);
	//		clickOnContextMenuItem(FIRST_TEST_SOUND_NAME, solo.getString(R.string.unpack));
	//		solo.waitForDialogToClose(TIME_TO_WAIT_BACKPACK);
	//		solo.goBack();
	//
	//		assertTrue("Sound wasn't unpacked!", solo.waitForText(FIRST_TEST_SOUND_NAME, 0, TIME_TO_WAIT));
	//		SoundInfo unpackedSound = adapter.getItem(0);
	//		File file = new File(unpackedSound.getAbsolutePath());
	//		assertTrue("File wasn't copied!", file.exists());
	//		assertTrue("SoundInfo shows wrong folder!", unpackedSound.getAbsolutePath().contains(UiTestUtils.PROJECTNAME1));
	//	}

	public void testSingleUnpackingWithActionBar() {

		if (!BuildConfig.FEATURE_BACKPACK_ENABLED) {
			return;
		}

		SoundAdapter adapter = getSoundAdapter();
		int oldCount = adapter.getCount();

		assertNotNull("Could not get Adapter", adapter);
		clickOnContextMenuItem(FIRST_TEST_SOUND_NAME, solo.getString(R.string.backpack));
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		solo.goBack();

		clickOnContextMenuItem(SECOND_TEST_SOUND_NAME, solo.getString(R.string.backpack));
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.unpacking), R.id.unpacking, getActivity());

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(true, false);

		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForDialogToClose(1000);

		solo.goBack();

		int newCount = adapter.getCount();
		assertEquals("There are sounds missing", oldCount + 1, newCount);
	}

	public void testMultipleUnpackingWithActionBar() {

		if (!BuildConfig.FEATURE_BACKPACK_ENABLED) {
			return;
		}

		SoundAdapter adapter = getSoundAdapter();
		int oldCount = adapter.getCount();

		assertNotNull("Could not get Adapter", adapter);
		clickOnContextMenuItem(FIRST_TEST_SOUND_NAME, solo.getString(R.string.backpack));
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		solo.goBack();

		clickOnContextMenuItem(SECOND_TEST_SOUND_NAME, solo.getString(R.string.backpack));
		solo.sleep(TIME_TO_WAIT_BACKPACK);

		UiTestUtils.openActionMode(solo, solo.getString(R.string.unpacking), R.id.unpacking, getActivity());

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);

		UiTestUtils.acceptAndCloseActionMode(solo);

		solo.waitForDialogToClose(1000);

		solo.goBack();

		int newCount = adapter.getCount();
		assertEquals("There are sounds missing", oldCount + 2, newCount);
	}

	public void testBackPackAndUnPackFromDifferentSprites() {

		if (!BuildConfig.FEATURE_BACKPACK_ENABLED) {
			return;
		}

		UiTestUtils.createTestProjectWithTwoSprites(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		SoundAdapter adapter = getSoundAdapter();

		assertNotNull("Could not get Adapter", adapter);
		clickOnContextMenuItem(FIRST_TEST_SOUND_NAME, solo.getString(R.string.backpack));
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.clickOnText(SECOND_SPRITE_NAME);
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		solo.clickOnText(solo.getString(R.string.sounds));
		UiTestUtils.openActionMode(solo, solo.getString(R.string.unpacking), R.id.unpacking, getActivity());
		solo.sleep(TIME_TO_WAIT_BACKPACK);
		clickOnContextMenuItem(FIRST_TEST_SOUND_NAME, solo.getString(R.string.unpack));
		solo.waitForDialogToClose(1000);
		solo.goBack();

		assertTrue("Sound wasn't unpacked!", solo.waitForText(FIRST_TEST_SOUND_NAME, 0, TIME_TO_WAIT));
	}

	public void testRenameActionModeIfSomethingSelectedAndPressingBack() {
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		solo.goBack();

		// Check if rename ActionMode disappears if back was pressed
		assertFalse("Rename dialog showed up", solo.waitForText(renameDialogTitle, 0, TIME_TO_WAIT));
		assertFalse("ActionMode didn't disappear", solo.waitForText(rename, 0, TIME_TO_WAIT));
	}

	public void testRenameActionModeEqualSoundNames() {
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int checkboxIndex = 1;

		// Rename second sound to the name of the first
		String newSoundName = FIRST_TEST_SOUND_NAME;

		solo.clickOnCheckBox(checkboxIndex);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		UiTestUtils.acceptAndCloseActionMode(solo);

		assertTrue("Rename dialog didn't show up", solo.searchText(renameDialogTitle, true));
		assertTrue("No EditText with actual sound name", solo.searchEditText(SECOND_TEST_SOUND_NAME));

		UiTestUtils.enterText(solo, 0, newSoundName);
		solo.sendKey(Solo.ENTER);

		// If an already existing name was entered a counter should be appended
		String expectedNewSoundName = newSoundName + "1";
		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		solo.sleep(TIME_TO_WAIT);
		assertEquals("Sound is not correctly renamed in SoundList (1 should be appended)", expectedNewSoundName,
				soundInfoList.get(checkboxIndex).getTitle());
		assertTrue("Sound not renamed in actual view", solo.searchText(expectedNewSoundName, true));
	}

	public void testBottomBarAndContextMenuOnActionModes() {
		LinearLayout bottomBarLayout = (LinearLayout) solo.getView(R.id.bottom_bar);
		ImageButton addButton = (ImageButton) bottomBarLayout.findViewById(R.id.button_add);
		ImageButton playButton = (ImageButton) bottomBarLayout.findViewById(R.id.button_play);

		int timeToWait = 300;
		String addDialogTitle = solo.getString(R.string.sound_select_source);

		checkIfContextMenuAppears(true, false);

		// Test on rename ActionMode
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.waitForText(rename, 1, timeToWait, false, true);

		checkIfContextMenuAppears(false, false);

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.sleep(500);

		checkIfContextMenuAppears(true, false);

		// Test on delete ActionMode
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.waitForText(delete, 1, timeToWait, false, true);

		checkIfContextMenuAppears(false, true);

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.sleep(500);

		checkIfContextMenuAppears(true, true);
	}

	public void testItemClick() {
		UiTestUtils.clickOnActionBar(solo, R.id.delete);
		solo.clickInList(1);
		solo.sleep(TIME_TO_WAIT);

		ArrayList<CheckBox> checkBoxList = solo.getCurrentViews(CheckBox.class);
		assertTrue("CheckBox not checked", checkBoxList.get(0).isChecked());

		UiTestUtils.acceptAndCloseActionMode(solo);
		assertTrue("default project not visible", solo.searchText(solo.getString(R.string.yes)));
		solo.clickOnButton(solo.getString(R.string.yes));

		assertFalse("Sound not deleted", solo.waitForText(FIRST_TEST_SOUND_NAME, 0, 200));
	}

	public void testDeleteSelectAll() {
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());
		solo.clickOnText(selectAll);
		solo.sleep(TIME_TO_WAIT);
		for (CheckBox checkBox : solo.getCurrentViews(CheckBox.class)) {
			assertTrue("CheckBox is not Checked!", checkBox.isChecked());
		}
		assertFalse("Select All is still shown", solo.waitForText(selectAll, 1, 200, false, true));

		UiTestUtils.acceptAndCloseActionMode(solo);
		String yes = solo.getString(R.string.yes);
		solo.waitForText(yes);
		solo.clickOnText(yes);

		assertFalse("Sound was not Deleted!", solo.waitForText(FIRST_TEST_SOUND_NAME, 1, 200));
		assertFalse("Sound was not Deleted!", solo.waitForText(SECOND_TEST_SOUND_NAME, 1, 200));
	}

	public void testDeleteActionModeCheckingAndTitle() {
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int timeToWaitForTitle = 300;

		String sound = solo.getString(R.string.category_sound);
		String sounds = solo.getString(R.string.sounds);

		assertFalse("Sound should not be displayed in title", solo.waitForText(sound, 5, 300, false, true));

		// Check if checkboxes are visible
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, VISIBLE);
		checkPlayAndStopButton(R.string.sound_play);

		checkIfCheckboxesAreCorrectlyChecked(false, false);

		int expectedNumberOfSelectedSounds = 1;
		String expectedTitle = delete + " " + expectedNumberOfSelectedSounds + " " + sound;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(true, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedSounds = 2;
		expectedTitle = delete + " " + expectedNumberOfSelectedSounds + " " + sounds;

		// Check if multiple-selection is possible
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		assertTrue("Title not as aspected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedNumberOfSelectedSounds = 1;
		expectedTitle = delete + " " + expectedNumberOfSelectedSounds + " " + sound;

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));

		expectedTitle = delete;

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		assertTrue("Title not as expected", solo.waitForText(expectedTitle, 0, timeToWaitForTitle, false, true));
	}

	public void testDeleteActionModeIfNothingSelected() {
		int expectedNumberOfSounds = getCurrentNumberOfSounds();

		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		// Check if rename ActionMode disappears if nothing was selected
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("Delete dialog showed up", solo.waitForText(deleteDialogTitle, 0, TIME_TO_WAIT));
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));

		checkIfNumberOfSoundsIsEqual(expectedNumberOfSounds);
	}

	public void testDeleteActionModeIfSelectedAndPressingBack() {
		int expectedNumberOfSounds = getCurrentNumberOfSounds();

		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		solo.goBack();

		// Check if rename ActionMode disappears if back was pressed
		assertFalse("Delete dialog showed up", solo.waitForText(deleteDialogTitle, 0, TIME_TO_WAIT));
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));

		checkIfNumberOfSoundsIsEqual(expectedNumberOfSounds);
	}

	public void testDeleteActionMode() {
		int expectedNumberOfSounds = getCurrentNumberOfSounds() - 1;

		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));

		checkIfNumberOfSoundsIsEqual(expectedNumberOfSounds);

		assertTrue("Unselected sound '" + FIRST_TEST_SOUND_NAME + "' has been deleted!",
				soundInfoList.contains(soundInfo));

		assertFalse("Selected sound '" + SECOND_TEST_SOUND_NAME + "' was not deleted!",
				soundInfoList.contains(soundInfo2));

		assertFalse("Sound '" + SECOND_TEST_SOUND_NAME + "' has been deleted but is still showing!",
				solo.waitForText(SECOND_TEST_SOUND_NAME, 0, 200, false, false));
	}

	public void testLongClickCancelDeleteAndCopy() {
		assertFalse("Sound is selected!", UiTestUtils.getContextMenuAndGoBackToCheckIfSelected(solo, getActivity(),
				R.id.delete, delete, FIRST_TEST_SOUND_NAME));
		solo.goBack();
		String copy = solo.getString(R.string.copy);
		assertFalse("Sound is selected!", UiTestUtils.getContextMenuAndGoBackToCheckIfSelected(solo, getActivity(),
				R.id.copy, copy, FIRST_TEST_SOUND_NAME));
	}

	public void testAddSoundAndDeleteActionMode() {
		String testSoundName = "testSound";

		addNewSound(testSoundName, "longsound.mp3", RESOURCE_SOUND);
		addNewSound(testSoundName, "longsound.mp3", RESOURCE_SOUND);
		addNewSound(testSoundName, "longsound.mp3", RESOURCE_SOUND);

		solo.sleep(500);

		soundInfoList = projectManager.getCurrentSprite().getSoundList();

		int currentNumberOfSounds = soundInfoList.size();
		assertEquals("Wrong number of sounds", 5, currentNumberOfSounds);

		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int[] checkboxIndicesToCheck = { solo.getCurrentViews(CheckBox.class).size() - 1, 0, 2 };
		int expectedNumberOfSounds = currentNumberOfSounds - checkboxIndicesToCheck.length;

		solo.scrollDown();
		solo.clickOnCheckBox(checkboxIndicesToCheck[0]);
		// Note: We don't actually click the first checkbox on lower resolution devices because
		//       solo won't perform, any sort of scrolling after a checkBox-click at the moment.
		//       But we delete 3 sounds anyways, so the test succeeds.
		solo.scrollToTop();
		solo.clickOnCheckBox(checkboxIndicesToCheck[1]);
		solo.clickOnCheckBox(checkboxIndicesToCheck[2]);

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));

		checkIfNumberOfSoundsIsEqual(expectedNumberOfSounds);
	}

	public void testStopSoundOnContextAndActionMenu() {
		// Mute before playing sound
		AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
		int timeToWait = 1000;

		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		SoundInfo soundInfo = soundInfoList.get(0);
		ImageButton playAndStopImageButton = (ImageButton) solo.getView(R.id.fragment_sound_item_image_button);

		solo.clickOnView(playAndStopImageButton);
		solo.sleep(timeToWait);
		solo.clickLongOnText(FIRST_TEST_SOUND_NAME);
		solo.waitForText(solo.getString(R.string.delete));
		solo.sleep(timeToWait);
		assertFalse("Mediaplayer continues playing even if context menu has been opened", soundInfo.isPlaying);
		solo.goBack();
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		checkPlayAndStopButton(R.string.sound_play);

		solo.clickOnView(playAndStopImageButton);
		solo.sleep(timeToWait);
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());
		solo.sleep(timeToWait);
		assertFalse("Mediaplayer continues playing even if rename action has been opened", soundInfo.isPlaying);
		solo.goBack();
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		checkPlayAndStopButton(R.string.sound_play);

		solo.clickOnView(playAndStopImageButton);
		solo.sleep(timeToWait);
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());
		solo.sleep(timeToWait);
		assertFalse("Mediaplayer continues playing even if delete action has been opened", soundInfo.isPlaying);
		solo.goBack();
		checkVisibilityOfViews(VISIBLE, VISIBLE, GONE, GONE);
		checkPlayAndStopButton(R.string.sound_play);

		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());
		solo.clickOnView(playAndStopImageButton);
		solo.clickOnCheckBox(0);
		solo.sleep(timeToWait);
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.sleep(timeToWait);
		assertFalse("Mediaplayer continues playing even if already deleted", soundInfo.isPlaying);
		solo.goBack();

		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
	}

	public void testEmptyView() {
		assertTrue("No sounds are present!", getCurrentNumberOfSounds() > 0);

		TextView emptyViewHeading = (TextView) solo.getCurrentActivity().findViewById(R.id.fragment_sound_text_heading);
		TextView emptyViewDescription = (TextView) solo.getCurrentActivity().findViewById(
				R.id.fragment_sound_text_description);

		// The Views are gone, we can still make assumptions about them
		assertEquals("Empty View heading is not correct", solo.getString(R.string.sounds), emptyViewHeading.getText()
				.toString());
		assertEquals("Empty View description is not correct", solo.getString(R.string.fragment_sound_text_description),
				emptyViewDescription.getText().toString());

		assertEquals("Empty View shown although there are items in the list!", View.GONE,
				solo.getView(android.R.id.empty).getVisibility());

		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());
		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.sleep(300);

		assertEquals("There are still sounds!", 0, getCurrentNumberOfSounds());
		assertEquals("Empty View not shown although there are items in the list!", View.VISIBLE,
				solo.getView(android.R.id.empty).getVisibility());
	}

	public void testBottombarElementsVisibilty() {
		assertTrue("Bottombar is not visible", solo.getView(R.id.button_play).getVisibility() == VISIBLE);
		assertTrue("Add button is not visible", solo.getView(R.id.button_add).getVisibility() == VISIBLE);
		assertTrue("Play button is not visible", solo.getView(R.id.button_play).getVisibility() == VISIBLE);
		assertTrue("Bottombar separator is not visible",
				solo.getView(R.id.bottom_bar_separator).getVisibility() == VISIBLE);
	}

	public void testSelectAllActionModeButton() {
		String selectAll = solo.getString(R.string.select_all).toUpperCase(Locale.getDefault());

		UiTestUtils.openActionMode(solo, solo.getString(R.string.copy), R.id.copy, getActivity());
		assertTrue("Select All is not shown", solo.getView(R.id.select_all).isShown());

		UiTestUtils.clickOnText(solo, selectAll);
		assertFalse("Select All is still shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), false));

		solo.clickOnCheckBox(0);
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		solo.clickOnCheckBox(1);
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		assertFalse("Select All is still shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), false));

		solo.goBack();

		UiTestUtils.openActionMode(solo, solo.getString(R.string.delete), R.id.delete, getActivity());
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		UiTestUtils.clickOnText(solo, selectAll);
		assertFalse("Select All is still shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), false));

		solo.clickOnCheckBox(0);
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		solo.clickOnCheckBox(1);
		assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		assertFalse("Select All is still shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), false));

		solo.goBack();

		if (BuildConfig.FEATURE_BACKPACK_ENABLED) {

			UiTestUtils.openActionMode(solo, solo.getString(R.string.backpack), R.id.backpack, getActivity());
			assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

			UiTestUtils.clickOnText(solo, selectAll);
			assertFalse("Select All is still shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), false));

			solo.clickOnCheckBox(0);
			assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

			solo.clickOnCheckBox(1);
			assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

			solo.clickOnCheckBox(0);
			solo.clickOnCheckBox(1);
			assertFalse("Select All is still shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), false));

			UiTestUtils.acceptAndCloseActionMode(solo);
			solo.sleep(500);
			solo.goBack();

			UiTestUtils.openActionMode(solo, solo.getString(R.string.unpacking), R.id.unpacking, getActivity());
			UiTestUtils.openActionMode(solo, solo.getString(R.string.unpacking), R.id.unpacking, getActivity());

			assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

			UiTestUtils.clickOnText(solo, selectAll);
			assertFalse("Select All is still shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), false));

			solo.clickOnCheckBox(0);
			assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

			solo.clickOnCheckBox(1);
			assertTrue("Select All is not shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), true));

			solo.clickOnCheckBox(0);
			solo.clickOnCheckBox(1);
			assertFalse("Select All is still shown", UiTestUtils.waitForShownState(solo, solo.getView(R.id.select_all), false));
		}
	}

	private void addNewSound(String title, String fileName, int resource) {
		File soundFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, fileName,
				resource, getInstrumentation().getContext(), UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(title);

		soundInfoList.add(soundInfo);
		projectManager.getFileChecksumContainer().addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
		StorageHandler.getInstance().saveProject(projectManager.getCurrentProject());
	}

	public void testOpenDeleteDialogAndGoBack() {
		int viewAmountBeforeDeleteMode = solo.getCurrentViews().size();
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		assertTrue("Bottom bar is visible", solo.getView(R.id.bottom_bar).getVisibility() == View.GONE);

		int[] checkboxIndicesToCheck = { solo.getCurrentViews(CheckBox.class).size() - 1, 0, 2 };

		solo.scrollDown();
		solo.clickOnCheckBox(checkboxIndicesToCheck[0]);
		solo.scrollToTop();

		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.clickOnButton(solo.getString(R.string.no));

		solo.sleep(500);
		int viewAmountAfterDeleteMode = solo.getCurrentViews().size();

		assertTrue("checkboxes or other delete elements are still visible", viewAmountBeforeDeleteMode == viewAmountAfterDeleteMode);
	}

	private void renameSound(String soundToRename, String newSoundName) {
		clickOnContextMenuItem(soundToRename, solo.getString(R.string.rename));
		assertTrue("Wrong title of dialog", solo.searchText(renameDialogTitle));
		assertTrue("No EditText with actual sound name", solo.searchEditText(soundToRename));

		UiTestUtils.enterText(solo, 0, newSoundName);
		solo.sendKey(Solo.ENTER);
	}

	private SoundFragment getSoundFragment() {
		ScriptActivity activity = (ScriptActivity) solo.getCurrentActivity();
		return (SoundFragment) activity.getFragment(ScriptActivity.FRAGMENT_SOUNDS);
	}

	private SoundAdapter getSoundAdapter() {
		return (SoundAdapter) getSoundFragment().getListAdapter();
	}

	private void checkVisibilityOfViews(int soundNameVisibility, int timePlayedVisibility, int soundSizeVisibility,
			int checkBoxVisibility) {
		assertTrue("Sound name " + getAssertMessageAffix(soundNameVisibility),
				solo.getView(R.id.fragment_sound_item_title_text_view).getVisibility() == soundNameVisibility);
		assertTrue("Chronometer " + getAssertMessageAffix(timePlayedVisibility),
				solo.getView(R.id.fragment_sound_item_time_played_chronometer).getVisibility() == timePlayedVisibility);
		assertTrue("Sound size " + getAssertMessageAffix(soundSizeVisibility),
				solo.getView(R.id.fragment_sound_item_size_text_view).getVisibility() == soundSizeVisibility);
		assertTrue("Checkboxes " + getAssertMessageAffix(checkBoxVisibility),
				solo.getView(R.id.fragment_sound_item_checkbox).getVisibility() == checkBoxVisibility);
	}

	private void checkPlayAndStopButton(int stringId) {
		assertTrue("Wrong media player icon displayed", solo.getView(R.id.fragment_sound_item_image_button)
				.getContentDescription().equals(solo.getString(stringId)));
	}

	private String getAssertMessageAffix(int visibility) {
		String assertMessageAffix = "";
		switch (visibility) {
			case View.VISIBLE:
				assertMessageAffix = "not visible";
				break;
			case View.GONE:
				assertMessageAffix = "not gone";
				break;
			default:
				break;
		}
		return assertMessageAffix;
	}

	private void clickOnContextMenuItem(String soundName, String menuItemName) {
		solo.clickLongOnText(soundName);
		solo.waitForText(menuItemName);
		solo.clickOnText(menuItemName);
	}

	private void checkIfNumberOfSoundsIsEqual(int expectedNumber) {
		assertEquals("Number of sounds is not as expected", expectedNumber, getCurrentNumberOfSounds());
	}

	private int getCurrentNumberOfSounds() {
		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		return soundInfoList.size();
	}

	private String getSoundTitle(int soundIndex) {
		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		return soundInfoList.get(soundIndex).getTitle();
	}

	private void checkIfCheckboxesAreCorrectlyChecked(boolean firstCheckboxExpectedChecked,
			boolean secondCheckboxExpectedChecked) {
		solo.sleep(300);
		firstCheckBox = solo.getCurrentViews(CheckBox.class).get(0);
		secondCheckBox = solo.getCurrentViews(CheckBox.class).get(1);
		assertEquals("First checkbox not correctly checked", firstCheckboxExpectedChecked, firstCheckBox.isChecked());
		assertEquals("Second checkbox not correctly checked", secondCheckboxExpectedChecked, secondCheckBox.isChecked());
	}

	private void checkIfContextMenuAppears(boolean contextMenuShouldAppear, boolean isDeleteActionMode) {
		solo.clickLongOnText(FIRST_TEST_SOUND_NAME);

		int timeToWait = 200;
		String assertMessageAffix = "";

		if (contextMenuShouldAppear) {
			assertMessageAffix = "should appear";

			assertTrue("Context menu with title '" + FIRST_TEST_SOUND_NAME + "' " + assertMessageAffix,
					solo.waitForText(FIRST_TEST_SOUND_NAME, 1, timeToWait, false, true));
			assertTrue("Context menu item '" + delete + "' " + assertMessageAffix,
					solo.waitForText(delete, 1, timeToWait, false, true));
			assertTrue("Context menu item '" + rename + "' " + assertMessageAffix,
					solo.waitForText(rename, 1, timeToWait, false, true));

			solo.goBack();
		} else {
			assertMessageAffix = "should not appear";

			int minimumMatchesDelete = 1;
			int minimumMatchesRename = 1;

			if (isDeleteActionMode) {
				minimumMatchesDelete = 2;
			} else {
				minimumMatchesRename = 2;
			}
			assertFalse("Context menu with title '" + FIRST_TEST_SOUND_NAME + "' " + assertMessageAffix,
					solo.waitForText(FIRST_TEST_SOUND_NAME, 2, timeToWait, false, true));
			assertFalse("Context menu item '" + delete + "' " + assertMessageAffix,
					solo.waitForText(delete, minimumMatchesDelete, timeToWait, false, true));
			assertFalse("Context menu item '" + rename + "' " + assertMessageAffix,
					solo.waitForText(rename, minimumMatchesRename, timeToWait, false, true));
		}
	}

	private void moveSoundDown(String soundToMove) {
		clickOnContextMenuItem(soundToMove, solo.getString(R.string.menu_item_move_down));
	}

	private void moveSoundUp(String soundToMove) {
		clickOnContextMenuItem(soundToMove, solo.getString(R.string.menu_item_move_up));
	}

	private void moveSoundToBottom(String soundToMove) {
		clickOnContextMenuItem(soundToMove, solo.getString(R.string.menu_item_move_to_bottom));
	}

	private void moveSoundToTop(String soundToMove) {
		clickOnContextMenuItem(soundToMove, solo.getString(R.string.menu_item_move_to_top));
	}
}
