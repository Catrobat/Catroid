package at.tugraz.ist.catroid.uitest.ui;

import java.io.File;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListAdapter;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.SoundActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class SoundActivityTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private String soundName = "testSound1";
	private String soundName2 = "testSound2";
	private File soundFile;
	private File soundFile2;
	private ArrayList<SoundInfo> soundInfoList;
	private final int RESOURCE_SOUND = at.tugraz.ist.catroid.uitest.R.raw.longsound;
	private final int RESOURCE_SOUND2 = at.tugraz.ist.catroid.uitest.R.raw.testsoundui;

	public SoundActivityTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();
		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();

		soundFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "longsound.mp3",
				RESOURCE_SOUND, getInstrumentation().getContext(), UiTestUtils.TYPE_SOUND_FILE);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(soundName);

		soundFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "testsoundui.mp3",
				RESOURCE_SOUND2, getInstrumentation().getContext(), UiTestUtils.TYPE_SOUND_FILE);
		SoundInfo soundInfo2 = new SoundInfo();
		soundInfo2.setSoundFileName(soundFile2.getName());
		soundInfo2.setTitle(soundName2);

		soundInfoList.add(soundInfo);
		soundInfoList.add(soundInfo2);
		ProjectManager.getInstance().fileChecksumContainer.addChecksum(soundInfo.getChecksum(),
				soundInfo.getAbsolutePath());
		ProjectManager.getInstance().fileChecksumContainer.addChecksum(soundInfo2.getChecksum(),
				soundInfo2.getAbsolutePath());

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

	public void testDeleteSound() {
		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.sleep(1000);
		ListAdapter adapter = ((SoundActivity) solo.getCurrentActivity()).getListAdapter();
		int oldCount = adapter.getCount();
		solo.clickOnButton(getActivity().getString(R.string.sound_delete));
		adapter = ((SoundActivity) solo.getCurrentActivity()).getListAdapter();
		int newCount = adapter.getCount();
		assertEquals("the old count was not rigth", 2, oldCount);
		assertEquals("the new count is not rigth - one costume should be deleted", 1, newCount);
		assertEquals("the count of the costumeDataList is not right", 1, soundInfoList.size());
	}

	public void testRenameSound() {
		String newName = "newSoundName";
		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.sleep(500);
		solo.clickOnButton(getActivity().getString(R.string.sound_rename));
		assertTrue("wrong title of dialog", solo.searchText(soundName));
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.sleep(100);
		solo.clickOnButton(getActivity().getString(R.string.ok));
		solo.sleep(500);
		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		assertEquals("sound is not renamed in SoundList", newName, soundInfoList.get(0).getTitle());
		if (!solo.searchText(newName)) {
			fail("sound not renamed in actual view");
		}
	}

	public void testPlayAndStopStound() {
		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.sleep(500);
		SoundInfo soundInfo = soundInfoList.get(0);
		assertFalse("Mediaplayer is playing although no play button was touched", soundInfo.isPlaying);
		solo.clickOnButton(getActivity().getString(R.string.sound_play));
		solo.sleep(20);
		assertTrue("Mediaplayer is not playing", soundInfo.isPlaying);
		solo.clickOnButton(getActivity().getString(R.string.sound_stop));
		solo.sleep(40);
		assertFalse("Mediaplayer is playing after touching stop button", soundInfo.isPlaying);
	}

	public void testToStageButton() {
		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.sleep(500);
		//fu!?
		solo.clickOnImageButton(2); //sorry UiTestUtils.clickOnImageButton just won't work after switching tabs

		solo.sleep(5000);
		solo.assertCurrentActivity("not in stage", StageActivity.class);
		solo.goBack();
		solo.sleep(3000);
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
		soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		assertEquals("soundlist in sprite doesn't hold the right number of soundinfos", 2, soundInfoList.size());
	}

	public void testMainMenuButton() {
		solo.clickOnText(getActivity().getString(R.string.sounds));
		solo.sleep(500);
		solo.clickOnImageButton(0);
		solo.assertCurrentActivity("Clicking on main menu button did not cause main menu to be displayed",
				MainMenuActivity.class);
	}
}
