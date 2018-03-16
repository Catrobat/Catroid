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
package org.catrobat.catroid.content;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

import org.catrobat.catroid.content.actions.EventSequenceAction;
import org.catrobat.catroid.content.actions.NotifyEventWaiterAction;

import java.util.Collection;

public class EventWrapperListener implements EventListener {

	private final Look look;

	EventWrapperListener(Look look) {
		this.look = look;
	}

	@Override
	public boolean handle(Event event) {
		if (event instanceof EventWrapper) {
			handleEvent((EventWrapper) event);
			return true;
		}
		return false;
	}

	private void handleEvent(EventWrapper event) {
		Collection<EventSequenceAction> sequenceActions = look.sprite.getIdToEventSequenceMap().get(event.eventId);
		for (EventSequenceAction actionToBeStarted : sequenceActions) {
			if (event.waitMode == EventWrapper.WAIT) {
				actionToBeStarted = createActionForWaitEvent(event, actionToBeStarted);
			}
			look.stopActionWithScript(actionToBeStarted.getScript());
			look.startAction(actionToBeStarted);
		}
	}

	private EventSequenceAction createActionForWaitEvent(EventWrapper event, EventSequenceAction actionToBeStarted) {
		event.addSpriteToWaitFor(look.sprite);
		actionToBeStarted = actionToBeStarted.clone(); // we want to add an action, but not to the original action
		actionToBeStarted.setNotifyAction((NotifyEventWaiterAction) ActionFactory.createNotifyEventWaiterAction(look.sprite, event));
		return actionToBeStarted;
	}
}
