package org.catrobat.catroid.uitest.ui;

import java.io.File;
import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.ScriptTabActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.SoundActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.CheckBox;

import com.jayway.android.robotium.solo.Solo;

public class SoundActivityTest extends ActivityInstrumentationTestCase2<SoundActivity> {
	private final int RESOURCE_SOUND = org.catrobat.catroid.uitest.R.raw.longsound;
	private final int RESOURCE_SOUND2 = org.catrobat.catroid.uitest.R.raw.testsoundui;

	private Solo solo = null;
	private ArrayList<SoundInfo> soundInfoList;
	private ProjectManager projectManager = ProjectManager.getInstance();

	private String soundName = "testSound1";
	private String soundName2 = "testSound2";
	private File soundFile;
	private File soundFile2;

	private static final int VISIBLE = View.VISIBLE;
	private static final int GONE = View.GONE;

	private static final int ACTION_MODE_ACCEPT_IMAGE_BUTTON_INDEX = 0;

	private CheckBox firstCheckBox;
	private CheckBox secondCheckBox;

	private String rename;

	public SoundActivityTest() {
		super(SoundActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject();
		soundInfoList = projectManager.getCurrentSprite().getSoundList();

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
		projectManager.getFileChecksumContainer().addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
		projectManager.getFileChecksumContainer().addChecksum(soundInfo2.getChecksum(), soundInfo2.getAbsolutePath());

		solo = new Solo(getInstrumentation(), getActivity());

		firstCheckBox = solo.getCurrentCheckBoxes().get(0);
		secondCheckBox = solo.getCurrentCheckBoxes().get(1);

		rename = solo.getString(R.string.rename);
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
		// projectManager.deleteCurrentProject();
	}

	public void testOrientation() throws NameNotFoundException {
		assertEquals("SoundActivity not initially in Portrait mode!", Configuration.ORIENTATION_PORTRAIT, solo
				.getCurrentActivity().getResources().getConfiguration().orientation);

		/// Method 2: Retrieve info about Activity as collected from AndroidManifest.xml
		// https://developer.android.com/reference/android/content/pm/ActivityInfo.html
		PackageManager packageManager = solo.getCurrentActivity().getPackageManager();
		ActivityInfo activityInfo = packageManager.getActivityInfo(solo.getCurrentActivity().getComponentName(),
				PackageManager.GET_ACTIVITIES);

		// Note that the activity is _indeed_ rotated on your device/emulator!
		// Robotium can _force_ the activity to be in landscape mode (and so could we, programmatically)
		solo.setActivityOrientation(Solo.LANDSCAPE);

		assertEquals(ProgramMenuActivity.class.getSimpleName()
				+ " not set to be in portrait mode in AndroidManifest.xml!", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
				activityInfo.screenOrientation);
	}

	public void testMainMenuButton() {
		int upImageIndex = 0;
		solo.clickOnImage(upImageIndex);
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.assertCurrentActivity("Clicking on main menu button did not cause main menu to be displayed",
				MainMenuActivity.class);
	}

	public void testAddSoundButton() {
		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		int numberOfSoundsBeforeAdding = soundInfoList.size();

		UiTestUtils.clickOnBottomBar(solo, R.id.btn_add);
		String addSoundDialogTitle = solo.getString(R.string.sound_select_source);
		assertTrue("New sound dialog did not appear", solo.searchText(addSoundDialogTitle, true));

		int soundRecorderIndex = 0;
		solo.clickOnImage(soundRecorderIndex);

		String startRecording = "Start recording";
		assertTrue("No button to start recording", solo.waitForText(startRecording, 0, 100));
		solo.clickOnText(startRecording);

		String stopRecording = "Stop recording";
		assertTrue("No button to stop recording", solo.waitForText(stopRecording, 0, 100));
		solo.clickOnText(stopRecording);

		solo.waitForActivity(SoundActivity.class.getSimpleName());
		solo.sleep(200);
		String assertAffix = "add button";
		checkIfNumberOfSoundsNotChanged(assertAffix, numberOfSoundsBeforeAdding + 1);
	}

	public void testPlayProgramButton() {
		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		int numberOfSoundsBeforePlayingProgram = soundInfoList.size();

		UiTestUtils.clickOnBottomBar(solo, R.id.btn_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.assertCurrentActivity("Not in StageActivity", StageActivity.class);

		solo.goBack();
		solo.goBack();

		solo.waitForActivity(SoundActivity.class.getSimpleName());
		solo.assertCurrentActivity("Not in SoundActivity", SoundActivity.class);

		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		int numberOfSoundsAfterPlayingProgram = soundInfoList.size();
		assertEquals("Size of the soundlist has changed", numberOfSoundsBeforePlayingProgram,
				numberOfSoundsAfterPlayingProgram);
	}

	public void testChangeViaSpinner() {
		int expectedNumberOfSpinnerItems = 3;
		int actualNumberOfSpinnerItems = solo.getCurrentSpinners().get(0).getAdapter().getCount();
		assertEquals("There should be " + expectedNumberOfSpinnerItems + " spinner items",
				expectedNumberOfSpinnerItems, actualNumberOfSpinnerItems);

		int numberOfSoundsBeforeSpinning = soundInfoList.size();

		String sounds = solo.getString(R.string.sounds);
		clickOnSpinnerItem(sounds);
		solo.waitForActivity(SoundActivity.class.getSimpleName());

		checkIfNumberOfSoundsNotChanged(sounds, numberOfSoundsBeforeSpinning);

		String scripts = solo.getString(R.string.scripts);
		clickOnSpinnerItem(scripts);

		//TODO CHANGE TO SCRIPTACTIVITY!
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(SoundActivity.class.getSimpleName());
		assertTrue("Sounds spinner item is not selected", solo.searchText(sounds, true));

		checkIfNumberOfSoundsNotChanged(scripts, numberOfSoundsBeforeSpinning);

		String looks = solo.getString(R.string.category_looks);
		clickOnSpinnerItem(looks);

		//TODO CHANGE TO LOOKACTIVITY!
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(SoundActivity.class.getSimpleName());
		assertTrue("Sounds spinner item is not selected", solo.searchText(sounds, true));

		checkIfNumberOfSoundsNotChanged(looks, numberOfSoundsBeforeSpinning);
	}

	public void testRenameActionModeChecking() {
		openRenameActionMode();

		// CHeck if checkboxes are visible
		checkVisabilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, VISIBLE);

		checkIfCheckboxesAreCorrectlyChecked(false, false);
		solo.clickOnCheckBox(0);
		solo.sleep(200);
		checkIfCheckboxesAreCorrectlyChecked(true, false);
		solo.clickOnCheckBox(1);
		solo.sleep(200);
		// Check if only single-selection is possible
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		solo.clickOnCheckBox(1);
		solo.sleep(200);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
	}

	public void testRenameActionModeIfNothingSelected() {
		openRenameActionMode();

		// Check if rename ActionMode disappears if nothing was selected
		String renameDialogTitle = solo.getString(R.string.rename_sound_dialog);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		acceptAndCloseActionMode();
		assertFalse("Rename dialog showed up", solo.waitForText(renameDialogTitle, 0, 100));
		assertFalse("ActionMode didn't disappear", solo.waitForText(rename, 0, 100));
	}

	public void testRenameActionModeIfSelectedAndPressingBack() {
		openRenameActionMode();
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		solo.goBack();

		// Check if rename ActionMode disappears if back was pressed
		String renameDialogTitle = solo.getString(R.string.rename_sound_dialog);
		assertFalse("Rename dialog showed up", solo.waitForText(renameDialogTitle, 0, 100));
		assertFalse("ActionMode didn't disappear", solo.waitForText(rename, 0, 100));
	}

	public void testRenameActionModeEqualSoundNames() {
		openRenameActionMode();

		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(true, false);
		acceptAndCloseActionMode();

		String newSoundName = "Renamed Sound";
		String renameDialogTitle = solo.getString(R.string.rename_sound_dialog);

		assertTrue("Rename dialog didn't show up", solo.searchText(renameDialogTitle));
		assertTrue("No EditText with actual soundname", solo.searchEditText(soundName));

		solo.clearEditText(0);
		solo.enterText(0, newSoundName);
		solo.sendKey(Solo.ENTER);

		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		assertEquals("Sound is not renamed in SoundList", newSoundName, soundInfoList.get(0).getTitle());
		assertTrue("Sound not renamed in actual view", solo.searchText(newSoundName));

		checkVisabilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);

		openRenameActionMode();

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		acceptAndCloseActionMode();

		assertTrue("Rename dialog didn't show up", solo.searchText(renameDialogTitle));
		assertTrue("No EditText with actual soundname", solo.searchEditText(soundName2));

		solo.clearEditText(0);
		solo.enterText(0, newSoundName);
		solo.sendKey(Solo.ENTER);

		checkVisabilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);

		String expectedNewSoundName = newSoundName + "1";
		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		assertEquals("Sound is not correctly renamed in SoundList (1 should be appended)", expectedNewSoundName,
				soundInfoList.get(1).getTitle());
		assertTrue("Sound not renamed in actual view", solo.searchText(expectedNewSoundName));
	}

	public void testOverflowMenuItemSettings() {
		int numberOfSoundsBeforeClickingOnSettings = soundInfoList.size();

		String settings = solo.getString(R.string.main_menu_settings);
		clickOnOverflowMenuItem(settings);
		solo.assertCurrentActivity("Not in SettingsActivity", SettingsActivity.class);

		solo.goBack();
		checkIfNumberOfSoundsNotChanged(settings, numberOfSoundsBeforeClickingOnSettings);
	}

	private void checkIfNumberOfSoundsNotChanged(String assertMessageAffix, int numberToCompare) {
		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		assertEquals("Number of sounds has changed after clicking on " + assertMessageAffix, numberToCompare,
				soundInfoList.size());
	}

	private void clickOnSpinnerItem(String itemName) {
		String sounds = solo.getString(R.string.sounds);
		solo.clickOnText(sounds);
		solo.clickOnText(itemName);
	}

	private void clickOnOverflowMenuItem(String itemName) {
		solo.clickOnImageButton(0);
		solo.waitForText(itemName);
		solo.clickOnText(itemName);
	}

	private void checkVisabilityOfViews(int playButtonVisibility, int pauseButtonVisibility, int soundNameVisibility,
			int timePlayedVisibility, int soundDurationVisibility, int soundSizeVisibility, int checkBoxVisibility) {
		assertTrue("Play button " + getAssertMessageAffix(playButtonVisibility), solo.getView(R.id.btn_sound_play)
				.getVisibility() == playButtonVisibility);
		assertTrue("Pause button " + getAssertMessageAffix(pauseButtonVisibility), solo.getView(R.id.btn_sound_pause)
				.getVisibility() == pauseButtonVisibility);
		assertTrue("Sound name " + getAssertMessageAffix(soundNameVisibility), solo.getView(R.id.sound_title)
				.getVisibility() == soundNameVisibility);
		assertTrue("Chronometer " + getAssertMessageAffix(timePlayedVisibility),
				solo.getView(R.id.sound_chronometer_time_played).getVisibility() == timePlayedVisibility);
		assertTrue("Sound duration " + getAssertMessageAffix(soundDurationVisibility), solo
				.getView(R.id.sound_duration).getVisibility() == soundDurationVisibility);
		assertTrue("Sound size " + getAssertMessageAffix(soundSizeVisibility), solo.getView(R.id.sound_size)
				.getVisibility() == soundSizeVisibility);
		assertTrue("Checkboxes " + getAssertMessageAffix(checkBoxVisibility), solo.getView(R.id.checkbox)
				.getVisibility() == checkBoxVisibility);
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

	private void checkIfCheckboxesAreCorrectlyChecked(boolean firstCheckboxExpectedChecked,
			boolean secondCheckboxExpectedChecked) {
		boolean firstCheckboxActuallyChecked = firstCheckBox.isChecked();
		assertEquals("First checkbox not correctly checked", firstCheckboxExpectedChecked, firstCheckboxActuallyChecked);

		boolean secondCheckboxActuallyChecked = secondCheckBox.isChecked();
		assertEquals("Second checkbox not correctly checked", secondCheckboxExpectedChecked,
				secondCheckboxActuallyChecked);
	}

	private void openRenameActionMode() {
		checkVisabilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
		clickOnOverflowMenuItem(rename);
		assertTrue("ActionMode didn't show up", solo.searchText(rename, true));
	}

	private void acceptAndCloseActionMode() {
		solo.clickOnImage(ACTION_MODE_ACCEPT_IMAGE_BUTTON_INDEX);
	}
}
