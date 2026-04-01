/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.util.List;

public class BroadcastReceiverBrick extends BroadcastMessageBrick implements ScriptBrick {

	private static final long serialVersionUID = 1L;

	private BroadcastScript broadcastScript;

	public BroadcastReceiverBrick() {
		broadcastScript = new BroadcastScript();
	}

	public BroadcastReceiverBrick(BroadcastScript broadcastScript) {
		broadcastScript.setScriptBrick(this);
		commentedOut = broadcastScript.isCommentedOut();
		this.broadcastScript = broadcastScript;
	}

	@Override
	public String getBroadcastMessage() {
		return broadcastScript.getBroadcastMessage();
	}

	@Override
	public void setBroadcastMessage(String broadcastMessage) {
		broadcastScript.setBroadcastMessage(broadcastMessage);
	}

	@Override
	public Script getScript() {
		return broadcastScript;
	}

	@Override
	public int getPositionInScript() {
		return -1;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		BroadcastReceiverBrick clone = (BroadcastReceiverBrick) super.clone();
		clone.broadcastScript = (BroadcastScript) broadcastScript.clone();
		clone.broadcastScript.setScriptBrick(clone);
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_broadcast_receive;
	}

	@Override
	public void addToFlatList(List<Brick> bricks) {
		super.addToFlatList(bricks);
		for (Brick brick : getScript().getBrickList()) {
			brick.addToFlatList(bricks);
		}
	}

	@Override
	public List<Brick> getDragAndDropTargetList() {
		return getScript().getBrickList();
	}

	@Override
	public int getPositionInDragAndDropTargetList() {
		return -1;
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		super.setCommentedOut(commentedOut);
		getScript().setCommentedOut(commentedOut);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
	}
}
