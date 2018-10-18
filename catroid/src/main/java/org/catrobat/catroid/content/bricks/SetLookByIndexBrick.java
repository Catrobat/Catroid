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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.EventWrapper;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.List;

import static org.catrobat.catroid.content.EventWrapper.NO_WAIT;

public class SetLookByIndexBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	@EventWrapper.WaitMode
	protected transient int wait = NO_WAIT;

	public SetLookByIndexBrick() {
		addAllowedBrickField(BrickField.LOOK_INDEX, R.id.brick_set_look_by_index_edit_text);
	}

	public SetLookByIndexBrick(int index) {
		this(new Formula(index));
	}

	public SetLookByIndexBrick(Formula formula) {
		this();
		setFormulaWithBrickField(BrickField.LOOK_INDEX, formula);
	}

	@Override
	public int getViewResource() {
		return wait == EventWrapper.WAIT
				? R.layout.brick_set_look_by_index_and_wait
				: R.layout.brick_set_look_by_index;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		if (getSprite().getName().equals(context.getString(R.string.background))) {
			TextView label = view.findViewById(R.id.brick_set_look_by_index_label);
			label.setText(R.string.brick_set_background_by_index);
		}

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);

		if (getSprite().getName().equals(context.getString(R.string.background))) {
			TextView label = prototypeView.findViewById(R.id.brick_set_look_by_index_label);
			label.setText(R.string.brick_set_background_by_index);
		}

		return prototypeView;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory()
				.createSetLookByIndexAction(sprite, getFormulaWithBrickField(BrickField.LOOK_INDEX), wait));
		return null;
	}

	protected Sprite getSprite() {
		return ProjectManager.getInstance().getCurrentSprite();
	}
}
