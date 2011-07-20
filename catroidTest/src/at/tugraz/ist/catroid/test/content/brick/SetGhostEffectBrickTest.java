/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.content.brick;

import java.io.File;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.SetGhostEffectBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

public class SetGhostEffectBrickTest extends InstrumentationTestCase {

	private double effectValue = 50.5;
	private final double effectToOpacity = 5.9;
	private final double effectToTransparent = 95.5;

	private static final int IMAGE_FILE_ID = R.raw.icon;

	private File testImage;
	private final String projectName = "testProject";

	@Override
	protected void setUp() throws Exception {

		File defProject = new File(Consts.DEFAULT_ROOT + "/" + projectName);

		if (defProject.exists()) {
			UtilFile.deleteDirectory(defProject);
		}

		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(this.projectName, "testImage.png", IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);
	}

	@Override
	protected void tearDown() throws Exception {
		File defProject = new File(Consts.DEFAULT_ROOT + "/" + projectName);

		if (defProject.exists()) {
			UtilFile.deleteDirectory(defProject);
		}
		if (testImage != null && testImage.exists()) {
			testImage.delete();
		}
	}

	public void testGhostEffect() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite scale value", 0.0, sprite.getGhostEffectValue());

		SetGhostEffectBrick brick = new SetGhostEffectBrick(sprite, effectValue);
		brick.execute();
		assertEquals("Incorrect sprite scale value after SetGhostEffectBrick executed", effectValue,
				sprite.getGhostEffectValue());
	}

	public void testNullSprite() {
		SetGhostEffectBrick brick = new SetGhostEffectBrick(null, effectValue);

		try {
			brick.execute();
			fail("Execution of SetGhostEffectBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException e) {
			// expected behavior
		}
	}

	public void testNegativeEffectValue() {
		Sprite sprite = new Sprite("testSprite");
		SetGhostEffectBrick brick = new SetGhostEffectBrick(sprite, -effectValue);
		brick.execute();
		assertEquals("Negative value doesn't change to positive.", effectValue, sprite.getGhostEffectValue());
	}

	public void testCostumeToOpacity() {
		Sprite sprite = new Sprite("testSprite");
		sprite.getCostume().setImagePath(testImage.getAbsolutePath());

		SetGhostEffectBrick brick = new SetGhostEffectBrick(sprite, effectToOpacity);

		brick.execute();
	}

	public void testCostumeToTransprent() {
		Sprite sprite = new Sprite("testSprite");
		sprite.getCostume().setImagePath(testImage.getAbsolutePath());

		SetGhostEffectBrick brick = new SetGhostEffectBrick(sprite, effectToTransparent);

		brick.execute();
	}
}
