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
import at.tugraz.ist.catroid.content.bricks.ScaleCostumeBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.util.Utils;
import at.tugraz.ist.catroid.utils.UtilFile;

public class ScaleCostumeBrickTest extends InstrumentationTestCase {

	private double scale = 70;
	private final double scaleToBig = 1000000.;
	private final double scaleToSmall = .00001;

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

		testImage = Utils.saveFileToProject(this.projectName, "testImage.png", IMAGE_FILE_ID,
				getInstrumentation().getContext(), Utils.TYPE_IMAGE_FILE);
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

	public void testScale() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite scale value", 100.0, sprite.getScale());

		ScaleCostumeBrick brick = new ScaleCostumeBrick(sprite, scale);
		brick.execute();
		assertEquals("Incorrect sprite scale value after ScaleCostumeBrick executed", scale, sprite.getScale());
	}

	public void testNullSprite() {
		ScaleCostumeBrick brick = new ScaleCostumeBrick(null, scale);

		try {
			brick.execute();
			fail("Execution of ScaleCostumeBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException e) {
			// expected behavior
		}
	}

	public void testBoundaryScale() {
		Sprite sprite = new Sprite("testSprite");

		ScaleCostumeBrick brick = new ScaleCostumeBrick(sprite,
				Double.MAX_VALUE);
		brick.execute();
		assertEquals("ScaleCostumeBrick failed to scale Sprite to maximum Double value", Double.MAX_VALUE,
				sprite.getScale());

		brick = new ScaleCostumeBrick(sprite, Double.MIN_VALUE);
		brick.execute();
		assertEquals("ScaleCostumeBrick failed to scale Sprite to minimum Double value", Double.MIN_VALUE,
				sprite.getScale());
	}

	public void testZeroScale() {
		Sprite sprite = new Sprite("testSprite");
		ScaleCostumeBrick brick = new ScaleCostumeBrick(sprite, 0);

		try {
			brick.execute();
			fail("Execution of ScaleCostumeBrick with 0.0 scale did not cause a IllegalArgumentException to be thrown.");
		} catch (IllegalArgumentException e) {
			// expected behavior
		}
	}

	public void testNegativeScale() {
		Sprite sprite = new Sprite("testSprite");
		ScaleCostumeBrick brick = new ScaleCostumeBrick(sprite, -scale);

		try {
			brick.execute();
			fail("Execution of ScaleCostumeBrick with negative scale did not cause a IllegalArgumentException to be thrown.");
		} catch (IllegalArgumentException e) {
			// expected behavior
		}
	}

	public void testCostumeToBig() {
		Values.SCREEN_HEIGHT = 800;
		Values.SCREEN_WIDTH = 480;

		Sprite sprite = new Sprite("testSprite");
		sprite.getCostume().setImagePath(testImage.getAbsolutePath());

		ScaleCostumeBrick brick = new ScaleCostumeBrick(sprite, scaleToBig);

		brick.execute();

		int newWidth = sprite.getCostume().getImageWidthHeight().first;
		int newHeight = sprite.getCostume().getImageWidthHeight().second;

		assertTrue("ScaleCostumeBrick scaled image has a wrong size", newWidth == Consts.MAX_COSTUME_WIDTH
				|| newHeight == Consts.MAX_COSTUME_HEIGHT);
	}

	public void testCostumeToSmall() {
		Values.SCREEN_HEIGHT = 800;
		Values.SCREEN_WIDTH = 480;

		Sprite sprite = new Sprite("testSprite");
		sprite.getCostume().setImagePath(testImage.getAbsolutePath());

		ScaleCostumeBrick brick = new ScaleCostumeBrick(sprite, scaleToSmall);

		brick.execute();

		int newWidth = sprite.getCostume().getImageWidthHeight().first;
		int newHeight = sprite.getCostume().getImageWidthHeight().second;

		assertTrue("ScaleCostumeBrick scaled image has a wrong size", newWidth == 1 || newHeight == 1);
	}
}
