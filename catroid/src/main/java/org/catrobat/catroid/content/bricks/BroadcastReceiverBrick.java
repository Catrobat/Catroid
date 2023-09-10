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
import org.catrobat.catroid.io.catlang.CatrobatLanguageAttributes;
import org.catrobat.catroid.io.catlang.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.CatrobatLanguageUtils;

import java.util.List;

import androidx.annotation.NonNull;

@CatrobatLanguageBrick(command = "When you receive")
public class BroadcastReceiverBrick extends BroadcastMessageBrick implements ScriptBrick, CatrobatLanguageAttributes {

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

	@Override
	public void appendCatrobatLanguageArguments(StringBuilder brickBuilder) {
		brickBuilder.append("message: (");
		String name = getBroadcastMessage();
		brickBuilder.append(CatrobatLanguageUtils.formatString(name == null ? "" : name));
		brickBuilder.append(')');
	}

	@NonNull
	@Override
	public String serializeToCatrobatLanguage(int indentionLevel) {
		StringBuilder catrobatLanguage = getCatrobatLanguageParameterizedCall(indentionLevel, true);

		for (Brick brick : getScript().getBrickList()) {
			catrobatLanguage.append(brick.serializeToCatrobatLanguage(indentionLevel + 1));
		}

		getCatrobatLanguageBodyClose(catrobatLanguage, indentionLevel);
		return catrobatLanguage.toString();
	}
}
