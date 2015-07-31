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

import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.SetLookAction;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;

public class SetLookActionTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = R.raw.icon;
	private File testImage;
	int width;
	int height;
	private String projectName = "testProject";
	private Project project;

	@Override
	protected void setUp() throws Exception {

		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(this.projectName, "testImage.png", IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(this.testImage.getAbsolutePath(), bitmapOptions);

		this.width = bitmapOptions.outWidth;
		this.height = bitmapOptions.outHeight;
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

	public void testSetLook() {

		ScreenValues.SCREEN_HEIGHT = 200;
		ScreenValues.SCREEN_WIDTH = 200;

		Sprite sprite = new Sprite("new sprite");
		project.addSprite(sprite);
		LookData lookData = new LookData();
		lookData.setLookFilename(testImage.getName());
		lookData.setLookName("testImage");
		sprite.getLookDataList().add(lookData);
		SetLookAction action = ExtendedActions.setLook(sprite, lookData);
		action.act(1.0f);
		assertNotNull("current Look is null", sprite.look);
	}
}
