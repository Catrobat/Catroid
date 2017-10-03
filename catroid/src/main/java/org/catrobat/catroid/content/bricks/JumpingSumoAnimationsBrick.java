/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;

import java.util.List;

public class JumpingSumoAnimationsBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;

	private transient View prototypeView;
	private String animationName;
	private transient Animation animation;

	public enum Animation {
		SPIN, TAB, SLOWSHAKE, METRONOME, ONDULATION, SPINJUMP, SPIRAL, SLALOM
	}

	public JumpingSumoAnimationsBrick(Animation animation) {
		this.animation = animation;
		this.animationName = animation.name();
	}

	protected Object readResolve() {
		if (animationName != null) {
			animation = Animation.valueOf(animationName);
		}
		return this;
	}

	@Override
	public int getRequiredResources() {
		return super.getRequiredResources() | Brick.JUMPING_SUMO;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_jumping_sumo_animations, null);

		Spinner jsAnimationSpinner = (Spinner) prototypeView.findViewById(R.id.brick_jumping_sumo_animation_spinner);
		jsAnimationSpinner.setFocusableInTouchMode(false);
		jsAnimationSpinner.setFocusable(false);
		jsAnimationSpinner.setEnabled(false);

		ArrayAdapter<CharSequence> animationAdapter = ArrayAdapter.createFromResource(context, R.array.brick_jumping_sumo_select_animation_spinner,
				android.R.layout.simple_spinner_item);
		animationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		jsAnimationSpinner.setAdapter(animationAdapter);
		jsAnimationSpinner.setSelection(animation.ordinal());

		return prototypeView;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_jumping_sumo_animations, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);
		setCheckboxView(R.id.brick_jumping_sumo_animation_checkbox);

		ArrayAdapter<CharSequence> animationAdapter = ArrayAdapter.createFromResource(context, R.array.brick_jumping_sumo_select_animation_spinner,
				android.R.layout.simple_spinner_item);
		animationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner animationSpinner = (Spinner) view.findViewById(R.id.brick_jumping_sumo_animation_spinner);

		animationSpinner.setAdapter(animationAdapter);
		animationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				animation = Animation.values()[position];
				animationName = animation.name();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		if (animation == null) {
			readResolve();
		}

		animationSpinner.setSelection(animation.ordinal());

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createJumpingSumoAnimationAction(animation));
		return null;
	}
}
