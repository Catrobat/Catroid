/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
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

		testImage = TestUtils.saveFileToProject(projectName, project.getDefaultScene().getName(), "testImage.png", IMAGE_FILE_ID, getInstrumentation()
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

		Sprite sprite = new SingleSprite("cat");

		LookData lookData1 = new LookData();
		lookData1.setLookFilename(testImage.getName());
		lookData1.setName("testImage1");
		sprite.getLookDataList().add(lookData1);

		LookData lookData2 = new LookData();
		lookData2.setLookFilename(testImage.getName());
		lookData2.setName("testImage2");
		sprite.getLookDataList().add(lookData2);

		ActionFactory factory = sprite.getActionFactory();
		Action setLookAction = factory.createSetLookAction(sprite, lookData1);
		Action nextLookAction = factory.createNextLookAction(sprite);

		setLookAction.act(1.0f);
		nextLookAction.act(1.0f);

		assertEquals("Look is not next look", lookData2, sprite.look.getLookData());
	}

	public void testLastLook() {
		Sprite sprite = new SingleSprite("cat");

		LookData lookData1 = new LookData();
		lookData1.setLookFilename(testImage.getName());
		lookData1.setName("testImage1");
		lookData1.setLookFilename("testImage1");
		sprite.getLookDataList().add(lookData1);

		LookData lookData2 = new LookData();
		lookData2.setLookFilename(testImage.getName());
		lookData2.setName("testImage");
		lookData2.setLookFilename("testImage2");
		sprite.getLookDataList().add(lookData2);

		LookData lookData3 = new LookData();
		lookData3.setLookFilename(testImage.getName());
		lookData3.setName("testImage");
		lookData3.setLookFilename("testImage3");
		sprite.getLookDataList().add(lookData3);

		ActionFactory factory = sprite.getActionFactory();
		Action setLookAction = factory.createSetLookAction(sprite, lookData3);
		Action nextLookAction = factory.createNextLookAction(sprite);

		setLookAction.act(1.0f);
		nextLookAction.act(1.0f);

		assertEquals("Look is not next look", lookData1, sprite.look.getLookData());
	}

	public void testLookGalleryNull() {

		Sprite sprite = new SingleSprite("cat");
		ActionFactory factory = sprite.getActionFactory();
		Action nextLookAction = factory.createNextLookAction(sprite);
		nextLookAction.act(1.0f);

		assertEquals("Look is not null", null, sprite.look.getLookData());
	}

	public void testLookGalleryWithOneLook() {
		Sprite sprite = new SingleSprite("cat");

		LookData lookData1 = new LookData();
		lookData1.setLookFilename(testImage.getName());
		lookData1.setName("testImage1");
		sprite.getLookDataList().add(lookData1);

		ActionFactory factory = sprite.getActionFactory();
		Action setLookAction = factory.createSetLookAction(sprite, lookData1);
		Action nextLookAction = factory.createNextLookAction(sprite);

		setLookAction.act(1.0f);
		nextLookAction.act(1.0f);

		assertEquals("Wrong look after executing NextLookBrick with just one look", lookData1,
				sprite.look.getLookData());
	}

	public void testNextLookWithNoLookSet() {

		Sprite sprite = new SingleSprite("cat");

		ActionFactory factory = sprite.getActionFactory();
		Action nextLookAction = factory.createNextLookAction(sprite);

		LookData lookData1 = new LookData();
		lookData1.setLookFilename(testImage.getName());
		lookData1.setName("testImage1");
		sprite.getLookDataList().add(lookData1);

		nextLookAction.act(1.0f);

		assertNull("No Custume should be set.", sprite.look.getLookData());
	}
}
