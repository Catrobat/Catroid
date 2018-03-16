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
package org.catrobat.catroid.content.bricks;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastMessageBrick;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.recyclerview.dialog.NewBroadcastMessageDialog;

import java.util.List;

public class BroadcastReceiverBrick extends BroadcastMessageBrick implements ScriptBrick, NewBroadcastMessageDialog.NewBroadcastMessageInterface {
	private static final long serialVersionUID = 1L;

	private final BroadcastScript broadcastScript;

	public BroadcastReceiverBrick(BroadcastScript broadcastScript) {
		this.broadcastScript = broadcastScript;
		setCommentedOut(broadcastScript.isCommentedOut());
		this.viewId = R.layout.brick_broadcast_receive;
	}

	@Override
	public Brick clone() {
		BroadcastScript broadcastScript = new BroadcastScript(getBroadcastMessage());
		broadcastScript.setCommentedOut(broadcastScript.isCommentedOut());
		return new BroadcastReceiverBrick(broadcastScript);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		return null;
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		super.setCommentedOut(commentedOut);
		getScriptSafe().setCommentedOut(commentedOut);
	}

	@Override
	public String getBroadcastMessage() {
		return broadcastScript.getBroadcastMessage();
	}

	@Override
	public void setBroadcastMessage(String newBroadcastMessage) {
		broadcastScript.setBroadcastMessage(newBroadcastMessage);
		messageAdapter.add(newBroadcastMessage);
	}

	@Override
	public Script getScriptSafe() {
		return broadcastScript;
	}
}
