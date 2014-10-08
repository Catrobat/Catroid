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
package org.catrobat.catroid.content;

import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;

import java.util.ArrayList;

public class BroadcastScript extends Script implements BroadcastMessage {

	private static final long serialVersionUID = 1L;
	private String receivedMessage;

	public BroadcastScript(Sprite sprite, String broadcastMessage) {
		super(sprite);
		setBroadcastMessage(broadcastMessage);
	}

	@Override
	public ScriptBrick getScriptBrick() {
		if (brick == null) {
			brick = new BroadcastReceiverBrick(object, this);
		}

		return brick;
	}

	@Override
	protected Object readResolve() {
		MessageContainer.addMessage(receivedMessage, this);
		super.readResolve();
		return this;
	}

	@Override
	public String getBroadcastMessage() {
		return receivedMessage;
	}

	public void setBroadcastMessage(String broadcastMessage) {
		MessageContainer.removeReceiverScript(this.receivedMessage, this);
		this.receivedMessage = broadcastMessage;
		MessageContainer.addMessage(this.receivedMessage, this);

	}

	@Override
	public Script copyScriptForSprite(Sprite copySprite) {
		BroadcastScript cloneScript = new BroadcastScript(copySprite, receivedMessage);
		ArrayList<Brick> cloneBrickList = cloneScript.getBrickList();

		for (Brick brick : getBrickList()) {
			Brick copiedBrick = brick.copyBrickForSprite(copySprite, cloneScript);
			if (copiedBrick instanceof IfLogicEndBrick) {
				setIfBrickReferences((IfLogicEndBrick) copiedBrick, (IfLogicEndBrick) brick);
			} else if (copiedBrick instanceof LoopEndBrick) {
				setLoopBrickReferences((LoopEndBrick) copiedBrick, (LoopEndBrick) brick);
			}
			cloneBrickList.add(copiedBrick);
		}

		return cloneScript;
	}
}
