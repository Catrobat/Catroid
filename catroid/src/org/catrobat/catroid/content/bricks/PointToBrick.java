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

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import java.util.List;

public class PointToBrick extends BrickBaseType {

	public static final String EXTRA_NEW_SPRITE_NAME = "EXTRA_NEW_SPRITE_NAME";

	private static final long serialVersionUID = 1L;
	private Sprite pointedObject;
	//TODO: IllyaBoyko: oldSelectedObject should be only present in UI Logic.
	private transient String oldSelectedObject;


	public PointToBrick() {
	}

	public PointToBrick(Sprite pointedSprite) {
		this.pointedObject = pointedSprite;
		this.oldSelectedObject = "";
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		PointToBrick copyBrick = (PointToBrick) clone();
		return copyBrick;
	}

	@Override
	public Brick clone() {
		return new PointToBrick(pointedObject);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.pointTo(sprite, pointedObject));
		return null;
	}

	public void setPointedObject(Sprite pointedObject) {
		this.pointedObject = pointedObject;
	}

	public Sprite getPointedObject() {
		return pointedObject;
	}

	public void setOldSelectedObject(String oldSelectedObject) {
		this.oldSelectedObject = oldSelectedObject;
	}

	public String getOldSelectedObject() {
		return oldSelectedObject;
	}
}
