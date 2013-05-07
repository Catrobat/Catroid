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

import java.util.ArrayList;

import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;

public class BroadcastScript extends Script {

	private static final long serialVersionUID = 1L;
	private String receivedMessage = "";

	public BroadcastScript(Sprite sprite) {
		super(sprite);
	}

	public BroadcastScript(Sprite sprite, BroadcastReceiverBrick brick) {
		this(sprite);
		this.brick = brick;
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
		if (receivedMessage != null && receivedMessage.length() != 0) {
			MessageContainer.addMessage(receivedMessage, this);
		}
		super.readResolve();
		return this;
	}

	public void setBroadcastMessage(String selectedMessage) {
		MessageContainer.deleteReceiverScript(this.receivedMessage, this);
		this.receivedMessage = selectedMessage;
		MessageContainer.addMessage(this.receivedMessage, this);
	}

	public String getBroadcastMessage() {
		return this.receivedMessage;
	}

	@Override
	public Script copyScriptForSprite(Sprite copySprite) {
		BroadcastScript cloneScript = new BroadcastScript(copySprite);
		ArrayList<Brick> cloneBrickList = cloneScript.getBrickList();
		cloneScript.receivedMessage = receivedMessage;

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
