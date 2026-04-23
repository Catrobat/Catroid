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

package org.catrobat.catroid.common;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.actions.ScriptSequenceActionWithWaiter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Iterator;

import androidx.annotation.IntDef;

public class ThreadScheduler {
	@Retention(RetentionPolicy.SOURCE)
	@IntDef({RUNNING, SUSPENDED})
	public @interface SchedulerState {
	}

	public static final int RUNNING = 0;
	public static final int SUSPENDED = 1;

	private Array<ScriptSequenceAction> startQueue = new Array<>();
	private Array<Action> stopQueue = new Array<>();
	private Actor actor;
	@SchedulerState
	private int state = RUNNING;

	public ThreadScheduler(Actor actor) {
		this.actor = actor;
	}

	public void tick(float delta) {
		Array<Action> actions = actor.getActions();
		startThreadsInStartQueue();
		runThreadsForOneTick(actions, delta);
		stopThreadsInStopQueue(actions);
	}

	private void startThreadsInStartQueue() {
		for (ScriptSequenceAction thread : startQueue) {
			thread.restart();
			actor.addAction(thread);
		}
		startQueue.clear();
	}

	private void runThreadsForOneTick(Array<Action> actions, float delta) {
		for (int i = 0; i < actions.size; i++) {
			Action action = actions.get(i);
			if (state == RUNNING && action.act(delta)) {
				stopQueue.add(action);
			}
		}
	}

	private void stopThreadsInStopQueue(Array<Action> actions) {
		actions.removeAll(stopQueue, true);
		for (Action action : stopQueue) {
			if (action instanceof ScriptSequenceActionWithWaiter) {
				((ScriptSequenceActionWithWaiter) action).notifyWaiter();
			}
		}
		stopQueue.clear();
	}

	public void startThread(ScriptSequenceAction sequenceAction) {
		removeThreadsWithEqualScriptFromStartQueue(sequenceAction);
		startQueue.add(sequenceAction);
	}

	private void removeThreadsWithEqualScriptFromStartQueue(ScriptSequenceAction sequenceAction) {
		Iterator<ScriptSequenceAction> iterator = startQueue.iterator();
		while (iterator.hasNext()) {
			ScriptSequenceAction action = iterator.next();
			if (action.getScript() == sequenceAction.getScript()) {
				if (action instanceof ScriptSequenceActionWithWaiter) {
					((ScriptSequenceActionWithWaiter) action).notifyWaiter();
				}
				iterator.remove();
			}
		}
	}

	public void stopThreadsWithScript(Script script) {
		for (Action action : actor.getActions()) {
			if (action instanceof ScriptSequenceAction && ((ScriptSequenceAction) action).getScript() == script) {
				stopQueue.add(action);
			}
		}
	}

	public void stopThreads(Array<Action> actions) {
		stopQueue.addAll(actions);
	}

	public boolean haveAllThreadsFinished() {
		return startQueue.size + actor.getActions().size == 0;
	}

	public void setState(@SchedulerState int schedulerState) {
		this.state = schedulerState;
	}
}
