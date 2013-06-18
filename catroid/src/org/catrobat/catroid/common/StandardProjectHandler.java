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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.utils.Utils;

import android.content.Context;

public class StandardProjectHandler {

	private static final String FILENAME_SEPARATOR = "_";

	public static Project createAndSaveStandardProject(Context context) throws IOException {
		String projectName = context.getString(R.string.default_project_name);
		return createAndSaveStandardProject(projectName, context);
	}

	public static Project createAndSaveStandardProject(String projectName, Context context) throws IOException {
		String mole1Name = context.getString(R.string.default_project_sprites_mole_name) + " 1";
		String mole2Name = context.getString(R.string.default_project_sprites_mole_name) + " 2";
		String mole3Name = context.getString(R.string.default_project_sprites_mole_name) + " 3";
		String mole4Name = context.getString(R.string.default_project_sprites_mole_name) + " 4";
		String whackedMoleName = context.getString(R.string.default_project_sprites_mole_whacked);
		String soundName = context.getString(R.string.default_project_sprites_mole_sound);
		String backgroundName = context.getString(R.string.default_project_backgroundname);

		String varRandomFrom = context.getString(R.string.default_project_var_random_from);
		String varRandomTo = context.getString(R.string.default_project_var_random_to);

		Project defaultProject = new Project(context, projectName);
		defaultProject.setDeviceData(context); // density anywhere here
		StorageHandler.getInstance().saveProject(defaultProject);
		ProjectManager.getInstance().setProject(defaultProject);

		UserVariablesContainer userVariables = defaultProject.getUserVariables();

		Sprite backgroundSprite = defaultProject.getSpriteList().get(0);

		File mole1File = copyFromResourceInProject(projectName, Constants.IMAGE_DIRECTORY, mole1Name,
				R.drawable.default_project_mole_1, context);
		File mole2File = copyFromResourceInProject(projectName, Constants.IMAGE_DIRECTORY, mole2Name,
				R.drawable.default_project_mole_2, context);
		File whackedMoleFile = copyFromResourceInProject(projectName, Constants.IMAGE_DIRECTORY, whackedMoleName,
				R.drawable.default_project_mole_whacked, context);
		File soundFile1 = copyFromResourceInProject(projectName, Constants.SOUND_DIRECTORY, soundName,
				R.raw.default_project_sound_mole_1, context);
		File soundFile2 = copyFromResourceInProject(projectName, Constants.SOUND_DIRECTORY, soundName,
				R.raw.default_project_sound_mole_2, context);
		File soundFile3 = copyFromResourceInProject(projectName, Constants.SOUND_DIRECTORY, soundName,
				R.raw.default_project_sound_mole_3, context);
		File soundFile4 = copyFromResourceInProject(projectName, Constants.SOUND_DIRECTORY, soundName,
				R.raw.default_project_sound_mole_4, context);
		File backgroundFile = copyFromResourceInProject(projectName, Constants.IMAGE_DIRECTORY, backgroundName,
				R.drawable.default_project_background, context);

		copyFromResourceInProject(projectName, ".", StageListener.SCREENSHOT_MANUAL_FILE_NAME,
				R.drawable.default_project_screenshot, context, false);

		LookData moleLookData1 = new LookData();
		moleLookData1.setLookName(mole1Name);
		moleLookData1.setLookFilename(mole1File.getName());

		LookData moleLookData2 = new LookData();
		moleLookData2.setLookName(mole2Name);
		moleLookData2.setLookFilename(mole2File.getName());

		LookData moleLookDataWhacked = new LookData();
		moleLookDataWhacked.setLookName(whackedMoleName);
		moleLookDataWhacked.setLookFilename(whackedMoleFile.getName());

		LookData backgroundLookData = new LookData();
		backgroundLookData.setLookName(backgroundName);
		backgroundLookData.setLookFilename(backgroundFile.getName());

		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setTitle(soundName);
		soundInfo.setSoundFileName(soundFile1.getName());

		userVariables.addProjectUserVariable(varRandomFrom);
		UserVariable randomFrom = userVariables.getUserVariable(varRandomFrom, backgroundSprite);

		userVariables.addProjectUserVariable(varRandomTo);
		UserVariable randomTo = userVariables.getUserVariable(varRandomTo, backgroundSprite);

		// Background sprite
		backgroundSprite.getLookDataList().add(backgroundLookData);
		Script backgroundStartScript = new StartScript(backgroundSprite);

		SetLookBrick setLookBrick = new SetLookBrick(backgroundSprite);
		setLookBrick.setLook(backgroundLookData);
		backgroundStartScript.addBrick(setLookBrick);

		SetVariableBrick setVariableBrick = new SetVariableBrick(backgroundSprite, new Formula(1), randomFrom);
		backgroundStartScript.addBrick(setVariableBrick);

		setVariableBrick = new SetVariableBrick(backgroundSprite, new Formula(5), randomTo);
		backgroundStartScript.addBrick(setVariableBrick);

		backgroundSprite.addScript(backgroundStartScript);

		FormulaElement randomElement = new FormulaElement(ElementType.FUNCTION, Functions.RAND.toString(), null);
		randomElement.setLeftChild(new FormulaElement(ElementType.USER_VARIABLE, varRandomFrom, randomElement));
		randomElement.setRightChild(new FormulaElement(ElementType.USER_VARIABLE, varRandomTo, randomElement));
		Formula randomWait = new Formula(randomElement);

		FormulaElement waitOneOrTwoSeconds = new FormulaElement(ElementType.FUNCTION, Functions.RAND.toString(), null);
		waitOneOrTwoSeconds.setLeftChild(new FormulaElement(ElementType.NUMBER, "1", waitOneOrTwoSeconds));
		waitOneOrTwoSeconds.setRightChild(new FormulaElement(ElementType.NUMBER, "2", waitOneOrTwoSeconds));

		// Mole 1 sprite
		Sprite mole1Sprite = new Sprite(context.getString(R.string.default_project_sprites_mole_name) + " 1");
		mole1Sprite.getLookDataList().add(moleLookData1);
		mole1Sprite.getLookDataList().add(moleLookData2);
		mole1Sprite.getLookDataList().add(moleLookDataWhacked);
		mole1Sprite.getSoundList().add(soundInfo);

		Script mole1StartScript = new StartScript(mole1Sprite);
		Script mole1WhenScript = new WhenScript(mole1Sprite);

		// start script
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(mole1Sprite, new Formula(30));
		mole1StartScript.addBrick(setSizeToBrick);

		ForeverBrick foreverBrick = new ForeverBrick(mole1Sprite);
		mole1StartScript.addBrick(foreverBrick);

		PlaceAtBrick placeAtBrick = new PlaceAtBrick(mole1Sprite, -160, -110);
		mole1StartScript.addBrick(placeAtBrick);

		WaitBrick waitBrick = new WaitBrick(mole1Sprite, new Formula(waitOneOrTwoSeconds));
		mole1StartScript.addBrick(waitBrick);

		ShowBrick showBrick = new ShowBrick(mole1Sprite);
		mole1StartScript.addBrick(showBrick);

		setLookBrick = new SetLookBrick(mole1Sprite);
		setLookBrick.setLook(moleLookData1);
		mole1StartScript.addBrick(setLookBrick);

		GlideToBrick glideToBrick = new GlideToBrick(mole1Sprite, -160, -95, 100);
		mole1StartScript.addBrick(glideToBrick);

		setLookBrick = new SetLookBrick(mole1Sprite);
		setLookBrick.setLook(moleLookData2);
		mole1StartScript.addBrick(setLookBrick);

		waitBrick = new WaitBrick(mole1Sprite, randomWait.clone());
		mole1StartScript.addBrick(waitBrick);

		HideBrick hideBrick = new HideBrick(mole1Sprite);
		mole1StartScript.addBrick(hideBrick);

		waitBrick = new WaitBrick(mole1Sprite, randomWait.clone());
		mole1StartScript.addBrick(waitBrick);

		LoopEndlessBrick loopEndlessBrick = new LoopEndlessBrick(mole1Sprite, foreverBrick);
		mole1StartScript.addBrick(loopEndlessBrick);

		// when script		
		PlaySoundBrick playSoundBrick = new PlaySoundBrick(mole1Sprite);
		playSoundBrick.setSoundInfo(soundInfo);
		mole1WhenScript.addBrick(playSoundBrick);

		setLookBrick = new SetLookBrick(mole1Sprite);
		setLookBrick.setLook(moleLookDataWhacked);
		mole1WhenScript.addBrick(setLookBrick);

		waitBrick = new WaitBrick(mole1Sprite, 1500);
		mole1WhenScript.addBrick(waitBrick);

		hideBrick = new HideBrick(mole1Sprite);
		mole1WhenScript.addBrick(hideBrick);

		mole1Sprite.addScript(mole1StartScript);
		mole1Sprite.addScript(mole1WhenScript);
		defaultProject.addSprite(mole1Sprite);

		StorageHandler.getInstance().fillChecksumContainer();

		// Mole 2 sprite
		Sprite mole2Sprite = mole1Sprite.clone();
		mole2Sprite.getSoundList().get(0).setSoundFileName(soundFile2.getName());
		mole2Sprite.setName(mole2Name);
		defaultProject.addSprite(mole2Sprite);

		Script tempScript = mole2Sprite.getScript(0);
		placeAtBrick = (PlaceAtBrick) tempScript.getBrick(2);
		placeAtBrick.setXPosition(new Formula(160));
		placeAtBrick.setYPosition(new Formula(-110));

		glideToBrick = (GlideToBrick) tempScript.getBrick(6);
		glideToBrick.setXDestination(new Formula(160));
		glideToBrick.setYDestination(new Formula(-95));

		// Mole 3 sprite
		Sprite mole3Sprite = mole1Sprite.clone();
		mole3Sprite.getSoundList().get(0).setSoundFileName(soundFile3.getName());
		mole3Sprite.setName(mole3Name);
		defaultProject.addSprite(mole3Sprite);

		tempScript = mole3Sprite.getScript(0);
		placeAtBrick = (PlaceAtBrick) tempScript.getBrick(2);
		placeAtBrick.setXPosition(new Formula(-160));
		placeAtBrick.setYPosition(new Formula(-290));

		glideToBrick = (GlideToBrick) tempScript.getBrick(6);
		glideToBrick.setXDestination(new Formula(-160));
		glideToBrick.setYDestination(new Formula(-275));

		// Mole 4 sprite
		Sprite mole4Sprite = mole1Sprite.clone();
		mole4Sprite.getSoundList().get(0).setSoundFileName(soundFile4.getName());
		mole4Sprite.setName(mole4Name);
		defaultProject.addSprite(mole4Sprite);

		tempScript = mole4Sprite.getScript(0);
		placeAtBrick = (PlaceAtBrick) tempScript.getBrick(2);
		placeAtBrick.setXPosition(new Formula(160));
		placeAtBrick.setYPosition(new Formula(-290));

		glideToBrick = (GlideToBrick) tempScript.getBrick(6);
		glideToBrick.setXDestination(new Formula(160));
		glideToBrick.setYDestination(new Formula(-275));

		StorageHandler.getInstance().saveProject(defaultProject);

		return defaultProject;
	}

	private static File copyFromResourceInProject(String projectName, String directoryName, String outputName,
			int fileId, Context context) throws IOException {
		return copyFromResourceInProject(projectName, directoryName, outputName, fileId, context, true);
	}

	private static File copyFromResourceInProject(String projectName, String directoryName, String outputName,
			int fileId, Context context, boolean prependMd5) throws IOException {
		final String filePath = Utils.buildPath(Utils.buildProjectPath(projectName), directoryName, outputName);
		File copiedFile = new File(filePath);
		if (!copiedFile.exists()) {
			copiedFile.createNewFile();
		}
		InputStream in = context.getResources().openRawResource(fileId);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(copiedFile), Constants.BUFFER_8K);
		byte[] buffer = new byte[Constants.BUFFER_8K];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		if (!prependMd5) {
			return copiedFile;
		}

		String directoryPath = Utils.buildPath(Utils.buildProjectPath(projectName), directoryName);
		String finalImageFileString = Utils.buildPath(directoryPath, Utils.md5Checksum(copiedFile) + FILENAME_SEPARATOR
				+ copiedFile.getName());
		File copiedFileWithMd5 = new File(finalImageFileString);
		copiedFile.renameTo(copiedFileWithMd5);

		return copiedFileWithMd5;
	}

}
