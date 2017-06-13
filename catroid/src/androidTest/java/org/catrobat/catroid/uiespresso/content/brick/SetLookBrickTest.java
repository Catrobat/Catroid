/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.content.brick;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.controller.LookController;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.uiespresso.annotations.Flaky;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.FileTestUtils;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.saveFileToProject;

//TODO incomplete Test!

@RunWith(AndroidJUnit4.class)
public class SetLookBrickTest {
	private static final int RESOURCE_LOOK = org.catrobat.catroid.test.R.raw.icon;
	private static final int RESOURCE_LOOK2 = org.catrobat.catroid.test.R.raw.icon2;

	private int brickPosition;
	private String projectName = "SetLookBrickAddNewLook";
	private String lookName = "testLook1";
	private String lookName2 = "testLook2";
	private List<LookData> lookDataList;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testSetLookBrick() {
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_set_look);

		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_set_look_spinner)
				.checkShowsText(lookName);
	}

	@Test
	@Flaky
	public void testAddNewLook() throws Exception {
		onView(withId(R.id.brick_set_look_spinner))
				.perform(click());
		onView(withText(R.string.brick_variable_spinner_create_new_variable))
				.perform(click());

		File paintroidFile = getPaintroidImageFile();
		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", paintroidFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		ScriptActivity currentActivity = baseActivityTestRule.getActivity();
		LookFragment lookFragment = (LookFragment) currentActivity.getFragment(ScriptActivity.FRAGMENT_LOOKS);
		lookFragment.startActivityForResult(intent, LookController.REQUEST_SELECT_OR_DRAW_IMAGE);
		pressBack();

		String paintroidFileName = paintroidFile.getName();

		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_set_look_spinner)
				.checkShowsText(paintroidFileName);

		onBrickAtPosition(brickPosition).onChildView(withId(R.id.brick_set_look_spinner))
				.perform(click());
		onView(withText(paintroidFileName))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testSelectLookAndPlay() {
		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_set_look_spinner)
				.checkShowsText(lookName);
		onView(withId(R.id.button_play))
				.perform(click());

		Look look = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0).look;
		assertEquals("look not set", look.getImagePath(), lookDataList.get(0).getAbsolutePath());
		pressBack();
		pressBack();

		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_set_look_spinner)
				.performSelect(lookName2);

		onView(withId(R.id.button_play))
				.perform(click());
		look = ProjectManager.getInstance().getCurrentProject().getDefaultScene().getSpriteList().get(0).look;
		assertEquals("look not set", look.getImagePath(), lookDataList.get(1).getAbsolutePath());
	}

	@Test
	public void testAdapterUpdateInScriptActivity() {
		String look1ImagePath = lookDataList.get(0).getAbsolutePath();
		String look2ImagePath = lookDataList.get(1).getAbsolutePath();

		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_set_look_spinner)
				.checkShowsText(lookName);

		onView(withId(R.id.button_play))
				.perform(click());
		String lookPath = ProjectManager.getInstance().getCurrentSprite().getLookDataList().get(0).getAbsolutePath();
		assertEquals(look1ImagePath, lookPath);
		pressBack();
		pressBack();

		for (int i = 0; i < 2; ++i) {
			selectAndCheckLook(lookName2, look2ImagePath);
			selectAndCheckLook(lookName, look1ImagePath);
		}
	}

	@Test
	public void testDismissNewSceneDialog() {
		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_set_look_spinner)
				.performSelect(R.string.brick_variable_spinner_create_new_variable);
		closeSoftKeyboard();
		pressBack();

		assertEquals("Not in ScriptActivity", "ui.ScriptActivity",
				UiTestUtils.getCurrentActivity().getLocalClassName());
		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_set_look_spinner)
				.checkShowsText(R.string.brick_variable_spinner_create_new_variable);
	}

	private void selectAndCheckLook(String newLook, String lookImagePath) {
		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_set_look_spinner)
				.performSelect(newLook);

		onView(withId(R.id.button_play))
				.perform(click());
		String lookPath = ProjectManager.getInstance().getCurrentSprite().look.getImagePath();
		assertEquals("Wrong image shown in stage --> Problem with Adapter update in Script", lookImagePath, lookPath);
		pressBack();
		pressBack();
	}

	private File getPaintroidImageFile() throws Exception {
		String fileName = "testFile";
		String sceneName = ProjectManager.getInstance().getCurrentScene().getName();
		int fileId = org.catrobat.catroid.test.R.drawable.catroid_banzai;

		return saveFileToProject(projectName, sceneName, fileName, fileId, getInstrumentation().getContext(),
				FileTestUtils.FileTypes.IMAGE);
	}

	private void createProject() {
		BrickTestUtils.createProjectAndGetStartScript(projectName).addBrick(new SetLookBrick());
		brickPosition = 1;

		ProjectManager projectManager = ProjectManager.getInstance();
		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		Project project = projectManager.getCurrentProject();

		File lookFile = saveFileToProject(projectName, project.getDefaultScene().getName(), "image.png",
				RESOURCE_LOOK, getInstrumentation().getContext(), FileTestUtils.FileTypes.IMAGE);
		LookData lookData = new LookData();
		lookData.setLookFilename(lookFile.getName());
		lookData.setLookName(lookName);

		File lookFile2 = saveFileToProject(projectName, project.getDefaultScene().getName(), "image2.png",
				RESOURCE_LOOK2, getInstrumentation().getContext(), FileTestUtils.FileTypes.IMAGE);
		LookData lookData2 = new LookData();
		lookData2.setLookFilename(lookFile2.getName());
		lookData2.setLookName(lookName2);

		lookDataList.add(lookData);
		lookDataList.add(lookData2);
		projectManager.getFileChecksumContainer().addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());
		projectManager.getFileChecksumContainer().addChecksum(lookData2.getChecksum(), lookData2.getAbsolutePath());
	}
}
