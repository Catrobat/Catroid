/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.stage.utils;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.uiespresso.util.UserVariableTestUtils;

public final class ScriptEvaluationGateBrick {
	private static int gateCounter;
	private UserVariable userVariableGate;
	private static final double DONEVALUE = 42.0;

	private ScriptEvaluationGateBrick(Script script) {
		Project project = ProjectManager.getInstance().getCurrentProject();
		DataContainer dataContainer = project.getDefaultScene().getDataContainer();
		userVariableGate = dataContainer.addProjectUserVariable("userVariableGate" + gateCounter);
		gateCounter++;
		reset();

		SetVariableBrick setVariableBrick = new SetVariableBrick(new Formula(DONEVALUE), userVariableGate);
		script.addBrick(setVariableBrick);
	}

	public static ScriptEvaluationGateBrick appendToScript(Script script) {
		return new ScriptEvaluationGateBrick(script);
	}

	public boolean waitUntilEvaluated(int timeoutMillis) {
		return UserVariableTestUtils.userVariableEqualsWithinTimeout(userVariableGate, DONEVALUE, timeoutMillis);
	}

	public void reset() {
		userVariableGate.setValue(0.0);
	}
}
