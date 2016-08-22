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
package org.catrobat.catroid.content;

import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.actions.SetLookAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class BackgroundWaitHandler {

	private static HashMap<LookData, Integer> numberOfRunningScriptsOfLookData = new HashMap<>();
	private static HashMap<Sprite, HashMap<LookData, ParallelAction>> actionsOfLookDataPerSprite = new HashMap<>();
	private static HashMap<LookData, ArrayList<SetLookAction>> observingActions = new HashMap<>();

	private BackgroundWaitHandler() {
		throw new AssertionError();
	}

	public static void reset() {
		numberOfRunningScriptsOfLookData.clear();
		actionsOfLookDataPerSprite.clear();
		observingActions.clear();
	}

	public static synchronized void decrementRunningScripts(LookData lookData) {
		Integer counter = numberOfRunningScriptsOfLookData.get(lookData);
		if (counter != null) {
			numberOfRunningScriptsOfLookData.put(lookData, --counter);
			if (counter == 0) {
				notifyObservingActions(lookData);
			}
		}
	}

	public static void addObserver(LookData lookData, SetLookAction action) {
		ArrayList<SetLookAction> actions = observingActions.get(lookData);
		if (actions == null) {
			actions = new ArrayList<>();
			observingActions.put(lookData, actions);
		}
		actions.add(action);
	}

	public static void notifyObservingActions(LookData lookData) {
		ArrayList<SetLookAction> actions = observingActions.get(lookData);
		if (actions == null) {
			return;
		}

		for (SetLookAction action : actions) {
			action.notifyScriptsCompleted();
		}
		actions.clear();
	}

	private static void resetNumberOfReceivers(LookData lookData) {
		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteListWithClones();

		Integer scriptsToRun = 0;
		for (Sprite sprite : spriteList) {
			scriptsToRun += sprite.getNumberOfWhenBackgroundChangesScripts(lookData);
		}
		numberOfRunningScriptsOfLookData.put(lookData, scriptsToRun);
		if (scriptsToRun == 0) {
			notifyObservingActions(lookData);
		}
	}

	public static void fireBackgroundChangedEvent(LookData lookData) {
		numberOfRunningScriptsOfLookData.put(lookData, 0);
		resetNumberOfReceivers(lookData);

		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteListWithClones();

		for (Sprite sprite : spriteList) {
			HashMap<LookData, ParallelAction> mapOfSprite = actionsOfLookDataPerSprite.get(sprite);
			if (mapOfSprite == null) {
				mapOfSprite = new HashMap<>();
				actionsOfLookDataPerSprite.put(sprite, mapOfSprite);
			}

			ParallelAction action = mapOfSprite.get(lookData);
			if (action == null) {
				action = sprite.createBackgroundChangedAction(lookData);
				mapOfSprite.put(lookData, action);
			} else {
				Look.actionsToRestartAdd(action);
			}
		}
	}
}
