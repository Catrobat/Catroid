/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
package org.catrobat.catroid.content.bricks;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.BroadcastMessage;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;

import java.util.List;

public class BroadcastReceiverBrick extends ScriptBrick implements BroadcastMessage {
	private static final long serialVersionUID = 1L;

	private BroadcastScript receiveScript;
	private transient String broadcastMessage;

	public BroadcastReceiverBrick(String broadcastMessage) {
		this.broadcastMessage = broadcastMessage;
		receiveScript = new BroadcastScript(broadcastMessage);
		MessageContainer.addMessage(getBroadcastMessage());
	}

	public BroadcastReceiverBrick(BroadcastScript receiveScript) {
		this.receiveScript = receiveScript;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		BroadcastReceiverBrick copyBrick = (BroadcastReceiverBrick) clone();
		copyBrick.receiveScript = receiveScript;
		return copyBrick;
	}

	@Override
	public Brick clone() {
		return new BroadcastReceiverBrick(new BroadcastScript(getBroadcastMessage()));
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public String getBroadcastMessage() {
		if (receiveScript == null) {
			return broadcastMessage;
		}
		return receiveScript.getBroadcastMessage();
	}

	@Override
	public Script getScriptSafe() {
		return receiveScript;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		return null;
	}

	public void setNewBroadcastMessage(String newBroadcastMessage) {
		receiveScript.setBroadcastMessage(newBroadcastMessage);
		broadcastMessage = newBroadcastMessage;
	}
}
