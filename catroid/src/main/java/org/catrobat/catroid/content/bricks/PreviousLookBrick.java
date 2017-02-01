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
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;

import java.util.List;

public class PreviousLookBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;

	public PreviousLookBrick() {
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		return clone();
	}

	@Override
	public View getPrototypeView(Context context) {
		View view = View.inflate(context, R.layout.brick_previous_look, null);

		if (ProjectManager.getInstance().getCurrentSprite().getName().equals(context.getString(R.string.background))) {
			TextView textField = (TextView) view.findViewById(R.id.brick_previous_look_text_view);
			textField.setText(R.string.brick_previous_background);
		}
		return view;
	}

	@Override
	public Brick clone() {
		return new PreviousLookBrick();
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		view = View.inflate(context, R.layout.brick_previous_look, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_previous_look_checkbox);
		if (ProjectManager.getInstance().getCurrentSprite().getName().equals(context.getString(R.string.background))) {
			TextView textField = (TextView) view.findViewById(R.id.brick_previous_look_text_view);
			textField.setText(R.string.brick_previous_background);
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPreviousLookAction(sprite));
		return null;
	}
}
