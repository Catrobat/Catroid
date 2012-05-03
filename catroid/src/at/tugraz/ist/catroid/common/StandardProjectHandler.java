/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.Utils;

public class StandardProjectHandler {

	private static final String FILENAME_SEPARATOR = "_";

	public static Project createAndSaveStandardProject(Context context) throws IOException {
		String projectName = context.getString(R.string.default_project_name);
		return createAndSaveStandardProject(projectName, context);
	}

	public static Project createAndSaveStandardProject(String projectName, Context context) throws IOException {
		String normalCatName = context.getString(R.string.default_project_sprites_catroid_normalcat);
		String banzaiCatName = context.getString(R.string.default_project_sprites_catroid_banzaicat);
		String cheshireCatName = context.getString(R.string.default_project_sprites_catroid_cheshirecat);
		String backgroundName = context.getString(R.string.default_project_backgroundname);

		Project defaultProject = new Project(context, projectName);
		StorageHandler.getInstance().saveProject(defaultProject);
		ProjectManager.getInstance().setProject(defaultProject);
		Sprite sprite = new Sprite(context.getString(R.string.default_project_sprites_catroid_name));
		Sprite backgroundSprite = defaultProject.getSpriteList().get(0);

		Script backgroundStartScript = new StartScript(backgroundSprite);
		Script startScript = new StartScript(sprite);
		Script whenScript = new WhenScript(sprite);

		File backgroundFile = createBackgroundImage(projectName, backgroundName,
				context.getString(R.string.default_project_backgroundcolor));

		File normalCat = copyAndScaleImageToProject(projectName, context, normalCatName, R.drawable.catroid);
		File banzaiCat = copyAndScaleImageToProject(projectName, context, banzaiCatName, R.drawable.catroid_banzai);
		File cheshireCat = copyAndScaleImageToProject(projectName, context, cheshireCatName,
				R.drawable.catroid_cheshire);

		CostumeData normalCatCostumeData = new CostumeData();
		normalCatCostumeData.setCostumeName(normalCatName);
		normalCatCostumeData.setCostumeFilename(normalCat.getName());

		CostumeData banzaiCatCostumeData = new CostumeData();
		banzaiCatCostumeData.setCostumeName(banzaiCatName);
		banzaiCatCostumeData.setCostumeFilename(banzaiCat.getName());

		CostumeData cheshireCatCostumeData = new CostumeData();
		cheshireCatCostumeData.setCostumeName(cheshireCatName);
		cheshireCatCostumeData.setCostumeFilename(cheshireCat.getName());

		CostumeData backgroundCostumeData = new CostumeData();
		backgroundCostumeData.setCostumeName(backgroundName);
		backgroundCostumeData.setCostumeFilename(backgroundFile.getName());

		ArrayList<CostumeData> costumeDataList = sprite.getCostumeDataList();
		costumeDataList.add(normalCatCostumeData);
		costumeDataList.add(banzaiCatCostumeData);
		costumeDataList.add(cheshireCatCostumeData);
		ArrayList<CostumeData> costumeDataList2 = backgroundSprite.getCostumeDataList();
		costumeDataList2.add(backgroundCostumeData);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		setCostumeBrick.setCostume(normalCatCostumeData);

		SetCostumeBrick setCostumeBrick1 = new SetCostumeBrick(sprite);
		setCostumeBrick1.setCostume(normalCatCostumeData);

		SetCostumeBrick setCostumeBrick2 = new SetCostumeBrick(sprite);
		setCostumeBrick2.setCostume(banzaiCatCostumeData);

		SetCostumeBrick setCostumeBrick3 = new SetCostumeBrick(sprite);
		setCostumeBrick3.setCostume(cheshireCatCostumeData);

		SetCostumeBrick backgroundBrick = new SetCostumeBrick(backgroundSprite);
		backgroundBrick.setCostume(backgroundCostumeData);

		WaitBrick waitBrick1 = new WaitBrick(sprite, 500);
		WaitBrick waitBrick2 = new WaitBrick(sprite, 500);

		startScript.addBrick(setCostumeBrick);

		whenScript.addBrick(setCostumeBrick2);
		whenScript.addBrick(waitBrick1);
		whenScript.addBrick(setCostumeBrick3);
		whenScript.addBrick(waitBrick2);
		whenScript.addBrick(setCostumeBrick1);
		backgroundStartScript.addBrick(backgroundBrick);

		defaultProject.addSprite(sprite);
		sprite.addScript(startScript);
		sprite.addScript(whenScript);
		backgroundSprite.addScript(backgroundStartScript);

		StorageHandler.getInstance().saveProject(defaultProject);

		return defaultProject;
	}

	private static File createBackgroundImage(String projectName, String backgroundName, String backgroundColor)
			throws FileNotFoundException {
		String directoryName = Utils.buildPath(Utils.buildProjectPath(projectName), Consts.IMAGE_DIRECTORY);
		File backgroundTemp = new File(Utils.buildPath(directoryName, backgroundName));
		Bitmap backgroundBitmap = ImageEditing.createSingleColorBitmap(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT,
				Color.parseColor(backgroundColor));
		StorageHandler.saveBitmapToImageFile(backgroundTemp, backgroundBitmap);
		File backgroundFile = new File(directoryName, Utils.md5Checksum(backgroundTemp) + FILENAME_SEPARATOR
				+ backgroundTemp.getName());
		backgroundTemp.renameTo(backgroundFile);
		return backgroundFile;
	}

	private static File copyAndScaleImageToProject(String projectName, Context context, String imageName, int imageId)
			throws IOException {
		String directoryName = Utils.buildPath(Utils.buildProjectPath(projectName), Consts.IMAGE_DIRECTORY);
		File tempImageFile = savePictureFromResourceInProject(projectName, imageName, imageId, context);

		int[] dimensions = ImageEditing.getImageDimensions(tempImageFile.getAbsolutePath());
		int originalWidth = dimensions[0];
		int originalHeight = dimensions[1];
		double ratio = (double) originalHeight / (double) originalWidth;

		// scale the cat, that its always 1/3 of the screen width
		Bitmap tempBitmap = ImageEditing.getScaledBitmapFromPath(tempImageFile.getAbsolutePath(),
				Values.SCREEN_WIDTH / 3, (int) (Values.SCREEN_WIDTH / 3 * ratio), false);
		StorageHandler.saveBitmapToImageFile(tempImageFile, tempBitmap);

		String finalImageFileString = Utils.buildPath(directoryName, Utils.md5Checksum(tempImageFile)
				+ FILENAME_SEPARATOR + tempImageFile.getName());
		File finalImageFile = new File(finalImageFileString);
		tempImageFile.renameTo(finalImageFile);

		return finalImageFile;
	}

	private static File savePictureFromResourceInProject(String project, String outputName, int fileId, Context context)
			throws IOException {

		final String imagePath = Utils.buildPath(Utils.buildProjectPath(project), Consts.IMAGE_DIRECTORY, outputName);
		File testImage = new File(imagePath);
		if (!testImage.exists()) {
			testImage.createNewFile();
		}
		InputStream in = context.getResources().openRawResource(fileId);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), Consts.BUFFER_8K);
		byte[] buffer = new byte[Consts.BUFFER_8K];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		return testImage;
	}

}
