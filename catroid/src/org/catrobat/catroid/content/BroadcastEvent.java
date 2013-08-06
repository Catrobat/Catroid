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

import com.badlogic.gdx.scenes.scene2d.Event;

public class BroadcastEvent extends Event {

	private BroadcastType type;
	private String broadcastMessage;
	private Sprite senderSprite;
	private BroadcastScript waitScript;
	private boolean run = true;
	private int numberOfReceivers = 0;
	private int numberOfFinishedReceivers = 0;

	public Sprite getSenderSprite() {
		return senderSprite;
	}

	public void setSenderSprite(Sprite senderSprite) {
		this.senderSprite = senderSprite;
	}

	public String getBroadcastMessage() {
		return broadcastMessage;
	}

	public void setBroadcastMessage(String broadcastMessage) {
		this.broadcastMessage = broadcastMessage;
	}

	public BroadcastScript getWaitScript() {
		return waitScript;
	}

	public void setWaitScript(BroadcastScript waitScript) {
		this.waitScript = waitScript;
	}

	public BroadcastType getType() {
		return type;
	}

	public void setType(BroadcastType type) {
		this.type = type;
	}

	public boolean getRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	public int getNumberOfReceivers() {
		return this.numberOfReceivers;
	}

	public void raiseNumberOfReceivers() {
		this.numberOfReceivers++;
	}

	public void raiseNumberOfFinishedReceivers() {
		this.numberOfFinishedReceivers++;
	}

	public boolean checkIfAllReceiversHaveFinished() {
		return numberOfReceivers == numberOfFinishedReceivers;
	}

	public static enum BroadcastType {
		broadcast, broadcastWait
	}
}
