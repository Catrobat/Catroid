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

import android.support.annotation.NonNull;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.EventWrapper;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;

public class EventThread extends ScriptSequenceAction {
	private NotifyEventWaiterAction notifyAction;

	public EventThread(@NonNull Script script) {
		super(script);
	}

	public EventThread(@NonNull EventThread originalThread, @NonNull Sprite sprite, @NonNull EventWrapper event) {
		super(originalThread.script);
		for (Action action : originalThread.getActions()) {
			addAction(action);
		}
		notifyAction = (NotifyEventWaiterAction) sprite.getActionFactory().createNotifyEventWaiterAction(sprite, event);
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
		super.reset();
	}

	public EventThread clone() {
		EventThread copy = (EventThread) ActionFactory.createEventThread(script);

		return copy;
	}

	public void setNotifyAction(NotifyEventWaiterAction notifyAction) {
		this.notifyAction = notifyAction;
	}
}
