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

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.EventWrapper;
import org.catrobat.catroid.content.Sprite;

import java.util.Collections;
import java.util.List;

public class SetBackgroundAndWaitBrick extends SetBackgroundBrick {

	public SetBackgroundAndWaitBrick() {
	}

	@Override
	public Brick clone() {
		SetBackgroundAndWaitBrick clonedBrick = new SetBackgroundAndWaitBrick();
		clonedBrick.setLook(look);
		return clonedBrick;
	}

	@Override
	protected View prepareView(Context context) {
		View view = View.inflate(context, R.layout.brick_set_look, null);
		((TextView) view.findViewById(R.id.brick_set_look_text_view))
				.setText(R.string.brick_set_background_and_wait);
		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetLookAction(getSprite(), look, EventWrapper.WAIT));
		return Collections.emptyList();
	}
}
