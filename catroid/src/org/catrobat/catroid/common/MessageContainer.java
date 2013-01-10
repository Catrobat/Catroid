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

import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.catrobat.catroid.content.BroadcastScript;

import android.content.Context;
import android.widget.ArrayAdapter;
import org.catrobat.catroid.R;

/**
 * @author Johannes Iber
 * 
 */
public class MessageContainer {

	private TreeMap<String, Vector<BroadcastScript>> receiverMap = new TreeMap<String, Vector<BroadcastScript>>();
	private ArrayAdapter<String> messageAdapter = null;

	public void addMessage(String message) {
		if (message.length() == 0) {
			return;
		}
		if (!receiverMap.containsKey(message)) {
			receiverMap.put(message, new Vector<BroadcastScript>());
			this.addMessageToAdapter(message);
		}
	}

	public void addMessage(String message, BroadcastScript script) {
		if (message.length() == 0) {
			return;
		}
		if (receiverMap.containsKey(message)) {
			receiverMap.get(message).add(script);
		} else {
			Vector<BroadcastScript> receiverVec = new Vector<BroadcastScript>();
			receiverVec.add(script);
			receiverMap.put(message, receiverVec);
			this.addMessageToAdapter(message);
		}
	}

	public void deleteReceiverScript(String message, BroadcastScript script) {
		if (receiverMap.containsKey(message)) {
			receiverMap.get(message).removeElement(script);
		}
	}

	public Vector<BroadcastScript> getReceiverOfMessage(String message) {
		return receiverMap.get(message);
	}

	public Set<String> getMessages() {
		return receiverMap.keySet();
	}

	private synchronized void addMessageToAdapter(String message) {
		if (messageAdapter != null) {
			messageAdapter.add(message);
		}
	}

	public synchronized ArrayAdapter<String> getMessageAdapter(Context context) {
		if (messageAdapter == null) {
			messageAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
			messageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			messageAdapter.add(context.getString(R.string.broadcast_nothing_selected));
			Set<String> messageSet = receiverMap.keySet();
			for (String message : messageSet) {
				messageAdapter.add(message);
			}
		}
		return messageAdapter;
	}

	public int getPositionOfMessageInAdapter(String message) {
		if (!receiverMap.containsKey(message)) {
			return -1;
		}
		return messageAdapter.getPosition(message);
	}
}
