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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class SpeakBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;
	private transient View prototypeView;

	public SpeakBrick() {
		addAllowedBrickField(BrickField.SPEAK);
	}

	public SpeakBrick(String speak) {
		initializeBrickFields(new Formula(speak));
	}

	public SpeakBrick(Formula speak) {
		initializeBrickFields(speak);
	}

	private void initializeBrickFields(Formula speak) {
		addAllowedBrickField(BrickField.SPEAK);
		setFormulaWithBrickField(BrickField.SPEAK, speak);
	}

	@Override
	public int getRequiredResources() {
		return TEXT_TO_SPEECH;
	}

	@Override
	public View getView(final Context context, int brickId, final BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		view = View.inflate(context, R.layout.brick_speak, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_speak_checkbox);
		TextView textField = (TextView) view.findViewById(R.id.brick_speak_edit_text);
		getFormulaWithBrickField(BrickField.SPEAK).setTextFieldId(R.id.brick_speak_edit_text);
		getFormulaWithBrickField(BrickField.SPEAK).refreshTextField(view);

		textField.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_speak, null);
		TextView textSpeak = (TextView) prototypeView.findViewById(R.id.brick_speak_edit_text);
		textSpeak.setText(context.getString(R.string.brick_speak_default_value));
		return prototypeView;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSpeakAction(sprite,
				getFormulaWithBrickField(BrickField.SPEAK)));
		return null;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.SPEAK);
	}
}
