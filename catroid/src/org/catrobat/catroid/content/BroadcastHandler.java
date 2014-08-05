/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BroadcastSequenceMap;
import org.catrobat.catroid.common.BroadcastWaitSequenceMap;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.actions.BroadcastNotifyAction;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.ArrayList;
import java.util.HashMap;

public final class BroadcastHandler {

	private static Multimap<String, String> actionsToRestartMap = ArrayListMultimap.create();
	private static HashMap<Action, Script> actionScriptMap = new HashMap<Action, Script>();
	private static HashMap<Script, Sprite> scriptSpriteMap = new HashMap<Script, Sprite>();
	private static HashMap<String, Action> stringActionMap = new HashMap<String, Action>();

	private static final String TAG = "BroadcastHandler";

	private BroadcastHandler() {
		throw new AssertionError();
	}

	public static void doHandleBroadcastEvent(Look look, String broadcastMessage) {
		if (!BroadcastSequenceMap.containsKey(broadcastMessage)) {
			return;
		}

		for (SequenceAction action : BroadcastSequenceMap.get(broadcastMessage)) {
			Script scriptOfAction = actionScriptMap.get(action);

			if (!handleAction(action, scriptOfAction)) {
				addOrRestartAction(look, action);
			}
		}

		if (BroadcastWaitSequenceMap.containsKey(broadcastMessage)) {
			for (SequenceAction action : BroadcastWaitSequenceMap.get(broadcastMessage)) {
				addOrRestartAction(look, action);
			}
			BroadcastWaitSequenceMap.getCurrentBroadcastEvent().resetEventAndResumeScript();
		}
	}

	public static void doHandleBroadcastFromWaiterEvent(Look look, BroadcastEvent event, String broadcastMessage) {
		if (!BroadcastSequenceMap.containsKey(broadcastMessage)) {
			return;
		}

		if (!BroadcastWaitSequenceMap.containsKey(broadcastMessage)) {
			addBroadcastMessageToBroadcastWaitSequenceMap(look, event, broadcastMessage);
		} else {
			if (BroadcastWaitSequenceMap.getCurrentBroadcastEvent() == event
					&& BroadcastWaitSequenceMap.getCurrentBroadcastEvent() != null) {
				for (SequenceAction action : BroadcastWaitSequenceMap.get(broadcastMessage)) {
					BroadcastWaitSequenceMap.getCurrentBroadcastEvent().resetNumberOfFinishedReceivers();
					addOrRestartAction(look, action);
				}
			} else {
				if (BroadcastWaitSequenceMap.getCurrentBroadcastEvent() != null) {
					BroadcastWaitSequenceMap.getCurrentBroadcastEvent().resetEventAndResumeScript();
				}
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
			if (!Look.actionsToRestartContains(action)) {
				Look.actionsToRestartAdd(action);
			}
		}
	}

	private static void addBroadcastMessageToBroadcastWaitSequenceMap(Look look, BroadcastEvent event,
			String broadcastMessage) {
		ArrayList<SequenceAction> actionList = new ArrayList<SequenceAction>();
		BroadcastWaitSequenceMap.setCurrentBroadcastEvent(event);
		for (SequenceAction action : BroadcastSequenceMap.get(broadcastMessage)) {
			SequenceAction broadcastWaitAction = ExtendedActions.sequence(action,
					ExtendedActions.broadcastNotify(event));
			Script receiverScript = actionScriptMap.get(action);
			actionScriptMap.put(broadcastWaitAction, receiverScript);
			Sprite receiverSprite = scriptSpriteMap.get(receiverScript);
			String actionName = broadcastWaitAction.toString() + Constants.ACTION_SPRITE_SEPARATOR + receiverSprite.getName() + receiverSprite.getScriptIndex(receiverScript);
			stringActionMap.put(actionName, broadcastWaitAction);
			if (!handleActionFromBroadcastWait(look, broadcastWaitAction)) {
				event.raiseNumberOfReceivers();
				actionList.add(broadcastWaitAction);
				addOrRestartAction(look, broadcastWaitAction);
			}
		}
		if (actionList.size() > 0) {
			BroadcastWaitSequenceMap.put(broadcastMessage, actionList);
		}
	}

	private static boolean handleAction(Action action, Script scriptOfAction) {
		Sprite spriteOfAction = scriptSpriteMap.get(scriptOfAction);
		String actionToHandle = action.toString() + Constants.ACTION_SPRITE_SEPARATOR + spriteOfAction.getName() + spriteOfAction.getScriptIndex(scriptOfAction);

		if (!actionsToRestartMap.containsKey(actionToHandle)) {
			return false;
		}
		for (String actionString : actionsToRestartMap.get(actionToHandle)) {
			Action actionOfLook = stringActionMap.get(actionString);
			if (actionOfLook == null) {
				Log.d(TAG, "Action of look is skipped with action: " + actionString + ". It is probably a BroadcastNotify-Action that must not be restarted yet");
				continue;
			}
			if (actionOfLook.getActor() == null) {
				return false;
			}
			Look.actionsToRestartAdd(actionOfLook);
		}
		return true;
	}

	private static boolean handleActionFromBroadcastWait(Look look,
			SequenceAction sequenceActionWithBroadcastNotifyAction) {
		Action actualAction = sequenceActionWithBroadcastNotifyAction.getActions().get(0);

		for (Sprite sprites : ProjectManager.getInstance().getCurrentProject().getSpriteList()) {
			for (Action actionOfLook : sprites.look.getActions()) {
				Action actualActionOfLook = null;
				if (actionOfLook instanceof SequenceAction && ((SequenceAction) actionOfLook).getActions().size > 0) {
					actualActionOfLook = ((SequenceAction) actionOfLook).getActions().get(0);
				}
				if (sequenceActionWithBroadcastNotifyAction == actionOfLook) {
					((BroadcastNotifyAction) ((SequenceAction) actionOfLook).getActions().get(1)).getEvent()
							.resetNumberOfFinishedReceivers();
					Look.actionsToRestartAdd(actionOfLook);
					return true;
				} else {
					if (actualActionOfLook != null && actualActionOfLook == actualAction) {
						((BroadcastNotifyAction) ((SequenceAction) actionOfLook).getActions().get(1)).getEvent()
								.resetEventAndResumeScript();
						Look.actionsToRestartAdd(actionOfLook);
						return false;
					} else {
						addOrRestartAction(look, sequenceActionWithBroadcastNotifyAction);
						return false;
					}
				}
			}
		}
		return false;
	}

	public static void clearActionMaps() {
		actionsToRestartMap.clear();
		actionScriptMap.clear();
		stringActionMap.clear();
	}

	public static Multimap<String, String> getActionsToRestartMap() {
		return actionsToRestartMap;
	}

	public static HashMap<Action, Script> getActionScriptMap() {
		return actionScriptMap;
	}

	public static HashMap<Script, Sprite> getScriptSpriteMapMap() {
		return scriptSpriteMap;
	}

	public static HashMap<String, Action> getStringActionMap() {
		return stringActionMap;
	}
}
