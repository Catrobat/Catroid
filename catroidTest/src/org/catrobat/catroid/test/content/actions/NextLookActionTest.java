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
package org.catrobat.catroid.test.content.actions;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.NextLookAction;
import org.catrobat.catroid.content.actions.SetLookAction;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;

public class NextLookActionTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = R.raw.icon;
	private File testImage;
	private String projectName = UiTestUtils.DEFAULT_TEST_PROJECT_NAME;

	@Override
	protected void setUp() throws Exception {

		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		UiTestUtils.createEmptyProject();
		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(projectName, "testImage.png", IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);

		ScreenValues.SCREEN_HEIGHT = 200;
		ScreenValues.SCREEN_WIDTH = 200;
	}

	@Override
	protected void tearDown() throws Exception {
		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}
		if (testImage != null && testImage.exists()) {
			testImage.delete();
		}
		super.tearDown();
	}

	public void testNextLook() {

		Sprite sprite = new Sprite("cat");

		LookData lookData1 = new LookData();
		lookData1.setLookFilename(testImage.getName());
		lookData1.setLookName("testImage1");
		sprite.getLookDataList().add(lookData1);

		LookData lookData2 = new LookData();
		lookData2.setLookFilename(testImage.getName());
		lookData2.setLookName("testImage2");
		sprite.getLookDataList().add(lookData2);

		SetLookAction setLookAction = ExtendedActions.setLook(sprite, lookData1);
		NextLookAction nextLookAction = ExtendedActions.nextLook(sprite);

		setLookAction.act(1.0f);
		nextLookAction.act(1.0f);

		assertEquals("Look is not next look", lookData2, sprite.look.getLookData());
	}

	public void testLastLook() {
		Sprite sprite = new Sprite("cat");

		LookData lookData1 = new LookData();
		lookData1.setLookFilename(testImage.getName());
		lookData1.setLookName("testImage2");
		sprite.getLookDataList().add(lookData1);

		LookData lookData2 = new LookData();
		lookData2.setLookFilename(testImage.getName());
		lookData2.setLookName("testImage");
		sprite.getLookDataList().add(lookData2);

		LookData lookData3 = new LookData();
		lookData3.setLookFilename(testImage.getName());
		lookData3.setLookName("testImage");
		sprite.getLookDataList().add(lookData3);

		SetLookAction setLookAction = ExtendedActions.setLook(sprite, lookData3);
		NextLookAction nextLookAction = ExtendedActions.nextLook(sprite);

		setLookAction.act(1.0f);
		nextLookAction.act(1.0f);

		assertEquals("Look is not next look", lookData1, sprite.look.getLookData());
	}

	public void testLookGalleryNull() {

		Sprite sprite = new Sprite("cat");
		NextLookAction nextLookAction = ExtendedActions.nextLook(sprite);
		nextLookAction.act(1.0f);

		assertEquals("Look is not null", null, sprite.look.getLookData());
	}

	public void testLookGalleryWithOneLook() {
		Sprite sprite = new Sprite("cat");

		LookData lookData1 = new LookData();
		lookData1.setLookFilename(testImage.getName());
		lookData1.setLookName("testImage1");
		sprite.getLookDataList().add(lookData1);

		SetLookAction setLookAction = ExtendedActions.setLook(sprite, lookData1);
		NextLookAction nextLookAction = ExtendedActions.nextLook(sprite);

		setLookAction.act(1.0f);
		nextLookAction.act(1.0f);

		assertEquals("Wrong look after executing NextLookBrick with just one look", lookData1,
				sprite.look.getLookData());
	}

	public void testNextLookWithNoLookSet() {

		Sprite sprite = new Sprite("cat");

		NextLookAction nextLookAction = ExtendedActions.nextLook(sprite);

		LookData lookData1 = new LookData();
		lookData1.setLookFilename(testImage.getName());
		lookData1.setLookName("testImage1");
		sprite.getLookDataList().add(lookData1);

		nextLookAction.act(1.0f);

		assertNull("No Custume should be set.", sprite.look.getLookData());
	}
}
