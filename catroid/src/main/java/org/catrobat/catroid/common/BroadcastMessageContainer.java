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

package org.catrobat.catroid.common;

import org.catrobat.catroid.ProjectManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BroadcastMessageContainer {

	private final List<String> broadcastMessages;

	public BroadcastMessageContainer() {
		this.broadcastMessages = new ArrayList<>();
	}

	public void update() {
		Set<String> usedMessages = ProjectManager.getInstance().getCurrentlyEditedScene().getBroadcastMessagesInUse();
		broadcastMessages.clear();
		broadcastMessages.addAll(usedMessages);
	}

	public boolean addBroadcastMessage(String messageToAdd) {
		return messageToAdd != null
				&& !messageToAdd.isEmpty()
				&& !broadcastMessages.contains(messageToAdd)
				&& broadcastMessages.add(messageToAdd);
	}

	public boolean removeBroadcastMessage(String messageToRemove) {
		return messageToRemove != null
				&& !messageToRemove.isEmpty()
				&& broadcastMessages.contains(messageToRemove)
				&& broadcastMessages.remove(messageToRemove);
	}

	public List<String> getBroadcastMessages() {
		if (broadcastMessages.size() == 0) {
			update();
		}
		return broadcastMessages;
	}
}
