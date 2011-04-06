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
package at.tugraz.ist.catroid.uitest.stage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.Values;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.brick.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.SetCostumeBrick;
import at.tugraz.ist.catroid.content.brick.WaitBrick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Costume;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class StageTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	final String projectName = "project1";
	final String projectName2 = "project2";

	File image1;
	File image2;
	String imageName1 = "image1";
	String imageName2 = "image2";
	int placeAt = 400;

	int image1Width;
	int image2Width;
	int image1Height;
	int image2Height;

	private static final int IMAGE_FILE_ID = R.raw.icon;
	private static final int IMAGE_FILE_ID2 = R.raw.icon2;

	public StageTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		File directory = new File("/sdcard/catroid/" + projectName);
		UtilFile.deleteDirectory(directory);

		directory = new File("/sdcard/catroid/" + projectName2);
		UtilFile.deleteDirectory(directory);
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		if (image1 != null && image1.exists()) {
			image1.delete();
		}
		if (image2 != null && image2.exists()) {
			image2.delete();
		}

		File directory = new File("/sdcard/catroid/" + projectName);
		UtilFile.deleteDirectory(directory);

		directory = new File("/sdcard/catroid/" + projectName2);
		UtilFile.deleteDirectory(directory);

		getActivity().finish();
		super.tearDown();
	}

	public void testClickOnPictureAndChangeCostume() throws IOException, InterruptedException {
		createTestProject1(projectName);

		System.out.println("image1: " + image1.getAbsolutePath() + " " + image1Width + " " + image1Height);
		System.out.println("image2: " + image2.getAbsolutePath() + " " + image2Width + " " + image2Height);
		solo.clickOnButton(1); // this is the stage //change it when you mess with the buttons

		Thread.sleep(500);
		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("image1 is not set ", (Integer) image1Width, costume.getImageWidthHeight().first);
		assertEquals("image1 is not set ", (Integer) image1Height, costume.getImageWidthHeight().second);
		solo.clickOnScreen(Values.SCREEN_WIDTH / 2, Values.SCREEN_HEIGHT / 2);

		Thread.sleep(500);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("image2 is not set ", (Integer) image2Width, costume.getImageWidthHeight().first);
		assertEquals("image2 is not set ", (Integer) image2Height, costume.getImageWidthHeight().second);
	}

	public void testRunScript() throws IOException, InterruptedException {
		createTestProject2(projectName2);

		System.out.println("image1: " + image1.getAbsolutePath() + " " + image1Width + " " + image1Height);

		solo.clickOnButton(1);
		Thread.sleep(500);
		Costume costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("some image is set", (Integer) 0, costume.getImageWidthHeight().first);
		assertEquals("some image is set", (Integer) 0, costume.getImageWidthHeight().second);

		solo.clickOnScreen(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT); // click somewhere ..

		Thread.sleep(500);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("image not right scaled", (Integer) (image1Width / 2), costume.getImageWidthHeight().first);

		Thread.sleep(3000);
		costume = ProjectManager.getInstance().getCurrentProject().getSpriteList().get(1).getCostume();
		assertEquals("image not right scaled", (Integer) (image1Width), costume.getImageWidthHeight().first);

		int drawPositionX = Math.round(((Values.SCREEN_WIDTH / (2f * Consts.MAX_REL_COORDINATES)) * placeAt)
		        + Values.SCREEN_WIDTH / 2f);
		drawPositionX = drawPositionX - image1Width / 2;
		assertEquals("image was not set right", drawPositionX, costume.getDrawPositionX());
	}


	public void createTestProject1(String projectName) throws IOException {
		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(getActivity(), projectName);
		storageHandler.saveProject(project);

		image1 = savePictureInProject(projectName, 4147, imageName1, IMAGE_FILE_ID);
		image2 = savePictureInProject(projectName, 4147, imageName2, IMAGE_FILE_ID2);

		Sprite firstSprite = new Sprite("sprite1");
		Script testScript = new Script("script1", firstSprite);
		Script touchScript = new Script("script2", firstSprite);
		touchScript.setTouchScript(true);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		setCostumeBrick.setCostume(image1.getAbsolutePath());
		SetCostumeBrick setCostumeBrick2 = new SetCostumeBrick(firstSprite);
		setCostumeBrick2.setCostume(image2.getAbsolutePath());

		project.addSprite(firstSprite);
		testScript.addBrick(setCostumeBrick);
		touchScript.addBrick(setCostumeBrick2);
		firstSprite.getScriptList().add(testScript);
		firstSprite.getScriptList().add(touchScript);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		ProjectManager.getInstance().setCurrentScript(testScript);

		storageHandler.saveProject(project);
	}

	public void createTestProject2(String projectName) throws IOException {
		StorageHandler storageHandler = StorageHandler.getInstance();

		Project project = new Project(getActivity(), projectName);
		storageHandler.saveProject(project);

		image1 = savePictureInProject(projectName, 4147, imageName1, IMAGE_FILE_ID);

		Sprite firstSprite = new Sprite("sprite1");
		Script touchScript = new Script("script2", firstSprite);
		touchScript.setTouchScript(true);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(firstSprite);
		setCostumeBrick.setCostume(image1.getAbsolutePath());
		ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(firstSprite, 50);
		ScaleCostumeBrick scaleCostumeBrick2 = new ScaleCostumeBrick(firstSprite, 100);
		WaitBrick waitBrick = new WaitBrick(firstSprite, 3000);
		PlaceAtBrick placeAt = new PlaceAtBrick(firstSprite, this.placeAt, this.placeAt);

		project.addSprite(firstSprite);
		touchScript.addBrick(setCostumeBrick);
		touchScript.addBrick(scaleCostumeBrick);
		touchScript.addBrick(waitBrick);
		touchScript.addBrick(scaleCostumeBrick2);
		touchScript.addBrick(placeAt);
		firstSprite.getScriptList().add(touchScript);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
		ProjectManager.getInstance().setCurrentScript(touchScript);

		storageHandler.saveProject(project);
	}

	public File savePictureInProject(String project, int fileSize, String name, int fileID) throws IOException {

		// final int fileSize = 4147;
		final String imagePath = "/mnt/sdcard/catroid/" + project + "/images/" + name;
		File testImage = new File(imagePath);
		if (!testImage.exists()) {
			testImage.createNewFile();
		}
		InputStream in = getInstrumentation().getContext().getResources().openRawResource(fileID);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), fileSize);
		byte[] buffer = new byte[fileSize];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, o);

		if (name.equalsIgnoreCase(imageName1)) {
			image1Width = o.outWidth;
			image1Height = o.outHeight;
		} else {
			image2Width = o.outWidth;
			image2Height = o.outHeight;
		}

		return testImage;
	}

}
