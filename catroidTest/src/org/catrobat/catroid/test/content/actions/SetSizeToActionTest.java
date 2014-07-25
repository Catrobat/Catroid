/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.SetSizeToAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;

public class SetSizeToActionTest extends InstrumentationTestCase {

	private Formula size = new Formula(70.0f);
	private static final int IMAGE_FILE_ID = R.raw.icon;

	private File testImage;
	private final String projectName = "testProject";

	@Override
	protected void setUp() throws Exception {
		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(this.projectName, "testImage.png", IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);
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

	public void testSize() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite size value", 1f, sprite.look.getScaleX());
		assertEquals("Unexpected initial sprite size value", 1f, sprite.look.getScaleY());

		SetSizeToAction action = ExtendedActions.setSizeTo(sprite, size);
		action.act(1.0f);
		assertEquals("Incorrect sprite size value after SetSizeToBrick executed", size.interpretFloat() / 100,
				sprite.look.getScaleX());
		assertEquals("Incorrect sprite size value after SetSizeToBrick executed", size.interpretFloat() / 100,
				sprite.look.getScaleY());
	}

	public void testNegativeSize() {
		Sprite sprite = new Sprite("testSprite");
		float initialSize = sprite.look.getSizeInUserInterfaceDimensionUnit();
		assertEquals("Unexpected initial sprite size value", 100f, initialSize);

		SetSizeToAction action = ExtendedActions.setSizeTo(sprite, new Formula(-10));
		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("Incorrect sprite size value after ChangeSizeByNBrick executed", 0f,
				sprite.look.getSizeInUserInterfaceDimensionUnit());
	}

	public void testNullSprite() {
		SetSizeToAction action = ExtendedActions.setSizeTo(null, size);
		try {
			action.act(1.0f);
			fail("Execution of SetSizeToBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			assertTrue("Exception thrown as expected", true);
		}
	}

}
