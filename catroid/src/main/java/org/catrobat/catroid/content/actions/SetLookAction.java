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

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.BackgroundWaitHandler;
import org.catrobat.catroid.content.Sprite;

public class SetLookAction extends Action {

	protected LookData look;
	protected Sprite sprite;

	protected boolean wait = false;
	protected boolean setLookDone = false;
	protected boolean scriptsAreCompleted = false;

	protected void doLookUpdate() {
		if (wait) {
			BackgroundWaitHandler.addObserver(look, this);
		}
		if (look != null && sprite != null && sprite.getLookDataList().contains(look)) {
			sprite.look.setLookData(look);
			setLookDone = true;
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

	public void notifyScriptsCompleted() {
		scriptsAreCompleted = true;
	}
}
