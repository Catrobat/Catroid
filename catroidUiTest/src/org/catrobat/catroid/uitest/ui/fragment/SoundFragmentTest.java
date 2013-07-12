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
package org.catrobat.catroid.uitest.ui.fragment;

import java.io.File;
import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.SoundAdapter;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.uitest.mockups.MockSoundActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.jayway.android.robotium.solo.Solo;

public class SoundFragmentTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private static final int RESOURCE_SOUND = org.catrobat.catroid.uitest.R.raw.longsound;
	private static final int RESOURCE_SOUND2 = org.catrobat.catroid.uitest.R.raw.testsoundui;
	private static final int VISIBLE = View.VISIBLE;
	private static final int GONE = View.GONE;

	private static final int TIME_TO_WAIT = 50;

	private static final String FIRST_TEST_SOUND_NAME = "testSound1";
	private static final String SECOND_TEST_SOUND_NAME = "testSound2";

	private Solo solo;

	private String rename;
	private String renameDialogTitle;
	private String delete;
	private String deleteDialogTitle;

	private SoundInfo soundInfo;
	private SoundInfo soundInfo2;

	private File externalSoundFile;

	private ArrayList<SoundInfo> soundInfoList;

	private CheckBox firstCheckBox;
	private CheckBox secondCheckBox;

	private ProjectManager projectManager;

	public SoundFragmentTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();

		projectManager = ProjectManager.getInstance();
		soundInfoList = projectManager.getCurrentSprite().getSoundList();

		File soundFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "longsound.mp3",
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

		solo = new Solo(getInstrumentation(), getActivity());

		UiTestUtils.getIntoSoundsFromMainMenu(solo);

		rename = solo.getString(R.string.rename);
		renameDialogTitle = solo.getString(R.string.rename_sound_dialog);
		delete = solo.getString(R.string.delete);
		deleteDialogTitle = solo.getString(R.string.dialog_confirm_delete_sound_title);

		if (getSoundAdapter().getShowDetails()) {
			solo.clickOnMenuItem(solo.getString(R.string.hide_details), true);
			solo.sleep(TIME_TO_WAIT);
		}
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
		assertFalse("Initially showing details", getSoundAdapter().getShowDetails());
		checkVisibilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
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

	public void testDeleteSoundContextMenu() {
		SoundAdapter adapter = getSoundAdapter();
		assertNotNull("Could not get Adapter", adapter);

		int oldCount = adapter.getCount();

		clickOnContextMenuItem(SECOND_TEST_SOUND_NAME, solo.getString(R.string.delete));
		solo.waitForText(deleteDialogTitle);
		solo.clickOnButton(solo.getString(R.string.yes));
		solo.sleep(50);

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

	public void testEqualSoundNames() {
		final String assertMessageText = "Sound not renamed correctly";

		String defaultSoundName = "renamedSound";
		String newSoundName = "newTestSound";
		addNewSound(newSoundName);

		renameSound(FIRST_TEST_SOUND_NAME, defaultSoundName);
		renameSound(SECOND_TEST_SOUND_NAME, defaultSoundName);
		renameSound(newSoundName, defaultSoundName);

		String expectedSoundName = defaultSoundName + "1";
		assertEquals(assertMessageText, expectedSoundName, getSoundTitle(1));

		expectedSoundName = defaultSoundName + "2";
		assertEquals(assertMessageText, expectedSoundName, getSoundTitle(2));

		newSoundName = "x";

		expectedSoundName = defaultSoundName + "1";
		renameSound(expectedSoundName, newSoundName);

		assertNotSame("Sound not renamed", expectedSoundName, getSoundTitle(1));
		renameSound(newSoundName, defaultSoundName);

		assertEquals(assertMessageText, expectedSoundName, getSoundTitle(1));
	}

	public void testShowAndHideDetails() {
		int timeToWait = 300;

		checkVisibilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
		solo.clickOnMenuItem(solo.getString(R.string.show_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, VISIBLE, GONE);

		// Test if showDetails is remembered after pressing back
		solo.goBack();
		solo.waitForActivity(ProgramMenuActivity.class.getSimpleName());
		solo.clickOnText(solo.getString(R.string.sounds));
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, VISIBLE, GONE);

		solo.clickOnMenuItem(solo.getString(R.string.hide_details));
		solo.sleep(timeToWait);
		checkVisibilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
	}

	public void testPlayAndStopSound() {
		// Mute before playing sound
		AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);

		int timeToWait = 3000;

		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		SoundInfo soundInfo = soundInfoList.get(0);
		assertFalse("Mediaplayer is playing although no play button was touched", soundInfo.isPlaying);

		ImageButton playImageButton = (ImageButton) solo.getView(R.id.fragment_sound_item_play_image_button);
		ImageButton pauseImageButton = (ImageButton) solo.getView(R.id.fragment_sound_item_pause_image_button);

		solo.clickOnView(playImageButton);
		solo.sleep(timeToWait);

		assertTrue("Mediaplayer is not playing although play button was touched", soundInfo.isPlaying);
		checkVisibilityOfViews(GONE, VISIBLE, VISIBLE, VISIBLE, VISIBLE, GONE, GONE);

		solo.sleep(timeToWait);
		solo.clickOnView(pauseImageButton);
		solo.sleep(timeToWait);

		assertFalse("Mediaplayer is playing after touching stop button", soundInfo.isPlaying);
		checkVisibilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);

		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
	}

	public void testStopSoundOnSpinnerPress() {
		// Mute before playing sound
		AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);

		int timeToWait = 1500;

		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		SoundInfo soundInfo = soundInfoList.get(0);
		assertFalse("Mediaplayer is playing although no play button was touched", soundInfo.isPlaying);

		ImageButton playImageButton = (ImageButton) solo.getView(R.id.fragment_sound_item_play_image_button);

		solo.clickOnView(playImageButton);
		solo.sleep(timeToWait);

		assertTrue("Mediaplayer is not playing although play button was touched", soundInfo.isPlaying);
		checkVisibilityOfViews(GONE, VISIBLE, VISIBLE, VISIBLE, VISIBLE, GONE, GONE);

		String soundsString = solo.getString(R.string.sounds);
		String looksString = solo.getString(R.string.looks);

		solo.sleep(timeToWait);
		UiTestUtils.changeToFragmentViaActionbar(solo, soundsString, looksString);
		solo.waitForFragmentById(R.id.fragment_look_relative_layout, 500);
		UiTestUtils.changeToFragmentViaActionbar(solo, looksString, soundsString);

		assertFalse("Mediaplayer is playing after switching fragment via spinner", soundInfo.isPlaying);
		checkVisibilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);

		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
	}

	public void testAddSound() {
		int expectedNumberOfSounds = getCurrentNumberOfSounds() + 1;

		String newSoundName = "Added Sound";
		addNewSound(newSoundName);

		assertEquals("No sound was added", expectedNumberOfSounds, getCurrentNumberOfSounds());
		assertTrue("Sound not added in actual view", solo.searchText(newSoundName));
	}

	public void testGetSoundFromExternalSource() {
		int expectedNumberOfSounds = getCurrentNumberOfSounds() + 1;
		String checksumExternalSoundFile = Utils.md5Checksum(externalSoundFile);

		// Use of MockSoundActivity
		Bundle bundleForExternalSource = new Bundle();
		bundleForExternalSource.putString("filePath", externalSoundFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(), MockSoundActivity.class);
		intent.putExtras(bundleForExternalSource);

		getSoundFragment().startActivityForResult(intent, SoundFragment.REQUEST_SELECT_MUSIC);
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
		checkVisibilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());

		// Check if checkboxes are visible
		checkVisibilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, VISIBLE);

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

		// Check if rename ActionMode disappears if nothing was selected
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		UiTestUtils.acceptAndCloseActionMode(solo);
		assertFalse("Rename dialog showed up", solo.waitForText(renameDialogTitle, 0, TIME_TO_WAIT));
		assertFalse("ActionMode didn't disappear", solo.waitForText(rename, 0, TIME_TO_WAIT));
	}

	public void testRenameActionModeIfSomethingSelectedAndPressingBack() {
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		solo.goBack();

		// Check if rename ActionMode disappears if back was pressed
		assertFalse("Rename dialog showed up", solo.waitForText(renameDialogTitle, 0, TIME_TO_WAIT));
		assertFalse("ActionMode didn't disappear", solo.waitForText(rename, 0, TIME_TO_WAIT));
	}

	public void testRenameActionModeEqualSoundNames() {
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());

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
		assertEquals("Sound is not correctly renamed in SoundList (1 should be appended)", expectedNewSoundName,
				soundInfoList.get(checkboxIndex).getTitle());
		assertTrue("Sound not renamed in actual view", solo.searchText(expectedNewSoundName, true));
	}

	public void testBottomBarAndContextMenuOnActionModes() {
		LinearLayout bottomBarLayout = (LinearLayout) solo.getView(R.id.bottom_bar);
		LinearLayout addButton = (LinearLayout) bottomBarLayout.findViewById(R.id.button_add);
		LinearLayout playButton = (LinearLayout) bottomBarLayout.findViewById(R.id.button_play);

		int timeToWait = 300;
		String addDialogTitle = solo.getString(R.string.sound_select_source);
		String soundSpinnerItemText = solo.getString(R.string.sounds);

		assertTrue("Add button not clickable", addButton.isClickable());
		assertTrue("Play button not clickable", playButton.isClickable());

		checkIfContextMenuAppears(true, false);

		// Test on rename ActionMode
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());
		solo.waitForText(rename, 1, timeToWait, false, true);

		checkIfContextMenuAppears(false, false);

		assertFalse("Add button clickable", addButton.isClickable());
		assertFalse("Play button clickable", playButton.isClickable());

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.waitForText(soundSpinnerItemText, 1, timeToWait, false, true);

		checkIfContextMenuAppears(true, false);

		assertTrue("Add button not clickable after ActionMode", addButton.isClickable());
		assertTrue("Play button not clickable after ActionMode", playButton.isClickable());

		// Test on delete ActionMode
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());
		solo.waitForText(delete, 1, timeToWait, false, true);

		checkIfContextMenuAppears(false, true);

		assertFalse("Add button clickable", addButton.isClickable());
		assertFalse("Play button clickable", playButton.isClickable());

		solo.clickOnView(addButton);
		assertFalse("Add dialog should not appear", solo.waitForText(addDialogTitle, 0, timeToWait, false, true));

		solo.clickOnView(playButton);
		assertFalse("Should not start playing program",
				solo.waitForActivity(StageActivity.class.getSimpleName(), timeToWait));

		solo.goBack();
		solo.waitForText(soundSpinnerItemText, 1, timeToWait, false, true);

		checkIfContextMenuAppears(true, true);
	}

	public void testDeleteActionModeCheckingAndTitle() {
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

		int timeToWaitForTitle = 300;

		String sound = solo.getString(R.string.category_sound);
		String sounds = solo.getString(R.string.sounds);

		assertFalse("Sound should not be displayed in title", solo.waitForText(sound, 3, 300, false, true));

		// Check if checkboxes are visible
		checkVisibilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, VISIBLE);

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

	public void testAddLookAndDeleteActionMode() {
		String testSoundName = "testSound";

		addNewSound(testSoundName);
		addNewSound(testSoundName);
		addNewSound(testSoundName);

		solo.sleep(300);

		soundInfoList = projectManager.getCurrentSprite().getSoundList();

		int currentNumberOfSounds = soundInfoList.size();
		assertEquals("Wrong number of sounds", 5, currentNumberOfSounds);

		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());

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
		ImageButton playImageButton = (ImageButton) solo.getView(R.id.fragment_sound_item_play_image_button);

		solo.clickOnView(playImageButton);
		solo.sleep(timeToWait);
		solo.clickLongOnText(FIRST_TEST_SOUND_NAME);
		solo.waitForText(solo.getString(R.string.delete));
		solo.sleep(timeToWait);
		assertFalse("Mediaplayer continues playing even if context menu has been opened", soundInfo.isPlaying);
		solo.goBack();
		checkVisibilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);

		solo.clickOnView(playImageButton);
		solo.sleep(timeToWait);
		UiTestUtils.openActionMode(solo, rename, 0, getActivity());
		solo.sleep(timeToWait);
		assertFalse("Mediaplayer continues playing even if rename action has been opened", soundInfo.isPlaying);
		solo.goBack();
		checkVisibilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);

		solo.clickOnView(playImageButton);
		solo.sleep(timeToWait);
		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());
		solo.sleep(timeToWait);
		assertFalse("Mediaplayer continues playing even if delete action has been opened", soundInfo.isPlaying);
		solo.goBack();
		checkVisibilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);

		UiTestUtils.openActionMode(solo, delete, R.id.delete, getActivity());
		solo.clickOnView(playImageButton);
		solo.clickOnCheckBox(0);
		solo.sleep(timeToWait);
		UiTestUtils.acceptAndCloseActionMode(solo);
		solo.sleep(timeToWait);
		assertFalse("Mediaplayer continues playing even if already deleted", soundInfo.isPlaying);
		solo.goBack();

		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
	}

	private void addNewSound(String title) {
		File soundFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "longsound.mp3",
				RESOURCE_SOUND, getInstrumentation().getContext(), UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(title);

		soundInfoList.add(soundInfo);
		projectManager.getFileChecksumContainer().addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
		StorageHandler.getInstance().saveProject(projectManager.getCurrentProject());
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

	private void checkVisibilityOfViews(int playButtonVisibility, int pauseButtonVisibility, int soundNameVisibility,
			int timePlayedVisibility, int soundDurationVisibility, int soundSizeVisibility, int checkBoxVisibility) {
		assertTrue("Play button " + getAssertMessageAffix(playButtonVisibility),
				solo.getView(R.id.fragment_sound_item_play_image_button).getVisibility() == playButtonVisibility);
		assertTrue("Pause button " + getAssertMessageAffix(pauseButtonVisibility),
				solo.getView(R.id.fragment_sound_item_pause_image_button).getVisibility() == pauseButtonVisibility);
		assertTrue("Sound name " + getAssertMessageAffix(soundNameVisibility),
				solo.getView(R.id.fragment_sound_item_title_text_view).getVisibility() == soundNameVisibility);
		assertTrue("Chronometer " + getAssertMessageAffix(timePlayedVisibility),
				solo.getView(R.id.fragment_sound_item_time_played_chronometer).getVisibility() == timePlayedVisibility);
		assertTrue("Sound duration " + getAssertMessageAffix(soundDurationVisibility),
				solo.getView(R.id.fragment_sound_item_duration_text_view).getVisibility() == soundDurationVisibility);
		assertTrue("Sound size " + getAssertMessageAffix(soundSizeVisibility),
				solo.getView(R.id.fragment_sound_item_size_text_view).getVisibility() == soundSizeVisibility);
		assertTrue("Checkboxes " + getAssertMessageAffix(checkBoxVisibility),
				solo.getView(R.id.fragment_sound_item_checkbox).getVisibility() == checkBoxVisibility);
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
}
