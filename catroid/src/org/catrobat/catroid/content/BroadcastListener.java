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

import org.catrobat.catroid.content.BroadcastEvent.BroadcastType;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public class BroadcastListener implements EventListener {

	@Override
	public boolean handle(Event e) {
		if (e instanceof BroadcastEvent) {
			BroadcastEvent event = (BroadcastEvent) e;
			if (event.getType().equals(BroadcastType.broadcast)) {
				handleBroadcastEvent(event, event.getBroadcastMessage());
				return true;
			}
			if (event.getType().equals(BroadcastType.broadcastFromWaiter)) {
				handleBroadcastFromWaiterEvent(event, event.getBroadcastMessage());
				return true;
			}
			if (event.getType().equals(BroadcastType.broadcastToWaiter)) {
				handleBroadcastToWaiterEvent(event, event.getBroadcastMessage());
				return true;
			}
		}
		return false;
	}

	public void handleBroadcastEvent(BroadcastEvent event, String broadcastMessage) {
	}

	public void handleBroadcastFromWaiterEvent(BroadcastEvent event, String broadcastMessage) {
	}

	public void handleBroadcastToWaiterEvent(BroadcastEvent event, String broadcastMessage) {
	}

}
