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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.ScriptBrick;

import java.util.List;

public class CollisionReceiverBrick extends BrickBaseType implements ScriptBrick {

	public static final String ANYTHING_ESCAPE_CHAR = "\0";
	private static final long serialVersionUID = 1L;
	private CollisionScript collisionScript;
	private transient ArrayAdapter<String> messageAdapter;

	public CollisionReceiverBrick(CollisionScript collisionScript) {
		collisionScript.setScriptBrick(this);
		commentedOut = collisionScript.isCommentedOut();
		this.collisionScript = collisionScript;
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		CollisionReceiverBrick clone = (CollisionReceiverBrick) super.clone();
		clone.messageAdapter = null;
		clone.collisionScript = (CollisionScript) collisionScript.clone();
		clone.collisionScript.setScriptBrick(clone);
		return clone;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_physics_collision_receive;
	}

	@Override
	public View getView(final Context context) {
		super.getView(context);

		final Spinner broadcastSpinner = view.findViewById(R.id.brick_collision_receive_spinner);

		broadcastSpinner.setAdapter(getCollisionObjectAdapter(context));
		broadcastSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String spriteToCollideWith = broadcastSpinner.getSelectedItem().toString();
				if (spriteToCollideWith.equals(getDisplayedAnythingString(context))) {
					spriteToCollideWith = null;
				}
				collisionScript.setSpriteToCollideWithName(spriteToCollideWith);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		setSpinnerSelection(broadcastSpinner);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);
		Spinner broadcastReceiverSpinner = prototypeView.findViewById(R.id.brick_collision_receive_spinner);
		SpinnerAdapter collisionReceiverSpinnerAdapter = getCollisionObjectAdapter(context);

		broadcastReceiverSpinner.setAdapter(collisionReceiverSpinnerAdapter);
		setSpinnerSelection(broadcastReceiverSpinner);
		return prototypeView;
	}

	private ArrayAdapter<String> getCollisionObjectAdapter(Context context) {
		String spriteName = ProjectManager.getInstance().getCurrentSprite().getName();
		messageAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
		messageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		messageAdapter.add(getDisplayedAnythingString(context));
		int resources = Brick.NO_RESOURCES;
		for (Sprite sprite : ProjectManager.getInstance().getCurrentlyEditedScene().getSpriteList()) {
			if (!spriteName.equals(sprite.getName())) {
				resources |= sprite.getRequiredResources();
				if ((resources & Brick.PHYSICS) > 0 && messageAdapter.getPosition(sprite.getName()) < 0) {
					messageAdapter.add(sprite.getName());
					resources &= ~Brick.PHYSICS;
				}
			}
		}
		return messageAdapter;
	}

	@Override
	public Script getScript() {
		return collisionScript;
	}

	private void setSpinnerSelection(Spinner spinner) {
		int position = getPositionOfMessageInAdapter(spinner.getContext(), getBroadcastMessage());
		spinner.setSelection(position);
	}

	private int getPositionOfMessageInAdapter(Context context, String message) {
		getCollisionObjectAdapter(context);
		int position = messageAdapter.getPosition(message);
		if (position == -1) {
			return 0;
		} else {
			return position;
		}
	}

	public String getBroadcastMessage() {
		if (collisionScript.getSpriteToCollideWith() == null) {
			return null;
		}
		return collisionScript.getSpriteToCollideWith().getName();
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		return null;
	}

	private String getDisplayedAnythingString(Context context) {
		return ANYTHING_ESCAPE_CHAR + context.getString(R.string.collision_with_anything) + ANYTHING_ESCAPE_CHAR;
	}

	@Override
	public void setCommentedOut(boolean commentedOut) {
		super.setCommentedOut(commentedOut);
		getScript().setCommentedOut(commentedOut);
	}

	@Override
	public int getRequiredResources() {
		return PHYSICS;
	}
}
