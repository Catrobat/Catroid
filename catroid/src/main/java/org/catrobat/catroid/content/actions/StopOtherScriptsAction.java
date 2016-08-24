/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class StopOtherScriptsAction extends TemporalAction {

	private Action currentAction;

	@Override
	protected void update(float percent) {
		if (this.actor == null || this.actor.getActions() == null) {
			return;
		}

		boolean alreadyStopped = false;
		String currentActionSignature = this.currentAction.toString();

		for (Action action : this.actor.getActions()) {
			if (!alreadyStopped && action.toString().contains(currentActionSignature)) {
				alreadyStopped = true;
				continue;
			} else {
				action.reset();
			}
		}
	}

	public void setCurrentAction(Action currentAction) {
		this.currentAction = currentAction;
	}
}
