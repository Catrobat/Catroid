/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BroadcastSequenceMap;
import org.catrobat.catroid.common.BroadcastWaitSequenceMap;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.ArrayList;

public final class BroadcastHandler {
	private BroadcastHandler() {
		throw new AssertionError();
	}

	public static void doHandleBroadcastEvent(Look look, String broadcastMessage) {
		if (!BroadcastSequenceMap.containsKey(broadcastMessage)) {
			return;
		}

		for (SequenceAction action : BroadcastSequenceMap.get(broadcastMessage)) {
			if (!handleAction(action)) {
				addOrRestartAction(look, action);
			}
		}

		if (BroadcastWaitSequenceMap.containsKey(broadcastMessage)) {
			for (SequenceAction action : BroadcastWaitSequenceMap.get(broadcastMessage)) {
				addOrRestartAction(look, action);
			}
			BroadcastWaitSequenceMap.currentBroadcastEvent.resetEventAndResumeScript();
		}
	}

	public static void doHandleBroadcastFromWaiterEvent(Look look, BroadcastEvent event, String broadcastMessage) {
		if (!BroadcastSequenceMap.containsKey(broadcastMessage)) {
			return;
		}

		if (!BroadcastWaitSequenceMap.containsKey(broadcastMessage)) {
			BroadcastWaitSequenceMap.currentBroadcastEvent = event;
			addBroadcastMessageToBroadcastWaitSequenceMap(look, event, broadcastMessage);
		} else {
			if (BroadcastWaitSequenceMap.currentBroadcastEvent == event
					&& BroadcastWaitSequenceMap.currentBroadcastEvent != null) {
				for (SequenceAction action : BroadcastWaitSequenceMap.get(broadcastMessage)) {
					BroadcastWaitSequenceMap.currentBroadcastEvent.resetNumberOfFinishedReceivers();
					addOrRestartAction(look, action);
				}
			} else {
				if (BroadcastWaitSequenceMap.currentBroadcastEvent != null) {
					BroadcastWaitSequenceMap.currentBroadcastEvent.resetEventAndResumeScript();
				}
				BroadcastWaitSequenceMap.currentBroadcastEvent = event;
				addBroadcastMessageToBroadcastWaitSequenceMap(look, event, broadcastMessage);
			}
		}
	}

	private static void addOrRestartAction(Look look, Action action) {
		if (action.getActor() == null) {
			if (!look.getActions().contains(action, false)) {
				look.addAction(action);
			}
		} else {
			if (!look.actionsToRestart.contains(action)) {
				look.actionsToRestart.add(action);
			}
		}
	}

	private static void addBroadcastMessageToBroadcastWaitSequenceMap(Look look, BroadcastEvent event,
			String broadcastMessage) {
		ArrayList<SequenceAction> actionList = new ArrayList<SequenceAction>();
		for (SequenceAction action : BroadcastSequenceMap.get(broadcastMessage)) {
			event.raiseNumberOfReceivers();
			SequenceAction broadcastWaitAction = ExtendedActions.sequence(action,
					ExtendedActions.broadcastNotify(event));
			actionList.add(broadcastWaitAction);
			addOrRestartAction(look, broadcastWaitAction);
		}
		BroadcastWaitSequenceMap.put(broadcastMessage, actionList);
	}

	private static boolean handleAction(Action action) {
		for (Sprite sprites : ProjectManager.getInstance().getCurrentProject().getSpriteList()) {
			for (Action actionOfLook : sprites.look.getActions()) {
				if (action == actionOfLook) {
					actionOfLook.restart();
					return true;
				} else {
					if (actionOfLook instanceof SequenceAction && ((SequenceAction) actionOfLook).getActions().size > 0
							&& ((SequenceAction) actionOfLook).getActions().get(0) == action) {
						actionOfLook.restart();
						return true;
					}
				}
			}
		}
		return false;
	}
}
