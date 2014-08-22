/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.common.standardprojectcreators;

import android.content.Context;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.conditional.GlideToBrick;
import org.catrobat.catroid.content.bricks.conditional.HideBrick;
import org.catrobat.catroid.content.bricks.conditional.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.conditional.SetLookBrick;
import org.catrobat.catroid.content.bricks.conditional.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.conditional.ShowBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.soundrecorder.SoundRecorder;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;
import java.io.IOException;

public class StandardProjectCreatorDefault extends StandardProjectCreator {
	private static final String TAG = StandardProjectCreatorDefault.class.getSimpleName();

	public StandardProjectCreatorDefault() {
		standardProjectNameID = R.string.default_project_name;
	}

	@Override
	public Project createStandardProject(String projectName, Context context) throws IOException, IllegalArgumentException {
		if (StorageHandler.getInstance().projectExists(projectName)) {
			throw new IllegalArgumentException("Project with name '" + projectName + "' already exists!");
		}
		String moleLookName = context.getString(R.string.default_project_sprites_mole_name);
		String mole1Name = moleLookName + " 1";
		String mole2Name = moleLookName + " 2";
		String mole3Name = moleLookName + " 3";
		String mole4Name = moleLookName + " 4";
		String whackedMoleLookName = context.getString(R.string.default_project_sprites_mole_whacked);
		String movingMoleLookName = context.getString(R.string.default_project_sprites_mole_moving);
		String soundName = context.getString(R.string.default_project_sprites_mole_sound);
		String backgroundName = context.getString(R.string.default_project_backgroundname);

		String varRandomFrom = context.getString(R.string.default_project_var_random_from);
		String varRandomTo = context.getString(R.string.default_project_var_random_to);

		Project defaultProject = new Project(context, projectName);
		defaultProject.setDeviceData(context); // density anywhere here
		StorageHandler.getInstance().saveProject(defaultProject);
		ProjectManager.getInstance().setProject(defaultProject);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactorToScreenSize(
				R.drawable.default_project_background, context);

		File backgroundFile = UtilFile.copyImageFromResourceIntoProject(projectName, backgroundName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_background, context, true,
				backgroundImageScaleFactor
		);
		File movingMoleFile = UtilFile.copyImageFromResourceIntoProject(projectName, movingMoleLookName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_mole_moving, context, true,
				backgroundImageScaleFactor
		);
		File diggedOutMoleFile = UtilFile.copyImageFromResourceIntoProject(projectName, moleLookName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_mole_digged_out, context, true,
				backgroundImageScaleFactor
		);
		File whackedMoleFile = UtilFile.copyImageFromResourceIntoProject(projectName, whackedMoleLookName
						+ Constants.IMAGE_STANDARD_EXTENTION, R.drawable.default_project_mole_whacked, context, true,
				backgroundImageScaleFactor
		);
		try {
			File soundFile1 = UtilFile.copySoundFromResourceIntoProject(projectName, soundName + "1"
					+ SoundRecorder.RECORDING_EXTENSION, R.raw.default_project_sound_mole_1, context, true);
			File soundFile2 = UtilFile.copySoundFromResourceIntoProject(projectName, soundName + "2"
					+ SoundRecorder.RECORDING_EXTENSION, R.raw.default_project_sound_mole_2, context, true);
			File soundFile3 = UtilFile.copySoundFromResourceIntoProject(projectName, soundName + "3"
					+ SoundRecorder.RECORDING_EXTENSION, R.raw.default_project_sound_mole_3, context, true);
			File soundFile4 = UtilFile.copySoundFromResourceIntoProject(projectName, soundName + "4"
					+ SoundRecorder.RECORDING_EXTENSION, R.raw.default_project_sound_mole_4, context, true);
			UtilFile.copyFromResourceIntoProject(projectName, ".", StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME,
					R.drawable.default_project_screenshot, context, false);

			LookData movingMoleLookData = new LookData();
			movingMoleLookData.setLookName(movingMoleLookName);
			movingMoleLookData.setLookFilename(movingMoleFile.getName());

			LookData diggedOutMoleLookData = new LookData();
			diggedOutMoleLookData.setLookName(moleLookName);
			diggedOutMoleLookData.setLookFilename(diggedOutMoleFile.getName());

			LookData whackedMoleLookData = new LookData();
			whackedMoleLookData.setLookName(whackedMoleLookName);
			whackedMoleLookData.setLookFilename(whackedMoleFile.getName());

			LookData backgroundLookData = new LookData();
			backgroundLookData.setLookName(backgroundName);
			backgroundLookData.setLookFilename(backgroundFile.getName());

			SoundInfo soundInfo = new SoundInfo();
			soundInfo.setTitle(soundName);
			soundInfo.setSoundFileName(soundFile1.getName());

			UserVariablesContainer userVariables = defaultProject.getUserVariables();
			Sprite backgroundSprite = defaultProject.getSpriteList().get(0);

			userVariables.addProjectUserVariable(varRandomFrom);
			UserVariable randomFrom = userVariables.getUserVariable(varRandomFrom, backgroundSprite);

			userVariables.addProjectUserVariable(varRandomTo);
			UserVariable randomTo = userVariables.getUserVariable(varRandomTo, backgroundSprite);

			// Background sprite
			backgroundSprite.getLookDataList().add(backgroundLookData);
			Script backgroundStartScript = new StartScript();

			SetLookBrick setLookBrick = new SetLookBrick();
			setLookBrick.setLook(backgroundLookData);
			backgroundStartScript.addBrick(setLookBrick);

			SetVariableBrick setVariableBrick = new SetVariableBrick(new Formula(1), randomFrom);
			backgroundStartScript.addBrick(setVariableBrick);

			setVariableBrick = new SetVariableBrick(new Formula(5), randomTo);
			backgroundStartScript.addBrick(setVariableBrick);

			backgroundSprite.addScript(backgroundStartScript);

			FormulaElement randomElement = new FormulaElement(FormulaElement.ElementType.FUNCTION, Functions.RAND.toString(), null);
			randomElement.setLeftChild(new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, varRandomFrom, randomElement));
			randomElement.setRightChild(new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, varRandomTo, randomElement));
			Formula randomWait = new Formula(randomElement);

			FormulaElement waitOneOrTwoSeconds = new FormulaElement(FormulaElement.ElementType.FUNCTION, Functions.RAND.toString(),
					null);
			waitOneOrTwoSeconds.setLeftChild(new FormulaElement(FormulaElement.ElementType.NUMBER, "1", waitOneOrTwoSeconds));
			waitOneOrTwoSeconds.setRightChild(new FormulaElement(FormulaElement.ElementType.NUMBER, "2", waitOneOrTwoSeconds));

			// Mole 1 sprite
			Sprite mole1Sprite = new Sprite(mole1Name);
			mole1Sprite.getLookDataList().add(movingMoleLookData);
			mole1Sprite.getLookDataList().add(diggedOutMoleLookData);
			mole1Sprite.getLookDataList().add(whackedMoleLookData);
			mole1Sprite.getSoundList().add(soundInfo);

			Script mole1StartScript = new StartScript();
			Script mole1WhenScript = new WhenScript();

			// start script
			SetSizeToBrick setSizeToBrick = new SetSizeToBrick(new Formula(30));
			mole1StartScript.addBrick(setSizeToBrick);

			ForeverBrick foreverBrick = new ForeverBrick();
			mole1StartScript.addBrick(foreverBrick);

			PlaceAtBrick placeAtBrick = new PlaceAtBrick(calculateValueRelativeToScaledBackground(-160),
					calculateValueRelativeToScaledBackground(-110));
			mole1StartScript.addBrick(placeAtBrick);

			WaitBrick waitBrick = new WaitBrick(new Formula(waitOneOrTwoSeconds));
			mole1StartScript.addBrick(waitBrick);

			ShowBrick showBrick = new ShowBrick();
			mole1StartScript.addBrick(showBrick);

			setLookBrick = new SetLookBrick();
			setLookBrick.setLook(movingMoleLookData);
			mole1StartScript.addBrick(setLookBrick);

			GlideToBrick glideToBrick = new GlideToBrick(calculateValueRelativeToScaledBackground(-160),
					calculateValueRelativeToScaledBackground(-95), 100);
			mole1StartScript.addBrick(glideToBrick);

			setLookBrick = new SetLookBrick();
			setLookBrick.setLook(diggedOutMoleLookData);
			mole1StartScript.addBrick(setLookBrick);

			waitBrick = new WaitBrick(randomWait.clone());
			mole1StartScript.addBrick(waitBrick);

			HideBrick hideBrick = new HideBrick();
			mole1StartScript.addBrick(hideBrick);

			waitBrick = new WaitBrick(randomWait.clone());
			mole1StartScript.addBrick(waitBrick);

			LoopEndlessBrick loopEndlessBrick = new LoopEndlessBrick(foreverBrick);
			mole1StartScript.addBrick(loopEndlessBrick);

			// when script
			PlaySoundBrick playSoundBrick = new PlaySoundBrick();
			playSoundBrick.setSoundInfo(soundInfo);
			mole1WhenScript.addBrick(playSoundBrick);

			setLookBrick = new SetLookBrick();
			setLookBrick.setLook(whackedMoleLookData);
			mole1WhenScript.addBrick(setLookBrick);

			waitBrick = new WaitBrick(1500);
			mole1WhenScript.addBrick(waitBrick);

			hideBrick = new HideBrick();
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
			placeAtBrick.setXPosition(new Formula(calculateValueRelativeToScaledBackground(160)));
			placeAtBrick.setYPosition(new Formula(calculateValueRelativeToScaledBackground(-110)));

			glideToBrick = (GlideToBrick) tempScript.getBrick(6);
			glideToBrick.setXDestination(new Formula(calculateValueRelativeToScaledBackground(160)));
			glideToBrick.setYDestination(new Formula(calculateValueRelativeToScaledBackground(-95)));

			// Mole 3 sprite
			Sprite mole3Sprite = mole1Sprite.clone();
			mole3Sprite.getSoundList().get(0).setSoundFileName(soundFile3.getName());
			mole3Sprite.setName(mole3Name);
			defaultProject.addSprite(mole3Sprite);

			tempScript = mole3Sprite.getScript(0);
			placeAtBrick = (PlaceAtBrick) tempScript.getBrick(2);
			placeAtBrick.setXPosition(new Formula(calculateValueRelativeToScaledBackground(-160)));
			placeAtBrick.setYPosition(new Formula(calculateValueRelativeToScaledBackground(-290)));

			glideToBrick = (GlideToBrick) tempScript.getBrick(6);
			glideToBrick.setXDestination(new Formula(calculateValueRelativeToScaledBackground(-160)));
			glideToBrick.setYDestination(new Formula(calculateValueRelativeToScaledBackground(-275)));

			// Mole 4 sprite
			Sprite mole4Sprite = mole1Sprite.clone();
			mole4Sprite.getSoundList().get(0).setSoundFileName(soundFile4.getName());
			mole4Sprite.setName(mole4Name);
			defaultProject.addSprite(mole4Sprite);

			tempScript = mole4Sprite.getScript(0);
			placeAtBrick = (PlaceAtBrick) tempScript.getBrick(2);
			placeAtBrick.setXPosition(new Formula(calculateValueRelativeToScaledBackground(160)));
			placeAtBrick.setYPosition(new Formula(calculateValueRelativeToScaledBackground(-290)));

			glideToBrick = (GlideToBrick) tempScript.getBrick(6);
			glideToBrick.setXDestination(new Formula(calculateValueRelativeToScaledBackground(160)));
			glideToBrick.setYDestination(new Formula(calculateValueRelativeToScaledBackground(-275)));
		} catch (IllegalArgumentException illegalArgumentException) {
			throw new IOException(TAG, illegalArgumentException);
		}

		StorageHandler.getInstance().saveProject(defaultProject);

		return defaultProject;
	}
}
