/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.BroadcastMessage;
import org.catrobat.catroid.content.CollisionScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.physics.PhysicsCollision;

import java.util.List;

public class CollisionReceiverBrick extends BrickBaseType implements ScriptBrick, BroadcastMessage, Cloneable {
	private static final long serialVersionUID = 1L;
	public static final String ANYTHING_ESCAPE_CHAR = "\0";

	private CollisionScript collisionScript;
	private transient String selectedMessage;
	ArrayAdapter<String> messageAdapter;

	public CollisionReceiverBrick(String spriteName) {
		this.selectedMessage = spriteName;
	}

	public CollisionReceiverBrick(CollisionScript collisionScript) {
		this.collisionScript = collisionScript;
		this.selectedMessage = "";
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		CollisionReceiverBrick copyBrick = (CollisionReceiverBrick) clone();
		copyBrick.collisionScript = collisionScript;
		return copyBrick;
	}

	@Override
	public Brick clone() {
		return new CollisionReceiverBrick(new CollisionScript(getBroadcastMessage()));
	}

	@Override
	public int getRequiredResources() {
		return PHYSICS;
	}

	@Override
	public String getBroadcastMessage() {
		if (collisionScript == null) {
			return selectedMessage;
		}
		return collisionScript.getBroadcastMessage();
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		if (collisionScript == null) {
			collisionScript = new CollisionScript(selectedMessage);
			MessageContainer.addMessage(getBroadcastMessage());
		}

		view = View.inflate(context, R.layout.brick_physics_collision_receive, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_collision_receive_checkbox);

		final Spinner broadcastSpinner = (Spinner) view.findViewById(R.id.brick_collision_receive_spinner);
		broadcastSpinner.setFocusableInTouchMode(false);
		broadcastSpinner.setFocusable(false);
		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			broadcastSpinner.setClickable(true);
			broadcastSpinner.setEnabled(true);
		} else {
			broadcastSpinner.setClickable(false);
			broadcastSpinner.setEnabled(false);
		}

		broadcastSpinner.setAdapter(getCollisionObjectAdapter(context));
		broadcastSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String collisionObjectOneIdentifier = ProjectManager.getInstance().getCurrentSprite().getName();
				String collisionObjectTwoIdentifier = broadcastSpinner.getSelectedItem().toString();
				if (collisionObjectTwoIdentifier.equals(getDisplayedAnythingString(context))) {
					collisionObjectTwoIdentifier = PhysicsCollision.COLLISION_WITH_ANYTHING_IDENTIFIER;
				}
				selectedMessage = collisionScript.setAndReturnBroadcastMessage(collisionObjectOneIdentifier, collisionObjectTwoIdentifier);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		setSpinnerSelection(broadcastSpinner);
		return view;
	}

	public ArrayAdapter<String> getCollisionObjectAdapter(Context context) {
		String spriteName = ProjectManager.getInstance().getCurrentSprite().getName();
		messageAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);
		messageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		messageAdapter.add(getDisplayedAnythingString(context));
		int resources = Brick.NO_RESOURCES;
		for (Sprite sprite : ProjectManager.getInstance().getCurrentProject().getSpriteListWithClones()) {
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
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_physics_collision_receive, null);
		Spinner broadcastReceiverSpinner = (Spinner) prototypeView.findViewById(R.id.brick_collision_receive_spinner);
		broadcastReceiverSpinner.setFocusableInTouchMode(false);
		broadcastReceiverSpinner.setFocusable(false);
		broadcastReceiverSpinner.setEnabled(false);
		SpinnerAdapter collisionReceiverSpinnerAdapter = getCollisionObjectAdapter(context);
		broadcastReceiverSpinner.setAdapter(collisionReceiverSpinnerAdapter);
		setSpinnerSelection(broadcastReceiverSpinner);
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_collision_receive_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);
			this.alphaValue = alphaValue;
		}
		return view;
	}

	@Override
	public Script getScriptSafe() {
		return collisionScript;
	}

	private void setSpinnerSelection(Spinner spinner) {
		String broadcastMessage = getBroadcastMessage();
		if (broadcastMessage == null || broadcastMessage.equals("")) {
			spinner.setSelection(0);
		} else if (collisionScript != null && collisionScript.getBroadcastMessage().equals(broadcastMessage)) {
			CollisionScript.CollisionObjectIdentifier identifier = collisionScript.splitBroadcastMessage();
			int position = getPositionOfMessageInAdapter(spinner.getContext(), identifier.getCollisionObjectTwoIdentifier());
			spinner.setSelection(position);
		} else {
			int position = getPositionOfMessageInAdapter(spinner.getContext(), broadcastMessage);
			spinner.setSelection(position);
		}
	}

	public int getPositionOfMessageInAdapter(Context context, String message) {
		getCollisionObjectAdapter(context);
		int position = messageAdapter.getPosition(message);
		if (position == -1) {
			return 0;
		} else {
			return position;
		}
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		return null;
	}

	private String getDisplayedAnythingString(Context context) {
		return ANYTHING_ESCAPE_CHAR + context.getString(R.string.collision_with_anything) + ANYTHING_ESCAPE_CHAR;
	}
}
