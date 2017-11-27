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
package org.catrobat.catroid.test.content.actions;

import android.graphics.BitmapFactory;
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
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.junit.Test;

import java.io.File;

public class SetLookActionTest extends InstrumentationTestCase {

	protected static final int IMAGE_FILE_ID = R.raw.icon;
	protected String projectName = "testProject";
	protected File testImage;
	protected Project project;
	protected Sprite sprite;
	protected LookData firstLookData;
	protected LookData secondLookData;

	@Override
	protected void setUp() throws Exception {
		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(this.projectName, project.getDefaultScene().getName(), "testImage.png", IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(this.testImage.getAbsolutePath(), bitmapOptions);

		ScreenValues.SCREEN_HEIGHT = 200;
		ScreenValues.SCREEN_WIDTH = 200;

		sprite = new SingleSprite("new sprite");
		project.getDefaultScene().addSprite(sprite);
		firstLookData = new LookData();
		firstLookData.setLookFilename(testImage.getName());
		firstLookData.setLookName("first look");
		secondLookData = new LookData();
		secondLookData.setLookFilename(testImage.getName());
		secondLookData.setLookName("second look");
		sprite.getLookDataList().add(firstLookData);
		sprite.getLookDataList().add(secondLookData);
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

	@Test
	public void testSetLook() {
		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createSetLookAction(sprite, firstLookData);
		action.act(1.0f);
		assertEquals("Action didn't set the LookData", firstLookData, sprite.look.getLookData());
	}

	@Test
	public void testSetLookByIndex() {
		Formula formula = new Formula(1);

		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createSetLookByIndexAction(sprite, formula);
		action.act(1.0f);
		assertEquals("Action didn't set the first LookData", firstLookData, sprite.look.getLookData());

		formula = new Formula(2);
		action = factory.createSetLookByIndexAction(sprite, formula);
		action.act(1.0f);
		assertEquals("Action didn't set the second LookData", secondLookData, sprite.look.getLookData());
	}

	@Test
	public void testSetLookByWrongIndex() {
		sprite.look.setLookData(firstLookData);

		Formula formula = new Formula(-1);
		ActionFactory factory = sprite.getActionFactory();
		Action action = factory.createSetLookByIndexAction(sprite, formula);
		action.act(1.0f);
		assertEquals("Action did set Lookdata wrongly with negative Formula value.", firstLookData, sprite.look
				.getLookData());

		formula = new Formula(42);
		action = factory.createSetLookByIndexAction(sprite, formula);
		action.act(1.0f);
		assertEquals("Action did set Lookdata wrongly with wrong Formula value.", firstLookData,
				sprite.look.getLookData());
	}
}
