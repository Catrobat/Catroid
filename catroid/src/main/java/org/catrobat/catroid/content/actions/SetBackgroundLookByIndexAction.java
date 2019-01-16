/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class SetBackgroundLookByIndexAction extends SetBackgroundLookAction {
	Formula formula;
	Sprite scopeSprite;
	@Override
	public boolean act(float delta) {
		updateLookFromFormula();
		return super.act(delta);
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public void setScopeSprite(Sprite scopeSprite) {
		this.scopeSprite = scopeSprite;
	}
	private void updateLookFromFormula() {
		try {
			int lookPosition = formula.interpretInteger(scopeSprite);
			background = ProjectManager.getInstance().getCurrentlyPlayingScene().getBackgroundSprite();
			if (lookPosition > 0 && lookPosition <= background.getLookList().size()) {
				lookData = background.getLookList().get(lookPosition - 1);
			}
		} catch (InterpretationException ex) {
			Log.d(getClass().getSimpleName(), "Formula Interpretation for look index failed", ex);
		}
	}
}
