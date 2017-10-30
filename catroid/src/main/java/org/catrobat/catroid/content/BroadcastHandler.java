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
package org.catrobat.catroid.content;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BroadcastWaitSequenceMap;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.actions.BroadcastNotifyAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class BroadcastHandler {

	private static Multimap<String, String> actionsToRestartMap = ArrayListMultimap.create();
	private static HashMap<Action, Script> actionScriptMap = new HashMap<>();
	private static HashMap<Script, Sprite> scriptSpriteMap = new HashMap<>();
	private static HashMap<String, Action> stringActionMap = new HashMap<>();

	private static final String TAG = "BroadcastHandler";

	private BroadcastHandler() {
		throw new AssertionError();
	}

	public static void doHandleBroadcastEvent(Look look, Sprite senderSprite, String broadcastMessage) {
		String sceneName = ProjectManager.getInstance().getSceneToPlay().getName();
		List<SequenceAction> broadcastSequence = look.sprite.getBroadcastSequenceMap().get(broadcastMessage, sceneName);

		if (broadcastSequence == null || broadcastSequence.isEmpty()) {
			return;
		}
		for (SequenceAction action : broadcastSequence) {
			Script scriptOfAction = actionScriptMap.get(action);
			if (!handleAction(action, scriptOfAction)) {
				addOrRestartAction(look, action);
			}
		}
		BroadcastWaitSequenceMap senderWaitSequenceMap = null;
		List<SequenceAction> broadcastWaitSequence = null;
		if (senderSprite != null) {
			senderWaitSequenceMap = senderSprite.getBroadcastWaitSequenceMap();
			broadcastWaitSequence = senderWaitSequenceMap.get(broadcastMessage, sceneName, look.sprite);
		}
		if (broadcastWaitSequence != null) {
			for (SequenceAction action : broadcastWaitSequence) {
				addOrRestartAction(look, action);
			}
			if (senderWaitSequenceMap.getCurrentBroadcastEvent() != null) {
				senderWaitSequenceMap.getCurrentBroadcastEvent().resetEventAndResumeScript();
			}
		}
	}

	public static void doHandleBroadcastFromWaiterEvent(Look look, BroadcastEvent event, String broadcastMessage) {
		String sceneName = ProjectManager.getInstance().getSceneToPlay().getName();
		BroadcastWaitSequenceMap broadcastWaitSequenceMap = event.getSenderSprite().getBroadcastWaitSequenceMap();
		List<SequenceAction> broadcastSequence = look.sprite.getBroadcastSequenceMap().get(broadcastMessage, sceneName);
		List<SequenceAction> broadcastWaitSequence = broadcastWaitSequenceMap.get(broadcastMessage, sceneName, look.sprite);
		if (broadcastSequence == null || broadcastSequence.isEmpty()) {
			return;
		}

		if (broadcastWaitSequence == null) {
			addBroadcastMessageToBroadcastWaitSequenceMap(look, event, broadcastMessage);
		} else {
			if (broadcastWaitSequenceMap.getCurrentBroadcastEvent() == event
					&& broadcastWaitSequenceMap.getCurrentBroadcastEvent() != null) {
				for (SequenceAction action : broadcastWaitSequence) {
					broadcastWaitSequenceMap.getCurrentBroadcastEvent().resetNumberOfFinishedReceivers();
					addOrRestartAction(look, action);
				}
			} else {
				if (broadcastWaitSequenceMap.getCurrentBroadcastEvent() != null) {
					broadcastWaitSequenceMap.getCurrentBroadcastEvent().resetEventAndResumeScript();
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
		ArrayList<SequenceAction> actionList = new ArrayList<>();
		BroadcastWaitSequenceMap senderBroadcastWaitSequenceMap = event.getSenderSprite().getBroadcastWaitSequenceMap();
		senderBroadcastWaitSequenceMap.setCurrentBroadcastEvent(event);
		String sceneName = ProjectManager.getInstance().getSceneToPlay().getName();
		List<SequenceAction> broadcastSequence = look.sprite.getBroadcastSequenceMap().get(broadcastMessage, sceneName);

		if (broadcastSequence != null) {
			for (SequenceAction action : broadcastSequence) {
				SequenceAction broadcastWaitAction = ActionFactory.sequence(action,
						ActionFactory.createBroadcastNotifyAction(event));
				Script receiverScript = actionScriptMap.get(action);
				actionScriptMap.put(broadcastWaitAction, receiverScript);
				Sprite receiverSprite = scriptSpriteMap.get(receiverScript);
				String actionName = broadcastWaitAction.toString() + Constants.ACTION_SPRITE_SEPARATOR + receiverSprite.getName() + receiverSprite.getScriptIndex(receiverScript);
				stringActionMap.put(actionName, broadcastWaitAction);
				if (!handleActionFromBroadcastWait(look.sprite, broadcastWaitAction)) {
					event.raiseNumberOfReceivers();
					actionList.add(broadcastWaitAction);
					addOrRestartAction(look, broadcastWaitAction);
				}
			}
			if (actionList.size() > 0) {
				senderBroadcastWaitSequenceMap.put(sceneName, broadcastMessage, look.sprite,
						actionList);
			}
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

	private static boolean handleActionFromBroadcastWait(Sprite sprite, SequenceAction
			sequenceActionWithBroadcastNotifyAction) {
		Action actualAction = sequenceActionWithBroadcastNotifyAction.getActions().get(0);
		for (Action actionOfLook : sprite.look.getActions()) {
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
					addOrRestartAction(sprite.look, sequenceActionWithBroadcastNotifyAction);
					return false;
				}
			}
		}
		return false;
	}

	public static void removeSpriteFromScriptSpriteMap(Sprite sprite) {
		Iterator<Script> it = scriptSpriteMap.keySet().iterator();
		while (it.hasNext()) {
			if (scriptSpriteMap.get(it.next()) == sprite) {
				it.remove();
			}
		}
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

	public static HashMap<Script, Sprite> getScriptSpriteMap() {
		return scriptSpriteMap;
	}

	public static HashMap<String, Action> getStringActionMap() {
		return stringActionMap;
	}
}
