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

import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageAttributes;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils;

import java.util.List;

import androidx.annotation.NonNull;

public abstract class ScriptBrickBaseType extends BrickBaseType implements ScriptBrick {

	@Override
	public void addToFlatList(List<Brick> bricks) {
		super.addToFlatList(bricks);
		for (Brick brick : getScript().getBrickList()) {
			brick.addToFlatList(bricks);
		}
	}

	@Override
	public int getPositionInScript() {
		return -1;
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

	@NonNull
	@Override
	public String serializeToCatrobatLanguage(int indentionLevel) {
		String indention = CatrobatLanguageUtils.getIndention(indentionLevel);

		int size = 60;
		if (getScript().getBrickList() != null) {
			size += getScript().getBrickList().size() * 60;
		}
		StringBuilder catrobatLanguage = new StringBuilder(size);
		catrobatLanguage.append(indention);

		if (commentedOut) {
			catrobatLanguage.append("//");
		}
		catrobatLanguage.append(getCatrobatLanguageCommand());

		if (this instanceof CatrobatLanguageAttributes) {
			catrobatLanguage.append(" (");
			((CatrobatLanguageAttributes) this).appendCatrobatLanguageArguments(catrobatLanguage);
			catrobatLanguage.append(')');
		}

		catrobatLanguage.append(" {\n");
		for (Brick subBrick : getScript().getBrickList()) {
			catrobatLanguage.append(subBrick.serializeToCatrobatLanguage(indentionLevel + 1));
		}
		getCatrobatLanguageBodyClose(catrobatLanguage, indentionLevel);
		return catrobatLanguage.toString();
	}
}
