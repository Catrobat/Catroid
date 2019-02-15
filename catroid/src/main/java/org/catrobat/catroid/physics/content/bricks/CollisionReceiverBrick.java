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
package org.catrobat.catroid.physics.content.bricks;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.StringOption;

import java.util.ArrayList;
import java.util.List;

public class CollisionReceiverBrick extends BrickBaseType implements ScriptBrick,
		BrickSpinner.OnItemSelectedListener<Sprite> {

	private static final long serialVersionUID = 1L;

	public static final String ANYTHING_ESCAPE_CHAR = "\0";

	private CollisionScript collisionScript;

	private transient BrickSpinner<Sprite> spinner;

	public CollisionReceiverBrick(CollisionScript collisionScript) {
		collisionScript.setScriptBrick(this);
		commentedOut = collisionScript.isCommentedOut();
		this.collisionScript = collisionScript;
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		CollisionReceiverBrick clone = (CollisionReceiverBrick) super.clone();

		clone.collisionScript = (CollisionScript) collisionScript.clone();
		clone.collisionScript.setScriptBrick(clone);
		clone.spinner = null;
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_physics_collision_receive;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		List<Nameable> items = new ArrayList<>();
		items.add(new StringOption(ANYTHING_ESCAPE_CHAR + context.getString(R.string.collision_with_anything)
				+ ANYTHING_ESCAPE_CHAR));

		for (Sprite sprite : ProjectManager.getInstance().getCurrentlyEditedScene().getSpriteList()) {
			if (sprite == ProjectManager.getInstance().getCurrentSprite()) {
				continue;
			}
			ResourcesSet resourcesSet = new ResourcesSet();
			sprite.addRequiredResources(resourcesSet);
			if (resourcesSet.contains(Brick.PHYSICS)) {
				items.add(sprite);
			}
		}

		spinner = new BrickSpinner<>(R.id.brick_collision_receive_spinner, view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(collisionScript.getSpriteToCollideWithName());

		return view;
	}

	@Override
	public void onNewOptionSelected() {
	}

	@Override
	public void onStringOptionSelected(String string) {
		collisionScript.setSpriteToCollideWithName(null);
	}

	@Override
	public void onItemSelected(@Nullable Sprite item) {
		collisionScript.setSpriteToCollideWithName(item != null ? item.getName() : null);
	}

	@Override
	public Script getScript() {
		return collisionScript;
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		super.setCommentedOut(commentedOut);
		getScript().setCommentedOut(commentedOut);
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(PHYSICS);
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		return null;
	}
}
