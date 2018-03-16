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

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Script;

public class EventSequenceAction extends SequenceAction {
	private Script script;
	private NotifyEventWaiterAction notifyAction;

	public EventSequenceAction(Script script) {
		super();
		this.script = script;
	}

	public EventSequenceAction(Action action1, Script script) {
		super(action1);
		this.script = script;
	}

	public EventSequenceAction(Action action1, Action action2, Script script) {
		super(action1, action2);
		this.script = script;
	}

	public EventSequenceAction(Action action1, Action action2, Action action3, Script script) {
		super(action1, action2, action3);
		this.script = script;
	}

	public EventSequenceAction(Action action1, Action action2, Action action3, Action action4, Script script) {
		super(action1, action2, action3, action4);
		this.script = script;
	}

	public EventSequenceAction(Action action1, Action action2, Action action3, Action action4, Action action5, Script script) {
		super(action1, action2, action3, action4, action5);
		this.script = script;
	}

	public Script getScript() {
		return script;
	}

	public EventSequenceAction clone() {
		EventSequenceAction copy = (EventSequenceAction) ActionFactory.eventSequence(script);
		for (Action childAction : getActions()) {
			copy.addAction(childAction);
		}
		return copy;
	}

	public void notifyWaiter() {
		if (notifyAction != null) {
			notifyAction.act(1);
		}
	}

	@Override
	public boolean act(float delta) {
		if (super.act(delta)) {
			notifyWaiter();
			return true;
		}
		return false;
	}

	@Override
	public void reset() {
		notifyWaiter();
		script = null;
		super.reset();
	}

	public void setNotifyAction(NotifyEventWaiterAction notifyAction) {
		this.notifyAction = notifyAction;
	}
}
