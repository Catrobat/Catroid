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
package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.BackgroundWaitHandler;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class SetLookAction extends Action {

	private LookData look;
	private Sprite sprite;
	private Formula formula;

	private boolean wait = false;
	private boolean setLookDone = false;
	private boolean scriptsAreCompleted = false;

	protected void doLookUpdate() {
		if (formula != null) {
			updateLookFromFormula();
			if (look.getLookFileName() == null) {
				scriptsAreCompleted = true;
				return;
			}
		}

		if (wait) {
			BackgroundWaitHandler.addObserver(look, this);
		}

		if (look != null && sprite != null && sprite.getLookDataList().contains(look)) {
			sprite.look.setLookData(look);
			setLookDone = true;
		}
	}

	private void updateLookFromFormula() {
		int lookPosition = -1;
		String parsedLookName = "";
		try {
			lookPosition = formula.interpretInteger(sprite);
		} catch (InterpretationException e) {
			try {
				parsedLookName = formula.interpretString(sprite);
			} catch (InterpretationException ex) {
				Log.d(getClass().getSimpleName(), "Formula interpretation for look failed.", e);
				return;
			}
		}
		if (lookPosition > 0 && lookPosition <= sprite.getLookDataList().size()) {
			look = sprite.getLookDataList().get(lookPosition - 1);
		} else {
			for (LookData lookData : sprite.getLookDataList()) {
				if (lookData.getLookName().equals(parsedLookName)) {
					look = lookData;
				}
			}
		}
	}

	@Override
	public boolean act(float delta) {
		if (!setLookDone) {
			doLookUpdate();
		}

		if (wait) {
			return scriptsAreCompleted;
		} else {
			return true;
		}
	}

	@Override
	public void restart() {
		setLookDone = false;
		if (wait) {
			scriptsAreCompleted = false;
		}
	}

	public void setLookData(LookData look) {
		this.look = look;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setWait(boolean wait) {
		this.wait = wait;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}

	public void notifyScriptsCompleted() {
		scriptsAreCompleted = true;
	}
}
