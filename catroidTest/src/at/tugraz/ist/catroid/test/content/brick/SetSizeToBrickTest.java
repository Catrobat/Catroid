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
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

public class SetSizeToBrickTest extends InstrumentationTestCase {

	private double size = 70;
	private final double sizeToBig = 1000000.;
	private final double sizeToSmall = .00001;

	private static final int IMAGE_FILE_ID = R.raw.icon;

	private File testImage;
	private final String projectName = "testProject";

	@Override
	protected void setUp() throws Exception {

		File projectFile = new File(Consts.DEFAULT_ROOT + "/" + projectName);

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
		File projectFile = new File(Consts.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}
		if (testImage != null && testImage.exists()) {
			testImage.delete();
		}
	}

	public void testSize() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite size value", 100.0, sprite.getSize());

		SetSizeToBrick brick = new SetSizeToBrick(sprite, size);
		brick.execute();
		assertEquals("Incorrect sprite size value after SetSizeToBrick executed", size, sprite.getSize());
	}

	public void testNullSprite() {
		SetSizeToBrick brick = new SetSizeToBrick(null, size);

		try {
			brick.execute();
			fail("Execution of SetSizeToBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException e) {
			// expected behavior
		}
	}

	public void testBoundarySize() {
		Sprite sprite = new Sprite("testSprite");

		SetSizeToBrick brick = new SetSizeToBrick(sprite, Double.MAX_VALUE);
		brick.execute();
		assertEquals("SetSizeToBrick failed to size Sprite to maximum Double value", Double.MAX_VALUE, sprite.getSize());

		brick = new SetSizeToBrick(sprite, Double.MIN_VALUE);
		brick.execute();
		assertEquals("SetSizeToBrick failed to size Sprite to minimum Double value", Double.MIN_VALUE, sprite.getSize());
	}

	public void testZeroSize() {
		Sprite sprite = new Sprite("testSprite");
		SetSizeToBrick brick = new SetSizeToBrick(sprite, 0);

		try {
			brick.execute();
			fail("Execution of SetSizeToBrick with 0.0 size did not cause a IllegalArgumentException to be thrown.");
		} catch (IllegalArgumentException e) {
			// expected behavior
		}
	}

	public void testNegativeSize() {
		Sprite sprite = new Sprite("testSprite");
		SetSizeToBrick brick = new SetSizeToBrick(sprite, -size);

		try {
			brick.execute();
			fail("Execution of SetSizeToBrick with negative size did not cause a IllegalArgumentException to be thrown.");
		} catch (IllegalArgumentException e) {
			// expected behavior
		}
	}

	public void testCostumeToBig() {
		Values.SCREEN_HEIGHT = 800;
		Values.SCREEN_WIDTH = 480;

		Sprite sprite = new Sprite("testSprite");
		sprite.getCostume().changeImagePath(testImage.getAbsolutePath());

		SetSizeToBrick brick = new SetSizeToBrick(sprite, sizeToBig);

		brick.execute();

		int newWidth = sprite.getCostume().getImageWidth();
		int newHeight = sprite.getCostume().getImageHeight();

		assertTrue("Costume has a wrong size after setting it!", newWidth == Consts.MAX_COSTUME_WIDTH
				|| newHeight == Consts.MAX_COSTUME_HEIGHT);
	}

	public void testCostumeToSmall() {
		Values.SCREEN_HEIGHT = 800;
		Values.SCREEN_WIDTH = 480;

		Sprite sprite = new Sprite("testSprite");
		sprite.getCostume().changeImagePath(testImage.getAbsolutePath());

		SetSizeToBrick brick = new SetSizeToBrick(sprite, sizeToSmall);

		brick.execute();

		int newWidth = sprite.getCostume().getImageWidth();
		int newHeight = sprite.getCostume().getImageHeight();

		assertTrue("Costume has a wrong size after setting it!", newWidth == 1 || newHeight == 1);
	}
}
