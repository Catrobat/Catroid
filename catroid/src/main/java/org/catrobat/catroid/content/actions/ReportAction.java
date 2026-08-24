/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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

package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.formulaeditor.Formula;

public class ReportAction extends Action {

	private Script currentScript;
	private Formula reportFormula;
	private Scope scope;
	private long callId = -1;

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public void setCurrentScript(Script currentScript) {
		this.currentScript = currentScript;
	}

	public void setReportFormula(Formula reportFormula) {
		this.reportFormula = reportFormula;
	}

	public void setCallId(long callId) {
		this.callId = callId;
	}

	public Formula getReportFormula() {
		return this.reportFormula;
	}

	public Scope getScope() {
		return scope;
	}

	@Override
	public boolean act(float delta) {
		if (callId != -1) {
			Object value = reportFormula.interpretObject(scope);
			org.catrobat.catroid.formulaeditor.FunctionCallManager.getInstance().setResult(callId, value);
		}
		if (actor instanceof org.catrobat.catroid.content.Look) {
			((org.catrobat.catroid.content.Look) actor).stopThreadWithScript(currentScript);
		}
		return true;
	}
}
