/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content;

import java.util.concurrent.CountDownLatch;

import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.bricks.BroadcastReceiverBrick;
import at.tugraz.ist.catroid.content.bricks.ScriptBrick;

public class BroadcastScript extends Script {

	private static final long serialVersionUID = 1L;
	private String receivedMessage = "";

	public BroadcastScript(Sprite sprite) {
		super(sprite);
		super.isFinished = true;
	}

	public BroadcastScript(Sprite sprite, BroadcastReceiverBrick brick) {
		this(sprite);
		this.brick = brick;
	}

	@Override
	public ScriptBrick getScriptBrick() {
		if (brick == null) {
			brick = new BroadcastReceiverBrick(sprite, this);
		}

		return brick;
	}

	@Override
	protected Object readResolve() {
		isFinished = true;
		if (receivedMessage != null && receivedMessage.length() != 0) {
			ProjectManager.getInstance().getMessageContainer().addMessage(receivedMessage, this);
		}
		super.readResolve();
		return this;
	}

	public void setBroadcastMessage(String selectedMessage) {
		ProjectManager.getInstance().getMessageContainer().deleteReceiverScript(this.receivedMessage, this);
		this.receivedMessage = selectedMessage;
		ProjectManager.getInstance().getMessageContainer().addMessage(this.receivedMessage, this);
	}

	public String getBroadcastMessage() {
		return this.receivedMessage;
	}

	public void executeBroadcast(CountDownLatch simultaneousStart) {
		sprite.startScriptBroadcast(this, simultaneousStart);
	}

	public void executeBroadcastWait(CountDownLatch simultaneousStart, CountDownLatch wait) {
		sprite.startScriptBroadcastWait(this, simultaneousStart, wait);
	}
}
