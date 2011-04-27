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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.Values;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.utils.UtilFile;

public class SetCostumeBrickTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = R.raw.icon;
	private File testImage;
	int width;
	int height;
	private String project = "testProject";

	@Override
	protected void setUp() throws Exception {

		File defProject = new File(Consts.DEFAULT_ROOT + "/" + project);

		if (defProject.exists()) {
			UtilFile.deleteDirectory(defProject);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		File defProject = new File(Consts.DEFAULT_ROOT + "/" + project);

		if (defProject.exists()) {
			UtilFile.deleteDirectory(defProject);
		}
	}

	public void testSetCostume() throws IOException {

		Values.SCREEN_HEIGHT = 200;
		Values.SCREEN_WIDTH = 200;

		Project myProject = new Project(getInstrumentation().getTargetContext(), project);
		StorageHandler.getInstance().saveProject(myProject);
		ProjectManager.getInstance().setProject(myProject);
		this.saveImage();
		Sprite sprite = new Sprite("new sprite");
		myProject.addSprite(sprite);
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		setCostumeBrick.setCostume(testImage.getName());
		setCostumeBrick.execute();
		assertNotNull("current Costume is null", sprite.getCostume());

		assertEquals("the new Costume is not in the costumeList of the sprite", width,
				sprite.getCostume().getBitmap().getWidth());
		assertEquals("the new Costume is not in the costumeList of the sprite", height,
				sprite.getCostume().getBitmap().getHeight());
		setCostumeBrick.execute(); //now setting current costume
		assertEquals("Width of loaded bitmap is not the same as width of original image", width,
				sprite.getCostume().getBitmap().getWidth());
		assertEquals("Height of loaded bitmap is not the same as height of original image", height,
				sprite.getCostume().getBitmap().getHeight());
	}

	private void saveImage() throws IOException {
		String pathToImage = Consts.DEFAULT_ROOT + "/" + ProjectManager.getInstance().getCurrentProject().getName()
				+ Consts.IMAGE_DIRECTORY + "/image.png";
		testImage = new File(pathToImage);

		if (!testImage.exists()) {
			testImage.createNewFile();
		}

		InputStream in = getInstrumentation().getContext().getResources().openRawResource(IMAGE_FILE_ID);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage));

		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathToImage, o);

		width = o.outWidth;
		height = o.outHeight;
	}

}
