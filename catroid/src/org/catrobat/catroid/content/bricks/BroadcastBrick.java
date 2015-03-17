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
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.List;

public class BroadcastBrick extends BrickBaseType implements BroadcastMessage {
	private static final long serialVersionUID = 1L;

	protected String broadcastMessage;

	protected Object readResolve() {
		MessageContainer.addMessage(broadcastMessage);
		return this;
	}

	public BroadcastBrick(String broadcastMessage) {
		this.broadcastMessage = broadcastMessage;
		MessageContainer.addMessage(broadcastMessage);
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		BroadcastBrick copyBrick = (BroadcastBrick) clone();
		return copyBrick;
	}

	@Override
	public Brick clone() {
		return new BroadcastBrick(broadcastMessage);
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public String getBroadcastMessage() {
		return broadcastMessage;
	}

	public void setBroadcastMessage(String broadcastMessage) {
		this.broadcastMessage = broadcastMessage;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.broadcast(sprite, broadcastMessage));
		return null;
	}

}