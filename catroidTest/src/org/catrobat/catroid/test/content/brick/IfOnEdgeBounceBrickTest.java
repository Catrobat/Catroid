/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.brick;

import java.io.File;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.common.Values;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.utils.UtilFile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;

public class IfOnEdgeBounceBrickTest extends InstrumentationTestCase {

	private int BOUNCE_LEFT_POS;
	private int BOUNCE_RIGHT_POS;
	private int BOUNCE_DOWN_POS;
	private int BOUNCE_UP_POS;
	private int SCREEN_HALF_HEIGHT;
	private int SCREEN_HALF_WIDTH;
	private static final int IMAGE_FILE_ID = R.raw.icon;

	private final String projectName = "testProject";
	private File testImage;
	private CostumeData costumeData;
	private int width;
	private int height;

	@Override
	public void setUp() throws Exception {

		File projectFile = new File(Constants.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		Values.SCREEN_HEIGHT = 800;
		Values.SCREEN_WIDTH = 480;
		SCREEN_HALF_HEIGHT = Values.SCREEN_HEIGHT / 2;
		SCREEN_HALF_WIDTH = Values.SCREEN_WIDTH / 2;

		BOUNCE_LEFT_POS = -(Values.SCREEN_WIDTH + 50);
		BOUNCE_RIGHT_POS = Values.SCREEN_WIDTH + 50;
		BOUNCE_DOWN_POS = -(Values.SCREEN_HEIGHT + 50);
		BOUNCE_UP_POS = Values.SCREEN_HEIGHT + 50;

		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		project.virtualScreenHeight = Values.SCREEN_HEIGHT;
		project.virtualScreenWidth = Values.SCREEN_WIDTH;
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(this.projectName, "testImage.png", IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);

		costumeData = new CostumeData();
		costumeData.setCostumeFilename(testImage.getName());
		costumeData.setCostumeName("CostumeName");

		Bitmap bitmap = BitmapFactory.decodeFile(testImage.getAbsolutePath());
		width = bitmap.getWidth();
		height = bitmap.getHeight();
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

	public void testNoBounce() {

		Sprite sprite = new Sprite("testSprite");
		sprite.costume.setCostumeData(costumeData);
		sprite.costume.setWidth(width);
		sprite.costume.setHeight(height);
		sprite.costume.setXPosition(0);
		sprite.costume.setYPosition(0);

		IfOnEdgeBounceBrick ifOnEdgeBounceBrick = new IfOnEdgeBounceBrick(sprite);

		ifOnEdgeBounceBrick.execute();
		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", 0f, sprite.costume.getRotation(), 1e-3);

	}

	public void testBounceNorth() {

		Sprite sprite = new Sprite("testSprite");
		sprite.costume.setCostumeData(costumeData);
		sprite.costume.setWidth(width);
		sprite.costume.setHeight(height);

		IfOnEdgeBounceBrick ifOnEdgeBounceBrick = new IfOnEdgeBounceBrick(sprite);

		sprite.costume.setRotation(90f);
		sprite.costume.setXYPosition(0, BOUNCE_UP_POS);

		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", (float) (SCREEN_HALF_HEIGHT - (height / 2)), sprite.costume.getYPosition());
		assertEquals("Wrong direction", 180f, -sprite.costume.getRotation() + 90f, 1e-3);
		assertEquals("Width shouldn't change", width, sprite.costume.getWidth(), 1e-3);
		assertEquals("Height shouldn't change", height, sprite.costume.getHeight(), 1e-3);

		sprite.costume.setRotation(-30f + 90f);
		sprite.costume.setXYPosition(0, BOUNCE_UP_POS);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", (float) (SCREEN_HALF_HEIGHT - (height / 2)), sprite.costume.getYPosition());
		assertEquals("Wrong direction", 150f, -sprite.costume.getRotation() + 90f, 1e-3);

		sprite.costume.setRotation(-150f + 90);
		sprite.costume.setXYPosition(0, BOUNCE_UP_POS);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", (float) (SCREEN_HALF_HEIGHT - (height / 2)), sprite.costume.getYPosition());
		assertEquals("Wrong direction", 150f, -sprite.costume.getRotation() + 90f, 1e-3);

		sprite.costume.setRotation(-42.42f + 90f);
		sprite.costume.setXYPosition(0, BOUNCE_UP_POS);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", (float) (SCREEN_HALF_HEIGHT - (height / 2)), sprite.costume.getYPosition());
		assertEquals("Wrong direction", 137.58, -sprite.costume.getRotation() + 90f, 1e-3);
	}

	public void testBounceSouth() {

		Sprite sprite = new Sprite("testSprite");
		sprite.costume.setCostumeData(costumeData);
		sprite.costume.setWidth(width);
		sprite.costume.setHeight(height);

		IfOnEdgeBounceBrick ifOnEdgeBounceBrick = new IfOnEdgeBounceBrick(sprite);

		sprite.costume.setRotation(-180f + 90f);
		sprite.costume.setXYPosition(0, BOUNCE_DOWN_POS);

		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", (float) (-SCREEN_HALF_HEIGHT + (height / 2)), sprite.costume.getYPosition());
		assertEquals("Wrong direction", 0f, -sprite.costume.getRotation() + 90f, 1e-3);
		assertEquals("Width shouldn't change", width, sprite.costume.getWidth(), 1e-3);
		assertEquals("Height shouldn't change", height, sprite.costume.getHeight(), 1e-3);

		sprite.costume.setRotation(-120f + 90f);
		sprite.costume.setXYPosition(0, BOUNCE_DOWN_POS);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", (float) (-SCREEN_HALF_HEIGHT + (height / 2)), sprite.costume.getYPosition());
		assertEquals("Wrong direction", 60f, -sprite.costume.getRotation() + 90f, 1e-3);

		sprite.costume.setRotation(-30f + 90f);
		sprite.costume.setXYPosition(0, BOUNCE_DOWN_POS);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", (float) (-SCREEN_HALF_HEIGHT + (height / 2)), sprite.costume.getYPosition());
		assertEquals("Wrong direction", 30f, -sprite.costume.getRotation() + 90f, 1e-3);

		sprite.costume.setRotation(-132.42f + 90f);
		sprite.costume.setXYPosition(0, BOUNCE_DOWN_POS);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", 0f, sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", (float) (-SCREEN_HALF_HEIGHT + (height / 2)), sprite.costume.getYPosition());
		assertEquals("Wrong direction", 47.58f, -sprite.costume.getRotation() + 90f, 1e-3);

	}

	public void testBounceEast() {

		Sprite sprite = new Sprite("testSprite");
		sprite.costume.setCostumeData(costumeData);
		sprite.costume.setWidth(width);
		sprite.costume.setHeight(height);

		IfOnEdgeBounceBrick brick = new IfOnEdgeBounceBrick(sprite);

		sprite.costume.setRotation(0f);
		sprite.costume.setXYPosition(BOUNCE_RIGHT_POS, 0);

		brick.execute();

		assertEquals("Wrong X-Position!", (float) (SCREEN_HALF_WIDTH - (width / 2)), sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", -90f, -sprite.costume.getRotation() + 90f, 1e-3);
		assertEquals("Width shouldn't change", width, sprite.costume.getWidth(), 1e-3);
		assertEquals("Height shouldn't change", height, sprite.costume.getHeight(), 1e-3);

		sprite.costume.setRotation(-30f + 90f);
		sprite.costume.setXYPosition(BOUNCE_RIGHT_POS, 0);
		brick.execute();

		assertEquals("Wrong X-Position!", (float) (SCREEN_HALF_WIDTH - (width / 2)), sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", -30f, -sprite.costume.getRotation() + 90f, 1e-3);

		sprite.costume.setRotation(30f + 90f);
		sprite.costume.setXYPosition(BOUNCE_RIGHT_POS, 0);
		brick.execute();

		assertEquals("Wrong X-Position!", (float) (SCREEN_HALF_WIDTH - (width / 2)), sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", -30f, -sprite.costume.getRotation() + 90f, 1e-3);

		sprite.costume.setRotation(-42.42f + 90f);
		sprite.costume.setXYPosition(BOUNCE_RIGHT_POS, 0);
		brick.execute();

		assertEquals("Wrong X-Position!", (float) (SCREEN_HALF_WIDTH - (width / 2)), sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", -42.42f, -sprite.costume.getRotation() + 90f, 1e-3);

	}

	public void testBounceWest() {

		Sprite sprite = new Sprite("testSprite");
		sprite.costume.setCostumeData(costumeData);
		sprite.costume.setWidth(width);
		sprite.costume.setHeight(height);

		IfOnEdgeBounceBrick ifOnEdgeBounceBrick = new IfOnEdgeBounceBrick(sprite);

		sprite.costume.setRotation(90f + 90f);
		sprite.costume.setXYPosition(BOUNCE_LEFT_POS, 0);

		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", (float) (-SCREEN_HALF_WIDTH + (width / 2)), sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", 90f, -sprite.costume.getRotation() + 90f, 1e-3);
		assertEquals("Width shouldn't change", width, sprite.costume.getWidth(), 1e-3);
		assertEquals("Height shouldn't change", height, sprite.costume.getHeight(), 1e-3);

		sprite.costume.setRotation(30f + 90f);
		sprite.costume.setXYPosition(BOUNCE_LEFT_POS, 0);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", (float) (-SCREEN_HALF_WIDTH + (width / 2)), sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", 30f, -sprite.costume.getRotation() + 90f, 1e-3);

		sprite.costume.setRotation(-30f + 90f);
		sprite.costume.setXYPosition(BOUNCE_LEFT_POS, 0);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", (float) (-SCREEN_HALF_WIDTH + (width / 2)), sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", 30f, -sprite.costume.getRotation() + 90f, 1e-3);

		sprite.costume.setRotation(42.42f + 90f);
		sprite.costume.setXYPosition(BOUNCE_LEFT_POS, 0);
		ifOnEdgeBounceBrick.execute();

		assertEquals("Wrong X-Position!", (float) (-SCREEN_HALF_WIDTH + (width / 2)), sprite.costume.getXPosition());
		assertEquals("Wrong Y-Position!", 0f, sprite.costume.getYPosition());
		assertEquals("Wrong direction", 42.42f, -sprite.costume.getRotation() + 90f, 1e-3);
	}
}
