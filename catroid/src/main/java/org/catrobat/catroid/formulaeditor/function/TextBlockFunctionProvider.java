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

package org.catrobat.catroid.formulaeditor.function;

import android.os.Handler;
import android.os.Looper;

import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.formulaeditor.Functions;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.utils.TextBlockUtil;

import java.util.Map;

public class TextBlockFunctionProvider implements FunctionProvider {
	@Override
	public void addFunctionsToMap(Map<Functions, FormulaFunction> formulaFunctions) {
		formulaFunctions.put(Functions.TEXT_BLOCK_X,
				new UnaryFunction(this::interpretFunctionTextBlockX));
		formulaFunctions.put(Functions.TEXT_BLOCK_Y,
				new UnaryFunction(this::interpretFunctionTextBlockY));
		formulaFunctions.put(Functions.TEXT_BLOCK_SIZE,
				new UnaryFunction(this::interpretFunctionTextBlockSize));
	}

	public void checkTextDetectionEnabled() {
		CameraManager cameraManager = StageActivity.getActiveCameraManager();
		if (cameraManager != null && !cameraManager.getDetectionOn()) {
			new Handler(Looper.getMainLooper()).post(() -> cameraManager.startDetection());
		}
	}

	public String interpretFunctionTextBlock(double argument) {
		checkTextDetectionEnabled();
		return TextBlockUtil.INSTANCE.getTextBlock((int) argument);
	}

	public String interpretFunctionTextBlockLanguage(double argument) {
		checkTextDetectionEnabled();
		return TextBlockUtil.INSTANCE.getTextBlockLanguage((int) argument);
	}

	private double interpretFunctionTextBlockX(double argument) {
		checkTextDetectionEnabled();
		return TextBlockUtil.INSTANCE.getCenterCoordinates((int) argument).x;
	}

	private double interpretFunctionTextBlockY(double argument) {
		checkTextDetectionEnabled();
		return TextBlockUtil.INSTANCE.getCenterCoordinates((int) argument).y;
	}

	private double interpretFunctionTextBlockSize(double argument) {
		checkTextDetectionEnabled();
		return TextBlockUtil.INSTANCE.getSize((int) argument);
	}
}
