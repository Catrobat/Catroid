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
package org.catrobat.catroid.uitest.ui.dialog;

import android.app.Activity;
import android.media.MediaPlayer;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.io.SoundManager;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StageDialogTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private String testProject = UiTestUtils.PROJECTNAME1;

	public StageDialogTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
	}

	public void testBackButtonPressedTwice() {
		Project project = createTestProject(testProject);
		ProjectManager.getInstance().setProject(project);

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(1000);
		solo.goBack();

		solo.goBack();
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		solo.assertCurrentActivity("Program is not in stage activity", ProjectActivity.class);
	}

	public void testBackToPreviousActivity() {
		createAndSaveTestProject(testProject);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("Cannot click project.", UiTestUtils.clickOnTextInList(solo, testProject));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		Activity previousActivity = getActivity();
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());

		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.stage_dialog_back));

		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		assertEquals("Not equal Activities", previousActivity, getActivity());
	}

	public void testPauseOnBackButton() {
		float defaultScale = 100.0f;
		float newScale = 50.0f;

		Project project = new Project(getActivity(), testProject);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();
		WaitBrick waitBrick = new WaitBrick(5000);
		SetSizeToBrick scaleLookBrick = new SetSizeToBrick(newScale);

		script.addBrick(waitBrick);
		script.addBrick(scaleLookBrick);
		sprite.addScript(script);
		project.addSprite(sprite);

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(1000);
		assertEquals("Unexpected look size", defaultScale, sprite.look.getSizeInUserInterfaceDimensionUnit());

		solo.goBack();
		solo.sleep(6000);
		assertEquals("Unexpected look size", defaultScale, sprite.look.getSizeInUserInterfaceDimensionUnit());

		solo.clickOnButton(solo.getString(R.string.stage_dialog_resume));
		solo.sleep(6000);
		assertEquals("Unexpected look size", newScale, sprite.look.getSizeInUserInterfaceDimensionUnit());
	}

	public void testRestartButtonActivityChain() {
		createAndSaveTestProject(testProject);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("Cannot click project.", UiTestUtils.clickOnTextInList(solo, testProject));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		Activity currentActivity = solo.getCurrentActivity();

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.goBack();
		solo.sleep(400);
		solo.clickOnButton(solo.getString(R.string.stage_dialog_restart));
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.assertCurrentActivity("Program is not in stage activity", StageActivity.class);

		solo.sleep(500);
		solo.goBack();
		solo.sleep(100);
		solo.clickOnButton(solo.getString(R.string.stage_dialog_back));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		assertEquals("Returned to wrong Activity", currentActivity, solo.getCurrentActivity());
	}

	public void testRestartButtonScriptPosition() {
		createAndSaveTestProject(testProject);
		ArrayList<Script> scriptStart = new ArrayList<Script>();
		ArrayList<Script> scriptRestart = new ArrayList<Script>();
		scriptStart.clear();
		scriptRestart.clear();

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("Cannot click project.", UiTestUtils.clickOnTextInList(solo, testProject));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(1000);

		ProjectManager projectManager = ProjectManager.getInstance();
		Project projectStart = projectManager.getCurrentProject();

		String projectNameStart = projectStart.getName();

		//scriptPositions at start
		List<Sprite> spriteList = projectStart.getSpriteList();
		for (int i = 0; i < spriteList.size(); i++) {
			Sprite sprite = spriteList.get(i);
			int size = sprite.getNumberOfScripts();
			for (int j = 0; j < size; j++) {
				scriptRestart.add(sprite.getScript(j));
			}
		}
		spriteList.clear();

		solo.clickOnScreen(ScreenValues.SCREEN_WIDTH / 2, ScreenValues.SCREEN_HEIGHT / 2);
		solo.sleep(200);
		solo.goBack();
		solo.sleep(100);
		solo.clickOnButton(solo.getString(R.string.stage_dialog_restart));
		solo.sleep(300);

		//scriptPositions in between
		Project projectRestart = ProjectManager.getInstance().getCurrentProject();
		String projectNameRestart = projectRestart.getName();

		assertEquals("Wrong project after restart", projectNameStart, projectNameRestart);

		spriteList = projectRestart.getSpriteList();
		for (int i = 0; i < spriteList.size(); i++) {
			Sprite sprite = spriteList.get(i);
			int size = sprite.getNumberOfScripts();
			for (int j = 0; j < size; j++) {
				scriptStart.add(sprite.getScript(j));
			}
		}

		for (int i = 0; i < scriptStart.size(); i++) {
			assertEquals("Script is not at starting position!", scriptRestart.get(i).getClass(), scriptStart.get(i)
					.getClass());
		}
	}

	public void testRestartProjectWithSound() {
		String projectName = UiTestUtils.PROJECTNAME1;
		//creating sprites for project:
		Sprite firstSprite = new Sprite("sprite1");
		Script startScript = new StartScript();

		PlaySoundBrick playSoundBrick = new PlaySoundBrick();

		startScript.addBrick(playSoundBrick);

		firstSprite.addScript(startScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());

		File soundFile = UiTestUtils.saveFileToProject(projectName, "soundfile.mp3",
				org.catrobat.catroid.test.R.raw.longsound, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.SOUND);

		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(soundFile.getName());
		playSoundBrick.setSoundInfo(soundInfo);

		firstSprite.getSoundList().add(soundInfo);

		StorageHandler.getInstance().saveProject(project);

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(4000);

		MediaPlayer mediaPlayer = getMediaPlayers().get(0);
		assertTrue("Sound not playing.", mediaPlayer.isPlaying());
		int positionBeforeRestart = mediaPlayer.getCurrentPosition();
		solo.goBack();
		solo.sleep(500);
		assertFalse("Sound playing but should be paused.", mediaPlayer.isPlaying());
		solo.clickOnButton(solo.getString(R.string.stage_dialog_restart));
		solo.sleep(2000);

		mediaPlayer = getMediaPlayers().get(0);
		int positionAfterRestart = mediaPlayer.getCurrentPosition();
		assertTrue("Sound not playing after stage restart.", mediaPlayer.isPlaying());
		assertTrue("Sound did not play from start!", positionBeforeRestart > positionAfterRestart);
	}

	public void testAxesOnOff() {
		createAndSaveTestProject(testProject);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("Cannot click project.", UiTestUtils.clickOnTextInList(solo, testProject));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.goBack();
		solo.sleep(600);
		solo.clickOnButton(solo.getString(R.string.stage_dialog_axes_on));
		solo.clickOnButton(solo.getString(R.string.stage_dialog_resume));
		solo.sleep(100);
		byte[] redPixel = { (byte) 255, 0, 0, (byte) 255 };
		byte[] stagePixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH / 2,
				ScreenValues.SCREEN_HEIGHT / 2, 1, 1);
		UiTestUtils.compareByteArrays(redPixel, stagePixel);
		stagePixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH / 2, 0, 1, 1);
		UiTestUtils.compareByteArrays(redPixel, stagePixel);
		stagePixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH - 1,
				ScreenValues.SCREEN_HEIGHT / 2, 1, 1);
		UiTestUtils.compareByteArrays(redPixel, stagePixel);
		stagePixel = StageActivity.stageListener.getPixels(0, ScreenValues.SCREEN_HEIGHT / 2, 1, 1);
		UiTestUtils.compareByteArrays(redPixel, stagePixel);
		stagePixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH / 2,
				ScreenValues.SCREEN_HEIGHT - 1, 1, 1);
		UiTestUtils.compareByteArrays(redPixel, stagePixel);
		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.stage_dialog_axes_off));
		solo.clickOnButton(solo.getString(R.string.stage_dialog_resume));
		solo.sleep(100);
		byte[] whitePixel = { (byte) 255, (byte) 255, (byte) 255, (byte) 255 };
		stagePixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH / 2,
				ScreenValues.SCREEN_HEIGHT / 2, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, stagePixel);
		stagePixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH / 2, 0, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, stagePixel);
		stagePixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH - 1,
				ScreenValues.SCREEN_HEIGHT / 2, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, stagePixel);
		stagePixel = StageActivity.stageListener.getPixels(0, ScreenValues.SCREEN_HEIGHT / 2, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, stagePixel);
		stagePixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH / 2,
				ScreenValues.SCREEN_HEIGHT - 1, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, stagePixel);
	}

	public void testMaximizeStretch() {
		Project project = createTestProject(testProject);
		project.getXmlHeader().virtualScreenWidth = 480;
		project.getXmlHeader().virtualScreenHeight = 700;
		project.setDeviceData(getActivity());
		StorageHandler.getInstance().saveProject(project);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("Cannot click project.", UiTestUtils.clickOnTextInList(solo, testProject));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		Utils.updateScreenWidthAndHeight(getActivity());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		assertTrue("Stage not resizeable.", ((StageActivity) solo.getCurrentActivity()).getResizePossible());
		byte[] whitePixel = { (byte) 255, (byte) 255, (byte) 255, (byte) 255 };
		byte[] screenPixel = StageActivity.stageListener.getPixels(0, 0, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH - 1,
				ScreenValues.SCREEN_HEIGHT - 1, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH - 1, 0, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(0, ScreenValues.SCREEN_HEIGHT - 1, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);
		solo.goBack();
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_maximize));
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_continue));
		solo.sleep(100);
		byte[] blackPixel = { 0, 0, 0, (byte) 255 };
		screenPixel = StageActivity.stageListener.getPixels(0, 0, 1, 1);
		UiTestUtils.compareByteArrays(blackPixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH - 1,
				ScreenValues.SCREEN_HEIGHT - 1, 1, 1);
		UiTestUtils.compareByteArrays(blackPixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH - 1, 0, 1, 1);
		UiTestUtils.compareByteArrays(blackPixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(0, ScreenValues.SCREEN_HEIGHT - 1, 1, 1);
		UiTestUtils.compareByteArrays(blackPixel, screenPixel);

		screenPixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH / 2,
				ScreenValues.SCREEN_HEIGHT / 2, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);

		solo.goBack();
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_maximize));
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_continue));
		solo.sleep(100);
		screenPixel = StageActivity.stageListener.getPixels(0, 0, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH - 1,
				ScreenValues.SCREEN_HEIGHT - 1, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH - 1, 0, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(0, ScreenValues.SCREEN_HEIGHT - 1, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);

		solo.goBack();
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_maximize));
		solo.goBack();

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		assertTrue("Stage not resizeable.", ((StageActivity) solo.getCurrentActivity()).getResizePossible());

		screenPixel = StageActivity.stageListener.getPixels(0, 0, 1, 1);
		UiTestUtils.compareByteArrays(blackPixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH - 1,
				ScreenValues.SCREEN_HEIGHT - 1, 1, 1);
		UiTestUtils.compareByteArrays(blackPixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH - 1, 0, 1, 1);
		UiTestUtils.compareByteArrays(blackPixel, screenPixel);
		screenPixel = StageActivity.stageListener.getPixels(0, ScreenValues.SCREEN_HEIGHT - 1, 1, 1);
		UiTestUtils.compareByteArrays(blackPixel, screenPixel);

		screenPixel = StageActivity.stageListener.getPixels(ScreenValues.SCREEN_WIDTH / 2,
				ScreenValues.SCREEN_HEIGHT / 2, 1, 1);
		UiTestUtils.compareByteArrays(whitePixel, screenPixel);
	}

	public void testMaximizeStretchRememeberSetting() {
		Project project = createTestProject(testProject);
		project.getXmlHeader().virtualScreenWidth = 480;
		project.getXmlHeader().virtualScreenHeight = 700;
		project.setDeviceData(getActivity());
		StorageHandler.getInstance().saveProject(project);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForFragmentById(R.id.fragment_projects_list);
		assertTrue("Cannot click project.", UiTestUtils.clickOnTextInList(solo, testProject));
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		Utils.updateScreenWidthAndHeight(getActivity());
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		assertTrue("Stage not resizeable.", ((StageActivity) solo.getCurrentActivity()).getResizePossible());

		solo.sleep(200);
		solo.goBack();
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_continue));
		solo.sleep(200);
		solo.goBack();
		solo.goBack();
		StorageHandler.getInstance().saveProject(project);
		solo.sleep(200);

		assertTrue("Wrong screenMode in xml-file.",
				ProjectManager.getInstance().getCurrentProject().getScreenMode() == ScreenModes.STRETCH);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(200);
		solo.goBack();
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_maximize));
		solo.clickOnView(solo.getView(R.id.stage_dialog_button_continue));
		solo.sleep(200);
		solo.goBack();
		solo.goBack();

		assertTrue("Wrong screenMode in xml-file.",
				ProjectManager.getInstance().getCurrentProject().getScreenMode() == ScreenModes.MAXIMIZE);
	}

	private Project createTestProject(String projectName) {
		Project project = new Project(getActivity(), projectName);
		Sprite firstSprite = new Sprite("cat");
		Sprite secondSprite = new Sprite("dog");
		Sprite thirdSprite = new Sprite("horse");
		Sprite fourthSprite = new Sprite("pig");

		project.addSprite(firstSprite);
		project.addSprite(secondSprite);
		project.addSprite(thirdSprite);
		project.addSprite(fourthSprite);

		return project;
	}

	private Project createAndSaveTestProject(String projectName) {
		Project project = createTestProject(projectName);
		StorageHandler.getInstance().saveProject(project);
		return project;
	}

	@SuppressWarnings("unchecked")
	private List<MediaPlayer> getMediaPlayers() {
		return (List<MediaPlayer>) Reflection.getPrivateField(SoundManager.getInstance(), "mediaPlayers");
	}
}
