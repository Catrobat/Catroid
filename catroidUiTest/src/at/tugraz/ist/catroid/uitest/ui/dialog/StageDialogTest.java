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
package at.tugraz.ist.catroid.uitest.ui.dialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaPlayer;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class StageDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private String testProject = UiTestUtils.PROJECTNAME1;
	private StorageHandler storageHandler;

	public StageDialogTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
		storageHandler = StorageHandler.getInstance();
	}

	@Override
	public void setUp() throws Exception {
		UiTestUtils.clearAllUtilTestProjects();

		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testBackButtonPressedTwice() {

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_play);
		solo.sleep(1000);
		solo.goBack();

		solo.goBack();

		solo.assertCurrentActivity("Program is not in stage activity", MainMenuActivity.class);
	}

	public void testBackToPreviousActivity() throws NameNotFoundException, IOException {
		createTestProject(testProject);
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.clickOnText(testProject);

		Activity previousActivity = getActivity();

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_play);

		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.back));

		solo.sleep(1000);
		assertEquals("Not equal Activities", previousActivity, getActivity());
	}

	public void testPauseOnBackButton() {
		double scale = 100.0;

		Project project = new Project(getActivity(), testProject);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript(sprite);
		WaitBrick waitBrick = new WaitBrick(sprite, 5000);
		SetSizeToBrick scaleCostumeBrick = new SetSizeToBrick(sprite, scale);

		script.addBrick(waitBrick);
		script.addBrick(scaleCostumeBrick);
		sprite.addScript(script);
		project.addSprite(sprite);

		storageHandler.saveProject(project);
		ProjectManager.getInstance().setProject(project);

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_play);

		//		assertEquals("Unexpected sprite size", 100.0, sprite.getSize());
		//		solo.goBack();
		//		solo.sleep(6000);
		//		solo.goBack();
		//		assertEquals("Unexpected sprite size", 100.0, sprite.getSize());
		//		solo.sleep(4000);
		//		assertEquals("Unexpected sprite size", scale, sprite.getSize());
	}

	public void testRestartButtonActivityChain() throws NameNotFoundException, IOException {
		createTestProject(testProject);
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.clickOnText(testProject);

		Activity currentActivity = solo.getCurrentActivity();

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_play);

		solo.sleep(1000);
		solo.goBack();
		solo.sleep(1000);
		solo.clickOnButton(getActivity().getString(R.string.restart_current_project));
		solo.sleep(1000);

		solo.assertCurrentActivity("Program is not in stage activity", StageActivity.class);

		solo.sleep(500);
		solo.goBack();
		solo.sleep(500);
		solo.clickOnButton(getActivity().getString(R.string.back));
		solo.sleep(500);
		assertEquals("Returned to wrong Activity", currentActivity, solo.getCurrentActivity());
	}

	public void testRestartButtonScriptPosition() {
		ArrayList<Integer> scriptPositionsStart = new ArrayList<Integer>();
		ArrayList<Integer> scriptPositionsRestart = new ArrayList<Integer>();
		scriptPositionsStart.clear();
		scriptPositionsRestart.clear();

		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_play);

		solo.sleep(2000);

		ProjectManager projectManager = ProjectManager.getInstance();
		Project projectStart = projectManager.getCurrentProject();

		String projectNameStart = projectStart.getName();

		//scriptPositions at start
		List<Sprite> spriteList = projectStart.getSpriteList();
		for (int i = 0; i < spriteList.size(); i++) {
			Sprite sprite = spriteList.get(i);
			int size = sprite.getNumberOfScripts();
			for (int j = 0; j < size; j++) {
				scriptPositionsRestart.add(sprite.getScript(j).getExecutingBrickIndex());
			}
		}
		spriteList.clear();

		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);
		solo.sleep(500);
		solo.goBack();
		solo.sleep(500);
		solo.clickOnButton(getActivity().getString(R.string.restart_current_project));
		solo.sleep(1000);

		//scriptPositions in between
		Project projectRestart = ProjectManager.getInstance().getCurrentProject();
		String projectNameRestart = projectRestart.getName();

		assertEquals("Wrong project after restart", projectNameStart, projectNameRestart);

		spriteList = projectRestart.getSpriteList();
		for (int i = 0; i < spriteList.size(); i++) {
			Sprite sprite = spriteList.get(i);
			int size = sprite.getNumberOfScripts();
			for (int j = 0; j < size; j++) {
				scriptPositionsRestart.add(sprite.getScript(j).getExecutingBrickIndex());
			}
		}

		for (int i = 0; i < scriptPositionsStart.size(); i++) {
			assertEquals("Script is not at starting position!", scriptPositionsStart.get(i).intValue(),
					scriptPositionsRestart.get(i).intValue());
		}
	}

	public void testRestartProjectWithSound() {

		String projectName = UiTestUtils.PROJECTNAME1;
		//creating sprites for project:
		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new StartScript(firstSprite);

		PlaySoundBrick playSoundBrick = new PlaySoundBrick(firstSprite);

		startScript.addBrick(playSoundBrick);

		firstSprite.addScript(startScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());

		File soundFile = UiTestUtils.saveFileToProject(projectName, "soundfile.mp3",
				at.tugraz.ist.catroid.uitest.R.raw.longsound, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.SOUND);

		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(soundFile.getName());
		playSoundBrick.setSoundInfo(soundInfo);

		firstSprite.getSoundList().add(soundInfo);

		storageHandler.saveProject(project);

		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_play);
		solo.sleep(3000);
		assertTrue("Sound not playing.", mediaPlayer.isPlaying());
		int positionBeforeRestart = mediaPlayer.getCurrentPosition();
		solo.goBack();
		solo.sleep(500);
		assertFalse("Sound playing but should be paused.", mediaPlayer.isPlaying());
		solo.clickOnButton(getActivity().getString(R.string.restart_current_project));
		solo.sleep(1000);
		@SuppressWarnings("unchecked")
		ArrayList<MediaPlayer> mediaPlayerArrayList = (ArrayList<MediaPlayer>) UiTestUtils.getPrivateField(
				"mediaPlayers", SoundManager.getInstance());
		int positionAfterRestart = mediaPlayerArrayList.get(0).getCurrentPosition();
		assertTrue("Sound not playing after stage restart.", mediaPlayerArrayList.get(0).isPlaying());
		assertTrue("Sound did not play from start!", positionBeforeRestart > positionAfterRestart);
	}

	public void testAxesOnOff() throws NameNotFoundException, IOException {
		createTestProject(testProject);
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.clickOnText(testProject);
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_play);
		solo.waitForActivity("StageActivity");
		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.stagemenu_axes_on));
		solo.clickOnButton(getActivity().getString(R.string.resume_current_project));
		solo.sleep(100);
		byte[] redPixel = { (byte) 255, 0, 0, (byte) 255 };
		byte[] stagePixel = StageActivity.stageListener.getPixels(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2, 1,
				1);
		UiTestUtils.compareByteArrays(redPixel, stagePixel);
		stagePixel = StageActivity.stageListener.getPixels(Values.SCREEN_WIDTH / 2, 0, 1, 1);
		UiTestUtils.compareByteArrays(redPixel, stagePixel);
		stagePixel = StageActivity.stageListener.getPixels(Values.SCREEN_WIDTH - 1, Values.SCREEN_HEIGHT / 2, 1, 1);
		UiTestUtils.compareByteArrays(redPixel, stagePixel);
		stagePixel = StageActivity.stageListener.getPixels(0, Values.SCREEN_HEIGHT / 2, 1, 1);
		UiTestUtils.compareByteArrays(redPixel, stagePixel);
		stagePixel = StageActivity.stageListener.getPixels(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT, 1, 1);
		UiTestUtils.compareByteArrays(redPixel, stagePixel);
		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.stagemenu_axes_off));
		solo.clickOnButton(getActivity().getString(R.string.resume_current_project));
		solo.sleep(100);
		byte[] whitePixel = { (byte) 255, (byte) 255, (byte) 255, (byte) 255 };
		stagePixel = StageActivity.stageListener.getPixels(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, stagePixel);
		stagePixel = StageActivity.stageListener.getPixels(Values.SCREEN_WIDTH / 2, 0, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, stagePixel);
		stagePixel = StageActivity.stageListener.getPixels(Values.SCREEN_WIDTH - 1, Values.SCREEN_HEIGHT / 2, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, stagePixel);
		stagePixel = StageActivity.stageListener.getPixels(0, Values.SCREEN_HEIGHT / 2, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, stagePixel);
		stagePixel = StageActivity.stageListener.getPixels(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, stagePixel);

	}

	public void testMaximizeStretch() throws NameNotFoundException, IOException {
		Project project = createTestProject(testProject);
		project.virtualScreenWidth = 480;
		project.virtualScreenHeight = 700;
		project.setDeviceData(getActivity());
		storageHandler.saveProject(project);
		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.clickOnText(testProject);
		UiTestUtils.clickOnLinearLayout(solo, R.id.btn_action_play);
		solo.waitForActivity("StageActivity");
		byte[] whitePixel = { (byte) 255, (byte) 255, (byte) 255, (byte) 255 };
		byte[] screenPixel = StageActivity.stageListener.getPixels(0, 0, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(Values.SCREEN_WIDTH - 1, Values.SCREEN_HEIGHT - 1, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(Values.SCREEN_WIDTH - 1, 0, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(0, Values.SCREEN_HEIGHT - 1, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);
		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.stagemenu_screen_size));
		solo.clickOnButton(getActivity().getString(R.string.resume_current_project));
		solo.sleep(100);
		byte[] blackPixel = { 0, 0, 0, (byte) 255 };
		screenPixel = StageActivity.stageListener.getPixels(0, 0, 1, 1);
		UiTestUtils.compareByteArrays(blackPixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(Values.SCREEN_WIDTH - 1, Values.SCREEN_HEIGHT - 1, 1, 1);
		UiTestUtils.compareByteArrays(blackPixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(Values.SCREEN_WIDTH - 1, 0, 1, 1);
		UiTestUtils.compareByteArrays(blackPixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(0, Values.SCREEN_HEIGHT - 1, 1, 1);
		UiTestUtils.compareByteArrays(blackPixel, screenPixel);

		screenPixel = StageActivity.stageListener.getPixels(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);

		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.stagemenu_screen_size));
		solo.clickOnButton(getActivity().getString(R.string.resume_current_project));
		solo.sleep(100);
		screenPixel = StageActivity.stageListener.getPixels(0, 0, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(Values.SCREEN_WIDTH - 1, Values.SCREEN_HEIGHT - 1, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(Values.SCREEN_WIDTH - 1, 0, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(0, Values.SCREEN_HEIGHT - 1, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);

	}

	public Project createTestProject(String projectName) throws IOException, NameNotFoundException {
		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(getActivity(), projectName);
		Sprite firstSprite = new Sprite("cat");
		Sprite secondSprite = new Sprite("dog");
		Sprite thirdSprite = new Sprite("horse");
		Sprite fourthSprite = new Sprite("pig");

		project.addSprite(firstSprite);
		project.addSprite(secondSprite);
		project.addSprite(thirdSprite);
		project.addSprite(fourthSprite);

		storageHandler.saveProject(project);
		return project;
	}

}
