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

package org.catrobat.catroid.common;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.actions.EventSequenceAction;

import java.util.Iterator;

public class ActionScheduler {

	private Array<Action> actionsToBeStarted = new Array<>();
	private Array<Action> actionsToBeRemoved = new Array<>();
	private Actor actor;

	public ActionScheduler(Actor actor) {
		this.actor = actor;
	}

	public void tick(float delta) {
		Array<Action> actions = actor.getActions();
		startActionsToBeStarted();
		runActionsForOneTick(actions, delta);
		removeActionsToBeRemoved(actions);
	}

	private void startActionsToBeStarted() {
		for (Action action : actionsToBeStarted) {
			action.restart();
			actor.addAction(action);
		}
		actionsToBeStarted.clear();
	}

	private void runActionsForOneTick(Array<Action> actions, float delta) {
		for (int i = 0; i < actions.size; i++) {
			Action action = actions.get(i);
			if (action.act(delta)) {
				actionsToBeRemoved.add(action);
			}
		}
	}

	private void removeActionsToBeRemoved(Array<Action> actions) {
		actions.removeAll(actionsToBeRemoved, true);
		for (Action action : actionsToBeRemoved) {
			if (action instanceof EventSequenceAction) {
				((EventSequenceAction) action).notifyWaiter();
			}
		}
		actionsToBeRemoved.clear();
	}

	public void startAction(Action action) {
		actionsToBeStarted.add(action);
	}

	public void stopActionsWithScript(Script script) {
		for (Action action : actor.getActions()) {
			if (action instanceof EventSequenceAction && ((EventSequenceAction) action).getScript() == script) {
				actionsToBeRemoved.add(action);
			}
		}
		Iterator<Action> iterator = actionsToBeStarted.iterator();
		while (iterator.hasNext()) {
			Action action = iterator.next();
			if (action instanceof EventSequenceAction && ((EventSequenceAction) action).getScript() == script) {
				((EventSequenceAction) action).notifyWaiter();
				iterator.remove();
			}
		}
	}

	public boolean getAllActionsFinished() {
		return actionsToBeStarted.size + actor.getActions().size == 0;
	}
}
