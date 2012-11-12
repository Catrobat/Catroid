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
import org.catrobat.catroid.ui.SoundActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.jayway.android.robotium.solo.Solo;

public class SoundActivityTest extends ActivityInstrumentationTestCase2<SoundActivity> {
	private final int RESOURCE_SOUND = org.catrobat.catroid.uitest.R.raw.longsound;

	private Solo solo = null;
	private ArrayList<SoundInfo> soundInfoList;
	private ProjectManager projectManager = ProjectManager.getInstance();
	private File soundFile;

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

		if (soundFile == null) {
			Log.d("CATROID", "File NULL!!!");
		} else {
			Log.d("CATROID", "not null :)");
			SoundInfo soundInfo = new SoundInfo();
			soundInfo.setSoundFileName(soundFile.getName());
			soundInfo.setTitle("Test Sound");
			soundInfoList.add(soundInfo);
			projectManager.getFileChecksumContainer().addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
		}

		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
		//		projectManager.deleteCurrentProject();
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
}
