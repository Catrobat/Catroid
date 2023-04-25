/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.common.defaultprojectcreators;

import android.content.Context;
import android.graphics.BitmapFactory;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.content.bricks.ZigZagStitchBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Operators;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.utils.ImageEditing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.catrobat.catroid.common.Constants.DEFAULT_IMAGE_EXTENSION;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.ScreenValues.SCREEN_HEIGHT;
import static org.catrobat.catroid.common.ScreenValues.SCREEN_WIDTH;
import static org.catrobat.catroid.common.Constants.SCREENSHOT_AUTOMATIC_FILE_NAME;

public class DefaultExampleProject extends DefaultProjectCreator {
	public DefaultExampleProject() {
		defaultProjectNameResourceId = R.string.default_project_name;
	}

	@Override
	public Project createDefaultProject(String name, Context context, boolean landscapeMode) throws IOException {
		Project project = new Project(context, name, landscapeMode);

		if (project.getDirectory().exists()) {
			throw new IOException("Cannot create new project at "
					+ project.getDirectory().getAbsolutePath()
					+ ", directory already exists.");
		}

		XstreamSerializer.getInstance().saveProject(project);

		if (!project.getDirectory().isDirectory()) {
			throw new FileNotFoundException("Cannot create project at " + project.getDirectory().getAbsolutePath());
		}

		int needleDrawableId;
		int backgroundDrawableId;
		int screenshotDrawableId;

		if (landscapeMode) {
			backgroundDrawableId = R.drawable.default_project_background_landscape;
			needleDrawableId = R.drawable.default_project_needle;
			screenshotDrawableId = R.drawable.default_project_screenshot_landscape;
		} else {
			backgroundDrawableId = R.drawable.default_project_background_portrait;
			needleDrawableId = R.drawable.default_project_needle;
			screenshotDrawableId = R.drawable.default_project_screenshot;
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), backgroundDrawableId, options);

		backgroundImageScaleFactor = ImageEditing.calculateScaleFactor(
				options.outWidth,
				options.outHeight,
				SCREEN_WIDTH,
				SCREEN_HEIGHT);

		Scene scene = project.getDefaultScene();

		File imageDir = new File(scene.getDirectory(), IMAGE_DIRECTORY_NAME);

		String imageFileName = "img" + DEFAULT_IMAGE_EXTENSION;

		File needleFile1 = ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				needleDrawableId,
				imageDir,
				imageFileName,
				backgroundImageScaleFactor);

		ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				screenshotDrawableId,
				scene.getDirectory(),
				SCREENSHOT_AUTOMATIC_FILE_NAME,
				1);

		Sprite needle = new Sprite(context.getString(R.string.default_project_needle_name));

		scene.addSprite(needle);

		needle.getLookList()
				.add(new LookData(context.getString(R.string.default_project_needle_name), needleFile1));

		Script script = new StartScript();

		UserVariable variableOuterLoop = new UserVariable(context.getString(R.string.default_project_outer_loop));
		UserVariable variableInnerLoop = new UserVariable(context.getString(R.string.default_project_inner_loop));

		needle.addUserVariable(variableInnerLoop);
		needle.addUserVariable(variableOuterLoop);

		script.addBrick(new SetVariableBrick(new Formula(8), variableInnerLoop));
		script.addBrick(new SetVariableBrick(new Formula(8), variableOuterLoop));
		script.addBrick(new ZigZagStitchBrick(new Formula(2), new Formula(10)));

		Formula repeatUntilFormulaOuterLoop = new Formula(1);
		repeatUntilFormulaOuterLoop.setRoot(new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, variableOuterLoop.getName(),
				null));

		RepeatBrick outerLoopRepeat = new RepeatBrick(repeatUntilFormulaOuterLoop);

		Formula repeatUntilFormulaInnerLoop = new Formula(1);
		repeatUntilFormulaInnerLoop.setRoot(new FormulaElement(FormulaElement.ElementType.USER_VARIABLE,
				variableInnerLoop.getName(),
				null));

		RepeatBrick innerLoopRepeat = new RepeatBrick(repeatUntilFormulaInnerLoop);
		innerLoopRepeat.addBrick(new MoveNStepsBrick(new Formula(100)));

		FormulaElement innerLoopFormula = new FormulaElement(FormulaElement.ElementType.OPERATOR,
				Operators.DIVIDE.name(), null,
				new FormulaElement(FormulaElement.ElementType.NUMBER, String.valueOf(360), null),
				new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, variableInnerLoop.getName(),
						null));

		innerLoopRepeat.addBrick(new TurnRightBrick(new Formula(innerLoopFormula)));

		outerLoopRepeat.addBrick(innerLoopRepeat);
		FormulaElement outerLoopFormula = new FormulaElement(FormulaElement.ElementType.OPERATOR,
				Operators.DIVIDE.name(), null,
				new FormulaElement(FormulaElement.ElementType.NUMBER, String.valueOf(360), null),
				new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, variableOuterLoop.getName(),
						null));
		outerLoopRepeat.addBrick(new TurnRightBrick(new Formula(outerLoopFormula)));

		script.addBrick(outerLoopRepeat);
		needle.addScript(script);

		XstreamSerializer.getInstance().saveProject(project);
		return project;
	}
}
