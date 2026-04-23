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

package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import org.catrobat.catroid.stage.StageActivity;

public class WaitTillIdleAction extends Action {

	@Override
	public boolean act(float delta) {
		return allActorsIdle();
	}

	private boolean allActorsIdle() {
		int numberOfActors = StageActivity.stageListener.getStage().getActors().size;
		if (numberOfActors == 0) {
			return false;
		}

		int actorsWithNoEventThreads = 0;
		int actorWithOnlyThisEventThread = 0;

		for (Actor actor : StageActivity.stageListener.getStage().getActors()) {
			Array<Action> actions = actor.getActions();
			if (actions.size == 0) {
				actorsWithNoEventThreads++;
			}
			if (actions.size == 1) {
				ScriptSequenceAction sequenceAction = (ScriptSequenceAction) actions.get(0);
				if (sequenceAction.getActions().contains(this, true)) {
					actorWithOnlyThisEventThread++;
				}
			}
		}
		return numberOfActors == actorsWithNoEventThreads + actorWithOnlyThisEventThread;
	}
}
