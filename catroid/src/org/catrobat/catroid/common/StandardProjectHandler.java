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
package org.catrobat.catroid.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

public class StandardProjectHandler {

	private static final String FILENAME_SEPARATOR = "_";

	public static Project createAndSaveStandardProject(Context context) throws IOException {
		String projectName = context.getString(R.string.default_project_name);
		return createAndSaveStandardProject(projectName, context);
	}

	public static Project createAndSaveStandardProject(String projectName, Context context) throws IOException {
		String normalCatName = context.getString(R.string.default_project_sprites_pocketcode_normalcat);
		String banzaiCatName = context.getString(R.string.default_project_sprites_pocketcode_banzaicat);
		String cheshireCatName = context.getString(R.string.default_project_sprites_pocketcode_cheshirecat);
		String backgroundName = context.getString(R.string.default_project_backgroundname);

		Project defaultProject = new Project(context, projectName);
		StorageHandler.getInstance().saveProject(defaultProject);
		ProjectManager.getInstance().setProject(defaultProject);
		Sprite sprite = new Sprite(context.getString(R.string.default_project_sprites_pocketcode_name));
		Sprite backgroundSprite = defaultProject.getSpriteList().get(0);

		Script backgroundStartScript = new StartScript(backgroundSprite);
		Script startScript = new StartScript(sprite);
		Script whenScript = new WhenScript(sprite);

		File backgroundFile = createBackgroundImage(projectName, backgroundName,
				context.getString(R.color.default_project_backgroundcolor));

		File normalCat = copyAndScaleImageToProject(projectName, context, normalCatName, R.drawable.catroid);
		File banzaiCat = copyAndScaleImageToProject(projectName, context, banzaiCatName, R.drawable.catroid_banzai);
		File cheshireCat = copyAndScaleImageToProject(projectName, context, cheshireCatName,
				R.drawable.catroid_cheshire);

		LookData normalCatLookData = new LookData();
		normalCatLookData.setLookName(normalCatName);
		normalCatLookData.setLookFilename(normalCat.getName());

		LookData banzaiCatLookData = new LookData();
		banzaiCatLookData.setLookName(banzaiCatName);
		banzaiCatLookData.setLookFilename(banzaiCat.getName());

		LookData cheshireCatLookData = new LookData();
		cheshireCatLookData.setLookName(cheshireCatName);
		cheshireCatLookData.setLookFilename(cheshireCat.getName());

		LookData backgroundLookData = new LookData();
		backgroundLookData.setLookName(backgroundName);
		backgroundLookData.setLookFilename(backgroundFile.getName());

		ArrayList<LookData> lookDataList = sprite.getLookDataList();
		lookDataList.add(normalCatLookData);
		lookDataList.add(banzaiCatLookData);
		lookDataList.add(cheshireCatLookData);
		ArrayList<LookData> lookDataList2 = backgroundSprite.getLookDataList();
		lookDataList2.add(backgroundLookData);

		SetLookBrick setLookBrick = new SetLookBrick(sprite);
		setLookBrick.setLook(normalCatLookData);

		SetLookBrick setLookBrick1 = new SetLookBrick(sprite);
		setLookBrick1.setLook(normalCatLookData);

		SetLookBrick setLookBrick2 = new SetLookBrick(sprite);
		setLookBrick2.setLook(banzaiCatLookData);

		SetLookBrick setLookBrick3 = new SetLookBrick(sprite);
		setLookBrick3.setLook(cheshireCatLookData);

		SetLookBrick backgroundBrick = new SetLookBrick(backgroundSprite);
		backgroundBrick.setLook(backgroundLookData);

		WaitBrick waitBrick1 = new WaitBrick(sprite, 500);
		WaitBrick waitBrick2 = new WaitBrick(sprite, 500);

		startScript.addBrick(setLookBrick);

		whenScript.addBrick(setLookBrick2);
		whenScript.addBrick(waitBrick1);
		whenScript.addBrick(setLookBrick3);
		whenScript.addBrick(waitBrick2);
		whenScript.addBrick(setLookBrick1);
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
		String directoryName = Utils.buildPath(Utils.buildProjectPath(projectName), Constants.IMAGE_DIRECTORY);
		File backgroundTemp = new File(Utils.buildPath(directoryName, backgroundName));
		Bitmap backgroundBitmap = ImageEditing.createSingleColorBitmap(ScreenValues.SCREEN_WIDTH,
				ScreenValues.SCREEN_HEIGHT, Color.parseColor(backgroundColor));
		StorageHandler.saveBitmapToImageFile(backgroundTemp, backgroundBitmap);
		File backgroundFile = new File(directoryName, Utils.md5Checksum(backgroundTemp) + FILENAME_SEPARATOR
				+ backgroundTemp.getName());
		backgroundTemp.renameTo(backgroundFile);
		return backgroundFile;
	}

	private static File copyAndScaleImageToProject(String projectName, Context context, String imageName, int imageId)
			throws IOException {
		String directoryName = Utils.buildPath(Utils.buildProjectPath(projectName), Constants.IMAGE_DIRECTORY);
		File tempImageFile = savePictureFromResourceInProject(projectName, imageName, imageId, context);

		int[] dimensions = ImageEditing.getImageDimensions(tempImageFile.getAbsolutePath());
		int originalWidth = dimensions[0];
		int originalHeight = dimensions[1];
		double ratio = (double) originalHeight / (double) originalWidth;

		// scale the cat, that its always 1/3 of the screen width
		Bitmap tempBitmap = ImageEditing.getScaledBitmapFromPath(tempImageFile.getAbsolutePath(),
				ScreenValues.SCREEN_WIDTH / 3, (int) (ScreenValues.SCREEN_WIDTH / 3 * ratio), false);
		StorageHandler.saveBitmapToImageFile(tempImageFile, tempBitmap);

		String finalImageFileString = Utils.buildPath(directoryName, Utils.md5Checksum(tempImageFile)
				+ FILENAME_SEPARATOR + tempImageFile.getName());
		File finalImageFile = new File(finalImageFileString);
		tempImageFile.renameTo(finalImageFile);

		return finalImageFile;
	}

	private static File savePictureFromResourceInProject(String project, String outputName, int fileId, Context context)
			throws IOException {

		final String imagePath = Utils
				.buildPath(Utils.buildProjectPath(project), Constants.IMAGE_DIRECTORY, outputName);
		File testImage = new File(imagePath);
		if (!testImage.exists()) {
			testImage.createNewFile();
		}
		InputStream in = context.getResources().openRawResource(fileId);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), Constants.BUFFER_8K);
		byte[] buffer = new byte[Constants.BUFFER_8K];
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
