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
package org.catrobat.catroid.common;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.BroadcastEvent;

import java.util.ArrayList;
import java.util.HashMap;

public final class BroadcastWaitSequenceMap {
	private static HashMap<String, ArrayList<SequenceAction>> broadcastWaitSequenceMap = new HashMap<String, ArrayList<SequenceAction>>();
	private static BroadcastEvent currentBroadcastEvent = null;

	private BroadcastWaitSequenceMap() {
		throw new AssertionError();
	}

	public static boolean containsKey(String key) {
		return BroadcastWaitSequenceMap.broadcastWaitSequenceMap.containsKey(key);
	}

	public static ArrayList<SequenceAction> get(String key) {
		return BroadcastWaitSequenceMap.broadcastWaitSequenceMap.get(key);
	}

	public static ArrayList<SequenceAction> put(String key, ArrayList<SequenceAction> value) {
		return BroadcastWaitSequenceMap.broadcastWaitSequenceMap.put(key, value);
	}

	public static ArrayList<SequenceAction> remove(String key) {
		return BroadcastWaitSequenceMap.broadcastWaitSequenceMap.remove(key);
	}

	public static void clear() {
		BroadcastWaitSequenceMap.broadcastWaitSequenceMap.clear();
	}

	public static BroadcastEvent getCurrentBroadcastEvent() {
		return BroadcastWaitSequenceMap.currentBroadcastEvent;
	}

	public static void setCurrentBroadcastEvent(BroadcastEvent broadcastEvent) {
		BroadcastWaitSequenceMap.currentBroadcastEvent = broadcastEvent;
	}

	public static void clearCurrentBroadcastEvent() {
		BroadcastWaitSequenceMap.currentBroadcastEvent = null;
	}
}
