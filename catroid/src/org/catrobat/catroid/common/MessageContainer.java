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
package org.catrobat.catroid.common;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * @author Johannes Iber
 * 
 */
public class MessageContainer {

	private static TreeMap<String, Vector<BroadcastScript>> receiverMap = new TreeMap<String, Vector<BroadcastScript>>();
	private static TreeMap<String, Vector<BroadcastScript>> backupReceiverMap = null;
	private static ArrayAdapter<String> messageAdapter = null;

	public static void clear() {
		receiverMap.clear();
		if (messageAdapter != null) {
			messageAdapter.clear();
			messageAdapter = null;
		}
	}

	public static void createBackup() {
		backupReceiverMap = receiverMap;
		receiverMap = new TreeMap<String, Vector<BroadcastScript>>();
	}

	public static void clearBackup() {
		if (backupReceiverMap != null) {
			backupReceiverMap.clear();
			backupReceiverMap = null;
		}
		if (messageAdapter != null) {
			messageAdapter.clear();
			messageAdapter = null;
		}
	}

	public static void restoreBackup() {
		receiverMap = backupReceiverMap;
		backupReceiverMap = null;
	}

	public static void addMessage(String message) {
		if (message.length() == 0) {
			return;
		}
		if (!receiverMap.containsKey(message)) {
			receiverMap.put(message, new Vector<BroadcastScript>());
			addMessageToAdapter(message);
		}
	}

	public static void addMessage(String message, BroadcastScript script) {
		if (message.length() == 0) {
			return;
		}
		if (receiverMap.containsKey(message)) {
			receiverMap.get(message).add(script);
		} else {
			Vector<BroadcastScript> receiverVec = new Vector<BroadcastScript>();
			receiverVec.add(script);
			receiverMap.put(message, receiverVec);
			addMessageToAdapter(message);
		}
	}

	public static void deleteReceiverScript(String message, BroadcastScript script) {
		if (receiverMap.containsKey(message)) {
			receiverMap.get(message).removeElement(script);
		}
	}

	public static Vector<BroadcastScript> getReceiverOfMessage(String message) {
		return receiverMap.get(message);
	}

	public static Set<String> getMessages() {
		return receiverMap.keySet();
	}

	private static void addMessageToAdapter(String message) {
		if (messageAdapter != null) {
			if (messageAdapter.getPosition(message) < 0) {
				messageAdapter.add(message);
			}
		}
	}

	public static ArrayAdapter<String> getMessageAdapter(Context context) {
		if (messageAdapter == null) {
			messageAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
			messageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			messageAdapter.add(context.getString(R.string.new_broadcast_message));
			addMessage(context.getString(R.string.brick_broadcast_default_value));
			Set<String> messageSet = receiverMap.keySet();
			for (String message : messageSet) {
				if (!message.equals(context.getString(R.string.brick_broadcast_default_value))) {
					messageAdapter.add(message);
				}
			}
		}
		return messageAdapter;
	}

	public static int getPositionOfMessageInAdapter(String message) {
		if (!receiverMap.containsKey(message)) {
			return -1;
		}
		return messageAdapter.getPosition(message);
	}

	public static void removeOtherMessages(List<String> usedMessages) {
		TreeMap<String, Vector<BroadcastScript>> receiverMapCopy = receiverMap;
		receiverMap = new TreeMap<String, Vector<BroadcastScript>>();

		for (String message : receiverMapCopy.keySet()) {
			if (usedMessages.contains(message)) {
				addMessage(message);
			}
		}

		if (messageAdapter != null) {
			Context context = messageAdapter.getContext();
			for (int messageIndex = 0; messageIndex < messageAdapter.getCount(); ++messageIndex) {
				if (!messageAdapter.getItem(messageIndex).equals(context.getString(R.string.new_broadcast_message))
						&& !usedMessages.contains(messageAdapter.getItem(messageIndex))) {
					messageAdapter.remove(messageAdapter.getItem(messageIndex));
				}
			}
		}
	}
}
