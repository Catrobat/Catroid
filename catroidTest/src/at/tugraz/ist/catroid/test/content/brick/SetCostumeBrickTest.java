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
import java.io.IOException;

import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

public class SetCostumeBrickTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = R.raw.icon;
	private File testImage;
	int width;
	int height;
	private String projectName = "testProject";
	private Project project;

	@Override
	protected void setUp() throws Exception {

		File projectFile = new File(Consts.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(this.projectName, "testImage.png", IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);

		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(this.testImage.getAbsolutePath(), o);

		this.width = o.outWidth;
		this.height = o.outHeight;
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

	public void testSetCostume() throws IOException {

		Values.SCREEN_HEIGHT = 200;
		Values.SCREEN_WIDTH = 200;

		Sprite sprite = new Sprite("new sprite");
		project.addSprite(sprite);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		setCostumeBrick.setCostume(testImage.getName());
		setCostumeBrick.execute();
		assertNotNull("current Costume is null", sprite.getCostume());

		assertEquals("the new Costume is not in the costumeList of the sprite", width, sprite.getCostume().getBitmap()
				.getWidth());
		assertEquals("the new Costume is not in the costumeList of the sprite", height, sprite.getCostume().getBitmap()
				.getHeight());
		setCostumeBrick.execute(); //now setting current costume
		assertEquals("Width of loaded bitmap is not the same as width of original image", width, sprite.getCostume()
				.getBitmap().getWidth());
		assertEquals("Height of loaded bitmap is not the same as height of original image", height, sprite.getCostume()
				.getBitmap().getHeight());
	}

}
