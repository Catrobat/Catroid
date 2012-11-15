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
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.SoundAdapter;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.CheckBox;

import com.jayway.android.robotium.solo.Solo;

public class SoundActivityTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private final int RESOURCE_SOUND = org.catrobat.catroid.uitest.R.raw.longsound;
	private final int RESOURCE_SOUND2 = org.catrobat.catroid.uitest.R.raw.testsoundui;

	private static final int VISIBLE = View.VISIBLE;
	private static final int GONE = View.GONE;

	private static final int TIME_TO_WAIT = 50;
	private static final int ACTION_MODE_ACCEPT_IMAGE_BUTTON_INDEX = 0;

	private Solo solo = null;
	private ArrayList<SoundInfo> soundInfoList;
	private ProjectManager projectManager;

	private String soundName = "testSound1";
	private String soundName2 = "testSound2";
	private File soundFile;
	private File soundFile2;

	private CheckBox firstCheckBox;
	private CheckBox secondCheckBox;

	private String rename;
	private String renameDialogTitle;
	private String delete;
	private String deleteDialogTitle;

	public SoundActivityTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject();
		projectManager = ProjectManager.getInstance();
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
		renameDialogTitle = solo.getString(R.string.rename_sound_dialog);
		delete = solo.getString(R.string.delete);
		deleteDialogTitle = solo.getString(R.string.delete_sound_dialog);

		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		SoundFragment soundFragment = (SoundFragment) fragmentManager.findFragmentById(R.id.fr_sound);
		SoundAdapter soundAdapter = (SoundAdapter) soundFragment.getListAdapter();

		if (soundAdapter.getShowDetails()) {
			clickOnOverflowMenuItem(solo.getString(R.string.hide_details));
			solo.sleep(TIME_TO_WAIT);
		}
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
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
		solo.assertCurrentActivity("Main menu is not displayed", MainMenuActivity.class);
	}

	public void testAddSoundButton() {
		int expectedNumberOfSounds = getActualNumberOfSounds() + 1;

		UiTestUtils.clickOnBottomBar(solo, R.id.btn_add);
		String addSoundDialogTitle = solo.getString(R.string.sound_select_source);
		assertTrue("New sound dialog did not appear", solo.searchText(addSoundDialogTitle, true));

		int soundRecorderIndex = 0;
		solo.clickOnImage(soundRecorderIndex);

		String startRecording = "Start recording";
		assertTrue("No button to start recording", solo.waitForText(startRecording, 0, TIME_TO_WAIT));
		solo.clickOnText(startRecording);

		String stopRecording = "Stop recording";
		assertTrue("No button to stop recording", solo.waitForText(stopRecording, 0, TIME_TO_WAIT));
		solo.clickOnText(stopRecording);

		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.sleep(200);

		checkIfNumberOfSoundsIsEqual("clicking on add button", expectedNumberOfSounds);
	}

	public void testPlayProgramButton() {
		int expectedNumberOfSounds = getActualNumberOfSounds();

		UiTestUtils.clickOnBottomBar(solo, R.id.btn_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.assertCurrentActivity("Not in StageActivity", StageActivity.class);

		solo.goBack();
		solo.goBack();

		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		solo.assertCurrentActivity("Not in SoundActivity", ScriptActivity.class);

		checkIfNumberOfSoundsIsEqual("clicking on play button", expectedNumberOfSounds);
	}

	public void testChangeViaSpinner() {
		int expectedNumberOfSpinnerItems = 3;
		int actualNumberOfSpinnerItems = solo.getCurrentSpinners().get(0).getAdapter().getCount();
		assertEquals("There should be " + expectedNumberOfSpinnerItems + " spinner items",
				expectedNumberOfSpinnerItems, actualNumberOfSpinnerItems);

		int expectedNumberOfSounds = getActualNumberOfSounds();

		String sounds = solo.getString(R.string.sounds);
		clickOnSpinnerItem(sounds);
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		checkIfNumberOfSoundsIsEqual("clicking on " + sounds, expectedNumberOfSounds);

		String scripts = solo.getString(R.string.scripts);
		clickOnSpinnerItem(scripts);

		//TODO CHANGE TO SCRIPTACTIVITY!
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertTrue("Sounds spinner item is not selected", solo.searchText(sounds, true));

		checkIfNumberOfSoundsIsEqual("clicking on " + scripts, expectedNumberOfSounds);

		String looks = solo.getString(R.string.category_looks);
		clickOnSpinnerItem(looks);

		//TODO CHANGE TO LOOKACTIVITY!
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());
		assertTrue("Sounds spinner item is not selected", solo.searchText(sounds, true));

		checkIfNumberOfSoundsIsEqual("clicking on " + looks, expectedNumberOfSounds);
	}

	public void testRenameActionModeChecking() {
		checkVisabilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
		openActionMode(rename);

		// CHeck if checkboxes are visible
		checkVisabilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, VISIBLE);

		checkIfCheckboxesAreCorrectlyChecked(false, false);
		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(true, false);
		solo.clickOnCheckBox(1);
		// Check if only single-selection is possible
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
	}

	public void testRenameActionModeIfNothingSelected() {
		openActionMode(rename);

		// Check if rename ActionMode disappears if nothing was selected
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		acceptAndCloseActionMode();
		assertFalse("Rename dialog showed up", solo.waitForText(renameDialogTitle, 0, TIME_TO_WAIT));
		assertFalse("ActionMode didn't disappear", solo.waitForText(rename, 0, TIME_TO_WAIT));
	}

	public void testRenameActionModeIfSelectedAndPressingBack() {
		openActionMode(rename);

		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		solo.goBack();

		// Check if rename ActionMode disappears if back was pressed
		assertFalse("Rename dialog showed up", solo.waitForText(renameDialogTitle, 0, TIME_TO_WAIT));
		assertFalse("ActionMode didn't disappear", solo.waitForText(rename, 0, TIME_TO_WAIT));
	}

	public void testRenameActionModeEqualSoundNames() {
		openActionMode(rename);

		int checkboxIndex = 1;

		// Rename second sound to the name of the first
		String newSoundName = soundName;

		solo.clickOnCheckBox(checkboxIndex);
		checkIfCheckboxesAreCorrectlyChecked(false, true);
		acceptAndCloseActionMode();

		assertTrue("Rename dialog didn't show up", solo.searchText(renameDialogTitle, true));
		assertTrue("No EditText with actual soundname", solo.searchEditText(soundName2));

		UiTestUtils.enterText(solo, 0, newSoundName);
		solo.sendKey(Solo.ENTER);

		checkVisabilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);

		// If an already existing name was entered a counter should be appended
		String expectedNewSoundName = newSoundName + "1";
		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		assertEquals("Sound is not correctly renamed in SoundList (1 should be appended)", expectedNewSoundName,
				soundInfoList.get(checkboxIndex).getTitle());
		assertTrue("Sound not renamed in actual view", solo.searchText(expectedNewSoundName, true));
	}

	public void testDeleteActionModeChecking() {
		openActionMode(delete);

		// Check if checkboxes are visible
		checkVisabilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, VISIBLE);

		checkIfCheckboxesAreCorrectlyChecked(false, false);
		solo.clickOnCheckBox(0);
		checkIfCheckboxesAreCorrectlyChecked(true, false);
		solo.clickOnCheckBox(1);
		// Check if multiple-selection is possible
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, false);
	}

	public void testDeleteActionModeIfNothingSelected() {
		int expectedNumberOfSounds = getActualNumberOfSounds();

		openActionMode(delete);

		// Check if rename ActionMode disappears if nothing was selected
		checkIfCheckboxesAreCorrectlyChecked(false, false);
		acceptAndCloseActionMode();
		assertFalse("Delete dialog showed up", solo.waitForText(deleteDialogTitle, 0, TIME_TO_WAIT));
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));

		checkIfNumberOfSoundsIsEqual("clicking on back button", expectedNumberOfSounds);
	}

	public void testDeleteActionModeIfSelectedAndPressingBack() {
		int expectedNumberOfSounds = getActualNumberOfSounds();

		openActionMode(delete);
		solo.clickOnCheckBox(0);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(true, true);
		solo.goBack();

		checkVisabilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);

		// Check if rename ActionMode disappears if back was pressed
		assertFalse("Delete dialog showed up", solo.waitForText(deleteDialogTitle, 0, TIME_TO_WAIT));
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));

		checkIfNumberOfSoundsIsEqual("clicking on back button", expectedNumberOfSounds);
	}

	public void testDeleteActionMode() {
		int expectedNumberOfSounds = getActualNumberOfSounds() - 1;

		openActionMode(delete);
		solo.clickOnCheckBox(1);
		checkIfCheckboxesAreCorrectlyChecked(false, true);

		acceptAndCloseActionMode();
		assertFalse("ActionMode didn't disappear", solo.waitForText(delete, 0, TIME_TO_WAIT));

		checkIfNumberOfSoundsIsEqual("delete ActionMode", expectedNumberOfSounds);
	}

	public void testOverflowMenuItemSettings() {
		String settings = solo.getString(R.string.main_menu_settings);
		clickOnOverflowMenuItem(settings);
		solo.assertCurrentActivity("Not in SettingsActivity", SettingsActivity.class);
		solo.goBack();
		solo.assertCurrentActivity("Not in SoundActivity", ScriptActivity.class);
	}

	private void checkIfNumberOfSoundsIsEqual(String assertMessageAffix, int numberToCompare) {
		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		assertEquals("Number of sounds has changed after " + assertMessageAffix, numberToCompare, soundInfoList.size());
	}

	private void clickOnSpinnerItem(String itemName) {
		solo.clickOnText(solo.getString(R.string.sounds));
		solo.clickOnText(itemName);
	}

	private void clickOnOverflowMenuItem(String itemName) {
		solo.clickOnImageButton(0);
		solo.waitForText(itemName, 0, TIME_TO_WAIT);
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
			case VISIBLE:
				assertMessageAffix = "not visible";
				break;
			case GONE:
				assertMessageAffix = "not gone";
				break;
			default:
				break;
		}
		return assertMessageAffix;
	}

	private void checkIfCheckboxesAreCorrectlyChecked(boolean firstCheckboxExpectedChecked,
			boolean secondCheckboxExpectedChecked) {
		solo.sleep(TIME_TO_WAIT);
		assertEquals("First checkbox not correctly checked", firstCheckboxExpectedChecked, firstCheckBox.isChecked());
		assertEquals("Second checkbox not correctly checked", secondCheckboxExpectedChecked, secondCheckBox.isChecked());
	}

	private void openActionMode(String overflowMenuItemName) {
		clickOnOverflowMenuItem(overflowMenuItemName);
		assertTrue("ActionMode didn't show up", solo.searchText(overflowMenuItemName, true));
	}

	private void acceptAndCloseActionMode() {
		solo.clickOnImage(ACTION_MODE_ACCEPT_IMAGE_BUTTON_INDEX);
	}

	private int getActualNumberOfSounds() {
		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		return soundInfoList.size();
	}
}
