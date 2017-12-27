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
package org.catrobat.catroid.common;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;

import java.util.List;

public final class MessageContainer {

	private static Multimap<String, BroadcastScript> receiverMap = ArrayListMultimap.create();
	private static ArrayAdapter<String> messageAdapter = null;

	// Suppress default constructor for noninstantiability
	private MessageContainer() {
		throw new AssertionError();
	}

	public static void clear() {
		receiverMap.clear();
		messageAdapter = null;
	}

	public static void clearBackup() {
		messageAdapter = null;
	}

	public static void addMessage(String message) {
		if (message == null) {
			return;
		}
		addMessageToAdapter(message);
		//}
	}

	public static void addMessage(String message, BroadcastScript script) {
		if (message == null) {
			return;
		}
		addMessage(message);
		receiverMap.put(message, script);
	}

	private static void addMessageToAdapter(String message) {
		if (messageAdapter != null && (messageAdapter.getPosition(message) < 0)) {
			messageAdapter.add(message);
		}
	}

	public static void removeReceiverScript(String message, BroadcastScript script) {
		if (message == null || message.isEmpty()) {
			return;
		}
		receiverMap.remove(message, script);
	}

	public static ArrayAdapter<String> getMessageAdapter(Context context) {
		if (messageAdapter == null) {
			messageAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
			messageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			messageAdapter.add(context.getString(R.string.new_broadcast_message));
			boolean addedMessage = false;
			for (String message : receiverMap.keySet()) {
				// BC-TODO: Create RASPI Event Identifier
				// if (message (Constants.RASPI_BROADCAST_PREFIX)) {
				//	continue;
				//}
				addMessageToAdapter(message);
				addedMessage = true;
			}
			if (!addedMessage) {
				addMessageToAdapter(context.getString(R.string.brick_broadcast_default_value));
			}
		}
		return messageAdapter;
	}

	public static int getPositionOfMessageInAdapter(Context context, String message) {
		if (messageAdapter == null) {
			getMessageAdapter(context);
		}
		return messageAdapter.getPosition(message);
	}

	public static String getFirst(Context context) {
		return getMessageAdapter(context).getItem(1);
	}

	public static void removeUnusedMessages(List<String> usedMessages) {
		messageAdapter = null;
		receiverMap = ArrayListMultimap.create();

		for (String message : usedMessages) {
			receiverMap.put(message, null);
		}

		if (messageAdapter != null) {
			Context context = messageAdapter.getContext();
			for (int index = 0; index < messageAdapter.getCount(); index++) {
				String message = messageAdapter.getItem(index);
				if (!message.equals(context.getString(R.string.new_broadcast_message))
						&& !usedMessages.contains(message)) {
					messageAdapter.remove(message);
				}
			}
		}
	}
}
